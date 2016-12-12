package com.aomai123.aomai123;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.aomai123.aomai123.Extra.Url_SHOP;
import com.aomai123.aomai123.callbacks.BuildingsLoadedListener;
import com.aomai123.aomai123.database.Session;
import com.aomai123.aomai123.network.VolleySingleton;
import com.aomai123.aomai123.task.TaskLoadBuildings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class editAddress extends AppCompatActivity implements Spinner.OnItemSelectedListener, BuildingsLoadedListener {
    private ProgressDialog pDialog;
    private Session session;
    private Button btnSubmitAddress,btngoback;
    private Spinner buildingList;
    private NumberPicker chooseFloor;
    private EditText room;
    private EditText company;
    private EditText phone;
    private ArrayList<String> buildings = new ArrayList<>();
    public String office;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_address);

        findViewById(R.id.editAddressLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();

        chooseFloor = (NumberPicker)findViewById(R.id.numP_floor);
        chooseFloor.setMaxValue(50);
        chooseFloor.setMinValue(1);
        room = (EditText)findViewById(R.id.room);
        company = (EditText)findViewById(R.id.company);
        phone = (EditText)findViewById(R.id.phone);

        session = new Session(getApplicationContext());
        if(session.isLoggedIn()){
            if(session.getFloor()!=""){
                chooseFloor.setValue(Integer.parseInt(session.getFloor()));
            }
            if(session.getRoom()!=""){
                room.setText(session.getRoom());
            }
            if(session.getCompany()!=""){
                company.setText(session.getCompany());
            }
            if(session.getPhone()!=""){
                phone.setText(session.getPhone());
            }
        }
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        buildingList = (Spinner)findViewById(R.id.spinner_building);
        buildingList.setOnItemSelectedListener(this);

        if(buildings.isEmpty()){
            new TaskLoadBuildings(this).execute();
        }
        //ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, buildings);
        // Drop down layout style - list view with radio button
        //dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //buildingList.setAdapter(dataAdapter);
        btnSubmitAddress = (Button)findViewById(R.id.submitAdress);
        btngoback = (Button)findViewById(R.id.cancel_submitAdress);
        btngoback.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("pos",2);
                startActivity(i);
                finish();
            }
        });

        btnSubmitAddress.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if(session.isLoggedIn()){
                    updateAddress();
                    //Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    //startActivity(i);
                    //finish();
                }else {
                    Toast.makeText(getApplicationContext(),R.string.Pleaselogin,Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
        office = item;
        //Toast.makeText(parent.getContext(), item+" Selected", Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onBuildingsLoaded(ArrayList<String> listBuildings){
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listBuildings);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        buildingList.setAdapter(dataAdapter);
        if(session.getBuilding()!=""){
            int p = dataAdapter.getPosition(session.getBuilding());
            buildingList.setSelection(p);
        }

    }

    public void updateAddress(){
        final String b = office;
        final String f = Integer.toString(chooseFloor.getValue());
        final String r = room.getText().toString().trim();
        final String c = company.getText().toString().trim();
        final String p = phone.getText().toString().trim();
        final String userID = Integer.toString(session.getUserID());

        if(!f.isEmpty() &&!r.isEmpty()){
            pDialog.setMessage("Updating your address ...");
            showDialog();

            StringRequest strReq = new StringRequest(Request.Method.POST,
                    Url_SHOP.URL_UPDATING_ADDRESS, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    hideDialog();
                    try {
                        JSONObject jObj = new JSONObject(response);
                        boolean error = jObj.getBoolean("error");
                        if (!error) {
                            Toast.makeText(getApplicationContext(), R.string.Addressupdated, Toast.LENGTH_LONG).show();
                            session.clearUserAddress();
                            JSONObject yhaddress = jObj.getJSONObject("userAddress");
                            String mobile = yhaddress.getString("phone");
                            String office = yhaddress.getString("building");
                            String flr = yhaddress.getString("floor");
                            String roomNo = yhaddress.getString("room");
                            String company = yhaddress.getString("company");
                            session.updateUserAddress(office,flr,roomNo,company,mobile);

                            //Intent i = new Intent(getApplicationContext(),MainActivity.class);
                            //startActivity(i);
                            //finish();
                        } else {
                            String errorMsg = jObj.getString("error_msg");
                            Toast.makeText(getApplicationContext(),
                                    errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    handleVolleyError(error);
                    hideDialog();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting params to register url
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("userID", userID);
                    params.put("building",b);
                    params.put("floor",f);
                    params.put("room",r);
                    params.put("company",c);
                    params.put("phone",p);

                    return params;
                }

            };
            requestQueue.add(strReq);

        }else {
            Toast.makeText(getApplicationContext(), R.string.PleaseInputAddress, Toast.LENGTH_LONG).show();
        }
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }
    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void handleVolleyError(VolleyError error){
        if(error instanceof TimeoutError || error instanceof NoConnectionError){
            Toast.makeText(getApplicationContext(), R.string.timeout_error, Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(), R.string.timeout_error, Toast.LENGTH_LONG).show();
        }
    }

}
