package com.itti7.itimeu;


        import android.graphics.Color;
        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.view.LayoutInflater;
        import android.view.Menu;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.RelativeLayout;
        import android.widget.Spinner;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.github.mikephil.charting.charts.LineChart;
        import com.github.mikephil.charting.components.Legend;
        import com.github.mikephil.charting.data.Entry;
        import com.github.mikephil.charting.data.LineData;
        import com.github.mikephil.charting.data.LineDataSet;
        import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;
        import com.github.mikephil.charting.utils.ColorTemplate;

        import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticFragment extends Fragment {
    private RelativeLayout mLineLayout;
    private LineChart mChart;
    private float[] yData = {5, 10, 7, 8, 13};
    private String[] xData = { "Done", "Still", "LG", "Apple111", "Samsung"};
    private Spinner statSpinner;

    public StatisticFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View statView = inflater.inflate(R.layout.activity_statistics, container, false); //statisticsView
        statSpinner = (Spinner)statView.findViewById(R.id.stat_spinner);

        ArrayAdapter statAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.stat_type, android.R.layout.simple_spinner_item);
        statAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statSpinner.setAdapter(statAdapter);



        mLineLayout = (RelativeLayout)statView.findViewById(R.id.stat_chart);
        mChart = new LineChart(getActivity());

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

                Toast.makeText(getActivity(), "Data : " + xData[entry.getXIndex()] + " , value : " + entry.getVal(), Toast.LENGTH_SHORT).show();
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


        return statView;
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

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }*/
}
