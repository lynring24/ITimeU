package com.itti7.itimeu;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
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
        BottomNavigationView bottomNavigation =
                (BottomNavigationView)  findViewById(R.id.menu_toolbar);
        bottomNavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int id = item.getItemId(); //pressed item ID
                        if (id == R.id.category_main) {
                            Intent passTo  = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(passTo);
                            return true;
                        }
                        if (id == R.id.category_about) {
                /*Intent passTo  = new Intent(getApplicationContext(),SettingActivity);
                * startActivity(passTo);*/
//            Toast.makeText(this, "setting", Toast.LENGTH_SHORT).show(); // test code
                            return true;
                        }
                        if (id == R.id.category_statistics) {
                            Intent passTo  = new Intent(getApplicationContext(),StatisticsActivity.class);
                            startActivity(passTo);
                            return true;
                        }
                        if (id == R.id.category_setting) {
                            Intent passTo  = new Intent(getApplicationContext(),SettingActivity.class);
                            startActivity(passTo);
                            return true;
                        }
                        return true;
                    }
                });
       /* if (savedInstanceState == null) {
            bottomNavigation.setSelectedItemId(R.id.category_statistics);
        }*/
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

}
