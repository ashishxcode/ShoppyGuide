package co.shoppyguide.App.Activity.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import co.shoppyguide.App.Activity.Profile.EditProfileActivity;
import co.shoppyguide.App.Activity.MainActivity;
import co.shoppyguide.R;
import co.shoppyguide.databinding.ActivityLoginBinding;
import es.dmoral.toasty.Toasty;
import spencerstudios.com.bungeelib.Bungee;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding bind;
    //defining views

    //firebase auth object
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String TAG = "LoginActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = DataBindingUtil.setContentView(this, R.layout.activity_login);

        //getting firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        initializeGUI();
    }

    private void initializeGUI() {
        bind.textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                Bungee.swipeRight(LoginActivity.this);
            }
        });

        bind.textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
                Bungee.swipeRight(LoginActivity.this);
            }
        });
        bind.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = bind.editTextEmail.getText().toString();
                final String password = bind.editTextPassword.getText().toString();

                if (email.isEmpty()) {

                } else if (password.isEmpty()) {

                } else {
                    bind.progress.setVisibility(View.VISIBLE);
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    bind.progress.setVisibility(View.INVISIBLE);
                                    if (task.isSuccessful()) {


                                        if (firebaseAuth.getCurrentUser().isEmailVerified()) {

                                            Task<DocumentSnapshot> defRef = firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot documentReference = task.getResult();
                                                        if (documentReference.exists()) {
                                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                            Bungee.swipeRight(LoginActivity.this);
                                                        } else {
                                                            Toasty.info(LoginActivity.this, "add your additional information", Toasty.LENGTH_SHORT).show();
                                                            signInFirstTime();
                                                        }
                                                    } else {
                                                        Toasty.error(LoginActivity.this, task.getException().getLocalizedMessage(), Toasty.LENGTH_SHORT).show();
                                                    }

                                                }
                                            });
                                            // Sign in success, update UI with the signed-in user's information

                                        } else {
                                            Toasty.info(LoginActivity.this, "Need to verify account", Toasty.LENGTH_SHORT).show();
                                            startActivity(new Intent(LoginActivity.this, AuthVerificationActivity.class));
                                            Bungee.swipeRight(LoginActivity.this);
                                        }
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toasty.error(LoginActivity.this, task.getException().getLocalizedMessage(), Toasty.LENGTH_SHORT).show();

                                    }


                                }
                            });
                }
            }
        });
    }

    void signInFirstTime() {
        Intent intent = new Intent(LoginActivity.this, EditProfileActivity.class);
        intent.putExtra(TAG, "sign_in_for_first_time");
        startActivity(intent);
    }
}