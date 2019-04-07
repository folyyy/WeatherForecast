package com.example.forecast;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class Graph extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph);

        Bundle extras = getIntent().getExtras();
        double[] main = extras.getDoubleArray("main");
        String[] dateTime = extras.getStringArray("dateTime");

        GraphView graph = findViewById(R.id.graph);
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[]{dateTime[0].substring(8,10), dateTime[1].substring(8,10), dateTime[2].substring(8,10)
                , dateTime[3].substring(8,10), dateTime[4].substring(8,10), dateTime[5].substring(8,10), dateTime[6].substring(8,10)
                , dateTime[7].substring(8,10), dateTime[8].substring(8,10), dateTime[9].substring(8,10)});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {

                new DataPoint(0, main[0]),
                new DataPoint(1, main[1]),
                new DataPoint(2, main[2]),
                new DataPoint(3, main[3]),
                new DataPoint(4, main[4]),
                new DataPoint(5, main[5]),
                new DataPoint(6, main[6]),
                new DataPoint(7, main[7]),
                new DataPoint(8, main[8]),
                new DataPoint(9, main[9]),
        });
        series.setColor(Color.YELLOW);
        graph.addSeries(series);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
