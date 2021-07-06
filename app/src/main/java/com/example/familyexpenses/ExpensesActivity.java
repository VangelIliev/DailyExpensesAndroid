package com.example.familyexpenses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.example.familyexpenses.Models.ExpenseModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;

public class ExpensesActivity extends AppCompatActivity {
    private DatePickerDialog datePickerDialog;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private TextView DailyExpensesForDate;
    private Button Refresh;
    private Button DateButtonExpenses;
    private ListView ListViewExpenses;
    public ArrayList<String> arrayList = new ArrayList<>();
    public ArrayList<ExpenseModel> expensesModels = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        DateButtonExpenses = findViewById(R.id.DateButtonExpenses);
        Refresh = findViewById(R.id.Refresh);
        DailyExpensesForDate = findViewById(R.id.DailyExpensesForDate);
        ListViewExpenses = findViewById(R.id.ListViewExpenses);
        DailyExpensesForDate.setText("0.0");
        initDatePicker();
    }
    public void RefreshListView(View view){
        CollectionReference expenses = db.collection("Expenses");
        Query query = db.collection("Expenses");
        query = query.whereEqualTo("UserId",FirebaseAuth.getInstance().getCurrentUser().getUid());
        query = query.whereEqualTo("Date",DateButtonExpenses.getText().toString());
        arrayList.clear();
        expensesModels.clear();
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String taskString = "";
                        String id = document.getId();
                        String Email = document.get("UserEmail").toString();
                        String Date = document.get("Date").toString();
                        String Amount = document.get("Amount").toString();
                        String Category = document.get("Category").toString();
                        String Remarks = document.get("Remarks").toString();

                        taskString = "Category: " + Category + "\n" + "Amount: " + Amount + "\n" + "Remarks: " + Remarks;
                        // populate arrayList of expenseModel to later use when deleting from db
                        ExpenseModel model = new ExpenseModel(Category, Date, Amount, Remarks, id);
                        expensesModels.add(model);
                        arrayList.add(taskString);
                        ListViewExpenses.setAdapter(null);
                    }
                    SetDailyExpensesCount();

                    ArrayAdapter adapter = new ArrayAdapter(ExpensesActivity.this, android.R.layout.simple_list_item_1, arrayList);
                    ListViewExpenses.setAdapter(adapter);
                }
                else{
                    DailyExpensesForDate.setText("0.0");
                }
            }
        });
    }
    public void SetDailyExpensesCount(){
        double DailyExpenseCount = 0;
        for (ExpenseModel model : expensesModels){
            double modelExpense = Double.parseDouble(model.getAmount());
            DailyExpenseCount += modelExpense;
        }
        String expensesAsString = String.valueOf(DailyExpenseCount);
        DailyExpensesForDate.setText(expensesAsString);
    }

    public void OpenDatePickerDialog(View view){
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
                DateButtonExpenses.setText(date);

            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this,style,dateSetListener,year,month,day);
    }
}