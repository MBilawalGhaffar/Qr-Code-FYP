package com.example.qrcodefyp.dialog;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.qrcodefyp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class PasswordResetDialog extends Dialog {
    private Context mContext;
    private ImageView buttonCancel;
    private TextInputEditText etEmail;
    private MaterialButton buttonSendLink;
    private String mEmail;
    private FirebaseAuth mAuth;
    public ProgressDialog loadingBar;
    public PasswordResetDialog(@NonNull Context context, int dialogStyle) {
        super(context,dialogStyle);
        mContext=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_reset_layout);
        buttonCancel=findViewById(R.id.button_cancel_dialog);
        buttonSendLink=findViewById(R.id.button_send_link);
        etEmail=findViewById(R.id.et_email);
        mAuth = FirebaseAuth.getInstance();
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        buttonSendLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEmail=etEmail.getText().toString().toLowerCase(Locale.ROOT);
                if(mEmail.isEmpty()){
                    etEmail.setError(mContext.getString(R.string.Field_empty));
                    return;
                }
                if(mEmail.contains("@gmail.com")){
                    sendLink(mEmail);
                }else {
                    Toast.makeText(mContext,mContext.getString(R.string.enter_email),Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void sendLink(String email) {
        loadingBar=new ProgressDialog(mContext);
        loadingBar.setMessage(mContext.getString(R.string.Sending_Email));
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingBar.dismiss();
                if(task.isSuccessful())
                {
                    // if isSuccessful then done message will be shown
                    // and you can change the password
                    dismiss();
                    Toast.makeText(mContext,mContext.getString(R.string.check_email),Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingBar.dismiss();
                Toast.makeText(mContext,e.getMessage().toString(),Toast.LENGTH_LONG).show();
            }
        });
    }
}
