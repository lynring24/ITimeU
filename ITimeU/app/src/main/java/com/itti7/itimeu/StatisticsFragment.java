package com.itti7.itimeu;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.itti7.itimeu.data.ItemContract;
import com.itti7.itimeu.data.ItemDbHelper;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatisticsFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    // For access ITimeU database
    ItemDbHelper dbHelper;
    SQLiteDatabase db;

    // Identifier for the item data loader
    private View mStatisticsView;
    private Activity mStatisticsActivity;
    private Context mStatisticsContext;

    // Statistics view components
    private Spinner mStatSpinner;
    private LineChart mChart;
    private TextView mStatResultText;
    private EditText mStatStartEditText;
    private EditText mStatEndEditText;

    // Spinner item's text
    private String mSpinnerText;

    // Date format
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
    // Today's date for getting period
    private Date mTodayDate;
    // Today : Date -> String
    private String mTodayStr;
    // A week ago date
    private Date mAWeekAgoDate;
    // A week ago: Date -> String
    private String mAWeekAgoStr;
    // A month ago
    private Date mAMonthAgoDate;
    // A month ago: Date -> String
    private String mAMonthAgoStr;

    // Percent in the period
    private double mPercent;

    // Date : year, month, day
    private int mYear;
    private int mMonth;
    private int mDay;
    private String mDate;
    private String mCustomStart;
    private String mCustomEnd;
    private boolean isClickStartDate;
    private boolean isClickEndDate;

    private ArrayList<Integer> sumOfDayUnit;
    private ArrayList<Integer> sumOfDayTotalUnit;
    private ArrayList<String> dates;

    /*ToDo:
        5. UI 커스터마이징 하기
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // Set identifier
        mStatisticsView = inflater.inflate(R.layout.fragment_statistics, container, false);
        mStatisticsActivity = getActivity();
        mStatisticsContext = mStatisticsView.getContext();

        dbHelper = new ItemDbHelper(mStatisticsContext);
        db = dbHelper.getReadableDatabase();

        // Get id from fragment_statistics
        mStatSpinner = mStatisticsView.findViewById(R.id.stat_spinner);
        selectedSpinnerItem();

        // Get chart view
        mChart = mStatisticsView.findViewById(R.id.chart);

        // Get result text view
        mStatResultText = mStatisticsView.findViewById(R.id.stat_result);

        // Get period edit text view
        mStatStartEditText = mStatisticsView.findViewById(R.id.start_edit_txt);
        mStatEndEditText = mStatisticsView.findViewById(R.id.end_edit_txt);

        return mStatisticsView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * Set data to chart view
     */
    private void addData() {
        // Unit data
        List<Entry> unitEntries = new ArrayList<>();

        for (int i = 0; i < sumOfDayUnit.size(); i++) {
            unitEntries.add(new Entry(i, sumOfDayUnit.get(i)));
        }

        LineDataSet unitDataSet = new LineDataSet(unitEntries, "Units");
        unitDataSet.setColor(Color.BLACK);
        unitDataSet.setCircleColor(Color.BLACK);
        unitDataSet.setValueTextColor(Color.BLACK);

        // Total unit data
        List<Entry> totalUnitEntries = new ArrayList<>();

        for (int i = 0; i < sumOfDayTotalUnit.size(); i++) {
            totalUnitEntries.add(new Entry(i, sumOfDayTotalUnit.get(i)));
        }

        LineDataSet totalUnitDataSet = new LineDataSet(totalUnitEntries, "Total Units");
        totalUnitDataSet.setColor(Color.BLACK);
        totalUnitDataSet.setCircleColor(Color.BLACK);
        totalUnitDataSet.setValueTextColor(Color.BLACK);

        // Set x-axis
        XAxis xAxis = mChart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return dates.get((int) value).replace(".", "/").substring(5);
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });

        // Customize the chart
        customChart();

        LineData data = new LineData();
        data.addDataSet(unitDataSet);
        data.addDataSet(totalUnitDataSet);

        mChart.setData(data);
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
                mSpinnerText = mStatSpinner.getSelectedItem().toString();

                if (mSpinnerText.equals(getString(R.string.arrays_week))) {
                    // Get Date
                    getToday();
                    getAWeekAgo();

                    // Set period to edit text
                    mStatStartEditText.setText(mAWeekAgoStr);
                    mStatEndEditText.setText(mTodayStr);

                    /* Get sum of unit/total unit each days, whole value of these,
                       and set text result.*/
                    getPeriod(mAWeekAgoStr, mTodayStr);

                    // Add data
                    addData();
                } else if (mSpinnerText.equals(getString(R.string.arrays_month))) {
                    // Get Date
                    getToday();
                    getAMonthAgo();

                    // Set period to text view
                    mStatStartEditText.setText(mAMonthAgoStr);
                    mStatEndEditText.setText(mTodayStr);

                    /* Get sum of unit/total unit each days, whole value of these,
                       and set text result.*/
                    getPeriod(mAMonthAgoStr, mTodayStr);

                    // Add data
                    addData();
                } else {
                    // initialize the chart
                    mChart.setData(null);
                    mChart.invalidate();
                    mStatResultText.setText(null);
                    mStatStartEditText.setText(null);
                    mStatEndEditText.setText(null);

                    // Can touch edit text, but focus is disabled.
                    mStatStartEditText.setClickable(true);
                    mStatEndEditText.setClickable(true);

                    // Click start edit text
                    mStatStartEditText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            checkClick(true, false);
                            showDateDialog();
                        }
                    });

                    // Click end edit text
                    mStatEndEditText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            checkClick(false, true);
                            showDateDialog();
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * Get today's date
     */
    void getToday() {
        mTodayDate = new Date();
        mTodayStr = mDateFormat.format(mTodayDate);
    }

    /**
     * Get a week ago's date
     */
    void getAWeekAgo() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -6);
        mAWeekAgoDate = calendar.getTime();
        mAWeekAgoStr = mDateFormat.format(mAWeekAgoDate);
    }

    /**
     * Get a month ago's date
     */
    void getAMonthAgo() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        mAMonthAgoDate = calendar.getTime();
        mAMonthAgoStr = mDateFormat.format(mAMonthAgoDate);
    }

    /**
     * This function gets the date corresponding to the period.
     *
     * @param startDate start date
     * @param endDate   ~ end date
     */
    void getPeriodFromSql(Date startDate, Date endDate) {
        int itemUnit = 0;
        int itemTotalUnit = 0;
        sumOfDayUnit = new ArrayList<>();
        sumOfDayTotalUnit = new ArrayList<>();
        int sumOfWholeUnits = 0;
        int sumOfWholeTotalUnits = 0;

        dates = new ArrayList<>();
        Date currentDate = startDate;
        while (currentDate.compareTo(endDate) <= 0) {
            dates.add(mDateFormat.format(currentDate));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            currentDate = calendar.getTime();
        }

        for (String date : dates) {
            String[] dateStr = {date};
            Cursor cursor = db.rawQuery("SELECT unit, totalUnit FROM list WHERE date = ?", dateStr);

            // Get each day's sum of unit/totalUnit
            if (cursor.moveToFirst()) {
                do {
                    itemUnit += cursor.getInt(
                            cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_UNIT));

                    itemTotalUnit += cursor.getInt(
                            cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_TOTAL_UNIT));
                } while (cursor.moveToNext());
            }

            sumOfDayUnit.add(itemUnit);
            sumOfDayTotalUnit.add(itemTotalUnit);

            // Re-initialize
            itemUnit = 0;
            itemTotalUnit = 0;
        }

        // Sum units
        for (int i : sumOfDayUnit) {
            sumOfWholeUnits += i;
        }

        // Sum total units
        for (int i : sumOfDayTotalUnit) {
            sumOfWholeTotalUnits += i;
        }

        mStatResultText.setText(getPercent(sumOfWholeTotalUnits, sumOfWholeUnits));
    }

    /**
     * This function get percent : sum of whole units in period / sum of whole total units in period
     *
     * @param sumOfWholeTotalUnits sum of whole total units in period
     * @param sumOfWholeUnits      sum of whole units in period
     * @return percent string set
     */
    String getPercent(int sumOfWholeTotalUnits, int sumOfWholeUnits) {
        if (sumOfWholeTotalUnits != 0) {
            mPercent = Math.round(((double) sumOfWholeUnits / sumOfWholeTotalUnits) * 100);
        } else {
            mPercent = 0;
        }
        return mPercent + "% ( " + sumOfWholeUnits + " / " + sumOfWholeTotalUnits + ")";
    }

    /**
     * Customizing line chart
     */
    void customChart() {
        mChart.setDrawGridBackground(false);
        mChart.getDescription().setEnabled(false);
        mChart.setDrawBorders(false);

        mChart.getAxisRight().setEnabled(false);
        mChart.getAxisRight().setDrawAxisLine(false);
        mChart.getAxisRight().setDrawGridLines(false);

        mChart.getXAxis().setDrawAxisLine(false);
        mChart.getXAxis().setDrawGridLines(false);

        mChart.getAxisLeft().setGranularity(1f);
        mChart.getAxisLeft().setAxisMinimum(0);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
    }

    /**
     * Set date to user selected date
     */
    @Override
    public void onDateSet(DatePickerDialog view, int selectedYear, int selectedMonth, int selectedDay) {
        Calendar calendar = Calendar.getInstance();

        // Assign Selected Date in DatePickerDialog
        mYear = selectedYear;
        mMonth = selectedMonth;
        mDay = selectedDay;

        // Set Date in List
        calendar.set(mYear, mMonth, mDay);
        mDate = mDateFormat.format(calendar.getTime());

        // Custom statistics
        if (mSpinnerText.equals(getString(R.string.arrays_custom))) {
            // Select start edit text
            if (isClickStartDate && !isClickEndDate) {
                mCustomStart = mDate;
                mStatStartEditText.setText(mDate);
            }
            // Select end edit text
            else {
                mCustomEnd = mDate;
                mStatEndEditText.setText(mDate);
            }

            // If the two edit text is not empty then get statistics in selected period.
            if (mCustomStart != null && mCustomEnd != null) {
                getPeriod(mCustomStart, mCustomEnd);
                addData();
            }
        }
    }

    /**
     * Show date picker dialog
     */
    void showDateDialog() {
        // ToDo: end date should not early than start date, and (end date) - (start date) >=  7
        Calendar calendar = Calendar.getInstance();

        if (mTodayDate != null) {
            calendar.setTime(mTodayDate);
        } else calendar.setTime(new Date());

        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog
                = DatePickerDialog.newInstance(StatisticsFragment.this,
                mYear, mMonth, mDay);
        datePickerDialog.show(mStatisticsActivity.getFragmentManager(),
                "DateFragment");
    }

    /**
     * Set bool values according to clicked edit text
     *
     * @param start is clicked start edit text?
     * @param end   is clicked end edit text?
     */
    void checkClick(boolean start, boolean end) {
        isClickStartDate = start;
        isClickEndDate = end;
    }

    /**
     * Call getPeriodFromSql function.
     *
     * @param start     start date string
     * @param end       end date string
     */
    void getPeriod(String start, String end) {
        try {
            getPeriodFromSql(mDateFormat.parse(start)
                    , mDateFormat.parse(end));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}

