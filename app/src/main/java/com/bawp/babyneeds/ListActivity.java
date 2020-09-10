package com.bawp.babyneeds;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bawp.babyneeds.data.DatabaseHandler;
import com.bawp.babyneeds.model.Item;
import com.bawp.babyneeds.ui.RecyclerViewAdapter;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ListActivity extends AppCompatActivity{
    private static final String TAG = "ListActivity";
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private List<Item> itemList;
    private DatabaseHandler databaseHandler;
    private FloatingActionButton fab;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private Button saveButton;
    private EditText Plan;
    private EditText Place;
    private EditText Time;
    private EditText Deadline;
    private TimePickerDialog timePickerDialog;
    private ImageButton alarm;
    public static TextView Total;
    Calendar calender;
    int total;
    int currenthour;
    int currentminute;
    String ampm;
    int hour;
    int Minute;
    int mYear;
    int mMonth;
    int mDay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        recyclerView = findViewById(R.id.recyclerview);
        fab = findViewById(R.id.fab);
        Total = findViewById(R.id.textView3);

        databaseHandler = new DatabaseHandler(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemList = new ArrayList<>();

        //Get items from db
        itemList = databaseHandler.getAllItems();

        for (Item item : itemList) {

            Log.d(TAG, "onCreate: " + item.getTime());

        }

        recyclerViewAdapter = new RecyclerViewAdapter(this, itemList);
        recyclerView.setAdapter(recyclerViewAdapter);
        total =databaseHandler.getItemsCount();
        recyclerViewAdapter.notifyDataSetChanged();
        Total.setText("Total:"+total);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPopDialog();
            }
        });
    }

    private void createPopDialog() {
        builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.popup, null);
        Plan= view.findViewById(R.id.Myplan);
        Place= view.findViewById(R.id.MyPlace);
        Time= view.findViewById(R.id.Mytime);
        Deadline= view.findViewById(R.id.Mydeadline);
        saveButton = view.findViewById(R.id.saveButton);
        alarm =view.findViewById(R.id.imageButton);
        Time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calender = Calendar.getInstance();
                currenthour =calender.get(Calendar.HOUR_OF_DAY);
                currentminute =calender.get(Calendar.MINUTE);
                timePickerDialog =new TimePickerDialog(ListActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        hour=hourOfDay;
                        Minute=minute;
                        if (hourOfDay >= 12) {
                            ampm = "PM";
                            Time.setText(String.format("%02d:%02d", hourOfDay-12, minute) + ampm);
                        } else {
                            ampm = "AM";
                            Time.setText(String.format("%02d:%02d", hourOfDay, minute) + ampm);
                        }
                    }
                },currenthour,currentminute,false);
                timePickerDialog.show();
            }
        });
        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Toast.makeText(ListActivity.this, "Alarm activated", Toast.LENGTH_SHORT).show();
                    MainActivity.Broadcastcode++;
                    Intent intent = new Intent(ListActivity.this, MyBroadcastReciver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(ListActivity.this.getApplicationContext(), MainActivity.Broadcastcode, intent, 0);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    Calendar cal_alarm = Calendar.getInstance();
                    cal_alarm.set(Calendar.HOUR_OF_DAY, hour);
                    cal_alarm.set(Calendar.MINUTE, Minute);
                    cal_alarm.set(Calendar.SECOND, 0);

                long alarmmillis =cal_alarm.getTimeInMillis();
                if(cal_alarm.before(calender)) {
                    alarmmillis += 86400000L;
                    Toast.makeText(ListActivity.this, "Alarm Set For Next Day", Toast.LENGTH_SHORT).show();
                }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP,alarmmillis, pendingIntent);
                    }
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmmillis, pendingIntent);
                    }


            }
        });
        Deadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(ListActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(android.widget.DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.YEAR,year);
                                c.set(Calendar.MONTH,monthOfYear);
                                c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                                String currentdatestring = DateFormat.getDateInstance().format(c.getTime());
                                Deadline.setText(currentdatestring);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });

        builder.setView(view);

        alertDialog = builder.create();
        alertDialog.show();


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Plan.getText().toString().isEmpty() && !Place.getText().toString().isEmpty()
                        && !Time.getText().toString().isEmpty()
                        && !Deadline.getText().toString().isEmpty()) {
                    saveItem(v);
                }else {
                    Snackbar.make(v, "Empty Fields not Allowed", Snackbar.LENGTH_SHORT)
                            .show();
                }

            }
        });
    }
    private void saveItem(View view) {
        //Todo: save each baby item to db
        Item item = new Item();

        String newPlan = Plan.getText().toString().trim();
        String newPlace = Place.getText().toString().trim();
        String newTime = Time.getText().toString().trim();
        String newDeadline= Deadline.getText().toString().trim();

        item.setPlan(newPlan);
        item.setPlace(newPlace);
        item.setTime(newTime);
        item.setDeadline(newDeadline);

        databaseHandler.addItem(item);

        Snackbar.make(view, "Item Saved",Snackbar.LENGTH_SHORT)
                .show();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //code to be run
                alertDialog.dismiss();
                //Todo: move to next screen - details screen
                startActivity(new Intent(ListActivity.this, ListActivity.class));
                finish();

            }
        }, 1200);// 1sec
    }


}
