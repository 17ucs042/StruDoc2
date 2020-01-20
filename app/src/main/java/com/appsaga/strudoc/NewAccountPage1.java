package com.appsaga.strudoc;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class NewAccountPage1 extends AppCompatActivity {

    TextView goToNext;
    TextView cancelText;
    EditText email_et;
    FirebaseAuth firebaseAuth;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account_page1);

        goToNext = findViewById(R.id.go_to_next_text);
        cancelText = findViewById(R.id.cancel_text);
        email_et = findViewById(R.id.email_edit_text);

        firebaseAuth = FirebaseAuth.getInstance();

        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        goToNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pd = new ProgressDialog(NewAccountPage1.this);
                pd.setMessage("loading");
                pd.show();

                final String id = email_et.getText().toString().trim();

                if (id.equals("")) {
                    Toast.makeText(NewAccountPage1.this, "Email id cannot be empty", Toast.LENGTH_LONG).show();
                    pd.dismiss();
                } else {

                    String pass = "testing_app";

                    firebaseAuth.createUserWithEmailAndPassword(id, pass)
                            .addOnCompleteListener(NewAccountPage1.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        Toast.makeText(NewAccountPage1.this, "We have send you a verification mail. Please verify your email", Toast.LENGTH_LONG).show();

                                        firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                Intent intent = new Intent(NewAccountPage1.this, com.appsaga.strudoc.NewAccountPage2.class);
                                                intent.putExtra("id",id);
                                                pd.dismiss();
                                                startActivity(intent);
                                            }
                                        });

                                    } else {

                                        Toast.makeText(NewAccountPage1.this, "Email ID not valid or some error occurred", Toast.LENGTH_LONG).show();
                                        pd.dismiss();

                                    }
                                }
                            });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
