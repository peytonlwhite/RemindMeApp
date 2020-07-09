package com.peytonwhite.reminderapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {


    private EditText login_Username, login_Password, login_confirm_password, login_Email,login_name;
    private Button register;
    private static String URL_REGIST = "http://www.peytonlwhite.com/blog/register/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        login_Username = findViewById(R.id.editTextusernamereg);
        login_Password = findViewById(R.id.editTextpasswordreg);
        login_confirm_password = findViewById(R.id.editTextconfirmreg);
        login_Email = findViewById(R.id.editTextEmailreg);
        login_name = findViewById(R.id.editTextNamereg);

        register = findViewById(R.id.buttonreg);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //make sure all fields are placed
                if(checkForEmptyFields())
                {
                    Toast.makeText(RegisterActivity.this,"Must fill in all fields", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Register();
                }

            }
        });


    }

    private boolean checkForEmptyFields()
    {
        if(login_Username.getText().toString().isEmpty() || login_Password.getText().toString().isEmpty()
           || login_confirm_password.getText().toString().isEmpty() || login_Email.getText().toString().isEmpty()
                || login_name.getText().toString().isEmpty())
        {
            return true;
        }
        else
        {
            return false;
        }



    }
    private void Register()
    {
        final String username = this.login_Username.getText().toString().trim();
        final String password = this.login_Password.getText().toString().trim();
        final String confirmPassword = this.login_confirm_password.getText().toString().trim();
        final String email = this.login_Email.getText().toString().trim();
        final String name = this.login_name.getText().toString().trim();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    try{

                        JSONParser jsonParser = new JSONParser();

                        JSONObject jsonObject = new JSONObject( response );
                        String success = jsonObject.getString("success");


                        if(success.equals("1"))
                        {
                            Toast.makeText(RegisterActivity.this,"Register success", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e){
                        e.printStackTrace();
                        Toast.makeText(RegisterActivity.this,"Register errorp" + e.toString(), Toast.LENGTH_SHORT).show();

                    }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterActivity.this,"Register error" + error.toString(), Toast.LENGTH_SHORT).show();

                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("login_Username",username);
                params.put("login_Password",password);
                params.put("login_Email",email);
                params.put("login_confirm_password",confirmPassword);
                params.put("Name",name);


                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);



        }


}
