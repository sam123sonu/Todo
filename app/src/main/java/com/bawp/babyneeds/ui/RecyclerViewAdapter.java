package com.bawp.babyneeds.ui;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bawp.babyneeds.DatepickerFragment;
import com.bawp.babyneeds.ListActivity;
import com.bawp.babyneeds.MainActivity;
import com.bawp.babyneeds.MyBroadcastReciver;
import com.bawp.babyneeds.R;
import com.bawp.babyneeds.data.DatabaseHandler;
import com.bawp.babyneeds.model.Item;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.List;

import static android.app.DatePickerDialog.*;
import static android.content.Context.ALARM_SERVICE;

public class RecyclerViewAdapter extends Adapter<RecyclerViewAdapter.ViewHolder>  {

    private Context context;
    private List<Item> itemList;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private LayoutInflater inflater;
    private TimePickerDialog timePickerDialog;
    public String datestring;
    int hour;
    int Minute;
    int currenthour;
    int currentminute;
    String ampm;
    Calendar calender;


    public RecyclerViewAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_row, viewGroup, false);


        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder viewHolder, int position) {

        Item item = itemList.get(position); // object Item

        viewHolder.Plan.setText(MessageFormat.format("Task: {0}", item.getPlan()));
        viewHolder.Place.setText(MessageFormat.format("Place: {0}", item.getPlace()));
        viewHolder.Time.setText(MessageFormat.format("Time: {0}",item.getTime()));
        viewHolder.Deadline.setText(MessageFormat.format("Deadline: {0}", String.valueOf(item.getDeadline())));
        viewHolder.dateAdded.setText(MessageFormat.format("Added on: {0}", item.getDateItemAdded()));


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }




    public class ViewHolder<currentdatestring> extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView Plan;
        public TextView Place;
        public TextView Time;
        public TextView Deadline;
        public TextView dateAdded;
        public Button editButton;
        public Button deleteButton;
        public int id;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            Plan= itemView.findViewById(R.id.plan);
            Place = itemView.findViewById(R.id.place);
            Time = itemView.findViewById(R.id.time);
            Deadline = itemView.findViewById(R.id.deadline);
            dateAdded = itemView.findViewById(R.id.item_date);

            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);

            editButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {

            int position;
            position = getAdapterPosition();
            Item item = itemList.get(position);

            switch (v.getId()) {
                case R.id.editButton:
                    //edit item
                    editItem(item);
                    break;
                case R.id.deleteButton:
                    //delete item
                    deleteItem(item.getId());
                    break;
            }

        }

        private void deleteItem(final int id) {

            builder = new AlertDialog.Builder(context);

            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.confirmation_pop, null);

            Button noButton = view.findViewById(R.id.conf_no_button);
            Button yesButton = view.findViewById(R.id.conf_yes_button);

            builder.setView(view);
            dialog = builder.create();
            dialog.show();


            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseHandler db = new DatabaseHandler(context);
                    db.deleteItem(id);
                    itemList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    ListActivity.Total.setText("Total:"+db.getItemsCount());

                    Intent intent = new Intent(context, MyBroadcastReciver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),id, intent, 0);
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);
                    dialog.dismiss();
                }
            });
            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });


        }

        private void editItem(final Item newItem) {

            builder = new AlertDialog.Builder(context);
            inflater = LayoutInflater.from(context);
            final View view = inflater.inflate(R.layout.popup, null);

            Button saveButton;
            final EditText plan;
            final EditText place;
            final EditText time;
            TextView title;
            final  EditText deadline;
            final ImageButton alarm;
            final int[] mYear = new int[1];
            final int[] mMonth = new int[1];
            final int[] mDay = new int[1];
            plan = view.findViewById(R.id.Myplan);
            place = view.findViewById(R.id.MyPlace);
            time = view.findViewById(R.id.Mytime);
            deadline = view.findViewById(R.id.Mydeadline);
            saveButton = view.findViewById(R.id.saveButton);
            saveButton.setText(R.string.update_text);
            title = view.findViewById(R.id.title);
            alarm = view.findViewById(R.id.imageButton);
            title.setText(R.string.edit_time);
            plan.setText(newItem.getPlan());
            place.setText(newItem.getPlace());
            time.setText(newItem.getTime());
            deadline.setText(newItem.getDeadline());

            time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    calender = Calendar.getInstance();
                    currenthour = calender.get(Calendar.HOUR_OF_DAY);
                    currentminute = calender.get(Calendar.MINUTE);
                    timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            hour = hourOfDay;
                            Minute = minute;
                            if (hourOfDay >= 12) {
                                ampm = "PM";
                                time.setText(String.format("%02d:%02d", hourOfDay - 12, minute) + ampm);
                            } else {
                                ampm = "AM";
                                time.setText(String.format("%02d:%02d", hourOfDay, minute) + ampm);
                            }


                        }
                    }, currenthour, currentminute, false);
                    timePickerDialog.show();
                }
            });

            alarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(context, "Alarm activated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, MyBroadcastReciver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), newItem.getId(), intent, 0);
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                    Calendar cal_alarm = Calendar.getInstance();
                    cal_alarm.set(Calendar.HOUR_OF_DAY, hour);
                    cal_alarm.set(Calendar.MINUTE, Minute);
                    cal_alarm.set(Calendar.SECOND, 0);
                    long alarmmillis = cal_alarm.getTimeInMillis();
                    if (cal_alarm.before(calender)) {
                        alarmmillis += 86400000L;
                        Toast.makeText(context, "Alarm Set For Next Day", Toast.LENGTH_SHORT).show();
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmmillis, pendingIntent);
                    }
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmmillis, pendingIntent);
                    }
                }


            });

            deadline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar c = Calendar.getInstance();
                    mYear[0] = c.get(Calendar.YEAR);
                    mMonth[0] = c.get(Calendar.MONTH);
                    mDay[0] = c.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                            new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(android.widget.DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {
                                    Calendar c = Calendar.getInstance();
                                    c.set(Calendar.YEAR,year);
                                    c.set(Calendar.MONTH,monthOfYear);
                                    c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                                    String currentdatestring = DateFormat.getDateInstance().format(c.getTime());
                                    deadline.setText(currentdatestring);

                                }
                            }, mYear[0], mMonth[0], mDay[0]);
                    datePickerDialog.show();

                }
            });
            builder.setView(view);
            dialog = builder.create();
            dialog.show();

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //update our item
                    DatabaseHandler databaseHandler = new DatabaseHandler(context);

                    //update items
                    newItem.setPlan(plan.getText().toString());
                    newItem.setPlace(place.getText().toString());
                    newItem.setTime(time.getText().toString());
                    newItem.setDeadline(deadline.getText().toString());

                    if (!Plan.getText().toString().isEmpty() && !Place.getText().toString().isEmpty()
                            && !Time.getText().toString().isEmpty()
                            && !Deadline.getText().toString().isEmpty()) {

                        databaseHandler.updateItem(newItem);
                        notifyItemChanged(getAdapterPosition(), newItem); //important!


                    } else {
                        Snackbar.make(view, "Fields Empty",
                                Snackbar.LENGTH_SHORT)
                                .show();
                    }

                    dialog.dismiss();

                }
            });

        }
    }
}
