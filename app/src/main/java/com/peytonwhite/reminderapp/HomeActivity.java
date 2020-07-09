package com.peytonwhite.reminderapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    FloatingActionButton addReminder;
    RecyclerView rvToday;
    RecyclerView rvUpcoming;

    Adapter adapter;
    Adapter adapterU;

    SessionManager sessionManager;

    //create arrays for adapter (  add in getReminders()  );
    ArrayList<String> descriptions;
    ArrayList<String> times;
    ArrayList<String> dates;
    ArrayList<String> repeats;

    //create strings for today
    String[] d ;
    String[] t ;
    String[] da ;
    String[] r ;

    //create strings for upcoming
    String[] dU ;
    String[] tU ;
    String[] daU ;
    String[] rU ;

    ArrayList<String> descriptionsU;
    ArrayList<String> timesU;
    ArrayList<String> datesU;
    ArrayList<String> repeatsU;
    private static String URL_LOGIN = "http://www.peytonlwhite.com/blog/getreminders/";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        addReminder = findViewById(R.id.floatingActionButtonaddreminder);

        addReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pops up a page adds des time date and repeat click add and cancel
                Intent intent = new Intent(getBaseContext(),AddReminderActivity.class);
                startActivity(intent);
            }
        });


        sessionManager = new SessionManager(this);


        descriptions = new ArrayList<>();
        times = new ArrayList<>();
        dates = new ArrayList<>();
        repeats = new ArrayList<>();

        descriptionsU = new ArrayList<>();
        timesU = new ArrayList<>();
        datesU = new ArrayList<>();
        repeatsU = new ArrayList<>();

        getReminders();




        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_naviagtion);

        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId())
                {
                    case R.id.home:
                        return true;

                    case R.id.reminders:
                        startActivity(new Intent(getApplicationContext(),ReminderActivity.class));
                        overridePendingTransition(0,0);
                        return true;


                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;






                }
                return false;
            }
        });



    }


    private void getReminders()
    {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("Responsee", response );
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("remind");


                            if (success.equals("1")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String description = object.getString("reminder_Description").trim();
                                    String date = object.getString("reminder_date").trim();
                                    String time = object.getString("reminder_time").trim();
                                    String repeat = object.getString("reminder_repeat").trim();

                                    //add to arrays
                                    //if statement for both today and upcoming strings
                                    //might need another set of arraylist for upcoming
                                    String currentDate = new
                                            SimpleDateFormat("M/d/yyyy", Locale.getDefault()).format(new Date());
                                    Log.i("testingdate",currentDate + "  " + date + "\n");

                                    if(currentDate.equals(date)) {
                                        descriptions.add(description);
                                        dates.add(date);
                                        times.add(time);
                                        repeats.add(repeat);
                                       // Log.i("testingdate",currentDate + date);
                                    }
                                    //string to date
                                    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

                                    Date dateConverted = format.parse(date);

                                    //upcoming array
                                    if(isDateInCurrentWeek(dateConverted))
                                    {
                                        descriptionsU.add(description);
                                        datesU.add(date);
                                        timesU.add(time);
                                        repeatsU.add(repeat);

                                    }



                                    //create strings for today
                                    d = new String[descriptions.size()];
                                    t = new String[descriptions.size()];
                                    da = new String[descriptions.size()];
                                    r = new String[descriptions.size()];

                                    //create Strings for upcoming
                                    dU = new String[descriptionsU.size()];
                                    tU = new String[descriptionsU.size()];
                                    daU = new String[descriptionsU.size()];
                                    rU = new String[descriptionsU.size()];


                                    for(int j = 0; j < dates.size();j++)
                                    {
                                        d[j] = descriptions.get(j);
                                        t[j] = times.get(j);
                                        da[j] = dates.get(j);
                                        r[j] = repeats.get(j);
                                        Log.i("ccc",d[j]);
                                        Log.i("ccc",descriptions.get(j));
                                    }

                                    for(int j = 0; j < datesU.size();j++)
                                    {
                                        dU[j] = descriptionsU.get(j);
                                        tU[j] = timesU.get(j);
                                        daU[j] = datesU.get(j);
                                        rU[j] = repeatsU.get(j);
                                        Log.i("ccc",dU[j]);
                                        Log.i("ccc",descriptionsU.get(j));
                                    }

                                }



                            }

                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                            Toast.makeText(HomeActivity.this, "ErrorJ " + e.toString(), Toast.LENGTH_SHORT).show();
                            Log.i("ErrorJ", e + "" );


                        }
                        if(descriptions.size() > 0 || descriptionsU.size() > 0)
                        {
                            //add both upcoming and todays
                            rvToday = findViewById(R.id.listviewtoday);
                            rvUpcoming = findViewById(R.id.listviewupcoming);


                            rvToday.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                            rvUpcoming.setLayoutManager(new LinearLayoutManager(HomeActivity.this));

                            adapter = new Adapter(HomeActivity.this,d,t,da,r);
                            adapterU = new Adapter(HomeActivity.this,dU,tU,daU,rU);

                            rvToday.setAdapter(adapter);
                            rvUpcoming.setAdapter(adapterU);

                            Log.i("adapter",   "AAAAAAAAAA" );

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(HomeActivity.this, "ErrorV " + error.toString(), Toast.LENGTH_SHORT).show();
                        Log.i("ErrorV", error + "" );

                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                Map<String,String> userDetails = sessionManager.getUserDetail();
                params.put("loginId",userDetails.get("LOGINID"));



                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);





    }


   public void addReminderToDB()
    {



    }

    public static boolean isDateInCurrentWeek(Date date) {

        Calendar currentCalendar = Calendar.getInstance();
        int week = currentCalendar.get(Calendar.WEEK_OF_YEAR);
        int hour = currentCalendar.get(Calendar.HOUR_OF_DAY );
        int year = currentCalendar.get(Calendar.YEAR);
        Date dateN = Calendar.getInstance().getTime();

       int m = date.getMonth();
       String d  = (String) DateFormat.format("d",   date);

        Log.i("num",d + " " + " " + currentCalendar.get(Calendar.DAY_OF_MONTH));
       if(m == currentCalendar.get(Calendar.MONTH) && !String.valueOf(d).equals(String.valueOf(currentCalendar.get(Calendar.DAY_OF_MONTH))))
       {
           Log.i("dayOf",date.getDay() + "  ");

           if(date.after(dateN)) {
               return true;
           }
       }
/*
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTime(date);

        int targetWeek = targetCalendar.get(Calendar.WEEK_OF_YEAR);
        int targetYear = targetCalendar.get(Calendar.YEAR);
        int targetHour = targetCalendar.get(Calendar.HOUR_OF_DAY );
        return week == targetWeek && year == targetYear && hour == targetHour;

 */
return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menutopright,menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId())
        {
            case R.id.logout:
                Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(i);
                return true;


            case R.id.changePassword:
                Intent intent = new Intent(getApplicationContext(),ForgotPasswordActivity.class);
                startActivity(intent);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


}
