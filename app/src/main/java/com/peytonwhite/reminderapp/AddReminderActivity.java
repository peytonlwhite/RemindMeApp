package com.peytonwhite.reminderapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddReminderActivity extends AppCompatActivity {


    CalendarView calendarView;
    Button addReminder;
    Calendar c;
    TextView textViewTime;

    Button cancel;
    Context context = this;
    Switch repeat;
    EditText descript;
    String repeatDay, time, date, descr;
    private static String URL_LOGIN = "http://www.peytonlwhite.com/blog/addreminder/";
    SessionManager sessionManager;
    TimePickerDialog timePickerDialog;
    int hourOf;
    int monthOf;
    int yearOf;
    int dayOf;
    int minOf;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);


        textViewTime = findViewById(R.id.textViewTime);
        calendarView =  findViewById(R.id.calendarView);
        descript = findViewById(R.id.editTextDesc);
        addReminder = findViewById(R.id.buttonadd);
        cancel = findViewById(R.id.buttoncancel);
        repeat = findViewById(R.id.switch3R);
        c = Calendar.getInstance();
        sessionManager = new SessionManager(this);


        final Calendar calendar = Calendar.getInstance();
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);



        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                date = (i1 + 1) + "/" + i2 + "/" + i;
                dayOf = i2;
                monthOf = (i1+1);
                yearOf = i;
                timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        boolean isPM = (hourOfDay >= 12);
                        textViewTime.setText(String.format("%02d:%02d %s", (hourOfDay == 12 || hourOfDay == 0) ? 12 : hourOfDay % 12, minute, isPM ? "PM" : "AM"));
                        hourOf = timePicker.getHour();
                        minOf = timePicker.getMinute();


                    }
                },hour,minute,android.text.format.DateFormat.is24HourFormat(context));

                timePickerDialog.show();

            }



        });

        addReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //save all info and add to database
                if (repeat.isChecked()) {
                    repeatDay = "1";
                } else {
                    repeatDay = "0";
                }

                time = textViewTime.getText().toString();
                descr = descript.getText().toString();
                Log.i("Datee", date + time + descr + repeatDay);


                ///make sure all fields are filled in
                if(time.equals("Time:") || descr.isEmpty())
                {
                    Toast.makeText(AddReminderActivity.this, "Must set date and description", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    AddReminder();

                    //make reminder
                    c.set(c.get(Calendar.YEAR),
                            c.get(Calendar.MONTH),
                            c.get(Calendar.DAY_OF_MONTH),
                            hourOf,
                            minOf, 0

                    );
                    Log.i("timeset",yearOf +  " " + monthOf + " " + dayOf + " " + hourOf + " " + minOf);

                    setAlarm(c.getTimeInMillis());





                    Intent intent = new Intent(getBaseContext(),ReminderActivity.class);
                    startActivity(intent);
                }





            }

        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(),ReminderActivity.class);
                startActivity(intent);
            }
        });


    }

    private void setAlarm(long timeInMillis)
    {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this,MyAlarm.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);

        alarmManager.setRepeating(AlarmManager.RTC,timeInMillis,AlarmManager.INTERVAL_DAY,pendingIntent);



    }

    private void AddReminder()
    {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("ResponseA", response );
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");


                            if (success.equals("1")) {

                                Toast.makeText(AddReminderActivity.this, "Reminder Added", Toast.LENGTH_SHORT).show();


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(AddReminderActivity.this, "ErrorJ " + e.toString(), Toast.LENGTH_SHORT).show();
                            Log.i("ErrorJ", e + "" );


                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AddReminderActivity.this, "ErrorV " + error.toString(), Toast.LENGTH_SHORT).show();
                        Log.i("ErrorV", error + "" );

                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                Map<String,String> userDetails = sessionManager.getUserDetail();


                params.put("loginId",userDetails.get("LOGINID"));
                params.put("reminder_Description",descr);
                params.put("reminder_date",date);
                params.put("reminder_time",time);
                params.put("reminder_repeat",repeatDay);


                Log.i("Datee", date + time + descr + repeatDay + userDetails.get("LOGINID"));


                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);





    }

}
