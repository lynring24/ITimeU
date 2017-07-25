package com.itti7.itimeu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hyemin on 17. 7. 26.
 */

public class ListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        TextView textView = (TextView) findViewById(R.id.date_txt_view);
        textView.setText(getDate());
    }

    /**
     * It is a function of today's date.
     * @return  Return the current month and day.
     */
    public String getDate(){
        String today = new SimpleDateFormat("MM.dd").format(new Date());
        return today;
    }
}
