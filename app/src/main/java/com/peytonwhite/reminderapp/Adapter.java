package com.peytonwhite.reminderapp;

import android.content.Context;
import android.content.Intent;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private LayoutInflater inflater;
    private String[] descriptions;
    private String[] dates;
    private String[] times;
    private String[] onOrOff;
    private static String URL_LOGIN = "http://www.peytonlwhite.com/blog/changerepeat/";
    private SessionManager sessionManager;




    public Adapter(Context context, String[] descriptions, String[] dates, String[] times,String[] onOrOff) {
        this.inflater = LayoutInflater.from(context);

        this.descriptions = descriptions;
        this.dates = dates;
        this.times = times;
        this.onOrOff = onOrOff;

        this.sessionManager = new SessionManager(inflater.getContext());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        final String descriptionOf = descriptions[i];
        String dateOf = dates[i];
        String timeOf = times[i];
        final String onOrOffOf = onOrOff[i];

        holder.description.setText(descriptionOf);
        holder.date.setText(dateOf);
        holder.time.setText(timeOf);

        if(onOrOffOf.equals("1"))
        {
            holder.aSwitch.setChecked(true);
        }
        else
        {
            holder.aSwitch.setChecked(false);
        }

        holder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                repeatChangeDB(descriptionOf,onOrOffOf);



            }
        });


    }


    @Override
    public int getItemCount() {

        return dates.length;

    }

    public class ViewHolder extends RecyclerView.ViewHolder{


       public TextView description, date, time;
       public Switch aSwitch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //onCLick for viewing alarm info
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(view.getContext(), Details.class);
                    //need info
                    i.putExtra("desc",descriptions[getAdapterPosition()]);
                    i.putExtra("date",dates[getAdapterPosition()]);
                    i.putExtra("time",times[getAdapterPosition()]);


                    view.getContext().startActivity(i);
                }
            });

            description = itemView.findViewById(R.id.textViewDescription);
            date = itemView.findViewById(R.id.textViewDate);
            time = itemView.findViewById(R.id.textViewTime);
            aSwitch = itemView.findViewById(R.id.switchonoroff);




        }
    }

    public void repeatChangeDB(final String des,final String onOrOff)
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

                                Toast.makeText(inflater.getContext(), "Repeat changed", Toast.LENGTH_SHORT).show();


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                           // Toast.makeText(AddReminderActivity.this, "ErrorJ " + e.toString(), Toast.LENGTH_SHORT).show();
                            Log.i("ErrorJ", e + "" );


                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       // Toast.makeText(AddReminderActivity.this, "ErrorV " + error.toString(), Toast.LENGTH_SHORT).show();
                        Log.i("ErrorV", error + "" );

                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                Map<String,String> userDetails = sessionManager.getUserDetail();


                params.put("loginId",userDetails.get("LOGINID"));
                params.put("reminder_Description",des);
                params.put("reminder_repeat",onOrOff);


                return params;
            }
        };
         RequestQueue requestQueue = Volley.newRequestQueue(inflater.getContext());
         requestQueue.add(stringRequest);




    }

}
