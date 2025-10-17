package com.example.bai1;

import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvDisplay, tvExpression;
    private String currentInput = "";
    private String operator = "";
    private double firstNumber = 0;
    private boolean isNewOperation = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDisplay = findViewById(R.id.tvDisplay);
        tvExpression = findViewById(R.id.tvExpression);

        GridLayout gridButtons = findViewById(R.id.gridButtons);
        int childCount = gridButtons.getChildCount();

        for (int i = 0; i < childCount; i++) {
            Button btn = (Button) gridButtons.getChildAt(i);
            btn.setOnClickListener(v -> onButtonClick(btn.getText().toString()));
        }
    }

    private void onButtonClick(String value) {
        switch (value) {
            case "C":
                currentInput = "";
                operator = "";
                firstNumber = 0;
                tvExpression.setText("");
                tvDisplay.setText("0");
                isNewOperation = true;
                break;

            case "±":
                if (!currentInput.isEmpty()) {
                    if (currentInput.startsWith("-")) {
                        currentInput = currentInput.substring(1);
                    } else {
                        currentInput = "-" + currentInput;
                    }
                    tvDisplay.setText(currentInput);
                }
                break;

            case "%":
                if (!currentInput.isEmpty()) {
                    double num = Double.parseDouble(currentInput) / 100;
                    currentInput = String.valueOf(num);
                    tvDisplay.setText(currentInput);
                }
                break;

            case "÷":
            case "×":
            case "-":
            case "+":
                if (!currentInput.isEmpty()) {
                    firstNumber = Double.parseDouble(currentInput);
                    operator = value;
                    tvExpression.setText(currentInput + " " + operator);
                    currentInput = "";
                }
                break;

            case "=":
                if (!operator.isEmpty() && !currentInput.isEmpty()) {
                    double secondNumber = Double.parseDouble(currentInput);
                    double result = 0;

                    switch (operator) {
                        case "+": result = firstNumber + secondNumber; break;
                        case "-": result = firstNumber - secondNumber; break;
                        case "×": result = firstNumber * secondNumber; break;
                        case "÷":
                            if (secondNumber == 0) {
                                tvDisplay.setText("Lỗi chia 0");
                                return;
                            }
                            result = firstNumber / secondNumber;
                            break;
                    }

                    tvExpression.setText(firstNumber + " " + operator + " " + secondNumber + " =");
                    tvDisplay.setText(removeTrailingZeros(result));
                    currentInput = String.valueOf(result);
                    operator = "";
                    isNewOperation = true;
                }
                break;

            case ".":
                if (!currentInput.contains(".")) {
                    currentInput += ".";
                    tvDisplay.setText(currentInput);
                }
                break;

            default: // số 0-9
                if (isNewOperation) {
                    currentInput = "";
                    isNewOperation = false;
                }
                currentInput += value;
                tvDisplay.setText(currentInput);
                break;
        }
    }

    private String removeTrailingZeros(double num) {
        if (num == (long) num)
            return String.format("%d", (long) num);
        else
            return String.format("%s", num);
    }

}
