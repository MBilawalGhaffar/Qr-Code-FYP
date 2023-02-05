package com.example.qrcodefyp.activity;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.qrcodefyp.BuildConfig;
import com.example.qrcodefyp.R;
import com.example.qrcodefyp.callback.OnImagePicked;
import com.example.qrcodefyp.callback.OnImagePicker;
import com.example.qrcodefyp.dialog.AddReceiptDialog;
import com.example.qrcodefyp.model.User;
import com.example.qrcodefyp.model.UserAuth;
import com.example.qrcodefyp.preference.AuthPreference;
import com.example.qrcodefyp.preference.UserPreference;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int ALL_PERMISSION_CODE = 101;
    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private AddReceiptDialog.ReturnCallback returnCallback;
    private ActivityResultLauncher<Intent> galleryActivityResultLauncher;
    private ActivityResultLauncher<Intent> cameraActivityResultLauncher;
    private @Nullable OnImagePicked onImagePicked;

    private User user;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private MaterialCardView buttonAddReceipt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        user=new UserPreference(Home.this).getUser();
        drawerLayout = findViewById(R.id.my_drawer_layout);
        buttonAddReceipt=findViewById(R.id.button_add);
        navigationView=findViewById(R.id.nav_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(this);


        buttonAddReceipt.setOnClickListener(view ->{
                    addReceiptDialog();
                }
        );


        galleryActivityResultLauncher =registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // Here, no request code
                            Intent data = result.getData();
                            Uri uri=data.getData();
                            CropImage.activity(uri)
                                    .setGuidelines(CropImageView.Guidelines.ON)
                                    .start(Home.this);
                        }
                    }
                });

        cameraActivityResultLauncher=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        if (result.getResultCode() == Activity.RESULT_OK) {

                            File captureFile=new File(getExternalCacheDir(),getString(R.string.cam_image_file_name));
                            if(captureFile.exists()){
                                @Nullable Uri uri;
                                try {
                                    uri= Uri.fromFile(captureFile);
                                }catch (Exception e){
                                    uri=FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", captureFile);
                                }

                                CropImage.activity(uri)
                                        .setGuidelines(CropImageView.Guidelines.ON)
                                        .start(Home.this);
                            }
                        }else{
                            Toast.makeText(Home.this,"Error while capturing image!",Toast.LENGTH_SHORT).show();
                        }

                    }
                }
        );



        cameraPermissionLauncher=registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted -> {
                    returnCallback.returnCall(isGranted);
                });

        requestAllPermission();

    }

    private void requestAllPermission() {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA},ALL_PERMISSION_CODE);
    }
    public interface Callback {
        void call(AddReceiptDialog.ReturnCallback returnCallback);
    }
    private void addReceiptDialog() {
        Dialog addReceiptDialog=new AddReceiptDialog(Home.this,R.style.ReceiptDialog,new Callback(){
            @Override
            public void call(AddReceiptDialog.ReturnCallback mreturnCallback) {
                returnCallback=mreturnCallback;
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        },new OnImagePicker(){
            @Override
            public void onImagePicker(OnImagePicked callback, Boolean camera) {
                onImagePicked=callback;
                if (camera){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", createImageFile());
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    cameraActivityResultLauncher.launch(intent);
                }else {

                    Intent intent=new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    galleryActivityResultLauncher.launch(intent);
                }


            }
        });
        addReceiptDialog.setCancelable(false);
        addReceiptDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addReceiptDialog.show();
    }

    private File createImageFile() {
        File camImageFile = new File(getExternalCacheDir(),getString(R.string.cam_image_file_name));
        return camImageFile;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout: {
                logoutUser();
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logoutUser() {
        new UserPreference(Home.this).addUser(new User("00000000","Ali","ali@gmail.com"));
        new AuthPreference(Home.this).addAuth(new UserAuth(false,false));
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(Home.this,"Logout successfully!",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(Home.this,LoginActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode== RESULT_OK){
                try {
                    Uri uri = result.getUri();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    if(onImagePicked!=null){
                        onImagePicked.onImagePicked(uri,bitmap);
                    }
                }catch (Exception e){
                    Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    onImagePicked.onImagePicked(null,null);
                }

            }
        }
    }
}