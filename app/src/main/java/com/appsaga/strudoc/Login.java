package com.appsaga.strudoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class Login extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    EditText email, password, confirm_pass;
    Button goToMain;
    TextView cancel;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email_edit_text);
        //  password = findViewById(R.id.password_edit_text);
        // confirm_pass = findViewById(R.id.confirm_edit_text);
        goToMain = findViewById(R.id.go_to_main);
        cancel=findViewById(R.id.cancel_text);

        firebaseAuth = FirebaseAuth.getInstance();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        goToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pd = new ProgressDialog(Login.this);
                pd.setMessage("loading");
                pd.show();

                String id = email.getText().toString().trim();
                //String pass = password.getText().toString().trim();
                //  String con_pass = confirm_pass.getText().toString().trim();

                if (id.equals("")) {
                    Toast.makeText(Login.this, "ID not registered", Toast.LENGTH_LONG).show();
                    pd.dismiss();
                } else {

                    firebaseAuth.signInWithEmailAndPassword(id, "testing_app")
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                   // Log.d("Testing",task.getResult()+"");
                                    if (!task.isSuccessful()) {

                                        Toast.makeText(Login.this, "Some error occurred", Toast.LENGTH_LONG).show();
                                        pd.dismiss();
                                    } else {

                                        checkIfEmailVerified();
                                    }

                                }
                            });
                }
            }
        });
    }

    private void checkIfEmailVerified() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.isEmailVerified()) {
            // user is verified, so you can finish this activity or send user to activity which you want.
            startActivity(new Intent(Login.this, MainScreen.class));
            finishAffinity();
            pd.dismiss();
            Toast.makeText(Login.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
        } else {
            // email is not verified, so just prompt the message to the user and restart this activity.
            // NOTE: don't forget to log out the user.
            Toast.makeText(Login.this, "Email id not verified", Toast.LENGTH_LONG).show();
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    Toast.makeText(Login.this, "We have sent you a verification email", Toast.LENGTH_SHORT).show();
                }
            });
            FirebaseAuth.getInstance().signOut();
            pd.dismiss();

            //restart this activity

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
