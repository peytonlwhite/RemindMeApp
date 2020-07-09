package com.peytonwhite.reminderapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.GLES10;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

public class ProfileActivity extends AppCompatActivity {

    private String username;
    private TextView usertv;
    private TextView name;
    Button changePassword;
    Button logout;
    private TextView email;
    private Button buttonChangePhoto;
    private ImageView profilePic;
    String getId;
    SessionManager sessionManager;
    private Bitmap bitmap;
    private Bitmap decodedImage;
    final private String URL = "http://www.peytonlwhite.com/blog/changeprofilepic/";
    final private String URLGETPIC = "http://www.peytonlwhite.com/blog/getpic/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();




        HashMap<String,String> user = sessionManager.getUserDetail();
        String username = user.get(sessionManager.USERNAME);
        String nameS = user.get(sessionManager.NAME);
        String emailS = user.get(sessionManager.EMAIL);
        getId = user.get(sessionManager.LOGINID);


        usertv = findViewById(R.id.textViewuser);
        name = findViewById(R.id.textViewnameprofile);
        email = findViewById(R.id.textViewemailprofile);
        buttonChangePhoto = findViewById(R.id.buttonchangephoto);
        profilePic = findViewById(R.id.imageViewprofilepic);
        changePassword = findViewById(R.id.buttonChangePasswordPr);
        logout = findViewById(R.id.buttonlogoutPr);

        int[] maxSize = new int[1];
        GLES10.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, maxSize, 0);


        ///sets image if saved
        getProfilePic();
        /*
        if(!decodedImage.toString().isEmpty())
        {
            profilePic.setImageBitmap(decodedImage);
        }

         */



        usertv.setText(username);
        name.setText(nameS);
        email.setText(emailS);


        buttonChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                chooseFile();


            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager.logout();
            }
        });



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_naviagtion);

        bottomNavigationView.setSelectedItemId(R.id.profile);

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
                        startActivity(new Intent(getApplicationContext(),ReminderActivity.class));
                        overridePendingTransition(0,0);
                        return true;


                    case R.id.profile:
                        return true;






                }
                return false;
            }
        });


    }

    private void chooseFile()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),1);
    }
    private void getProfilePic()
    {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLGETPIC,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.i("ResponsePic", response );
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("pic");
                            String success = jsonObject.getString("success");

                            if (success.equals("1")) {
                                JSONObject object = jsonArray.getJSONObject(0);
                                String bit = object.getString("photo").trim();

                                byte[] decodedString = Base64.decode(bit, Base64.DEFAULT);
                                decodedImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);




                                profilePic.setImageBitmap(decodedImage);

                                Log.i("pic",bit);

                               // Toast.makeText(ProfileActivity.this, "Profile Pic Set", Toast.LENGTH_SHORT).show();


                            }
                        } catch (JSONException e)
                        {
                            e.printStackTrace();

                            Toast.makeText(ProfileActivity.this, "Try Again" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(ProfileActivity.this, "Try Again" + error.toString(), Toast.LENGTH_SHORT).show();

                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();

                params.put("loginId",getId);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                profilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            UploadPicture(getId,getStringImage(bitmap));

        }
    }

    private void UploadPicture(final String id, final String photo)
    {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.i("ResponseA", response );
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");


                            if (success.equals("1")) {

                                Toast.makeText(ProfileActivity.this, "Profile Pic Set", Toast.LENGTH_SHORT).show();


                            }
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(ProfileActivity.this, "Try Again" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Try Again" + error.toString(), Toast.LENGTH_SHORT).show();

                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();

                params.put("loginId",getId);
                params.put("photo",photo);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public String getStringImage(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);

        byte[] imageByteArray = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageByteArray,Base64.DEFAULT);

        return encodedImage;
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
                sessionManager.logout();
                return true;

            case R.id.changePassword:
                Intent intent = new Intent(getApplicationContext(),ForgotPasswordActivity.class);
                startActivity(intent);
                return true;


        }
        return super.onOptionsItemSelected(item);
    }
}
