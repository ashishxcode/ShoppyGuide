package co.shoppyguide.App.Activity.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import co.shoppyguide.App.Activity.Auth.RegisterActivity;
import co.shoppyguide.App.Activity.MainActivity;
import co.shoppyguide.App.Model.User;
import co.shoppyguide.R;
import co.shoppyguide.databinding.ActivityProfileBinding;
import spencerstudios.com.bungeelib.Bungee;

public class ProfileActivity extends AppCompatActivity {
    ActivityProfileBinding bind;
    User user;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = DataBindingUtil.setContentView(this, R.layout.activity_profile);


        setSupportActionBar(bind.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bind.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                Bungee.swipeLeft(ProfileActivity.this);
            }
        });


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        stateLoading(true);
        firebaseFirestore.disableNetwork().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadData(); //lo
                firebaseFirestore.enableNetwork().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadData();
                    }
                });
            }
        });
        bind.buttonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), EditProfileActivity.class));
                finish();
                Bungee.swipeRight(ProfileActivity.this);
            }
        });

    }

    private void loadData() {
        user = new User();

        firebaseFirestore.collection("Users").document(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                bind.profileName.setText(firebaseUser.getDisplayName());
                bind.profileEmail.setText(firebaseUser.getEmail());
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        bind.profilePhone.setText(task.getResult().getString("phone"));
                    }
                    if (firebaseUser.getPhotoUrl() != null) {
                        Picasso.get().load(firebaseUser.getPhotoUrl()).placeholder(R.drawable.ic_profile_default).into(bind.userProfilePhoto);

                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(getApplicationContext(), "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();
                }
                stateLoading(false);
            }
        });
    }

    private void stateLoading(boolean stateStatus) {
        if (stateStatus) {
            bind.ProfileLayout.setVisibility(View.GONE);
            bind.progressLoading.setVisibility(View.VISIBLE);
        } else {
            bind.ProfileLayout.setVisibility(View.VISIBLE);
            bind.progressLoading.setVisibility(View.GONE);
        }
    }
}
