package com.appsaga.strudoc;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity {

    TextView newAccount;   // text view for adding new account
    Button Login;          // login using this button
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        newAccount = findViewById(R.id.new_account);
        Login = findViewById(R.id.login_button);

        newAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginPage.this,NewAccountPage1.class));
            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginPage.this, com.appsaga.strudoc.Login.class));
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        boolean verified = false;

        try {

            verified=firebaseAuth.getCurrentUser().isEmailVerified();
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }

        if (user != null && verified) {
            Intent intent = new Intent(LoginPage.this, MainScreen.class);
            startActivity(intent);
            finish();
        }
        /*else if(!verified && user!=null)
        {
            Toast.makeText(LoginPage.this,"Please verify your email id",Toast.LENGTH_LONG).show();

            firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    firebaseAuth.signOut();
                }
            });
        }*/
    }
}
