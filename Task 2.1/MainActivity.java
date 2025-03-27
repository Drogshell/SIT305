package com.trevin.unitconverterapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Spinner categorySpinner, fromSpinner, toSpinner;
    private EditText editTextFrom;
    private TextView resultView;
    private final StringBuilder currentInput = new StringBuilder();

    private final UnitConverter weightConverter = new WeightConverter();
    private final UnitConverter lengthConverter = new LengthConverter();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        categorySpinner = findViewById(R.id.categorySpinner);
        fromSpinner = findViewById(R.id.fromSpinner);
        toSpinner = findViewById(R.id.toSpinner);
        editTextFrom = findViewById(R.id.editTextFrom);
        resultView = findViewById(R.id.resultView);

        editTextFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentInput.setLength(0);
                currentInput.append(s);
                updateConversion();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        String[] categories = {"Length", "Temperature", "Weight"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categories
        );

        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = categories[position];
                updateUnitSpinners(selectedCategory);
                currentInput.setLength(0);
                editTextFrom.setText("");
                updateConversion();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Not sure what I can use this for yet
            }
        });

        setUpKeypad();

    }

    private void updateUnitSpinners(String category){
        String[] units;
        switch (category){
            case "Weight":
                units = weightConverter.getUnits().toArray(new String[0]);
                break;
            case "Temperature":
                units = new String[]{"°C", "°F", "°K"};
                break;
            case "Length":
                units = lengthConverter.getUnits().toArray(new String[0]);
                break;
            default:
                units = new String[]{};
        }

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, units);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(unitAdapter);
        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateConversion();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        toSpinner.setAdapter(unitAdapter);
        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateConversion();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setUpKeypad() {
        // Clear
        findViewById(R.id.btnClear).setOnClickListener(clicked -> {
            currentInput.setLength(0);
            editTextFrom.setText("");
            updateConversion();
        });
        // Backspace
        findViewById(R.id.btnBackspace).setOnClickListener(clicked -> {
            if (currentInput.length() > 0){
                currentInput.deleteCharAt(currentInput.length() - 1);
                editTextFrom.setText(currentInput.toString());
                updateConversion();
            }
        });

        // Digits
        findViewById(R.id.btnZero).setOnClickListener(clicked -> appendInput("0"));
        findViewById(R.id.btnOne).setOnClickListener(clicked -> appendInput("1"));
        findViewById(R.id.btnTwo).setOnClickListener(clicked -> appendInput("2"));
        findViewById(R.id.btnThree).setOnClickListener(clicked -> appendInput("3"));
        findViewById(R.id.btnFour).setOnClickListener(clicked -> appendInput("4"));
        findViewById(R.id.btnFive).setOnClickListener(clicked -> appendInput("5"));
        findViewById(R.id.btnSix).setOnClickListener(clicked -> appendInput("6"));
        findViewById(R.id.btnSeven).setOnClickListener(clicked -> appendInput("7"));
        findViewById(R.id.btnEight).setOnClickListener(clicked -> appendInput("8"));
        findViewById(R.id.btnNine).setOnClickListener(clicked -> appendInput("9"));

        findViewById(R.id.btnPeriod).setOnClickListener(clicked -> appendInput("."));
        findViewById(R.id.btnEquals).setOnClickListener(clicked -> updateConversion());
    }

    private void appendInput(String digit) {
        currentInput.append(digit);
        editTextFrom.setText(currentInput);
        updateConversion();
    }

    private void updateConversion() {
        if (currentInput.length() == 0){
            resultView.setText("");
            return;
        }
        try {
            double inputValue = Double.parseDouble(currentInput.toString());
            double convertedValue = convertInput(inputValue);
            if (!(convertedValue == -1)) resultView.setText(String.format("%.2f",convertedValue));
        } catch (NumberFormatException e) {
            resultView.setText("ERROR");
        }
    }

    private double convertTemperature(double unitToConvert, String fromUnit, String toUnit) {
        double valueInClesius;
        if (fromUnit.equalsIgnoreCase("°C")){
            valueInClesius = unitToConvert;
        } else if (fromUnit.equalsIgnoreCase("°F")) {
            valueInClesius = (unitToConvert - 32) * 5 / 9;
        } else if (fromUnit.equalsIgnoreCase("°K")) {
            valueInClesius = unitToConvert - 273.15;
        } else {
            return unitToConvert;
        }

        // Convert from celsuis to the target unit
        if (toUnit.equalsIgnoreCase("°C")){
            return valueInClesius;
        } else if (toUnit.equalsIgnoreCase("°F")) {
            return (valueInClesius * 9 / 5) + 32;
        } else if (toUnit.equalsIgnoreCase("°K")) {
            return valueInClesius + 273.15;
        }
        return unitToConvert;
    }

    private double convertInput(double unitToConvert) {
        String category = categorySpinner.getSelectedItem().toString();
        String fromUnit = fromSpinner.getSelectedItem().toString();
        String toUnit = toSpinner.getSelectedItem().toString();

        if (fromUnit.equalsIgnoreCase(toUnit)){
            return unitToConvert;
        }

        switch (category){
            case "Weight":
                return weightConverter.Convert(unitToConvert, fromUnit, toUnit);
            case "Length":
                return lengthConverter.Convert(unitToConvert, fromUnit, toUnit);
            case "Temperature":
                return convertTemperature(unitToConvert, fromUnit, toUnit);
            default:
                return -1;
        }
    }

}