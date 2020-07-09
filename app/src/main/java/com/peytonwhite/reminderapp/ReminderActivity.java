package com.peytonwhite.reminderapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReminderActivity extends AppCompatActivity {

    RecyclerView rv;
    Adapter adapter;
    private static String URL_LOGIN = "http://www.peytonlwhite.com/blog/getreminders/";
    SessionManager sessionManager;
    Switch aSwitch;

    //create arrays for adapter (  add in getReminders()  );
    ArrayList<String> descriptions;
    ArrayList<String> times;
    ArrayList<String> dates;
    ArrayList<String> repeats;

    //create strings
    String[] d ;
    String[] t ;
    String[] da ;
    String[] r ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);
        sessionManager = new SessionManager(this);
        aSwitch = findViewById(R.id.switchonoroff);


        descriptions = new ArrayList<>();
        times = new ArrayList<>();
        dates = new ArrayList<>();
        repeats = new ArrayList<>();


        getReminders();













        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_naviagtion);

        bottomNavigationView.setSelectedItemId(R.id.reminders);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId())
                {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.reminders:
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
                                    descriptions.add(description);
                                    dates.add(date);
                                    times.add(time);
                                    repeats.add(repeat);
                                    Log.i("ccc",descriptions.get(i));

                                    //create strings
                                    d = new String[descriptions.size()];
                                     t = new String[descriptions.size()];
                                     da = new String[descriptions.size()];
                                     r = new String[descriptions.size()];

                                    for(int j = 0; j < dates.size();j++)
                                    {
                                        d[j] = descriptions.get(j);
                                        t[j] = times.get(j);
                                        da[j] = dates.get(j);
                                        r[j] = repeats.get(j);
                                        Log.i("ccc",d[j]);
                                        Log.i("ccc",descriptions.get(j));
                                    }

                                }



                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ReminderActivity.this, "ErrorJ " + e.toString(), Toast.LENGTH_SHORT).show();
                            Log.i("ErrorJ", e + "" );


                        }
                        if(descriptions.size() > 0)
                        {
                            rv = findViewById(R.id.listreminders);
                            rv.setLayoutManager(new LinearLayoutManager(ReminderActivity.this));

                            adapter = new Adapter(ReminderActivity.this,d,t,da,r);
                            rv.setAdapter(adapter);

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ReminderActivity.this, "ErrorV " + error.toString(), Toast.LENGTH_SHORT).show();
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
