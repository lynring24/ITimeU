package com.itti7.itimeu;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

        //when user click add FloatingActionButton for add a item in the list.
        final FloatingActionButton addFab = (FloatingActionButton) this.findViewById(R.id.add_fab_btn);
        addFab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showAddDialog();
            }
        });
    }

    /**
     * It is a function of today's date.
     * @return  Return the current month and day.
     */
    public String getDate(){
        String today = new SimpleDateFormat("MM.dd").format(new Date());
        return today;
    }

    /**
     * This function open the dialog window to add the item for TdDo list.
     */
    private void showAddDialog(){
        LayoutInflater dialog = LayoutInflater.from(this);

        //assign Dialog
        final View dialogLayout = dialog.inflate(R.layout.add_dialog, null);
        final Dialog addDialog = new Dialog(this);

        addDialog.setContentView(dialogLayout);
        addDialog.show();

        Button mOkButton = dialogLayout.findViewById(R.id.add_ok_btn);
        Button mCancelButton = dialogLayout.findViewById(R.id.add_cancel_btn);

        mOkButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_SHORT).show();
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addDialog.cancel();
            }
        });
    }
}
