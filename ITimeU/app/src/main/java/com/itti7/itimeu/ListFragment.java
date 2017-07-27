package com.itti7.itimeu;

import android.support.v4.app.Fragment;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hyemin on 17. 7. 26.
 */

public class ListFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        TextView textView = getView().findViewById(R.id.date_txt_view);
        textView.setText(getDate());

        final FloatingActionButton addFab = getActivity().findViewById(R.id.add_fab_btn);
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


    private void showAddDialog(){
        LayoutInflater dialog = LayoutInflater.from(getActivity());
        final View dialogLayout = dialog.inflate(R.layout.add_dialog, null);
        final Dialog addDialog = new Dialog(getActivity());

        addDialog.setContentView(dialogLayout);
        addDialog.show();

        Button mOkButton = dialogLayout.findViewById(R.id.add_ok_btn);
        Button mCancelButton = dialogLayout.findViewById(R.id.add_cancel_btn);

        mOkButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getActivity(), "OK", Toast.LENGTH_SHORT).show();
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
