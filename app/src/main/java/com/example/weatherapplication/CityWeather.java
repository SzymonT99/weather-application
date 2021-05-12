package com.example.weatherapplication;

import java.util.ArrayList;

public class CityWeather {
        private Coord coord;
        private ArrayList<Weather> weather;
        private String base;
        private Main main;
        private Wind wind;
        private Clouds clouds;
        private int dt;
        private Sys sys;
        private int timezone;
        private int id;
        private String name;
        private int cod;

        public Coord getCoord() {
            return coord;
        }

        public ArrayList<Weather> getWeatherList(){
            return weather;
        }
        public String getBase() {
            return base;
        }

        public Main getMain() {
            return main;
        }

        public Wind getWind() {
            return wind;
        }

        public Clouds getClouds() {
            return clouds;
        }

        public int getDt() {
            return dt;
        }

        public Sys getSys() {
            return sys;
        }

        public int getTimezone() {
            return timezone;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getCod() {
            return cod;
        }
}

