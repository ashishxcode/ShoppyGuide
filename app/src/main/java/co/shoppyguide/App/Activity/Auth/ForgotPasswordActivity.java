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
import co.shoppyguide.databinding.ActivityForgotPasswordBinding;
import spencerstudios.com.bungeelib.Bungee;

public class ForgotPasswordActivity extends AppCompatActivity {

    ActivityForgotPasswordBinding bind;
    //firebase auth object
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = DataBindingUtil.setContentView(ForgotPasswordActivity.this, R.layout.activity_forgot_password);

        firebaseAuth = FirebaseAuth.getInstance();

        bind.buttonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = bind.editTextEmail.getText().toString().trim();

                if (email.isEmpty()) {
                    bind.editTextEmail.setError("Enter your registered email");
                    bind.editTextEmail.requestFocus();
                } else {
                    bind.progress.setVisibility(View.VISIBLE);

                    firebaseAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                    }
                                    bind.progress.setVisibility(View.INVISIBLE);
                                }
                            });
                }
            }
        });
    }

    public void backLogin(View view) {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
        Bungee.swipeLeft(ForgotPasswordActivity.this);
    }
}
