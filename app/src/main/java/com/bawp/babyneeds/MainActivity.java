package com.bawp.babyneeds;

import android.app.AlarmManager;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bawp.babyneeds.data.DatabaseHandler;
import com.bawp.babyneeds.model.Item;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private Button saveButton;
    private EditText Plan;
    private EditText Place;
    private EditText Time;
    private EditText Deadline;
    private DatabaseHandler databaseHandler;
    private TimePickerDialog timePickerDialog;
    public static int Broadcastcode=0;
    Calendar calender;
    int currenthour;
    int currentminute;
    int hour;
    int Minute;
    String ampm;
    int mYear;
    int mMonth;
    int mDay;
    private ImageButton alarm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        databaseHandler = new DatabaseHandler(this);

        byPassActivity();

        //check if item was saved
      List<Item> items = databaseHandler.getAllItems();
       for (Item item : items) {
            Log.d("Main", "onCreate: " + item.getPlace());
        }





        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createPopupDialog();
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
    }

    private void byPassActivity() {
        if (databaseHandler.getItemsCount() > 0) {
            startActivity(new Intent(MainActivity.this, ListActivity.class));
            finish();
        }
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
                dialog.dismiss();
                //Todo: move to next screen - details screen
                startActivity(new Intent(MainActivity.this, ListActivity.class));

            }
        }, 1200);// 1sec
    }

    private void createPopupDialog() {

        builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.popup, null);

         Plan= view.findViewById(R.id.Myplan);
         Place= view.findViewById(R.id.MyPlace);
         Time= view.findViewById(R.id.Mytime);
         Deadline = view.findViewById(R.id.Mydeadline);
        saveButton = view.findViewById(R.id.saveButton);
        alarm = view.findViewById(R.id.imageButton);
        Time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calender =Calendar.getInstance();
                currenthour =calender.get(Calendar.HOUR_OF_DAY);
                currentminute =calender.get(Calendar.MINUTE);
                timePickerDialog =new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        hour=hourOfDay;
                        Minute=minute;
                        if(hourOfDay>=12) {
                            ampm = "PM";
                            Time.setText(String.format("%02d:%02d",hourOfDay-12,minute)+ampm);
                        }
                        else
                        { ampm="AM";
                        Time.setText(String.format("%02d:%02d",hourOfDay,minute)+ampm);
                    }
                    }
                },currenthour,currentminute,false);
                timePickerDialog.show();
            }
        });

        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Broadcastcode++;
                    Toast.makeText(MainActivity.this, "Alarm activated", Toast.LENGTH_SHORT).show();
                    Broadcastcode++;
                    Intent intent = new Intent(MainActivity.this, MyBroadcastReciver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this.getApplicationContext(), Broadcastcode, intent, 0);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    Calendar cal_alarm = Calendar.getInstance();
                    cal_alarm.set(Calendar.HOUR_OF_DAY, hour);
                    cal_alarm.set(Calendar.MINUTE, Minute);
                    cal_alarm.set(Calendar.SECOND, 0);

                long alarmmillis =cal_alarm.getTimeInMillis();
                if(cal_alarm.before(calender)) {
                    alarmmillis += 86400000L;
                    Toast.makeText(MainActivity.this, "Alarm Set For Next Day", Toast.LENGTH_SHORT).show();
                }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP,alarmmillis, pendingIntent);
                    }
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                        alarmManager.set(AlarmManager.RTC_WAKEUP,alarmmillis, pendingIntent);
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

                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
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

        builder.setView(view);
        dialog = builder.create();// creating our dialog object
        dialog.show();// important step!



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
