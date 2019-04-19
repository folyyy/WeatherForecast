package com.example.forecast;

public class HistoryPreferredW {
    int minPreferred;
    int maxPreferred;

    HistoryPreferredW() {
        this.minPreferred = -100;
        this.maxPreferred = -100;
    }

    HistoryPreferredW(int minPreferred, int maxPreferred) {
        this.minPreferred = minPreferred;
        this.maxPreferred = maxPreferred;
    }

    @Override
    public String toString() {
        return "Радиус предпочитаемой погоды: (" + minPreferred + "\\u2103.." + maxPreferred + "\\u2103)";
    }
}
