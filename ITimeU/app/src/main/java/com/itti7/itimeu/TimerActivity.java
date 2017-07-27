package com.itti7.itimeu;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class TimerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }
    @Override
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
        if (id == R.id.action_list) {
            /*Intent passTo  = new Intent(getApplicationContext(),ListActivity);
                * startActivity(passTo);*/
            return true;
        }
        if (id == R.id.action_statistics) {
            /*Intent passTo  = new Intent(getApplicationContext(),StatisticsActivity);
                * startActivity(passTo);*/
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //액션바 숨기기
    private void hideActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.hide();
    }
}
