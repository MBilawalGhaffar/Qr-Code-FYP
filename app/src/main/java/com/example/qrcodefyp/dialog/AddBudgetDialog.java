package com.example.qrcodefyp.dialog;


import static com.example.qrcodefyp.util.FirebaseUtil.DB_RECEIPT_REF;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.qrcodefyp.R;
import com.example.qrcodefyp.activity.Home;
import com.example.qrcodefyp.activity.QrCodeScannerActivity;
import com.example.qrcodefyp.callback.OnAddBudget;
import com.example.qrcodefyp.callback.OnImagePicked;
import com.example.qrcodefyp.callback.OnImagePicker;
import com.example.qrcodefyp.model.BudgetModel;
import com.example.qrcodefyp.model.ReceiptModel;
import com.example.qrcodefyp.preference.BudgetPreference;
import com.example.qrcodefyp.util.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

public class AddBudgetDialog extends Dialog {


    private MaterialButton buttonAddBudget;
    private ImageView buttonCancel;

    private FirebaseAuth mAuth;

    private FirebaseDatabase mDatabase;

    private String[] currencyArray;

    private AutoCompleteTextView spinnerCurrency;

    private TextInputEditText etTotalBudget;

    Dialog dialog;

    private StorageReference storageReference;

    private FirebaseUser user;

    private Context context;

    private OnAddBudget onAddBudget;


    public AddBudgetDialog(@NonNull Context context, int dialogStyle , OnAddBudget onAddBudget) {
        super(context,dialogStyle);
        this.context=context;
        this.onAddBudget=onAddBudget;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_budget_layout);

        storageReference= FirebaseStorage.getInstance().getReference();

        etTotalBudget=findViewById(R.id.et_total_budget);

        buttonAddBudget=findViewById(R.id.button_add_budget);
        buttonCancel=findViewById(R.id.button_cancel_dialog);

        spinnerCurrency=findViewById(R.id.currency_dropdown);

        currencyArray=new String[]{"SAR", "$"};

        ArrayAdapter<String> currencyAdapter =new ArrayAdapter(context,R.layout.dropdown_menu_popup_item,currencyArray);

        spinnerCurrency.setAdapter(currencyAdapter);

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_wait1);
        dialog.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        user = mAuth.getCurrentUser();

        buttonAddBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                String totalBudget=etTotalBudget.getText().toString();
                String currency=spinnerCurrency.getText().toString();

                if (totalBudget.isEmpty()){
                    etTotalBudget.setError(context.getString(R.string.Field_empty));
                    dialog.dismiss();
                    return;
                }

                if(currency.isEmpty()){
                    spinnerCurrency.setError(context.getString(R.string.Field_empty));
                    dialog.dismiss();
                    return;
                }
                BudgetModel budgetModel=new BudgetModel(Integer. parseInt(totalBudget),0,Integer. parseInt(totalBudget),currency);
                new BudgetPreference(context).addBudget(budgetModel);
                FirebaseDatabase.getInstance().getReference(FirebaseUtil.DB_BUDGET_REF).child(user.getUid()).setValue(budgetModel);
                onAddBudget.onAdd();
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        dismiss();
                    }
                }, 1000);

            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }


}
