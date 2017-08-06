package com.itti7.itimeu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class StatisticsActivity extends AppCompatActivity {
    private Button mTestor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        mTestor=(Button)findViewById(R.id.button_testor);
        mTestor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"got in",Toast.LENGTH_LONG).show();
            }
        });
    }
}
