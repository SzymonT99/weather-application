package com.example.weatherapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    EditText cityNameText;
    TextView textWarning, textInternetConnection;
    Button checkWeatherButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityNameText = findViewById(R.id.cityNameText);
        textWarning = findViewById(R.id.textWarning);
        checkWeatherButton = findViewById(R.id.checkWeatherButton);
        textInternetConnection = findViewById(R.id.textInternetConnection);

        loadData();

        Intent intent = getIntent();
        final String invalidName = intent.getStringExtra("KEY_WARNING");
        textWarning.setText(invalidName);


        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                if(isNetworkConnected()) {
                                    textInternetConnection.setVisibility(View.INVISIBLE);
                                    checkWeatherButton.setClickable(false);
                                    cityNameText.setEnabled(true);
                                }
                                else {
                                    textInternetConnection.setVisibility(View.VISIBLE);
                                    checkWeatherButton.setEnabled(false);
                                    cityNameText.setEnabled(false);
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    System.out.println(e.getLocalizedMessage());
                }
            }
        };
        thread.start();

    }

    public boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void sendCityName(View view) {
        String cityName = cityNameText.getText().toString();

        saveData(cityName);

        Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
        intent.putExtra("KEY_NAME_CITY", cityName);
        startActivity(intent);
    }
    private void saveData(String output){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("DATE_KEY", output);
        editor.apply();
    }

    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        String savedCityName = sharedPreferences.getString("DATE_KEY", "City");
        cityNameText.setText(savedCityName);
    }


}
