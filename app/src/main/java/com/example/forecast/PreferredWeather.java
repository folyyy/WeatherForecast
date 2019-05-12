package com.example.forecast;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PreferredWeather extends AppCompatActivity {

    int min, max;
    EditText minPreferred;
    EditText maxPreferred;
    Button submitButton;
    MyDatabase db = new MyDatabase(this);
    HistoryPreferredW historyPreferredW;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferred_weather);

        minPreferred = findViewById(R.id.minPreferred);
        maxPreferred = findViewById(R.id.maxPreferred);
        submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(minPreferred.getText().toString()) || TextUtils.isEmpty(maxPreferred.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Пожалуйста, заполните поля", Toast.LENGTH_SHORT).show();
                    return;
                }
                min = Integer.valueOf(minPreferred.getText().toString());
                max = Integer.valueOf(maxPreferred.getText().toString());
                historyPreferredW = new HistoryPreferredW();
                historyPreferredW.minPreferred = min;
                historyPreferredW.maxPreferred = max;
                if (db.preferredWisEmpty()) {
                    db.addPreferredW(historyPreferredW);
                    Toast.makeText(getApplicationContext(), "Данные успешно сохранены!", Toast.LENGTH_SHORT).show();
                } else if (!db.preferredWisEmpty()) {
                    db.updatePreferredW(historyPreferredW);
                    Toast.makeText(getApplicationContext(), "Данные успешно сохранены!", Toast.LENGTH_SHORT).show();
                }
                Intent i = new Intent(PreferredWeather.this, MainMenu.class);
                startActivity(i);
                finish();
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
