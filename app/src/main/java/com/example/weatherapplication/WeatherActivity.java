package com.example.weatherapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherActivity extends AppCompatActivity {

    TextView textTime, temp, pressure, humidity, tempMin, tempMax, textNameCity;
    ImageView weatherIcon;
    boolean connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        temp = findViewById(R.id.textVariableTemp);
        pressure = findViewById(R.id.textVariablePressure);
        humidity = findViewById(R.id.textVariableHumidity);
        tempMin = findViewById(R.id.textVariableTempMin);
        tempMax = findViewById(R.id.textVariableTempMax);
        textTime = findViewById(R.id.textTime);
        weatherIcon = findViewById(R.id.weatherIcon);
        textNameCity = findViewById(R.id.textNameCity);

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);

        Intent intent = getIntent();
        final String enteredName = intent.getStringExtra("KEY_NAME_CITY") + ",pl";

        checkWeather(enteredName);

        connect = true;

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                if(isNetworkConnected()) {
                                    pullToRefresh.setEnabled(true);
                                }
                                else {
                                    connect = false;
                                    pullToRefresh.setRefreshing(false);
                                    pullToRefresh.setEnabled(false);
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

        if(connect){
            automaticRefresh(enteredName);
        }

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkWeather(enteredName);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullToRefresh.setRefreshing(false);
                    }
                }, 500);
            }
        });

    }


    public void checkWeather(String enteredName){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceholderAPI jsonPlaceholderAPI = retrofit.create(JsonPlaceholderAPI.class);
        Call<CityWeather> call = jsonPlaceholderAPI.getCityWeather(enteredName, "749561a315b14523a8f5f1ef95e45864", "metric");
        call.enqueue(new Callback<CityWeather>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<CityWeather> call, Response<CityWeather> response) {
                try {
                    CityWeather cityWeather = response.body();
                    Main main = cityWeather.getMain();
                    ArrayList<Weather> weatherList = cityWeather.getWeatherList();
                    Weather weather = weatherList.get(0);
                    setTime();
                    setIcon(weather.getIcon());
                    textNameCity.setText(cityWeather.getName());
                    temp.setText(String.valueOf(main.getTemp()) + " °C");
                    pressure.setText(String.valueOf(main.getPressure()) + " hPa");
                    humidity.setText(String.valueOf(main.getHumidity()) + " %");
                    tempMin.setText(String.valueOf(main.getTemp_min()) + " °C");
                    tempMax.setText(String.valueOf(main.getTemp_max()) + " °C");

                } catch (NullPointerException e) {
                    Intent intent = new Intent(WeatherActivity.this, MainActivity.class);
                    intent.putExtra("KEY_WARNING", "City not found!");
                    startActivity(intent);
                }
            }
            @Override
            public void onFailure(Call<CityWeather> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }


    public void setTime(){
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String output = dateFormat.format(currentTime);
        textTime.setText(output);
    }

    public void setIcon(String icon){
        String iconUrl = "https://openweathermap.org/img/w/" + icon + ".png";
        Picasso.get().load(iconUrl).into(weatherIcon);

    }

    public void automaticRefresh(final String cityName){
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(300000);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                checkWeather(cityName);
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

}
