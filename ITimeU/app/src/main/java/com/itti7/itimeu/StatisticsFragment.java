package com.itti7.itimeu;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.itti7.itimeu.data.ItemContract;
import com.itti7.itimeu.data.ItemDbHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StatisticsFragment extends Fragment {
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
    private TextView mStatPeriodText;

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

    private ArrayList<Integer> sumOfDayUnit;
    private ArrayList<Integer> sumOfDayTotalUnit;
    private ArrayList<String> dates;

    /*ToDo:
        일단 week, month 만!
        1. week, month 에 해당하는 unit sum, total unit sum 정보 얻어오기
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

        dbHelper = new ItemDbHelper(mStatisticsContext);
        db = dbHelper.getReadableDatabase();

        // Get id from fragment_statistics
        mStatSpinner = mStatisticsView.findViewById(R.id.stat_spinner);
        selectedSpinnerItem();

        mChart = mStatisticsView.findViewById(R.id.chart);

        mStatResultText = mStatisticsView.findViewById(R.id.stat_result);

        mStatPeriodText = mStatisticsView.findViewById(R.id.stat_period);

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

                    // Set period to text view
                    mStatPeriodText.setText(mAWeekAgoStr + " - " + mTodayStr);

                    /* Get sum of unit/total unit each days, whole value of these,
                       and set text result.*/
                    getPeriodFromSql(mAWeekAgoDate, mTodayDate);
                } else if (mSpinnerText.equals(getString(R.string.arrays_month))) {
                    // Get Date
                    getToday();
                    getAMonthAgo();

                    // Set period to text view
                    mStatPeriodText.setText(mAMonthAgoStr + " - " + mTodayStr);

                    /* Get sum of unit/total unit each days, whole value of these,
                       and set text result.*/
                    getPeriodFromSql(mAMonthAgoDate, mTodayDate);
                } else {
                    // @ToDo
                    Toast.makeText(mStatisticsContext, "custom", Toast.LENGTH_SHORT).show();
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
        calendar.add(Calendar.DATE, -7);
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

    String getPercent(int sumOfWholeTotalUnits, int sumOfWholeUnits){
        if (sumOfWholeTotalUnits != 0) {
            mPercent = Math.round(((double) sumOfWholeUnits / sumOfWholeTotalUnits) * 100);
        } else {
            mPercent = 0;
        }
        return mPercent + "% ( "+sumOfWholeUnits+" / "+sumOfWholeTotalUnits+ ")";
    }
}

