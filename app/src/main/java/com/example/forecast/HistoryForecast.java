package com.example.forecast;

import java.text.DateFormat;
import java.util.Locale;

public class HistoryForecast {
    String dayDate;
    double dayTemp;
    String dayDescription;
    int minPreferred;
    int maxPreferred;

    HistoryForecast() {
        this.dayTemp = 0.0;
        this.dayDescription = "No Description";
        this.dayDate = "No Date";
        this.minPreferred = -100;
        this.maxPreferred = -100;
    }

    HistoryForecast(String dayDate, Double dayTemp, String dayDescription, int minPreferred, int maxPreferred) {
        this.dayDate = dayDate;
        this.dayTemp = dayTemp;
        this.dayDescription = dayDescription;
        this.minPreferred = minPreferred;
        this.maxPreferred = maxPreferred;
    }

    @Override
    public String toString() {
        return dayDate + ". Температура: " + dayTemp + "\u2103. " + dayDescription + ". Радиус предпочитаемой погоды: ("
                + minPreferred + "\u2103.." + maxPreferred + "\u2103)";
    }
}
