package com.peytonwhite.reminderapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;

    public static final String PREF_NAME = "LOGIN";
    public static final String LOGIN = "IS_LOGIN";
    public static final String USERNAME = "USERNAME";
    public static final String NAME = "NAME";
    public static final String EMAIL = "EMAIL";
    public static final String LOGINID = "LOGINID";

    public SessionManager(Context context)
    {
        this.context = context;

        sharedPreferences = context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);

        editor = sharedPreferences.edit();
    }

    public void createSession(String username,String name, String email,String loginId)
    {
        editor.putBoolean(LOGIN,true);
        editor.putString(USERNAME,username);
        editor.putString(NAME,name);
        editor.putString(EMAIL,email);
        editor.putString(LOGINID,loginId);
        editor.apply();

    }



    public boolean isLoggin()
    {
        return sharedPreferences.getBoolean(LOGIN,false);
    }

    public void checkLogin()
    {
        if(!this.isLoggin())
        {
            Intent intent = new Intent(context,LoginActivity.class);
            context.startActivity(intent);
            ((ProfileActivity) context).finish();
        }
    }

    public HashMap<String, String> getUserDetail()
    {
        HashMap<String,String> user = new HashMap<>();
        user.put(USERNAME,sharedPreferences.getString(USERNAME,null));
        user.put(NAME,sharedPreferences.getString(NAME,null));
        user.put(EMAIL,sharedPreferences.getString(EMAIL,null));
        user.put(LOGINID,sharedPreferences.getString(LOGINID,null));
        return user;
    }



    public void logout()
    {
        editor.clear();
        editor.commit();
        Intent intent = new Intent(context,LoginActivity.class);
        context.startActivity(intent);
        ((ProfileActivity) context).finish();
    }

}
