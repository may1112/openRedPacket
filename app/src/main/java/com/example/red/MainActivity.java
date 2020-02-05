package com.example.red;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private LetterDialog letterOpenView;

    private ImageView btnLetter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLetter = findViewById(R.id.btn_bubble);

        letterOpenView = new LetterDialog(this);
        letterOpenView.addInitialPoint(btnLetter);
        btnLetter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                letterOpenView.show();
            }
        });
    }
}
