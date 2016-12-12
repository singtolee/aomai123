package com.aomai123.aomai123;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.aomai123.aomai123.API.ApiKey;
import com.aomai123.aomai123.Extra.Url_SHOP;
import com.aomai123.aomai123.adapters.AdapterOrder;
import com.aomai123.aomai123.database.Session;
import com.aomai123.aomai123.network.VolleySingleton;
import com.aomai123.aomai123.obj.Order;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class fg_me extends Fragment {
    public static TextView userName,userBalance,userPoints;
    private Button btnLinkToRegister;
    private Button btnLinkToLogin;
    private Button btnLogout;
    private Button btnEditAddress;
    private Button btnTopup;
    private Button btnHistory;
    private Button btnOrder;
    private Button btnRefreshBP;
    private Session session;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private AdapterOrder adapterOrder;
    private RecyclerView recyclerView;
    private ArrayList<Order> orders = new ArrayList<>();
    private ProgressDialog pDialog;


    public fg_me() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fg_me, container, false);
        userName = (TextView) layout.findViewById(R.id.userName);
        userBalance = (TextView) layout.findViewById(R.id.userBalance);
        userPoints = (TextView) layout.findViewById(R.id.userPoints);
        btnLinkToRegister = (Button) layout.findViewById(R.id.goToRegister);
        btnLinkToLogin = (Button) layout.findViewById(R.id.goToLogin);
        btnLogout = (Button) layout.findViewById(R.id.btnlogout);
        btnEditAddress = (Button) layout.findViewById(R.id.btneditaddress);
        btnTopup = (Button) layout.findViewById(R.id.btntopup);
        btnHistory = (Button) layout.findViewById(R.id.btnhistory);
        btnOrder = (Button) layout.findViewById(R.id.btnorder);
        btnRefreshBP = (Button) layout.findViewById(R.id.btnRefresh);
        session = new Session(getContext());
        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.rcyvOrderToday);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterOrder = new AdapterOrder(getActivity());
        adapterOrder.setOrders(orders);
        recyclerView.setAdapter(adapterOrder);
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();

        if(!session.isLoggedIn()){
            logoutUser();
            btnLogout.setVisibility(View.GONE);
            btnEditAddress.setVisibility(View.GONE);
            btnTopup.setVisibility(View.GONE);
            btnHistory.setVisibility(View.GONE);
            btnOrder.setVisibility(View.GONE);
        }else {
            btnLinkToRegister.setVisibility(View.GONE);
            btnLinkToLogin.setVisibility(View.GONE);
            btnEditAddress.setVisibility(View.VISIBLE);
            btnTopup.setVisibility(View.VISIBLE);
            btnHistory.setVisibility(View.VISIBLE);
            btnOrder.setVisibility(View.VISIBLE);
            userName.setText(session.getName());
            userBalance.setText(String.valueOf(session.getBalance()));
            userPoints.setText(String.valueOf(session.getPoint()));
        }

        btnLogout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                logoutUser();
                ApiKey.getWritableDatabase().deleteCarts();
                btnLogout.setVisibility(View.GONE);
                btnLinkToLogin.setVisibility(View.VISIBLE);
                btnLinkToRegister.setVisibility(View.VISIBLE);
                btnEditAddress.setVisibility(View.GONE);
                btnTopup.setVisibility(View.GONE);
                btnHistory.setVisibility(View.GONE);
                btnOrder.setVisibility(View.GONE);
            }
        });

        btnLinkToRegister.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Intent i = new Intent(view.getContext(),register.class);
                startActivity(i);
            }

        });

        btnLinkToLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent i = new Intent(view.getContext(),logIn.class);
                startActivity(i);
            }
        });

        btnEditAddress.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent i = new Intent(view.getContext(),editAddress.class);
                startActivity(i);
            }

        });
        btnHistory.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent i = new Intent(view.getContext(),orderHistory.class);
                startActivity(i);
            }

        });

        btnTopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),topUp.class);
                startActivity(i);
            }
        });
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(session.isLoggedIn()){
                    loadOrderToday();
                }else{
                    Toast.makeText(getContext(), R.string.notlogintyet, Toast.LENGTH_LONG).show();
                }
            }
        });
        btnRefreshBP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(session.isLoggedIn()){
                    updateUserBandP(session.getUserID());
                }
            }
        });

        return layout;
    }

    private void logoutUser(){
        session.setLogin(false);
        session.clearUserInfo();
        userName.setText(session.getName());
        userBalance.setText(String.valueOf(session.getBalance()));
        userPoints.setText(String.valueOf(session.getPoint()));
        orders.clear();
        recyclerView.setVisibility(View.GONE);

    }

    public void loadOrderToday(){
        orders.clear();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Url_SHOP.URL_ORDER_TODAY, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    // Check for error node in json
                    if (!error) {
                        // this user has order
                        JSONArray order = jObj.getJSONArray("order");
                        for(int i=0;i<order.length();i++){
                            JSONObject obj = order.getJSONObject(i);
                            Order o = new Order();
                            o.setOid(obj.getInt("oid"));
                            o.setTotal(obj.getInt("total"));
                            o.setOutstanding(obj.getInt("outstanding"));
                            o.setPm(obj.getString("pm"));
                            o.setDt(obj.getString("dt"));
                            o.setOp(obj.getString("prds"));
                            o.setStatus(obj.getString("status"));
                            o.setDate(obj.getString("date"));
                            orders.add(o);
                        }
                        adapterOrder.setOrders(orders);
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                handleVolleyError(error);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid",String.valueOf(session.getUserID()));
                return params;
            }

        };

        // Adding request to request queue
        requestQueue.add(strReq);

    }
    public void handleVolleyError(VolleyError error){
        if(error instanceof TimeoutError || error instanceof NoConnectionError){
            Toast.makeText(getContext(), R.string.timeout_error, Toast.LENGTH_LONG).show();
        }else if(error instanceof AuthFailureError){
            Toast.makeText(getContext(), "Auth fail", Toast.LENGTH_LONG).show();
        }else if(error instanceof ServerError){
            Toast.makeText(getContext(), "Server error", Toast.LENGTH_LONG).show();
        }else if(error instanceof NetworkError){
            Toast.makeText(getContext(), "NetWork error", Toast.LENGTH_LONG).show();
        }else if(error instanceof ParseError){
            Toast.makeText(getContext(), "Parse error", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(getContext(), "Something Error", Toast.LENGTH_LONG).show();
        }
    }

    public void updateUserBandP(final int uid){
        pDialog.setMessage("Retrieving your Balance and Points...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Url_SHOP.URL_UPDATE_BP, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        JSONObject bp = jObj.getJSONObject("order");
                        int bb = bp.getInt("balance");
                        int pp = bp.getInt("points");
                        session.setPoints(pp);
                        session.setBalance(bb);
                        userBalance.setText(String.valueOf(session.getBalance()));
                        userPoints.setText(String.valueOf(session.getPoint()));

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid", Integer.toString(uid));
                return params;
            }

        };

        // Adding request to request queue
        requestQueue.add(strReq);
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
