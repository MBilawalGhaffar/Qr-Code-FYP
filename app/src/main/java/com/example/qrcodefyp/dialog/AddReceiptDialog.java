package com.example.qrcodefyp.dialog;


import static com.example.qrcodefyp.util.FirebaseUtil.DB_RECEIPT_REF;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import com.example.qrcodefyp.model.BudgetModel;
import com.example.qrcodefyp.model.ReceiptModel;
import com.example.qrcodefyp.preference.BudgetPreference;
import com.example.qrcodefyp.util.FirebaseUtil;
import com.example.qrcodefyp.util.NotificationReceiver;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
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
    private TextInputLayout currencyHint;
    private AutoCompleteTextView categoryDropdown;
    Dialog dialog;
    private StorageReference storageReference;
    private FirebaseUser user;
    private String mCurrency="";
    private int mTotalBudget=0,mRemainingBudget=0,mUsedBudget=0;


    AlarmManager alarmManager;
    PendingIntent broadcast;
    Intent notificationIntent;
    int MONTH;
    int DAY;
    int YEAR;
    int HOUR;
    int MINUTE;
    String heading_todo;
    String message_todo;
    String date_todo;
    String time_todo;
    int id_todo;
    int notification_todo;

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
        currencyHint=findViewById(R.id.et_sar_amount);
        getBudget();
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
                int todayBill=Integer.parseInt(totalBill);

                if(mRemainingBudget<todayBill){
                    dialog.dismiss();
                    MaterialAlertDialogBuilder alertDialogBuilder=new MaterialAlertDialogBuilder(mHome);
                    alertDialogBuilder.setTitle("Budget Alert!");
                    alertDialogBuilder.setMessage("Your monthly limit exceeded,Please reset your monthly limit.");
                    alertDialogBuilder.setCancelable(true);
                    alertDialogBuilder.setPositiveButton("Ok", new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialogBuilder.show();
                    return;
                }
                mUsedBudget=mUsedBudget+todayBill;
                mRemainingBudget=mRemainingBudget-todayBill;
                BudgetModel budgetModel=new BudgetModel(mTotalBudget,mUsedBudget,mRemainingBudget,mCurrency);
                new BudgetPreference(mHome).addBudget(budgetModel);
                FirebaseDatabase.getInstance().getReference(FirebaseUtil.DB_BUDGET_REF).child(user.getUid()).setValue(budgetModel);
                ReceiptModel receiptModel=new ReceiptModel();
                receiptModel.setTotal_bill(totalBill);
                receiptModel.setDescription(description);
                receiptModel.setCategory(category);
                receiptModel.setPayment(payment);
                receiptModel.setExpiry_date(expiry);
                receiptModel.setCurrency(mCurrency);
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

                onImagePicker.onImageScan(new OnImagePicked() {
                    @Override
                    public void onImagePicked(@Nullable Uri uri, @Nullable Bitmap bitmap) {
                        if(uri!=null||bitmap!=null){
                            imageUri=getImageUri(mHome,bitmap);
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
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    private void Notification() {
        Log.i("NOTIFICATION", " CREATED");
        alarmManager = (AlarmManager) mHome.getSystemService(Context.ALARM_SERVICE);
        notificationIntent = new Intent(mHome, NotificationReceiver.class);
        notificationIntent.putExtra("ID", id_todo);
        notificationIntent.putExtra("Title", heading_todo);
        notificationIntent.putExtra("Message", message_todo);
        notificationIntent.putExtra("SwitchChecked", true);
        try {
            broadcast = PendingIntent.getBroadcast(mHome, id_todo, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }catch (Exception e){
            broadcast = PendingIntent.getBroadcast(mHome, id_todo, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        }
//        broadcast = PendingIntent.getBroadcast(mHome, id_todo, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

//        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.DAY_OF_MONTH, DAY);
//        cal.set(Calendar.MONTH, MONTH - 1);
//        cal.set(Calendar.YEAR, YEAR);
//        cal.set(Calendar.HOUR_OF_DAY, HOUR);
//        cal.set(Calendar.MINUTE, MINUTE);
//        cal.set(Calendar.SECOND, 0);

        long timeInMilli=Calendar.getInstance().get(Calendar.MILLISECOND);
        timeInMilli=timeInMilli+100000;

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMilli, broadcast);
    }

    private void getBudget() {
        BudgetModel budgetModel=new BudgetPreference(mHome).getBudget();
        currencyHint.setPlaceholderText(budgetModel.getCurrency());
        mCurrency=budgetModel.getCurrency();
        mTotalBudget=budgetModel.getTotalBudget();
        mUsedBudget=budgetModel.getUsedBudget();
        mRemainingBudget=budgetModel.getRemainingBudget();
    }

    public static String getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }

    private void addReceipt(ReceiptModel receiptModel) {
        String id=getRandomNumberString();
        receiptModel.setId(id);
        StorageReference ref=storageReference.child("IMAGE/"+id);
        ref.putFile(imageUri).
                addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if(taskSnapshot.getTask().isSuccessful()){
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            receiptModel.setImage_url(uri.toString());
                            mDatabase.getReference().child(DB_RECEIPT_REF).child(user.getUid()).child(id).setValue(receiptModel)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
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
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            dialog.dismiss();
                                            Toast.makeText(mHome,"Failed,"+e.getMessage(),Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(mHome,"Failed,"+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    dialog.dismiss();
                    Toast.makeText(mHome,"Failed...",Toast.LENGTH_SHORT).show();
                }

            }
        })
                .addOnFailureListener(new OnFailureListener() {
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
