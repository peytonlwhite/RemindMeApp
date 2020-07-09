package com.peytonwhite.reminderapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class Details extends AppCompatActivity {

    TextView date;
    TextView time;
    TextView desc;
    Button delete;
    private static String URL = "http://www.peytonlwhite.com/blog/deletereminder/";
    private SessionManager sessionManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        date = findViewById(R.id.textViewdate);
        time = findViewById(R.id.textViewtime);
        desc = findViewById(R.id.textViewdesc);
        delete = findViewById(R.id.buttondelete);
        sessionManager = new SessionManager(this);

        Intent i = getIntent();
        final String dateString = i.getStringExtra("date");
        final String timeString = i.getStringExtra("time");
        final String descString = i.getStringExtra("desc");

        date.append(timeString);
        time.append(dateString);
        desc.setText(descString);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteReminderFromDb(descString, dateString,timeString);
            }
        });









    }


    public void deleteReminderFromDb(final String desc, final String date, final String time)
    {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("ResponseA", response );
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")) {

                                Toast.makeText(Details.this, "Reminder Deleted", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getBaseContext(),ReminderActivity.class);
                                startActivity(intent);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Details.this, "ErrorJ " + e.toString(), Toast.LENGTH_SHORT).show();
                            Log.i("ErrorJ", e + "" );


                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Details.this, "ErrorV " + error.toString(), Toast.LENGTH_SHORT).show();
                        Log.i("ErrorV", error + "" );

                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                Map<String,String> userDetails = sessionManager.getUserDetail();


                params.put("loginId",userDetails.get("LOGINID"));
                params.put("reminder_Description",desc);
                params.put("reminder_date",date);
                params.put("reminder_time",time);


                Log.i("stuff",desc + " " + date + " " + time + userDetails.get("LOGINID"));


                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);





    }


}
