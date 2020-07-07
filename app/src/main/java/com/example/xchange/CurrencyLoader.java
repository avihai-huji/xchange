package com.example.xchange;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class CurrencyLoader {

    private static CurrencyLoader instance;

    private final ExecutorService executorService;

    static CurrencyLoader getInstance() {
        if (instance == null) {
            instance = new CurrencyLoader();
        }

        return instance;
    }

    private CurrencyLoader() {
        executorService = Executors.newFixedThreadPool(1);
    }

    LiveData<HashMap<String, Float>> getExchangeRates() {
        MutableLiveData<HashMap<String, Float>> response = new MutableLiveData<>();
        executorService.execute(new GetExchangeRatesWork(response));

        return response;
    }

}
