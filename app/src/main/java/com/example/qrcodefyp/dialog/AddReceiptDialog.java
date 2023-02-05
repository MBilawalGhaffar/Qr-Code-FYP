package com.example.qrcodefyp.dialog;


import static com.example.qrcodefyp.util.FirebaseUtil.DB_RECEIPT_REF;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.qrcodefyp.R;
import com.example.qrcodefyp.activity.Home;
import com.example.qrcodefyp.activity.QrCodeScannerActivity;
import com.example.qrcodefyp.activity.SigUpActivity;
import com.example.qrcodefyp.callback.OnImagePicked;
import com.example.qrcodefyp.callback.OnImagePicker;
import com.example.qrcodefyp.model.ReceiptModel;
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
import java.util.TimeZone;
import java.util.UUID;

public class AddReceiptDialog extends Dialog {
    private static final int CAMERA_PERMISSION_CODE = 102;
    private static final int GALLERY_PERMISSION_CODE = 103;
    private LinearLayout imagePickLt;
    private LinearLayout imagePickedLt;
    private Boolean isImageSelected=false;

    private ImageView pickedImage;

    private ActivityResultLauncher<String> galleryPermissionLauncher;
    private Home mHome;
    private ImageView buttonCancel;
    private TextInputEditText expiryDateField;
    private MaterialButton buttonAddReceipt;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private String[] categoryArray,paymentArray;
    private AutoCompleteTextView spinnerCategory,spinnerPayment;
    private SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private Home.Callback callback;
    private OnImagePicker onImagePicker;
    private MaterialCardView buttonGallery,buttonCamera,buttonScan;

    private Uri imageUri;
    private TextInputEditText etTotalBill,etDescription;
    private AutoCompleteTextView categoryDropdown;
    Dialog dialog;
    private StorageReference storageReference;
    private FirebaseUser user;
    public interface ReturnCallback {
        void returnCall(Boolean permission);
    }

    public AddReceiptDialog(@NonNull Home context, int dialogStyle, Home.Callback callback, OnImagePicker onImagePicker) {
        super(context,dialogStyle);
        this.mHome=context;
        this.callback=callback;
        this.onImagePicker=onImagePicker;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_receipt_layout);
        Activity activity = (Activity) mHome;

        storageReference= FirebaseStorage.getInstance().getReference();

        etTotalBill=findViewById(R.id.et_total_bill);
        etDescription=findViewById(R.id.et_description);


        buttonScan=findViewById(R.id.button_scan);
        buttonCamera=findViewById(R.id.button_camera);
        buttonGallery=findViewById(R.id.button_gallery);

        buttonCancel=findViewById(R.id.button_cancel_dialog);
        buttonAddReceipt=findViewById(R.id.button_add_receipt);
        spinnerCategory=findViewById(R.id.category_dropdown);
        spinnerPayment=findViewById(R.id.payment_dropdown);
        expiryDateField=findViewById(R.id.expiryDateField);

        imagePickLt=findViewById(R.id.image_pick_lt);
        imagePickedLt=findViewById(R.id.image_picked_lt);

        pickedImage=findViewById(R.id.picked_image);

        categoryArray=new String[]{"Food", "Clothing", "Medicine", "Grocery","Crockery"};
        paymentArray=new String[]{"Cash", "Debit Card", "Credit Card"};
        ArrayAdapter<String> categoryAdapter =new ArrayAdapter(mHome,R.layout.dropdown_menu_popup_item,categoryArray);
        ArrayAdapter<String> paymentAdapter =new ArrayAdapter(mHome,R.layout.dropdown_menu_popup_item,paymentArray);
        spinnerCategory.setAdapter(categoryAdapter);
        spinnerPayment.setAdapter(paymentAdapter);

        dialog = new Dialog(mHome);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_wait1);
        dialog.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        user = mAuth.getCurrentUser();

        buttonAddReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                String totalBill=etTotalBill.getText().toString();
                String description=etDescription.getText().toString();
                String category=spinnerCategory.getText().toString();
                String payment=spinnerPayment.getText().toString();
                String expiry=expiryDateField.getText().toString();
                if (totalBill.isEmpty()){
                    etTotalBill.setError("Field can't be  empty");
                    dialog.dismiss();
                    return;
                }
                if(description.isEmpty()){
                    etDescription.setError("Field can't be  empty");
                    dialog.dismiss();
                    return;
                }
                if(category.isEmpty()){
                    spinnerCategory.setError("Field can't be  empty");
                    dialog.dismiss();
                    return;
                }
                if(payment.isEmpty()){
                    spinnerPayment.setError("Field can't be  empty");
                    dialog.dismiss();
                    return;
                }
                if(expiry.isEmpty()){
                    expiryDateField.setError("Field can't be  empty");
                    dialog.dismiss();
                    return;
                }
                if(!isImageSelected){
                    dialog.dismiss();
                    Toast.makeText(mHome,"Please select image first",Toast.LENGTH_SHORT).show();
                    return;
                }
                ReceiptModel receiptModel=new ReceiptModel();
                receiptModel.setTotal_bill(totalBill);
                receiptModel.setDescription(description);
                receiptModel.setCategory(category);
                receiptModel.setPayment(payment);
                receiptModel.setExpiry_date(expiry);

                addReceipt(receiptModel);




            }
        });

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

    private void addReceipt(ReceiptModel receiptModel) {
        StorageReference ref=storageReference.child("IMAGE/"+UUID.randomUUID().toString());
        ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if(taskSnapshot.getTask().isSuccessful()){
                    String imageUrl=taskSnapshot.getStorage().getDownloadUrl().toString();
                    receiptModel.setImage_url(imageUrl);
                    mDatabase.getReference().child(DB_RECEIPT_REF).child(user.getUid()).setValue(receiptModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                dialog.dismiss();
                                dismiss();
                                Toast.makeText(mHome,"Receipt add Successfully",Toast.LENGTH_SHORT).show();

                            }else {
                                dialog.dismiss();
                                Toast.makeText(mHome,"Failed",Toast.LENGTH_SHORT).show();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(mHome,"Failed,"+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });


                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(mHome,"Failed,"+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openCamera() {
        onImagePicker.onImagePicker(new OnImagePicked() {
            @Override
            public void onImagePicked(@Nullable Uri uri, @Nullable Bitmap bitmap) {
                if(uri!=null||bitmap!=null){
                    imageUri=uri;
                    imagePickLt.setVisibility(View.GONE);
                    imagePickedLt.setVisibility(View.VISIBLE);
                    pickedImage.setImageBitmap(bitmap);
                    isImageSelected=true;
                }else {
                    imagePickLt.setVisibility(View.VISIBLE);
                    imagePickedLt.setVisibility(View.GONE);
                    isImageSelected=false;
                }
            }
        },true);
    }

    private void openGallery() {
        onImagePicker.onImagePicker(new OnImagePicked() {
            @Override
            public void onImagePicked(@Nullable Uri uri, @Nullable Bitmap bitmap) {
                if(uri!=null||bitmap!=null){
                    imageUri=uri;
                    imagePickLt.setVisibility(View.GONE);
                    imagePickedLt.setVisibility(View.VISIBLE);
                    pickedImage.setImageBitmap(bitmap);
                    isImageSelected=true;

                }else {
                    imagePickLt.setVisibility(View.VISIBLE);
                    imagePickedLt.setVisibility(View.GONE);
                    isImageSelected=false;
                }
            }
        },false);
    }

    private boolean galleryPermission() {
        if(ActivityCompat.checkSelfPermission(mHome,Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            return true;
        }else {
            return false;
        }
    }

    private boolean cameraPermission() {
        if(ActivityCompat.checkSelfPermission(mHome,Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
            return true;
        }else {
            return false;
        }
    }



}
