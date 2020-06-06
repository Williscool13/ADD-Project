package com.example.addproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef=database.getReference("message");

        myRef.setValue("Hello, World!");


        Button btn = (Button) findViewById(R.id.big_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("My App", "This is a magic log message");
                myRef.setValue("Hello, World 2: Electric Boogaloo!");
                Toast.makeText(getApplicationContext(), "It's Magic", Toast.LENGTH_LONG)
                        .show();


            }
        });
    }

    /*
    ValueEventListener changeListener = new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            String change = dataSnapshot.child(
                    currentUser.getUid()).child("message")
                    .getValue(String.class);

            userText.setText(change);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            notifyUser("Database error: " + databaseError.toException());
        }

        private void notifyUser(String message) {
            Toast.makeText(MainActivity.this, message,
                    Toast.LENGTH_SHORT).show();
        }
    };
     */


}
