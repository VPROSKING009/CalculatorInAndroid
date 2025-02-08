package com.example.calculator;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    private EditText input;
    private TextView output;
    private boolean isNewInput = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input = findViewById(R.id.input);
        output = findViewById(R.id.output);

        // Disable keyboard popup
        input.setShowSoftInputOnFocus(false);
        input.setInputType(InputType.TYPE_NULL);

        setNumberButtonListeners();
        setOperatorButtonListeners();
        setDecimalButtonListener();
        setBackspaceButtonListener();

        // Cursor management
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Keep cursor at the right place after edit
                int cursorPosition = input.getSelectionStart();
                Selection.setSelection(input.getText(), cursorPosition);
            }
        });
    }

    // Handle Number Buttons (0-9)
    private void setNumberButtonListeners() {
        int[] numberIds = {
                R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
                R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9
        };

        View.OnClickListener listener = v -> {
            Button button = (Button) v;
            insertText(button.getText().toString());
        };

        for (int id : numberIds) {
            findViewById(id).setOnClickListener(listener);
        }
    }

    // Handle Operator Buttons (+, -, *, /, %)
    private void setOperatorButtonListeners() {
        int[] operatorIds = {
                R.id.buttonAdd, R.id.buttonSubtract, R.id.buttonMultiply,
                R.id.buttonDivide, R.id.buttonPercentage
        };

        View.OnClickListener listener = v -> {
            Button button = (Button) v;
            insertText(button.getText().toString());
        };

        for (int id : operatorIds) {
            findViewById(id).setOnClickListener(listener);
        }

        // Handle Equal Button (=)
        findViewById(R.id.buttonEqual).setOnClickListener(v -> {
            if (!input.getText().toString().isEmpty()) {
                String result = evaluateExpression(input.getText().toString());
                output.setText(result);
            }
        });

        // Handle Clear Button (C)
        findViewById(R.id.buttonClear).setOnClickListener(v -> {
            input.setText("");
            output.setText("");
        });
    }

    // Handle Decimal Button (.)
    private void setDecimalButtonListener() {
        findViewById(R.id.buttonDecimal).setOnClickListener(v -> insertText("."));
    }

    // Handle Backspace Button (⌫)
    private void setBackspaceButtonListener() {
        findViewById(R.id.buttonBackspace).setOnClickListener(v -> {
            int cursorPosition = input.getSelectionStart();
            if (cursorPosition > 0) {
                String text = input.getText().toString();
                String newText = text.substring(0, cursorPosition - 1) + text.substring(cursorPosition);
                input.setText(newText);
                input.setSelection(cursorPosition - 1);
            }
        });
    }

    // Insert text at the cursor position
    private void insertText(String text) {
        int cursorPosition = input.getSelectionStart();
        String oldText = input.getText().toString();
        String newText = oldText.substring(0, cursorPosition) + text + oldText.substring(cursorPosition);
        input.setText(newText);
        input.setSelection(cursorPosition + text.length()); // Move cursor forward
    }

    // Custom function to evaluate math expressions
    private String evaluateExpression(String expression) {
        try {
            return String.valueOf(calculate(expression));
        } catch (Exception e) {
            return "Error";
        }
    }

    // Function to evaluate mathematical expressions using Stacks
    private double calculate(String expression) {
        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();
        int length = expression.length();
        for (int i = 0; i < length; i++) {
            char c = expression.charAt(i);

            if (c == ' ') continue;

            if (Character.isDigit(c) || c == '.') {
                StringBuilder sb = new StringBuilder();
                while (i < length && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    sb.append(expression.charAt(i));
                    i++;
                }
                i--;
                numbers.push(Double.parseDouble(sb.toString()));
            } else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%') {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(c)) {
                    numbers.push(applyOperator(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.push(c);
            }
        }

        while (!operators.isEmpty()) {
            numbers.push(applyOperator(operators.pop(), numbers.pop(), numbers.pop()));
        }

        return numbers.pop();
    }

    private int precedence(char op) {
        if (op == '+' || op == '-') return 1;
        if (op == '*' || op == '/' || op == '%') return 2;
        return 0;
    }

    private double applyOperator(char op, double b, double a) {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/': return a / b;
            case '%': return a % b;
        }
        return 0;
    }
}
