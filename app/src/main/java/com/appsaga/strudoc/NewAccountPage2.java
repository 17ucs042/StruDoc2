package com.appsaga.strudoc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class NewAccountPage2 extends AppCompatActivity {

    TextView goBack;
    TextView createAccount;
    FirebaseAuth firebaseAuth;

    EditText first_name,last_name,company,type,phone;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account_page2);

        goBack = findViewById(R.id.go_back_text);
        createAccount = findViewById(R.id.create_account_text);
        first_name=findViewById(R.id.first_name_edit_text);
        last_name=findViewById(R.id.last_name_edit_text);
        company=findViewById(R.id.company_edit_text);
        type=findViewById(R.id.company_type_edit_text);
        phone=findViewById(R.id.phone_edit_text);
        firebaseAuth=FirebaseAuth.getInstance();
        final String id = getIntent().getStringExtra("id");
        pd = new ProgressDialog(NewAccountPage2.this);
        pd.setMessage("loading");

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        phone.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    //do what you want on the press of 'done'
                    pd.show();
                    createAccount.performClick();
                    pd.dismiss();
                }
                return false;
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pd.show();

                String first = first_name.getText().toString().trim();
                String last=last_name.getText().toString().trim();
                String comp=company.getText().toString().trim();
                String ph = phone.getText().toString().trim();

                if(!first.equals("") && !last.equals("") && !comp.equals("") && !ph.equals(""))
                {
                    firebaseAuth.signInWithEmailAndPassword(id, "testing_app")
                            .addOnCompleteListener(NewAccountPage2.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (!task.isSuccessful()) {

                                        Toast.makeText(NewAccountPage2.this, "Some error occurred", Toast.LENGTH_LONG).show();
                                        pd.dismiss();
                                    } else {

                                        checkIfEmailVerified();
                                    }

                                }
                            });
                }
                else
                {
                    Toast.makeText(NewAccountPage2.this,"Please fill in all the details",Toast.LENGTH_LONG).show();
                    pd.dismiss();
                }
            }
        });
    }

    private void checkIfEmailVerified() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.isEmailVerified()) {
            // user is verified, so you can finish this activity or send user to activity which you want.
            startActivity(new Intent(NewAccountPage2.this, MainScreen.class));
            Toast.makeText(NewAccountPage2.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
            pd.dismiss();
            finishAffinity();
        } else {
            // email is not verified, so just prompt the message to the user and restart this activity.
            // NOTE: don't forget to log out the user.
            Toast.makeText(NewAccountPage2.this, "Email id not verified", Toast.LENGTH_LONG).show();
            FirebaseAuth.getInstance().signOut();
            pd.dismiss();
            //restart this activity

        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        firebaseAuth.getCurrentUser().delete();
    }

}
