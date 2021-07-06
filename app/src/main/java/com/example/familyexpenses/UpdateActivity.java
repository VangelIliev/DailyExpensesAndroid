package com.example.familyexpenses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UpdateActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private DatePickerDialog datePickerDialog;
    private Spinner spinnerUpdate;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView AmountUpdate;
    private TextView RemarksUpdate;
    private String spinnerValue = "";
    private Button BtnEdit;
    private Bundle  extras;
    private Button DateButtonUpdate;
    private String ExpenseId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        extras = getIntent().getExtras();
        spinnerUpdate = findViewById(R.id.spinnerUpdate);
        DateButtonUpdate = findViewById(R.id.DateButtonUpdate);
        AmountUpdate = findViewById(R.id.AmountUpdate);
        RemarksUpdate = findViewById(R.id.RemarksUpdate);
        BtnEdit = findViewById(R.id.BtnEdit);
        initDatePicker();
        ArrayAdapter<CharSequence> myAdapter = ArrayAdapter.createFromResource(this,R.array.spinnerCategories, android.R.layout.simple_spinner_item);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUpdate.setAdapter(myAdapter);
        spinnerUpdate.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        if(extras != null){
            String Amount = extras.getString("Amount");
            AmountUpdate.setText(Amount);
            String Remarks = extras.getString("Remarks");
            RemarksUpdate.setText(Remarks);
            String Date = extras.getString("Date");
            DateButtonUpdate.setText(Date);
            String Id = extras.getString("Id");
            ExpenseId = Id;
            String Category = extras.getString("Category");

            int selectionPosition= myAdapter.getPosition(Category);
            spinnerUpdate.setSelection(selectionPosition);
            //spinnerUpdate.setSelection(Category);
        }


    }

    public void EditExpense(View view){
        String Date = DateButtonUpdate.getText().toString();
        String Category = spinnerValue;
        String Amount = AmountUpdate.getText().toString();
        String Remarks = RemarksUpdate.getText().toString();

        Map<String,Object> expense = new HashMap<>();
        expense.put("UserId",FirebaseAuth.getInstance().getCurrentUser().getUid());
        expense.put("UserEmail",FirebaseAuth.getInstance().getCurrentUser().getEmail());
        expense.put("Category",Category);
        expense.put("Date",Date);
        expense.put("Amount",Amount);
        expense.put("Remarks",Remarks);
        db.collection("Expenses").document(ExpenseId).set(expense).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(UpdateActivity.this, "Successfully updated expense!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(UpdateActivity.this,LogInActivity.class));
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {
                Toast.makeText(UpdateActivity.this, "Try again", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void OpenDatePicker(View view){
        datePickerDialog.show();
    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month) {
        if(month == 1){
            return "JAN";
        }
        if(month == 2){
            return "FEB";
        }
        if(month == 3){
            return "MAR";
        }
        if(month == 4){
            return "APR";
        }
        if(month == 5){
            return "MAY";
        }
        if(month == 6){
            return "JUN";
        }
        if(month == 7){
            return "JULY";
        }
        if(month == 8){
            return "AUG";
        }
        if(month == 9){
            return "SEP";
        }
        if(month == 10){
            return "OCT";
        }
        if(month == 11){
            return "NOV";
        }
        if(month == 12){
            return "DEC";
        }

        return "JAN";

    }
    private void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                DateButtonUpdate.setText(date);

            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this,style,dateSetListener,year,month,day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        spinnerValue = text;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}