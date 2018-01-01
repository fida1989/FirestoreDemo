package com.firestoredemo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String MESSAGE = "message";
    private static final String NAME = "name";
    private static final String ID = "id";
    private FirebaseFirestore dRef = FirebaseFirestore.getInstance();
    private EditText et, et2;
    ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    private SimpleAdapter sa;
    HashMap<String, String> item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et = (EditText) findViewById(R.id.editText);
        et2 = (EditText) findViewById(R.id.editText2);

        sa = new SimpleAdapter(this, list,
                R.layout.twolines,
                new String[]{"line1", "line2"},
                new int[]{R.id.line_a, R.id.line_b});
        ((ListView) findViewById(R.id.list)).setAdapter(sa);


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = et.getText().toString();
                String message = et2.getText().toString();
                if (name.isEmpty() || message.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Fill All Data!", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> emailData = new HashMap<String, Object>();
                    emailData.put(NAME, name);
                    emailData.put(MESSAGE, message);
                    long millisStart = Calendar.getInstance().getTimeInMillis();
                    dRef.collection("emailData").document(String.valueOf(millisStart)).set(emailData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Toast.makeText(MainActivity.this, "Data Saved!", Toast.LENGTH_SHORT).show();
                            et.setText(null);
                            et2.setText(null);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();


        dRef.collection("emailData")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {

                        if (e != null) {
                            System.out.println(e);
                            return;
                        }
                        list.clear();
                        for (int i = 0; i < snapshots.getDocuments().size(); i++) {
                            item = new HashMap<String, String>();
                            item.put("line1", snapshots.getDocuments().get(i).getString("message"));
                            item.put("line2", snapshots.getDocuments().get(i).getString("name"));
                            list.add(item);
                        }
                        Collections.reverse(list);
                        sa.notifyDataSetChanged();
                    }
                });
    }
}
