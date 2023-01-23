package com.example.qrcodefyp.activity;

import static com.example.qrcodefyp.util.FirebaseUtil.DB_LOGIN_REF;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qrcodefyp.R;
import com.example.qrcodefyp.dialog.PasswordResetDialog;
import com.example.qrcodefyp.model.User;
import com.example.qrcodefyp.model.UserAuth;
import com.example.qrcodefyp.preference.AuthPreference;
import com.example.qrcodefyp.preference.UserPreference;
import com.example.qrcodefyp.util.FirebaseUtil;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    String TAG ="Google_SignIn";
    private TextView buttonSignup;
    private TextView buttonForgotPassword;
    private MaterialCardView buttonGoogleLogin;
    private static final int RC_SIGN_IN = 2;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseDatabase mDatabase;
    Dialog dialog;
    private FirebaseAuth mAuth;
    private boolean isRemember =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        buttonSignup=findViewById(R.id.button_signup);
        buttonGoogleLogin=findViewById(R.id.button_google_login);
        buttonForgotPassword=findViewById(R.id.button_forget_password);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_wait1);
        dialog.setCanceledOnTouchOutside(false);
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,SigUpActivity.class));
            }
        });
        buttonGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        buttonForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog passwordDialog=new PasswordResetDialog(LoginActivity.this,R.style.Dialog);
                passwordDialog.setCancelable(false);
                passwordDialog.show();
            }
        });

    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            isRemember=true;
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            User mUser=new User(user.getUid(),user.getDisplayName(),user.getEmail());
                            addUserToDatabase(mUser);
                            Log.d(TAG,"uuid -> "+user.getUid().toString());
                            Log.d(TAG,"name -> "+user.getDisplayName().toString());
                            Log.d(TAG,"email -> "+user.getEmail().toString());
                            //  updateUI(user);
                        } else {
                            dialog.dismiss();
                            Toast.makeText(LoginActivity.this,"Login failed",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void addUserToDatabase(User mUser) {
//        Map<String, Object> data = new HashMap<String, Object>();
//        data.put(mUser.getUUID(),mUser);
//        mDatabase.getReference(DB_LOGIN_REF).child(mUser.getUUID()).setValue(data);

        mDatabase.getReference(DB_LOGIN_REF).child(mUser.getUUID()).setValue(mUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    new UserPreference(LoginActivity.this).addUser(mUser);
                    new AuthPreference(LoginActivity.this).addAuth(new UserAuth(true,isRemember));
                    FirebaseUtil.USER=mUser;
                    dialog.dismiss();
                    Toast.makeText(LoginActivity.this,"You login as "+mUser.getEmail().toString(),Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this,Home.class));
                    finish();
                }else {
                    dialog.dismiss();
                    Toast.makeText(LoginActivity.this,"Login failed",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN && resultCode==RESULT_OK) {
            dialog.show();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                //Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                Log.d(TAG,"id -> "+account.getId().toString());
                Log.d(TAG,"account -> "+account.getAccount().toString());
                Log.d(TAG,"email -> "+account.getEmail().toString());
                Log.d(TAG,"display name -> "+account.getDisplayName().toString());
                Log.d(TAG,"given name -> "+account.getGivenName().toString());
                Log.d(TAG,"photo url -> "+account.getPhotoUrl().toString());
                Log.d(TAG,"id token -> "+account.getIdToken().toString());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately

                dialog.dismiss();
                // ...
            }
        }else {
            Toast.makeText(LoginActivity.this,"Something went wrong.",Toast.LENGTH_SHORT).show();
        }
    }

}