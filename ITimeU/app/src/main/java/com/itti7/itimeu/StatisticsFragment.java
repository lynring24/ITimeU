package com.itti7.itimeu;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class StatisticsFragment extends Fragment {

    private View mStatisticsView;
    private Activity mStatisticsActivity;
    private Context mStatisticsContext;

    private RelativeLayout mLineLayout;
    private LineChart mChart;
    private float[] yData = {5, 10, 7, 8, 13};
    private String[] xData = { "Done", "Still", "LG", "Apple111", "Samsung"};
    private Spinner statSpinner;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mStatisticsView = inflater.inflate(R.layout.fragment_statistics, container, false);
        mStatisticsActivity = getActivity();
        mStatisticsContext = mStatisticsView.getContext();

        statSpinner = mStatisticsView.findViewById(R.id.stat_spinner);

        ArrayAdapter statAdapter = ArrayAdapter.createFromResource(mStatisticsContext, R.array.stat_type, android.R.layout.simple_spinner_item);
        statAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statSpinner.setAdapter(statAdapter);



        mLineLayout = mStatisticsView.findViewById(R.id.stat_chart);
        mChart = new LineChart(mStatisticsContext);

        // add pie chart to main layout
        mLineLayout.addView(mChart);
        //mPieLayout.setBackgroundColor(Color.LTGRAY);

        // configure pie chart
        mChart.setDescription("Session works");


        // set a chart value selected listener
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i) {
                if (entry == null)
                    return;

                Toast.makeText(mStatisticsActivity.getBaseContext(), "Data : " + xData[entry.getXIndex()] + " , value : " + entry.getVal(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

        // add data
        addData();

        // customize legends
        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);

        return mStatisticsView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void addData() {
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        for (int i = 0; i < yData.length; i++)
            yVals1.add(new Entry(yData[i],i));

        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < xData.length; i++)
            xVals.add(xData[i]);

        // create pie data set
        LineDataSet dataSet = new LineDataSet(yVals1, "Market Share");

        // add mana colors
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        // instantiate pie data object now
        LineData data = new LineData(xVals, dataSet);

        //mChart.setValueFormatter();
        mChart.setValueTextSize(14f);
        mChart.setValueTextColor(Color.BLACK);

        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        // update pie chart
        mChart.invalidate();


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
    }
}

