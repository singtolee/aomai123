package com.aomai123.aomai123.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nang Juann on 2/12/2016.
 */
public class Session {
    SharedPreferences sp;

    Editor editor;
    Context context;

    int PRIVATE_MODE = 0;

    private static final String SP_NAME = "UserLogin";
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    public Session(Context context){
        this.context = context;
        sp = context.getSharedPreferences(SP_NAME, PRIVATE_MODE);
        editor = sp.edit();
    }

    public void setLogin(boolean isLoggedIn){
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.commit();
    }

    public boolean isLoggedIn(){
        return sp.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public void saveUsernamePsw (int user_ID, int balance, int points, String firstname, String lastname, String email, String password, String phone, String building, String floor, String room, String company){
        editor.putInt("user_ID", user_ID);
        editor.putInt("balance",balance);
        editor.putInt("points", points);
        editor.putString("firstname", firstname);
        editor.putString("lastname", lastname);
        editor.putString("email", email);
        editor.putString("password", password);
        editor.putString("phone", phone);
        editor.putString("building", building);
        editor.putString("floor", floor);
        editor.putString("room", room);
        editor.putString("company", company);
        editor.commit();
    }

    public Map<String, String> readUserPsw(){
        Map<String, String> uandp = new HashMap<String,String>();

        uandp.put("email", sp.getString("email", ""));
        uandp.put("password", sp.getString("password", ""));
        return uandp;
    }
    public int getUserID(){
        return sp.getInt("user_ID", 0);
    }
    public String getName(){
        return sp.getString("firstname","")+" "+sp.getString("lastname","");
    }
    public String getUserAddress(){
        return "Room "+sp.getString("room","")+" ,Floor"+sp.getString("floor","")+" ,"+sp.getString("building","");
    }

    public String getPhone(){
        return sp.getString("phone","");
    }
    public String getCompany(){
        return sp.getString("company","");
    }
    public String getBuilding(){
        return sp.getString("building","");
    }
    public String getFloor(){
        return sp.getString("floor","");
    }
    public String getRoom(){
        return sp.getString("room","");
    }

    public int getBalance(){
        return sp.getInt("balance", 0);
    }
    public int getPoint(){
        return sp.getInt("points", 0);
    }

    public void setBalance(int balance){
        editor.putInt("balance",balance);
        editor.commit();
    }
    public void setPoints(int points){
        editor.putInt("points", points);
        editor.commit();
    }
    public void clearUserInfo(){
        editor.putInt("user_ID", 0);
        editor.putInt("balance",0);
        editor.putInt("points", 0);
        editor.putString("firstname", "");
        editor.putString("lastname", "");
        editor.putString("phone", "");
        editor.putString("building", "");
        editor.putString("floor", "");
        editor.putString("room", "");
        editor.putString("company", "");
        editor.commit();
    }

    public void clearUserAddress(){
        editor.putString("phone", "");
        editor.putString("building", "");
        editor.putString("floor", "");
        editor.putString("room", "");
        editor.putString("company", "");
        editor.commit();
    }

    public void updateUserAddress(String b,String f, String r, String c, String p){
        editor.putString("phone", p);
        editor.putString("building", b);
        editor.putString("floor", f);
        editor.putString("room", r);
        editor.putString("company", c);
        editor.commit();
    }


}
