package com.example.qrcodefyp.dialog;

import static com.example.qrcodefyp.util.FirebaseUtil.DB_PROFILE_REF;
import static com.example.qrcodefyp.util.FirebaseUtil.DB_RECEIPT_REF;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.qrcodefyp.R;
import com.example.qrcodefyp.callback.OnImagePicked;
import com.example.qrcodefyp.callback.OnImagePicker;
import com.example.qrcodefyp.model.BudgetModel;
import com.example.qrcodefyp.model.ProfileModel;
import com.example.qrcodefyp.model.ReceiptModel;
import com.example.qrcodefyp.preference.BudgetPreference;
import com.example.qrcodefyp.util.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Random;

public class AddProfileDialog extends Dialog {
    private OnImagePicker onImagePicker;
    private Context context;
    private Uri imageUri;
    private LinearLayout imagePickLt;
    private LinearLayout imagePickedLt;
    private Boolean isImageSelected=false;
    private static final int GALLERY_PERMISSION_CODE = 103;
    private ImageView pickedImage;
    Dialog dialog;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private StorageReference storageReference;
    private FirebaseUser user;
    private MaterialCardView buttonGallery;
    private ImageView buttonCancel;
    private MaterialButton buttonAddProfile;
    private TextInputEditText etAge;
    private AutoCompleteTextView spinnerGender;
    private String[] genderArray;


    public AddProfileDialog(@NonNull Context context, int themeResId, OnImagePicker onImagePicker) {
        super(context, themeResId);
        this.context=context;
        this.onImagePicker=onImagePicker;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_profile_layout);
        imagePickLt=findViewById(R.id.image_pick_lt);
        imagePickedLt=findViewById(R.id.image_picked_lt);
        buttonGallery=findViewById(R.id.button_gallery);

        buttonCancel=findViewById(R.id.button_cancel_dialog);
        buttonAddProfile=findViewById(R.id.button_add_profile);
        pickedImage=findViewById(R.id.picked_image);
        storageReference= FirebaseStorage.getInstance().getReference();
        etAge=findViewById(R.id.et_age);
        spinnerGender=findViewById(R.id.gender_dropdown);
        genderArray=new String[]{"Male", "Female"};
        ArrayAdapter<String> genderAdapter =new ArrayAdapter(context,R.layout.dropdown_menu_popup_item,genderArray);
        spinnerGender.setAdapter(genderAdapter);
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_wait1);
        dialog.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        user = mAuth.getCurrentUser();
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        buttonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(galleryPermission()){
                    openGallery();
                }else {
                    ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},GALLERY_PERMISSION_CODE);
                }
            }
        });


        buttonAddProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                String totalAge=etAge.getText().toString();
                String gender=spinnerGender.getText().toString();
                if (totalAge.isEmpty()){
                    etAge.setError("Field can't be  empty");
                    dialog.dismiss();
                    return;
                }

                if(gender.isEmpty()){
                    spinnerGender.setError("Field can't be  empty");
                    dialog.dismiss();
                    return;
                }


                if(!isImageSelected){
                    dialog.dismiss();
                    Toast.makeText(context,"Please select image first",Toast.LENGTH_SHORT).show();
                    return;
                }
                int age=Integer.parseInt(totalAge);
                ProfileModel profileModel=new ProfileModel();
                profileModel.setAge(totalAge);
                profileModel.setGender(gender);
                AddProfileToDb(profileModel);


            }
        });


    }
    public static String getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }

    private void AddProfileToDb(ProfileModel profileModel) {
        String id=getRandomNumberString();
        StorageReference ref=storageReference.child("IMAGE/"+id);
        ref.putFile(imageUri).
                addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        if(taskSnapshot.getTask().isSuccessful()){
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    profileModel.setImageUrl(uri.toString());
                                    mDatabase.getReference().child(DB_PROFILE_REF).child(user.getUid()).setValue(profileModel)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        dialog.dismiss();
                                                        dismiss();
                                                        Toast.makeText(context,"Profile add Successfully",Toast.LENGTH_SHORT).show();

                                                    }else {
                                                        dialog.dismiss();
                                                        Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    dialog.dismiss();
                                                    Toast.makeText(context,"Failed,"+e.getMessage(),Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialog.dismiss();
                                    Toast.makeText(context,"Failed,"+e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else {
                            dialog.dismiss();
                            Toast.makeText(context,"Failed...",Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(context,"Failed,"+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean galleryPermission() {
        if(ActivityCompat.checkSelfPermission(context,Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            return true;
        }else {
            return false;
        }
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
}
