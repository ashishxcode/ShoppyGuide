package co.shoppyguide.App.Activity.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import co.shoppyguide.App.Activity.MainActivity;
import co.shoppyguide.App.Model.User;
import co.shoppyguide.R;
import co.shoppyguide.databinding.ActivityEditProfileBinding;
import es.dmoral.toasty.Toasty;
import id.zelory.compressor.Compressor;
import spencerstudios.com.bungeelib.Bungee;

public class EditProfileActivity extends AppCompatActivity {

    ActivityEditProfileBinding bind;
    User user;
    FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    private Uri mainImageURI = null;
    private boolean isChanged = false;
    private StorageReference storageReference;
    private Bitmap compressedImageFile;
    Boolean isUserFirstTime = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);

        user = new User();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user_id = firebaseAuth.getCurrentUser().getUid();

        setSupportActionBar(bind.toolbar);
        bind.toolbar.setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bind.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (getIntent().getExtras() != null) {
            String firstFirstTime = getIntent().getStringExtra("LoginActivity");
            if (firstFirstTime.equals("sign_in_for_first_time")) {
                isUserFirstTime = true;
                bind.toolbar.setNavigationIcon(null);
                bind.toolbar.setTitle("Save Profile");
            }
        }
        stateLoading(true);
        firebaseFirestore.disableNetwork().addOnCompleteListener(new OnCompleteListener<Void>() {//loading data from cache
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadData(); //lo
                firebaseFirestore.enableNetwork().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    //loading data from network
                    public void onSuccess(Void aVoid) {
                        loadData();
                    }
                });
            }
        });
        bind.buttonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
        bind.changeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }
                    BringImagePicker();
                } else {
                    BringImagePicker();
                }
            }

        });
    }

    private void loadData() {
        bind.textViewEmail.setText(firebaseUser.getEmail());

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                user.setName(firebaseUser.getDisplayName());

                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        user = task.getResult().toObject(User.class);
                    }
                    if (firebaseUser.getPhotoUrl() != null) {


                        Picasso.get().load(firebaseUser.getPhotoUrl()).placeholder(R.drawable.ic_profile_default).
                                into(bind.profileImage);
                    }
                    bind.editTextName.setText(user.getName());
                    bind.editTextPhoneNumber.setText(user.getPhone());

                } else {
                    if (!isUserFirstTime) {
                        Toasty.error(getApplicationContext(), task.getException().getLocalizedMessage(), Toasty.LENGTH_SHORT).show();
                    }
                }
                stateLoading(false);
            }
        });
    }


    private void storeData(final User user) {
        UserProfileChangeRequest profileUpdates;
        if (isChanged && user.getProfilePhotoUrl() != null) {
            profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(user.getName())
                    .setPhotoUri(Uri.parse(user.getProfilePhotoUrl()))
                    .build();
        } else {
            profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(user.getName())
                    .build();

        }

        firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    storeFirestoreData(user);
                } else {
                    Toast.makeText(getApplicationContext(), "error in storing image", Toast.LENGTH_LONG).show();
                }
            }
        });


        stateLoading(true);


    }

    private void storeFirestoreData(User user) {

        firebaseFirestore.collection("Users").document(user_id).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {

                    Toasty.success(getApplicationContext(), "Profile Updated.", Toast.LENGTH_LONG).show();
                    Intent mainIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                    startActivity(mainIntent);
                    Bungee.swipeLeft(EditProfileActivity.this);
                    finish();

                } else {


                    Toasty.error(getApplicationContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();

                }
            }
        });
    }


    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(EditProfileActivity.this);
    }


    private void stateLoading(boolean stateStatus) {
        if (stateStatus) {
            bind.ProfileLayout.setVisibility(View.GONE);
            bind.progressCricular.setVisibility(View.VISIBLE);
        } else {
            bind.ProfileLayout.setVisibility(View.VISIBLE);
            bind.progressCricular.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                Log.i("onactresultresult", "mainurl:" + mainImageURI.toString());

                bind.profileImage.setImageURI(mainImageURI);

                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();

            }
        }
    }

    void saveProfile() {
        user.setName(bind.editTextName.getText().toString());
        user.setPhone(bind.editTextPhoneNumber.getText().toString());


        UserProfileChangeRequest profileUpdates;
        if (user.getName() != null && user.getPhone() != null) {
            stateLoading(true);
            if (isChanged) {


                File newImageFile = new File(mainImageURI.getPath());
                try {

                    compressedImageFile = new Compressor(EditProfileActivity.this)
                            .setMaxHeight(125)
                            .setMaxWidth(125)                                             //compressing image
                            .setQuality(50)
                            .compressToBitmap(newImageFile);

                } catch (IOException e) {
                    e.printStackTrace();
                }


                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 40, baos);   //converting into bitmap
                byte[] thumbData = baos.toByteArray();

                final StorageReference ref = storageReference.child("profile_images").child(user_id + ".jpg");
                UploadTask image_path = ref.putBytes(thumbData);                                                         //uploading image

                Task<Uri> urlTask = image_path.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    //getting image reference

                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            user.setProfilePhotoUrl(downloadUri.toString());

                            storeData(user);
                            Log.i("image uploaded", user.getProfilePhotoUrl());

                        } else {
                            // Handle failures
                            Toast.makeText(getApplicationContext(), "error on uploading image", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                storeData(user);
            }
        }
    }
}
