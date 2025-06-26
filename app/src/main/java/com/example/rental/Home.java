package com.example.rental;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
    public void myRentIntent(View view){
        Intent intent = new Intent(this, RentalForm.class);
        startActivity(intent);
        finish();
    }
    public void myBorrowIntent(View view){
        Intent intent = new Intent(this, BorrowView.class);
        startActivity(intent);
        finish();
    }
    public void myCalcIntent(View view){
        Intent intent = new Intent(this, Calculator.class);
        startActivity(intent);
        finish();
    }
    public void myAccountIntent(View view){
        Intent intent = new Intent(this, MyAccount.class);
        startActivity(intent);
        finish();
    }
}