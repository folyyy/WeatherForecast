package com.example.forecast;

import java.text.DateFormat;
import java.util.Locale;

public class HistoryForecast {
    String dayDate;
    double dayTemp;
    String dayDescription;

    HistoryForecast() {
        this.dayTemp = 0.0;
        this.dayDescription = "No Description";
        this.dayDate = "No Date";
    }

    HistoryForecast(String dayDate, Double dayTemp, String dayDescription) {
        this.dayDate = dayDate;
        this.dayTemp = dayTemp;
        this.dayDescription = dayDescription;
    }

    @Override
    public String toString() {
        return dayDate + ". Температура: " + dayTemp + "\u2103. \n" + dayDescription;
    }
}
