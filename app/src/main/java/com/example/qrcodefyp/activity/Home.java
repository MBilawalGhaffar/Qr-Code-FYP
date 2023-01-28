package com.example.qrcodefyp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.qrcodefyp.R;
import com.example.qrcodefyp.dialog.AddReceiptDialog;
import com.example.qrcodefyp.model.User;
import com.example.qrcodefyp.model.UserAuth;
import com.example.qrcodefyp.preference.AuthPreference;
import com.example.qrcodefyp.preference.UserPreference;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
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
    }

    private void addReceiptDialog() {
        Dialog passwordDialog=new AddReceiptDialog(Home.this,R.style.ReceiptDialog);
        passwordDialog.setCancelable(false);
        passwordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        passwordDialog.show();
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
}