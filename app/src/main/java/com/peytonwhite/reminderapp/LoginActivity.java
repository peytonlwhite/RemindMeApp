package com.peytonwhite.reminderapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity
{

    private Button buttonregister;
    private Button buttonlogin;
    private Button forgot;
    private EditText username;
    private EditText password;
    private static String URL_LOGIN = "http://www.peytonlwhite.com/blog/login/";
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        buttonregister = findViewById(R.id.buttonregister);
        buttonlogin = findViewById(R.id.buttonlogin);
        username = findViewById(R.id.edittextusername);
        password = findViewById(R.id.edittextpassword);
        forgot = findViewById(R.id.buttonforgotpassword);


        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentFp = new Intent(getBaseContext(),ForgotPasswordActivity.class);
                startActivity(intentFp);

            }
        });




        buttonregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(),RegisterActivity.class);
                startActivity(intent);
            }
        });

        buttonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 String usernametxt = username.getText().toString().trim();
                 String passwordtxt = password.getText().toString().trim();

                if(!usernametxt.isEmpty() || !passwordtxt.isEmpty())
                {
                    checkLogin(usernametxt,passwordtxt);
                }
                else
                {
                    username.setError("Please enter username");
                    password.setError("Please enter password");
                }

            }
        });



    }


    private void checkLogin(final String username, final String password)
    {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("Responsee", response );
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("login");

                            //for reminders
                           // JSONArray jsonArrayR = jsonObject.getJSONArray("remind");

                            if (success.equals("1")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String uname = object.getString("login_Username").trim();
                                    String name = object.getString("Name").trim();
                                    String email = object.getString("login_Email").trim();
                                    String loginId = object.getString("loginId").trim();
                                    Log.i("loginid", loginId );



                                    Toast.makeText(LoginActivity.this, "Hello \n " + uname, Toast.LENGTH_SHORT).show();

                                    sessionManager.createSession(uname,name,email,loginId);

                                    Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);

                                    startActivity(intent);

                                }



                            }
                            else
                            {
                                Toast.makeText(LoginActivity.this, "Invalid Username/Password ", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "ErrorJ " + e.toString(), Toast.LENGTH_SHORT).show();


                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, "ErrorV " + error.toString(), Toast.LENGTH_SHORT).show();

                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("login_Username",username);
                params.put("login_Password",password);


                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);





    }


}
