package co.shoppyguide.App.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import co.shoppyguide.App.Activity.Auth.LoginActivity;
import co.shoppyguide.R;
import spencerstudios.com.bungeelib.Bungee;

public class SplashScreenActivity extends AppCompatActivity {

    //Check current user
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        setAnimation();
        int SPLASH_TIME_OUT = 4000;
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                checkUser();
            }
        }, SPLASH_TIME_OUT);

    }


    private void setAnimation() {
        findViewById(R.id.ivImageLogo).setAlpha(1.0F);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade);
        findViewById(R.id.ivImageLogo).startAnimation(anim);
    }


    private void checkUser() {
        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if (firebaseUser == null) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    Bungee.inAndOut(SplashScreenActivity.this);
                    finish();
                }
                if (firebaseUser != null) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    Bungee.inAndOut(SplashScreenActivity.this);
                    finish();
                }
            }
        });
    }

}
