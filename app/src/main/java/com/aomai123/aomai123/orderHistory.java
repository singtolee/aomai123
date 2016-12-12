package com.aomai123.aomai123;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
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
import com.android.volley.toolbox.StringRequest;
import com.aomai123.aomai123.Extra.Url_SHOP;
import com.aomai123.aomai123.adapters.AdapterHistory;
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

public class orderHistory extends AppCompatActivity {
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private AdapterHistory adapterHistory;
    private RecyclerView recyclerView;
    private ArrayList<Order> orders = new ArrayList<>();
    private Session session;
    private Button goBackMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_history);

        goBackMain = (Button) findViewById(R.id.fromHistoryToMain);
        goBackMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("pos",2);
                startActivity(i);
                finish();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.rcyOrderHistory);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterHistory = new AdapterHistory(orders);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapterHistory);
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        session = new Session(getApplicationContext());
        orders.clear();
        loadOrderHistory();

    }
    public void loadOrderHistory(){
        orders.clear();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Url_SHOP.URL_ORDER_HISTORY, new Response.Listener<String>() {

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
                            //o.setOid(obj.getInt("oid"));
                            o.setTotal(obj.getInt("total"));
                            //o.setOutstanding(obj.getInt("outstanding"));
                            //o.setPm(obj.getString("pm"));
                            o.setDt(obj.getString("dt"));
                            o.setOp(obj.getString("prds"));
                            o.setStatus(obj.getString("status"));
                            o.setDate(obj.getString("date"));
                            orders.add(o);
                        }
                        adapterHistory.notifyDataSetChanged();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
            Toast.makeText(getApplicationContext(), R.string.timeout_error, Toast.LENGTH_LONG).show();
        }else if(error instanceof AuthFailureError){
            Toast.makeText(getApplicationContext(), "Auth fail", Toast.LENGTH_LONG).show();
        }else if(error instanceof ServerError){
            Toast.makeText(getApplicationContext(), "Server error", Toast.LENGTH_LONG).show();
        }else if(error instanceof NetworkError){
            Toast.makeText(getApplicationContext(), "NetWork error", Toast.LENGTH_LONG).show();
        }else if(error instanceof ParseError){
            Toast.makeText(getApplicationContext(), "Parse error", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(getApplicationContext(), "Something Error", Toast.LENGTH_LONG).show();
        }
    }
}
