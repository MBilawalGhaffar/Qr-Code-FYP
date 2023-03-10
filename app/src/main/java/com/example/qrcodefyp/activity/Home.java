package com.example.qrcodefyp.activity;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qrcodefyp.BuildConfig;
import com.example.qrcodefyp.MyApplication;
import com.example.qrcodefyp.R;
import com.example.qrcodefyp.callback.OnImagePicked;
import com.example.qrcodefyp.callback.OnImagePicker;
import com.example.qrcodefyp.dialog.AddReceiptDialog;
import com.example.qrcodefyp.model.BudgetModel;
import com.example.qrcodefyp.model.User;
import com.example.qrcodefyp.model.UserAuth;
import com.example.qrcodefyp.preference.AuthPreference;
import com.example.qrcodefyp.preference.BudgetPreference;
import com.example.qrcodefyp.preference.LanguagePreference;
import com.example.qrcodefyp.preference.UserPreference;
import com.example.qrcodefyp.util.FirebaseUtil;
import com.example.qrcodefyp.util.NotificationReceiver;
import com.example.qrcodefyp.util.ScanUtil;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

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
    private MaterialCardView buttonAddReceipt,buttonExpense,buttonMyReceipt,buttonMyBill,buttonProfile,buttonCurrency,buttonShop,buttonAbout;

    private BudgetModel budgetModel;
    private TextView tvTotal,tvUsed,tvRemaining;
    private CircularProgressBar circularProgressBar;
    private FirebaseDatabase firebaseDatabase;

    AlarmManager alarmManager;
    PendingIntent broadcast;
    Intent notificationIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        user=new UserPreference(Home.this).getUser();
        drawerLayout = findViewById(R.id.my_drawer_layout);
        circularProgressBar=findViewById(R.id.progress_bar);
        buttonAddReceipt=findViewById(R.id.button_add);
        buttonShop=findViewById(R.id.button_shop);
        buttonExpense=findViewById(R.id.button_expense);
        buttonMyReceipt=findViewById(R.id.button_receipt);
        buttonMyBill=findViewById(R.id.button_bill);
        buttonProfile=findViewById(R.id.button_profile);
        buttonCurrency=findViewById(R.id.button_converter);
        buttonAbout=findViewById(R.id.button_about);
        tvTotal=findViewById(R.id.tv_total_sar);
        tvUsed=findViewById(R.id.tv_used_sar);
        tvRemaining=findViewById(R.id.tv_left_sar);
        Log.d("BUDGET", "*******************************getBudget***************************");
        getBudget();

        navigationView=findViewById(R.id.nav_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        MenuItem menuItem = navigationView.getMenu().findItem(R.id.arabic_switch);
        SwitchCompat switch_id = (SwitchCompat) menuItem.getActionView();
//        switch_id.setChecked(true);
        switch_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switch_id.isChecked()){
                    new LanguagePreference(Home.this).addLanguage(true);

                }else {
                    new LanguagePreference(Home.this).addLanguage(false);
                }
                MyApplication.setLocale(Home.this,"ar");
                restart();
            }
        });
        if(new LanguagePreference(this).getLanguage()){
            switch_id.setChecked(true);
        }

        buttonAddReceipt.setOnClickListener(view -> {
                    addReceiptDialog();
        });

        buttonMyReceipt.setOnClickListener(view -> {
            startActivity(new Intent(Home.this,MyReceiptActivity.class));
        });

        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this,ProfileActivity.class));
            }
        });

        buttonExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this,BudgetActivity.class));
            }
        });

        buttonShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this,ShopsActivity.class));
            }
        });
        buttonAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this,AboutActivity.class));
            }
        });

        buttonMyBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this,MyBillActivity.class));
            }
        });
        buttonCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this,CurrencyConverterActivity.class));
            }
        });

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
                            Toast.makeText(Home.this,getString(R.string.Error_image),Toast.LENGTH_SHORT).show();
                        }

                    }
                }
        );



        cameraPermissionLauncher=registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted -> {
                    returnCallback.returnCall(isGranted);
                });

        requestAllPermission();
        firebaseDatabase=FirebaseDatabase.getInstance();
        getBudgetFromDb();
//        Notification(1,"A","a",2);
//        Notification(2,"B","b",4);
//        Notification(3,"C","c",6);

    }
    public void restart() {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }
    private void getBudgetFromDb() {
        firebaseDatabase.getReference(FirebaseUtil.DB_BUDGET_REF).child(user.getUUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    BudgetModel dbBudget=snapshot.getValue(BudgetModel.class);
                    assert dbBudget != null;
                    if(dbBudget.getUsedBudget()>budgetModel.getUsedBudget()){
                        new BudgetPreference(Home.this).addBudget(dbBudget);
                        getBudget();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBudget();
        MyApplication.setLocale(this,"ar");
    }

    private void getBudget() {
        budgetModel= new BudgetPreference(Home.this).getBudget();
        float total=budgetModel.getTotalBudget();
        float used=budgetModel.getUsedBudget();
        float remain=budgetModel.getRemainingBudget();
        tvTotal.setText((int)total+" "+budgetModel.getCurrency());
        tvRemaining.setText((int)remain+" "+budgetModel.getCurrency());
        tvUsed.setText((int)used+"");

        Log.d("BUDGET", "total ->"+total);
        Log.d("BUDGET", "used ->"+used);
        Log.d("BUDGET", "remain ->"+remain);

        float value=used/total;
        if (Double.isNaN(value)){
            value=0;
        }
        Log.d("BUDGET", "value ->"+value);
        float usedBudgetProgress=value*100;
        Log.d("BUDGET", "usedBudgetProgress ->"+usedBudgetProgress);
        circularProgressBar.setProgress(usedBudgetProgress);
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

            @Override
            public void onImageScan(OnImagePicked callback, Boolean result) {
                onImagePicked=callback;
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
        Toast.makeText(Home.this,getString(R.string.Logout_msg),Toast.LENGTH_SHORT).show();
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
    private void Notification(int id_todo,String heading_todo,String message_todo,int time) {
        Log.i("NOTIFICATION", " CREATED");
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        notificationIntent = new Intent(Home.this, NotificationReceiver.class);
        notificationIntent.putExtra("ID", id_todo);
        notificationIntent.putExtra("Title", heading_todo);
        notificationIntent.putExtra("Message", message_todo);
        notificationIntent.putExtra("SwitchChecked", 1);
        try {
            broadcast = PendingIntent.getBroadcast(Home.this, id_todo, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }catch (Exception e){
            broadcast = PendingIntent.getBroadcast(Home.this, id_todo, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        }
//        broadcast = PendingIntent.getBroadcast(mHome, id_todo, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 15);
        cal.set(Calendar.MONTH, 1);
        cal.set(Calendar.YEAR, 2023);
        cal.set(Calendar.HOUR_OF_DAY, 1);
        cal.set(Calendar.MINUTE, 0+time);
        cal.set(Calendar.SECOND, 0);
        Log.i("NOTIFICATION   ", "***************************");

        long timeInMilli= Calendar.getInstance().get(Calendar.MILLISECOND);
        Log.i("NOTIFICATION   ","timeInMilli "+timeInMilli );
        timeInMilli=timeInMilli+time;
        Log.i("NOTIFICATION   ","timeInMilli "+timeInMilli );

        alarmManager.setExact(AlarmManager.RTC, cal.getTimeInMillis(), broadcast);
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
                    if(onImagePicked!=null){
                        onImagePicked.onImagePicked(null,null);
                    }
                }

            }
        }
        if(requestCode==101){
            if(resultCode==RESULT_OK){
                if(onImagePicked!=null){
                    onImagePicked.onImagePicked(null, ScanUtil.scanBitmap);
                }
            }else {
                if(onImagePicked!=null){
                    onImagePicked.onImagePicked(null,null);
                }
            }
        }
    }

}