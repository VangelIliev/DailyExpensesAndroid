package com.example.familyexpenses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.familyexpenses.Models.ExpenseModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;

public class LogInActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private TextView dailyExpenses;
    private Button AddExpense;
    private Button DeleteExpense;
    private Button UpdateExpense;
    private Button CheckExpenses;
    private ListView ListView;
    private String selectedItem = "";
    private int selectedItemPosition = 0;
    public ArrayList<ExpenseModel> expensesModels = new ArrayList<>();
    public ArrayList<String> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        AddExpense = findViewById(R.id.AddExpense);
        DeleteExpense = findViewById(R.id.DeleteExpense);
        UpdateExpense = findViewById(R.id.UpdateExpense);
        CheckExpenses = findViewById(R.id.CheckExpenses);
        ListView = findViewById(R.id.ListView);
        dailyExpenses = findViewById(R.id.dailyExpenses);
        PopulateListView();
    }
    public void SetDailyExpensesCount(){
        double DailyExpenseCount = 0;
        for (ExpenseModel model : expensesModels){
            double modelExpense = Double.parseDouble(model.getAmount());
            DailyExpenseCount += modelExpense;
        }
        String expensesAsString = String.valueOf(DailyExpenseCount);
        dailyExpenses.setText(expensesAsString);
    }
    public void LogOut(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
        builder.setMessage("Are you sure you want to LogOut ?").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(LogInActivity.this,MainActivity.class));
            }
        }).setNegativeButton("Cancel",null);
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void AddExpense(View view){
        startActivity(new Intent(LogInActivity.this,AddActivity.class));
    }

    public void DeleteExpense(View view) {
        if(selectedItem != "") {
            String expenseId = expensesModels.get(selectedItemPosition).getId();
            AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
            builder.setMessage("Are you sure you want to delete this expense ?").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    db.collection("Expenses").document(expenseId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            arrayList.remove(selectedItemPosition);
                            expensesModels.remove(selectedItemPosition);
                            SetDailyExpensesCount();
                            ArrayAdapter adapter = new ArrayAdapter(LogInActivity.this, android.R.layout.simple_list_item_1, arrayList);
                            ListView.setAdapter(adapter);
                            //adapter.notifyDataSetChanged();
                            Toast.makeText(LogInActivity.this, "Successfully deleted expense", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).setNegativeButton("Cancel", null);

            AlertDialog alert = builder.create();
            alert.show();
        }
        else{
            Toast.makeText(this, "You must first select a expense to delete!", Toast.LENGTH_SHORT).show();
        }
    }
    public void UpdateExpense(View view){
        if(selectedItem != ""){
            Intent intent = new Intent(LogInActivity.this,UpdateActivity.class);
            intent.putExtra("Category",expensesModels.get(selectedItemPosition).getCategory());
            intent.putExtra("Amount",expensesModels.get(selectedItemPosition).getAmount());
            intent.putExtra("Date",expensesModels.get(selectedItemPosition).getDate());
            intent.putExtra("Remarks",expensesModels.get(selectedItemPosition).getRemarks());
            intent.putExtra("Id",expensesModels.get(selectedItemPosition).getId());
            startActivity(intent);
        }
        else{
            Toast.makeText(this, "You must first select a expense to update!", Toast.LENGTH_SHORT).show();
        }

    }

    public void GetExpenses(View view){
        Intent intent = new Intent(LogInActivity.this,ExpensesActivity.class);
        startActivity(intent); 
    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return makeDateString(day,month,year);
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
    private void PopulateListView(){
        // get currentTime
        String current = getTodaysDate();

        CollectionReference expenses = db.collection("Expenses");
        Query query = db.collection("Expenses");
        query = query.whereEqualTo("UserId",FirebaseAuth.getInstance().getCurrentUser().getUid());
        query = query.whereEqualTo("Date",current);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        String taskString = "";
                        String id = document.getId();
                        //String Email = document.get("UserEmail").toString();
                        String Date = document.get("Date").toString();
                        String Amount = document.get("Amount").toString();
                        String Category = document.get("Category").toString();
                        String Remarks = document.get("Remarks").toString();

                        taskString  = "Category: " + Category + "\n" + "Amount: " + Amount + "\n" + "Remarks: " + Remarks;
                        // populate arrayList of expenseModel to later use when deleting from db
                        ExpenseModel model = new ExpenseModel(Category,Date,Amount,Remarks,id);
                        expensesModels.add(model);
                        arrayList.add(taskString);
                    }
                    SetDailyExpensesCount();
                    ArrayAdapter adapter = new ArrayAdapter(LogInActivity.this,android.R.layout.simple_list_item_1,arrayList);
                    ListView.setAdapter(adapter);

                    ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String text = parent.getItemAtPosition(position).toString();
                            selectedItem = text;
                            selectedItemPosition = position;
                            view.setSelected(true);
                        }
                    });
                }
            }
        });
    }


}