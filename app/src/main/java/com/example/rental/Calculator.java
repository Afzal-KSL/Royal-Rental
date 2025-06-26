package com.example.rental;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Calculator extends AppCompatActivity {

    EditText num1, num2;
    TextView result;
    Button add, subtract, multiply, divide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);

        num1 = findViewById(R.id.num1);
        num2 = findViewById(R.id.num2);
        result = findViewById(R.id.result);

        add = findViewById(R.id.add);
        subtract = findViewById(R.id.subtract);
        multiply = findViewById(R.id.multiply);
        divide = findViewById(R.id.divide);

        add.setOnClickListener(view -> calculate('+'));
        subtract.setOnClickListener(view -> calculate('-'));
        multiply.setOnClickListener(view -> calculate('*'));
        divide.setOnClickListener(view -> calculate('/'));
    }

    private void calculate(char operator) {
        String input1 = num1.getText().toString().trim();
        String input2 = num2.getText().toString().trim();

        if (input1.isEmpty() || input2.isEmpty()) {
            result.setText("Enter both numbers!");
            return;
        }

        double number1 = Double.parseDouble(input1);
        double number2 = Double.parseDouble(input2);
        double output = 0;

        switch (operator) {
            case '+': output = number1 + number2; break;
            case '-': output = number1 - number2; break;
            case '*': output = number1 * number2; break;
            case '/':
                if (number2 == 0) {
                    result.setText("Cannot divide by zero!");
                    return;
                }
                output = number1 / number2;
                break;
        }

        result.setText("Result: " + output);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Calculator.this, Home.class); // Navigate to Home
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        super.onBackPressed(); // Ensure default back behavior
    }
}
