package co.shoppyguide.App.Activity.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import co.shoppyguide.R;
import co.shoppyguide.databinding.ActivityAuthVerificationBinding;
import spencerstudios.com.bungeelib.Bungee;

public class AuthVerificationActivity extends AppCompatActivity {

    ActivityAuthVerificationBinding bind;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = DataBindingUtil.setContentView(AuthVerificationActivity.this, R.layout.activity_auth_verification);
        firebaseAuth = FirebaseAuth.getInstance();
        initializeGUI();
        setSupportActionBar(bind.bgHeader);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void initializeGUI() {
        bind.buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bind.progress.setVisibility(View.VISIBLE);
                if (firebaseAuth.getCurrentUser() != null) {
                    firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            bind.progress.setVisibility(View.INVISIBLE);
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "verification email send to " + firebaseAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AuthVerificationActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();


                            }

                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "sign in first to verify", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    Bungee.swipeLeft(AuthVerificationActivity.this);
                    finish();
                }
            }
        });
        bind.textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
                Bungee.swipeLeft(AuthVerificationActivity.this);
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
        Bungee.swipeLeft(AuthVerificationActivity.this);

    }
}
