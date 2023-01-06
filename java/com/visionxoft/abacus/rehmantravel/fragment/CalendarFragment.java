package com.visionxoft.abacus.rehmantravel.fragment;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.utils.IntentHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Display full screen calendar or dialog in large layouts
 */
public class CalendarFragment extends DialogFragment implements DayViewDecorator {

    private Dialog dialog;
    private List<String> years_list, months_list;
    private SimpleDateFormat dateFormat;
    private Spinner sp_month, sp_year;
    private MaterialCalendarView calendarView;
    private Calendar min_date, max_date;
    private boolean enableFocus;

    /**
     * The system calls this to get the DialogFragment's layout, regardless
     * of whether it's being displayed as a dialog or an embedded fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init Values
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format), Locale.getDefault());
        dateFormat = new SimpleDateFormat(getString(R.string.date_format_calendar), Locale.getDefault());
        months_list = Arrays.asList(getResources().getStringArray(R.array.monthsValues));
        enableFocus = false;

        // Get Objects data
        final View selected_view = (View) IntentHelper.getObjectForKey("selected_view", true);
        final boolean do_focus = (boolean) IntentHelper.getObjectForKey("do_focus", true);
        final Calendar calendar_date = (Calendar) IntentHelper.getObjectForKey("calendar_date", true);
        min_date = (Calendar) IntentHelper.getObjectForKey("min_date", true);
        max_date = (Calendar) IntentHelper.getObjectForKey("max_date", true);

        //Inflate View
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);

        // Find Views
        sp_month = (Spinner) rootView.findViewById(R.id.sp_month);
        sp_year = (Spinner) rootView.findViewById(R.id.sp_year);
        calendarView = (MaterialCalendarView) rootView.findViewById(R.id.datePicker);
        calendarView.addDecorator(this);
        calendarView.invalidateDecorators();

        // Populate Years
        years_list = new ArrayList<>();
        int min_year = min_date.get(Calendar.YEAR);
        int max_year = max_date.get(Calendar.YEAR);
        int years_to_populate = min_year;
        while (years_to_populate <= max_year) years_list.add(String.valueOf(years_to_populate++));

        //Set Adapters
        sp_year.setAdapter(new ArrayAdapter<>(getContext(), R.layout.layout_calendar_spinner_item, years_list));
        sp_month.setAdapter(new ArrayAdapter<>(getContext(), R.layout.layout_calendar_spinner_item, months_list));

        // Set minimum and maximum dates
        calendarView.state().edit().setMinimumDate(min_date).setMaximumDate(max_date).commit();

        // Year spinner item change listener
        sp_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changeCalendarFocus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Month spinner item change listener
        sp_month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changeCalendarFocus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Setup Calendar current date
        sp_year.post(new Runnable() {
            @Override
            public void run() {
                if (((TextView) selected_view).getText().length() > 0) {
                    try {
                        Date selected_date = simpleDateFormat.parse(((TextView) selected_view).getText().toString());
                        calendarView.setSelectedDate(selected_date);
                        calendarView.setCurrentDate(selected_date);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(selected_date);
                        sp_year.setSelection(years_list.indexOf(String.valueOf(cal.get(Calendar.YEAR))), false);
                        sp_month.setSelection(months_list.indexOf(cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())), false);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    sp_month.setSelection(months_list.indexOf(min_date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())), false);
                    if (do_focus) sp_year.setSelection(sp_year.getCount() - 1, false);
                    calendarView.setCurrentDate(calendar_date);
                }
                sp_month.post(new Runnable() {
                    @Override
                    public void run() {
                        enableFocus = true;
                    }
                });
            }
        });

        // Calendar set date listener
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                // Update TextView value
                Calendar cal = Calendar.getInstance();
                cal.set(date.getYear(), date.getMonth(), date.getDay());
                ((TextView) selected_view).setText(simpleDateFormat.format(cal.getTime()));

                // Change spinner values to selected month and year
                enableFocus = false;
                sp_year.setSelection(years_list.indexOf(String.valueOf(date.getYear())), false);
                sp_month.setSelection(months_list.indexOf(cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())), false);
                sp_month.post(new Runnable() {
                    @Override
                    public void run() {
                        enableFocus = true;
                    }
                });
            }
        });

        // Select Date Button
        final LinearLayout btn_back_calendar = (LinearLayout) rootView.findViewById(R.id.btn_back_calendar);
        btn_back_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        return rootView;
    }

    // Change calendar focus to selected month and year
    private void changeCalendarFocus() {
        if (enableFocus) {
            try {
                Date date = dateFormat.parse(sp_month.getSelectedItem().toString() + "-" + sp_year.getSelectedItem().toString());
                if (date.getTime() < min_date.getTime().getTime()) {
                    calendarView.setCurrentDate(min_date);
                    sp_month.setSelection(months_list.indexOf(min_date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())), false);
                } else if (date.getTime() > max_date.getTime().getTime()) {
                    calendarView.setCurrentDate(max_date);
                    sp_month.setSelection(months_list.indexOf(max_date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())), false);
                } else calendarView.setCurrentDate(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.BottomToTopAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return true;
    }

    @Override
    public void decorate(DayViewFacade view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            view.setSelectionDrawable(getResources().getDrawable(R.drawable.selector_calendar_date, null));
        else
            view.setSelectionDrawable(getResources().getDrawable(R.drawable.selector_calendar_date));
    }
}
