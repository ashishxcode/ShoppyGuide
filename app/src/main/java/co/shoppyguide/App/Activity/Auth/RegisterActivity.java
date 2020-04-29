package co.shoppyguide.App.Activity.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import co.shoppyguide.App.Activity.MainActivity;
import co.shoppyguide.App.Model.User;
import co.shoppyguide.R;
import co.shoppyguide.databinding.ActivityRegisterBinding;
import spencerstudios.com.bungeelib.Bungee;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding bind;
    public static final String TAG = "TAG";
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestoree;
    String userID;
    User user;
    //progressbar
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = DataBindingUtil.setContentView(this, R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestoree = FirebaseFirestore.getInstance();
        user = new User();
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            Bungee.swipeRight(RegisterActivity.this);
            finish();
        }

        bind.buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        bind.textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                Bungee.swipeLeft(RegisterActivity.this);
                finish();
            }
        });
    }

    private void registerUser() {
        final String email = bind.editTextEmail.getText().toString().trim();
        String password = bind.editTextPassword.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            bind.editTextEmail.setError("Email is Required.");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            bind.editTextPassword.setError("Password is Required.");
            return;
        }

        if (password.length() < 6) {
            bind.editTextPassword.setError("Password Must be >= 6 Characters");
            return;
        }

        bind.progress.setVisibility(View.VISIBLE);

        // register the user in firebase

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "New account created", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                    bind.progress.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(RegisterActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}
