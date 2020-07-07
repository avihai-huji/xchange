package com.example.xchange;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private HashMap<String, Float> exchangeRates = null;

    private Spinner targetCurrency;

    private TextView amount;

    private TextView convertResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.targetCurrency = findViewById(R.id.target_currency);
        this.amount = findViewById(R.id.amount);
        this.convertResult = findViewById(R.id.convert_text);

        this.amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    convertResult.setText("");
                    return;
                }
                String res = amount.getText().toString() + " ILS = " + convert().toString() + " " +
                        targetCurrency.getSelectedItem().toString();
                convertResult.setText(res);
            }
        });

        LiveData<HashMap<String, Float>> exchangeRates =
                CurrencyLoader.getInstance().getExchangeRates();
        exchangeRates.observe(this, this::onExchangeRatesLoaded);
    }

    private void onExchangeRatesLoaded(HashMap<String, Float> exchangeRates) {
        if (exchangeRates.isEmpty()) {
            return;
        }

        List<String> spinnerArray = new ArrayList<>();
        this.exchangeRates = exchangeRates;
        this.exchangeRates.forEach((k,v) -> {
            spinnerArray.add(k);
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        targetCurrency.setAdapter(adapter);
    }

    private Float convert() {
        String selectedItem = targetCurrency.getSelectedItem().toString();
        Float currencyExchangeRate = exchangeRates.get(selectedItem);
        Float amount = Float.valueOf(this.amount.getText().toString());

        return amount * currencyExchangeRate;
    }

}
