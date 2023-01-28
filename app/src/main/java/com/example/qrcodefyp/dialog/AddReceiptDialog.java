package com.example.qrcodefyp.dialog;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

public class AddReceiptDialog extends Dialog {
    private Context mContext;
    private ImageView buttonCancel;
    private TextInputEditText etEmail;
    private MaterialButton buttonSendLink;
    private FirebaseAuth mAuth;
    private String[] categoryArray,paymentArray;
    private AutoCompleteTextView spinnerCategory,spinnerPayment;

    public AddReceiptDialog(@NonNull Context context, int dialogStyle) {
        super(context,dialogStyle);
        mContext=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_receipt_layout);
        buttonCancel=findViewById(R.id.button_cancel_dialog);
        buttonSendLink=findViewById(R.id.button_add_receipt);
        spinnerCategory=findViewById(R.id.category_dropdown);
        spinnerPayment=findViewById(R.id.payment_dropdown);
        categoryArray=new String[]{"Food", "Clothing", "Medicine", "Grocery","Crockery"};
        paymentArray=new String[]{"Cash", "Debit Card", "Credit Card"};
        ArrayAdapter<String> categoryAdapter =new ArrayAdapter(mContext,R.layout.dropdown_menu_popup_item,categoryArray);
        ArrayAdapter<String> paymentAdapter =new ArrayAdapter(mContext,R.layout.dropdown_menu_popup_item,paymentArray);
        spinnerCategory.setAdapter(categoryAdapter);
        spinnerPayment.setAdapter(paymentAdapter);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }


}
