package com.itti7.itimeu;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class StatisticsFragment extends Fragment {
    // Identifier for the item data loader
    private View mStatisticsView;
    private Activity mStatisticsActivity;
    private Context mStatisticsContext;

    // Statistics view components
    private Spinner mStatSpinner;
    private LineChart mChart;
    private TextView mStatResultText;

    // Spinner item's text
    String spinnerText;

    // xData: date, yData: unit
    private int[] yData = {0};
    private String[] xData = {""};


    /*ToDo:
        0. Spinner 아이템에 따른 토스트창 띄우기
        1. week, month, custom 에 해당하는 unit, total unit 정보 얻어오기
        2. 리스트 엔트리에 저장하기(x축은 날짜, y축은 포모도로 단위)
        3. 데이터 셋에 저장하기
        4. 차트에 데이터 뿌리기
        5. UI 커스터마이징 하기
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // Set identifier
        mStatisticsView = inflater.inflate(R.layout.fragment_statistics, container, false);
        mStatisticsActivity = getActivity();
        mStatisticsContext = mStatisticsView.getContext();

        // Get id from fragment_statistics
        mStatSpinner = mStatisticsView.findViewById(R.id.stat_spinner);
        selectedSpinnerItem();

        mChart = mStatisticsView.findViewById(R.id.chart);
        mStatResultText = mStatisticsView.findViewById(R.id.stat_result);

        // Add data
        addData();

        // Customize legends
        Legend l = mChart.getLegend();
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);

        return mStatisticsView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * Set data to chart view (?)
     */
    private void addData() {
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        for (int i = 0; i < yData.length; i++) {
            yVals1.add(new Entry(yData[i], i));
        }

        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < xData.length; i++) {
            xVals.add(xData[i]);
        }

        // Create pie data set
        LineDataSet dataSet = new LineDataSet(yVals1, "Market Share");

        // Add mana colors
        ArrayList<Integer> colors = new ArrayList<Integer>();

        colors.add(ColorTemplate.getHoloBlue());

        // Undo all highlights
        mChart.highlightValues(null);

        // Update pie chart
        mChart.invalidate();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    /**
     * This function is branched according to selected item.
     */
    void selectedSpinnerItem() {
        mStatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerText = mStatSpinner.getSelectedItem().toString();

                if(spinnerText.equals(getString(R.string.arrays_week))) {
                    Toast.makeText(mStatisticsContext, "week", Toast.LENGTH_SHORT).show();
                }
                else if (spinnerText.equals(getString(R.string.arrays_month))) {
                    Toast.makeText(mStatisticsContext, "month", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(mStatisticsContext, "custom", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}

