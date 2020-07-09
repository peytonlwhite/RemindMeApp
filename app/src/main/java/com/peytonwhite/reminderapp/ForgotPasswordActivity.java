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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordActivity extends AppCompatActivity {

    Button changePassword;
    EditText username;
    EditText name;
    EditText email;
    EditText newPassword;
    SessionManager sessionManager;
    private static String URL = "http://www.peytonlwhite.com/blog/changepass";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        username = findViewById(R.id.editTextUserFP);
        name = findViewById(R.id.editTextNameFP);
        email = findViewById(R.id.editTextEmailFP);
        newPassword = findViewById(R.id.editTextNewPassFP);
        changePassword = findViewById(R.id.buttonchangePasswordFP);
        sessionManager = new SessionManager(this);


        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String usernameS = username.getText().toString().trim();
                Log.i("stringv",usernameS);
                String nameS = name.getText().toString().trim();
                String emailS = email.getText().toString().trim();
                String changePasswordS = newPassword.getText().toString().trim();
                if(usernameS.isEmpty() || nameS.isEmpty() || emailS.isEmpty() || changePasswordS.isEmpty())
                {
                    Toast.makeText(ForgotPasswordActivity.this, "Must fill in all fields", Toast.LENGTH_SHORT).show();

                }
                else {
                    ChangePW(usernameS, nameS, emailS, changePasswordS);
                }
            }
        });



    }




    private void ChangePW(final String usernameS, final String nameS, final String emailS, final String changedPasswordS)
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

                                Toast.makeText(ForgotPasswordActivity.this, "Password changed", Toast.LENGTH_SHORT).show();
                                Intent in = new Intent(getApplicationContext(),LoginActivity.class);
                                startActivity(in);


                            }
                            else
                            {
                                Toast.makeText(ForgotPasswordActivity.this, "Invalid Field", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ForgotPasswordActivity.this, "ErrorJ " + e.toString(), Toast.LENGTH_SHORT).show();
                            Log.i("ErrorJ", e + "" );


                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ForgotPasswordActivity.this, "ErrorV " + error.toString(), Toast.LENGTH_SHORT).show();
                        Log.i("ErrorV", error + "" );

                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                Map<String,String> userDetails = sessionManager.getUserDetail();


                params.put("loginId",userDetails.get("LOGINID"));
                params.put("Name",nameS);
                params.put("login_Email",emailS);
                params.put("login_Password",changedPasswordS);
                params.put("login_Username",usernameS);



                Log.i("change",emailS + "  " + nameS + "  " + usernameS + "  "+ changedPasswordS + " " + userDetails.get("LOGINID"));



                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);





    }


}
