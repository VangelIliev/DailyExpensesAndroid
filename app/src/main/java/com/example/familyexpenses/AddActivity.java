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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.familyexpenses.Models.ExpenseModel;
import com.example.familyexpenses.Models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private DatePickerDialog datePickerDialog;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Button dateButton;
    private EditText AmountAdd;
    private EditText RemarksAdd;
    private Button BtnAdd;
    private FirebaseDatabase rootNode;
    private Spinner spinner;
    private List<String> CategoriesNames = new ArrayList<String>();
    private DatabaseReference referenceCategories = FirebaseDatabase.getInstance().getReference().child("Categories");
    private DatabaseReference referenceExpenses;
    private String spinnerValue = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        mAuth = FirebaseAuth.getInstance();
        AmountAdd = findViewById((R.id.AmountAdd));
        RemarksAdd = findViewById((R.id.RemarksAdd));
        BtnAdd = findViewById((R.id.BtnAdd));
        spinner = (Spinner)findViewById(R.id.spinner);
        dateButton = findViewById(R.id.DateButton);
        db = FirebaseFirestore.getInstance();
        //ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,CategoriesNames);
        ArrayAdapter<CharSequence> myAdapter = ArrayAdapter.createFromResource(this,R.array.spinnerCategories, android.R.layout.simple_spinner_item);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);
        spinner.setOnItemSelectedListener(this);
        dateButton.setText(getTodaysDate());
        initDatePicker();
        referenceCategories.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot datasnapshot : snapshot.getChildren()){
                    CategoriesNames.add(datasnapshot.getValue().toString());
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return makeDateString(day,month,year);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        spinnerValue = text;

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                dateButton.setText(date);

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

    public void OpenDatePicker(View view){
        datePickerDialog.show();
    }

    public void AddExpense(View view){
        String Category = spinnerValue;
        String Date = dateButton.getText().toString();
        String Amount = AmountAdd.getText().toString();
        String Remarks = RemarksAdd.getText().toString();
        //ExpenseModel model = new ExpenseModel(Category,Date,Amount,Remarks);
        rootNode = FirebaseDatabase.getInstance();
        Map<String,Object> expense = new HashMap<>();
        expense.put("UserId",FirebaseAuth.getInstance().getCurrentUser().getUid());
        expense.put("UserEmail",FirebaseAuth.getInstance().getCurrentUser().getEmail());
        expense.put("Category",Category);
        expense.put("Date",Date);
        expense.put("Amount",Amount);
        expense.put("Remarks",Remarks);
        db.collection("Expenses").add(expense);
        Toast.makeText(this, "Successfully added expense", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AddActivity.this,LogInActivity.class);
        startActivity(intent);
    }
}