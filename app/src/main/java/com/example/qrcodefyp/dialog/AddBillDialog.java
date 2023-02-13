package com.example.qrcodefyp.dialog;

import static com.example.qrcodefyp.util.FirebaseUtil.DB_BILL_REF;
import static com.example.qrcodefyp.util.FirebaseUtil.DB_RECEIPT_REF;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodefyp.R;
import com.example.qrcodefyp.model.BillModel;
import com.example.qrcodefyp.model.BudgetModel;
import com.example.qrcodefyp.model.ReceiptModel;
import com.example.qrcodefyp.preference.BudgetPreference;
import com.example.qrcodefyp.util.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

public class AddBillDialog extends Dialog {

    private MaterialButton buttonAddBudget;
    private ImageView buttonCancel;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    Dialog dialog;
    private StorageReference storageReference;
    private FirebaseUser user;
    private Context context;
    private TextInputEditText billDateField;
    private TextInputEditText etTotalBill,etDescription;
    private MaterialButton buttonAddBill;


    private SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());


    public AddBillDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_bill_layout);
        storageReference= FirebaseStorage.getInstance().getReference();
        etTotalBill=findViewById(R.id.et_total_bill);
        etDescription=findViewById(R.id.et_description);
        buttonCancel=findViewById(R.id.button_cancel_dialog);
        buttonAddBill=findViewById(R.id.button_add_bill);
        billDateField=findViewById(R.id.expiryDateField);

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

        buttonAddBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                String totalBill=etTotalBill.getText().toString();
                String description=etDescription.getText().toString();
                String expiry=billDateField.getText().toString();
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
                if(expiry.isEmpty()){
                    billDateField.setError("Field can't be  empty");
                    dialog.dismiss();
                    return;
                }

                BillModel billModel=new BillModel();
                billModel.setTotal_bill(totalBill);
                billModel.setDescription(description);
                billModel.setBill_date(expiry);
                addBill(billModel);

            }
        });

        billDateField.setOnClickListener(new View.OnClickListener() {
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
                        .setTitleText("Select bill date")
                        .setCalendarConstraints(constraints).build();
                datePickerBuilder.show(((AppCompatActivity) context).getSupportFragmentManager() ,"" );
                datePickerBuilder.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        String date =sdf.format(selection);
                        billDateField.setText(date);

                    }
                });


            }
        });

    }

    private void addBill(BillModel billModel) {
        String id=getRandomNumberString();
        billModel.setId(id);
        mDatabase.getReference().child(DB_BILL_REF).child(user.getUid()).child(id).setValue(billModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            dialog.dismiss();
                            dismiss();
                            Toast.makeText(context,"Bill add Successfully",Toast.LENGTH_SHORT).show();

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

    public static String getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }
}
