package com.example.qrcodefyp.dialog;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.qrcodefyp.R;
import com.example.qrcodefyp.activity.Home;
import com.example.qrcodefyp.activity.QrCodeScannerActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class AddReceiptDialog extends Dialog {
    private static final int CAMERA_PERMISSION_CODE = 102;
    private static final int GALLERY_PERMISSION_CODE = 103;

    private ActivityResultLauncher<String> galleryPermissionLauncher;
    private Home mHome;
    private ImageView buttonCancel;
    private TextInputEditText expiryDateField;
    private MaterialButton buttonSendLink;
    private FirebaseAuth mAuth;
    private String[] categoryArray,paymentArray;
    private AutoCompleteTextView spinnerCategory,spinnerPayment;
    private SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private Home.Callback callback;
    private MaterialCardView buttonGallery,buttonCamera,buttonScan;

    public interface ReturnCallback {
        void returnCall(Boolean permission);
    }

    public AddReceiptDialog(@NonNull Home context, int dialogStyle, Home.Callback callback) {
        super(context,dialogStyle);
        this.mHome=context;
        this.callback=callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_receipt_layout);
        Activity activity = (Activity) mHome;


        buttonScan=findViewById(R.id.button_scan);
        buttonCamera=findViewById(R.id.button_camera);
        buttonGallery=findViewById(R.id.button_gallery);

        buttonCancel=findViewById(R.id.button_cancel_dialog);
        buttonSendLink=findViewById(R.id.button_add_receipt);
        spinnerCategory=findViewById(R.id.category_dropdown);
        spinnerPayment=findViewById(R.id.payment_dropdown);
        expiryDateField=findViewById(R.id.expiryDateField);

        categoryArray=new String[]{"Food", "Clothing", "Medicine", "Grocery","Crockery"};
        paymentArray=new String[]{"Cash", "Debit Card", "Credit Card"};
        ArrayAdapter<String> categoryAdapter =new ArrayAdapter(mHome,R.layout.dropdown_menu_popup_item,categoryArray);
        ArrayAdapter<String> paymentAdapter =new ArrayAdapter(mHome,R.layout.dropdown_menu_popup_item,paymentArray);
        spinnerCategory.setAdapter(categoryAdapter);
        spinnerPayment.setAdapter(paymentAdapter);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) mHome).startActivityForResult(new Intent(mHome, QrCodeScannerActivity.class),101);
            }
        });

        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cameraPermission()){
                    Toast.makeText(mHome,"true",Toast.LENGTH_SHORT).show();
                    openCamera();
                }else {
                    callback.call(new ReturnCallback() {
                        @Override
                        public void returnCall(Boolean permission) {
                            if(permission){
                                openCamera();
                            }
                            else {
                                Toast.makeText(mHome,"You didn't allow camera permission, Allow it manually",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        buttonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(galleryPermission()){
                    openGallery();
                }else {
                    ActivityCompat.requestPermissions((Activity) mHome,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},GALLERY_PERMISSION_CODE);
                }
            }
        });
//        ActivityResultLauncher<Intent> someActivityResultLauncher =((ComponentActivity) mContext).registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                new ActivityResultCallback<ActivityResult>() {
//                    @Override
//                    public void onActivityResult(ActivityResult result) {
//                        if (result.getResultCode() == Activity.RESULT_OK) {
//                            // Here, no request code
//                            Intent data = result.getData();
//
//                        }
//                    }
//                });






        expiryDateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 long today = MaterialDatePicker.todayInUtcMilliseconds();
                 Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendar.setTimeInMillis(today);
                calendar.set(Calendar.YEAR,2023);
                calendar.set(Calendar.MONTH,calendar.get(Calendar.MONTH));
                calendar.set(Calendar.DATE,calendar.get(Calendar.DATE));
                long startDate = calendar.getTimeInMillis();

                calendar.setTimeInMillis(today);
                calendar.set(Calendar.YEAR,2023);
                calendar.set(Calendar.MONTH,calendar.get(Calendar.MONTH));
                long endDate =calendar.getTimeInMillis();

                CalendarConstraints constraints = new CalendarConstraints.Builder()
//                        .setOpenAt(startDate)
                        .setValidator(DateValidatorPointForward.from(today+86400000))
                        .setStart(MaterialDatePicker.thisMonthInUtcMilliseconds())
                        .setEnd((long) (endDate+2.628e+9))
                        .build();

                MaterialDatePicker<Long> datePickerBuilder = MaterialDatePicker
                        .Builder
                        .datePicker()
//                        .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
                        .setTitleText("Select receipt expiry date")
                        .setCalendarConstraints(constraints).build();
                datePickerBuilder.show(((AppCompatActivity) activity).getSupportFragmentManager() ,"" );
                datePickerBuilder.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        String date =sdf.format(selection);
                        expiryDateField.setText(date);

                    }
                });


            }
        });




    }

    private void openCamera() {
        Toast.makeText(mHome,"Open Camera",Toast.LENGTH_SHORT).show();
    }
    private void openGallery() {
        Toast.makeText(mHome,"Open Gallery",Toast.LENGTH_SHORT).show();
    }

    private boolean galleryPermission() {
        return true;
    }

    private boolean cameraPermission() {
        return false;
    }



}
