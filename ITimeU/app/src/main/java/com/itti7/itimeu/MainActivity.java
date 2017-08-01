package com.itti7.itimeu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*timer fragment call*/
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.timer_container, new TimerFragment()).commit();
/*Toolbar init*/
        Toolbar myToolbar = (Toolbar) findViewById(R.id.menu_toolbar);
        setSupportActionBar(myToolbar);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId(); //pressed item ID
        if (id == R.id.action_setting) {
                /*Intent passTo  = new Intent(getApplicationContext(),SettingActivity);
                * startActivity(passTo);*/
//            Toast.makeText(this, "setting", Toast.LENGTH_SHORT).show(); // test code
            return true;
        }
        if (id == R.id.action_statistics) {
            /*Intent passTo  = new Intent(getApplicationContext(),StatisticsActivity);
                * startActivity(passTo);*/
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
