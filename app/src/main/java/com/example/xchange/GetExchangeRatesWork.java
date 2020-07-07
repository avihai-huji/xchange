package com.example.xchange;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetExchangeRatesWork implements Runnable {

    private final OkHttpClient client = new OkHttpClient();

    private MutableLiveData<HashMap<String, Float>> response;

    GetExchangeRatesWork(MutableLiveData<HashMap<String, Float>> response) {
        this.response = response;
    }

    @Override
    public void run() {
        Request request = new Request.Builder()
                .url("https://www.boi.org.il/currency.xml")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String content = response.body().string();
            HashMap<String, Float> exchangeRates = parseExchangeRates(content);
            Log.d("workerTag", exchangeRates.toString());
            this.response.postValue(exchangeRates);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, Float> parseExchangeRates(String rawXml) {
        HashMap<String, Float> exchangeRates = new HashMap<>();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(new StringReader(rawXml));
            int eventType = xpp.getEventType();
            String curCurrencyCode = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("CURRENCYCODE")) {
                        eventType = xpp.next();
                        if (eventType != XmlPullParser.TEXT) {
                            Log.d("workerTag", "Invalid XML format.");
                            break;
                        }
                        curCurrencyCode = xpp.getText();
                        eventType = xpp.next();
                        if (eventType != XmlPullParser.END_TAG) {
                            Log.d("workerTag", "Invalid XML format.");
                            break;
                        }
                    } else if (xpp.getName().equals("RATE")) {
                        eventType = xpp.next();
                        if (eventType != XmlPullParser.TEXT) {
                            Log.d("workerTag", "Invalid XML format.");
                            break;
                        }
                        exchangeRates.put(curCurrencyCode, Float.valueOf(xpp.getText()));
                        eventType = xpp.next();
                        if (eventType != XmlPullParser.END_TAG) {
                            Log.d("workerTag", "Invalid XML format.");
                            break;
                        }
                    }
                }

                eventType = xpp.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return exchangeRates;
    }
}
