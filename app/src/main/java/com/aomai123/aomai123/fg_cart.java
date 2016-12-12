package com.aomai123.aomai123;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.aomai123.aomai123.API.ApiKey;
import com.aomai123.aomai123.Extra.Url_SHOP;
import com.aomai123.aomai123.adapters.AdapterCart;
import com.aomai123.aomai123.callbacks.CartsLoadedListener;
import com.aomai123.aomai123.database.Session;
import com.aomai123.aomai123.logging.L;
import com.aomai123.aomai123.network.VolleySingleton;
import com.aomai123.aomai123.obj.Cart;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 */
public class fg_cart extends Fragment implements CartsLoadedListener{

    private ProgressDialog pDialog;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private ArrayList<Cart> pListCarts = new ArrayList<>();
    private AdapterCart mAdapter;
    private RecyclerView mRecyclerProducts;
    public static TextView itemSum, totalAmount;
    private Button btnCheckOut;
    private Session session;
    private RadioGroup rgTime, rgPay;
    private RadioButton rbTime12,rbTime3,rbPayCash,rbUseBanlance,rbRedeemPoints,rbMix;
    public int oID;

    private AlertDialog alertDialog = null;
    private AlertDialog.Builder builder = null;


    public fg_cart() {
        // Required empty public constructor
    }

    public static fg_cart newInstance(String param1, String param2) {
        fg_cart fragment = new fg_cart();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fg_cart, container, false);
        rgTime = (RadioGroup) layout.findViewById(R.id.radioGroup_deliveryTime);
        rgPay = (RadioGroup) layout.findViewById(R.id.radioGroup_pay);
        rbTime12 = (RadioButton) layout.findViewById(R.id.radio12AM);
        rbTime12.setChecked(true);
        rbTime3 = (RadioButton) layout.findViewById(R.id.radio3PM);
        rbPayCash = (RadioButton) layout.findViewById(R.id.radio_cash);
        rbPayCash.setChecked(true);
        rbUseBanlance = (RadioButton) layout.findViewById(R.id.radio_Balance);
        rbRedeemPoints = (RadioButton) layout.findViewById(R.id.radio_redeemPoints);
        rbMix = (RadioButton) layout.findViewById(R.id.radio_mix);

        session = new Session(getContext());

        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();

        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);

        btnCheckOut = (Button) layout.findViewById(R.id.checkout);

        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(session.isLoggedIn()){
                    final String paycash = "C";
                    final String deductBalance = "B";
                    final String cashAndBalance = "M";
                    final String redeemPoints = "P";

                    final String lunch = "12:00";
                    final String gate12 = "11:30";
                    final String afternoon = "15:00";
                    final String gate15 = "14:30";
                    final String uid = Integer.toString(session.getUserID());
                    pListCarts = ApiKey.getWritableDatabase().readCarts();

                    int n =getItemSum();
                    final String pNo = Integer.toString(n);

                    final int money = Integer.parseInt(totalAmount.getText().toString());
                    final String total = Integer.toString(money);

                    final String reward = Integer.toString(money/20);

                    final String zero = "0";

                    final int uB = session.getBalance();
                    final int uP = session.getPoint();

                    if(n>0){
                        if(session.getBuilding()!=""&&session.getFloor()!=""&&session.getRoom()!=""){
                            switch (rgPay.getCheckedRadioButtonId()){
                                case R.id.radio_cash:
                                    switch (rgTime.getCheckedRadioButtonId()){
                                        case R.id.radio12AM:
                                            if(comparetime(gate12)){
                                                alertDialog = null;
                                                builder = new AlertDialog.Builder(getContext());
                                                alertDialog = builder.setMessage(R.string.ConfirmToProceed)
                                                        .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {

                                                            }
                                                        })
                                                        .setPositiveButton(R.string.Confirm, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                //submitOrder(uid, paycash, lunch, pNo, total, total, zero, zero, reward);
                                                                submitJsonOrder(uid, paycash, lunch, pListCarts);
                                                            }
                                                        }).create();
                                                alertDialog.show();
                                            }else {
                                                alertDialog = null;
                                                builder = new AlertDialog.Builder(getContext());
                                                alertDialog = builder.setMessage(R.string.lateOrder)
                                                        .setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {

                                                            }
                                                        })
                                                        .setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                submitJsonOrder(uid, paycash, lunch, pListCarts);
                                                            }
                                                        }).create();
                                                alertDialog.show();
                                            }
                                            break;
                                        case R.id.radio3PM:
                                            if(comparetime(gate15)){
                                                alertDialog = null;
                                                builder = new AlertDialog.Builder(getContext());
                                                alertDialog = builder.setMessage(R.string.ConfirmToProceed)
                                                        .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {

                                                            }
                                                        })
                                                        .setPositiveButton(R.string.Confirm, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                submitJsonOrder(uid, paycash, afternoon, pListCarts);
                                                            }
                                                        }).create();
                                                alertDialog.show();
                                            }else {
                                                alertDialog = null;
                                                builder = new AlertDialog.Builder(getContext());
                                                alertDialog = builder.setMessage(R.string.lateOrder)
                                                        .setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {

                                                            }
                                                        })
                                                        .setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                submitJsonOrder(uid, paycash, afternoon, pListCarts);
                                                            }
                                                        }).create();
                                                alertDialog.show();
                                            }
                                            break;
                                    }
                                    break;
                                case R.id.radio_Balance:
                                    if(uB>=money){
                                        switch (rgTime.getCheckedRadioButtonId()){
                                            case R.id.radio12AM:
                                                if(comparetime(gate12)){
                                                    alertDialog = null;
                                                    builder = new AlertDialog.Builder(getContext());
                                                    alertDialog = builder.setMessage(R.string.ConfirmToProceed)
                                                            .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                }
                                                            })
                                                            .setPositiveButton(R.string.Confirm, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    //submitOrder(uid, deductBalance, lunch, pNo, total, zero, total, zero, reward);
                                                                    submitJsonOrder(uid, deductBalance, lunch, pListCarts);
                                                                }
                                                            }).create();
                                                    alertDialog.show();
                                                }else {
                                                    alertDialog = null;
                                                    builder = new AlertDialog.Builder(getContext());
                                                    alertDialog = builder.setMessage(R.string.lateOrder)
                                                            .setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                }
                                                            })
                                                            .setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    //submitOrder(uid, deductBalance, lunch, pNo, total, zero, total, zero, reward);
                                                                    submitJsonOrder(uid, deductBalance, lunch, pListCarts);
                                                                }
                                                            }).create();
                                                    alertDialog.show();
                                                }
                                                break;
                                            case R.id.radio3PM:
                                                if(comparetime(gate15)){
                                                    alertDialog = null;
                                                    builder = new AlertDialog.Builder(getContext());
                                                    alertDialog = builder.setMessage(R.string.ConfirmToProceed)
                                                            .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                }
                                                            })
                                                            .setPositiveButton(R.string.Confirm, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    submitJsonOrder(uid, deductBalance, afternoon, pListCarts);
                                                                }
                                                            }).create();
                                                    alertDialog.show();
                                                }else {
                                                    alertDialog = null;
                                                    builder = new AlertDialog.Builder(getContext());
                                                    alertDialog = builder.setMessage(R.string.lateOrder)
                                                            .setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                }
                                                            })
                                                            .setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    submitJsonOrder(uid, deductBalance, afternoon, pListCarts);
                                                                }
                                                            }).create();
                                                    alertDialog.show();
                                                }
                                                break;
                                        }
                                    }else {
                                        Toast.makeText(v.getContext(),R.string.BalanceNotEnough,Toast.LENGTH_LONG).show();
                                    }
                                    break;
                                case R.id.radio_mix:
                                    if(uB>0){
                                        switch (rgTime.getCheckedRadioButtonId()){
                                            case R.id.radio12AM:
                                                if(comparetime(gate12)){
                                                    alertDialog = null;
                                                    builder = new AlertDialog.Builder(getContext());
                                                    alertDialog = builder.setMessage(R.string.ConfirmToProceed)
                                                            .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                }
                                                            })
                                                            .setPositiveButton(R.string.Confirm, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    submitJsonOrder(uid, cashAndBalance, lunch, pListCarts);
                                                                }
                                                            }).create();
                                                    alertDialog.show();
                                                }else {
                                                    alertDialog = null;
                                                    builder = new AlertDialog.Builder(getContext());
                                                    alertDialog = builder.setMessage(R.string.lateOrder)
                                                            .setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                }
                                                            })
                                                            .setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    submitJsonOrder(uid, cashAndBalance, lunch, pListCarts);
                                                                }
                                                            }).create();
                                                    alertDialog.show();
                                                }
                                                break;
                                            case R.id.radio3PM:
                                                if(comparetime(gate15)){
                                                    alertDialog = null;
                                                    builder = new AlertDialog.Builder(getContext());
                                                    alertDialog = builder.setMessage(R.string.ConfirmToProceed)
                                                            .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                }
                                                            })
                                                            .setPositiveButton(R.string.Confirm, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    submitJsonOrder(uid, cashAndBalance, afternoon, pListCarts);
                                                                }
                                                            }).create();
                                                    alertDialog.show();
                                                }else {
                                                    alertDialog = null;
                                                    builder = new AlertDialog.Builder(getContext());
                                                    alertDialog = builder.setMessage(R.string.lateOrder)
                                                            .setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                }
                                                            })
                                                            .setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    submitJsonOrder(uid, cashAndBalance, afternoon, pListCarts);
                                                                }
                                                            }).create();
                                                    alertDialog.show();
                                                }
                                                break;
                                        }

                                    }else {
                                        Toast.makeText(v.getContext(),R.string.BalanceIsZero,Toast.LENGTH_LONG).show();
                                    }
                                    break;
                                case R.id.radio_redeemPoints:
                                    if(uP>=money){
                                        switch (rgTime.getCheckedRadioButtonId()){
                                            case R.id.radio12AM:
                                                if(comparetime(gate12)){
                                                    alertDialog = null;
                                                    builder = new AlertDialog.Builder(getContext());
                                                    alertDialog = builder.setMessage(R.string.ConfirmToProceed)
                                                            .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                }
                                                            })
                                                            .setPositiveButton(R.string.Confirm, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    submitJsonOrder(uid, redeemPoints, lunch, pListCarts);
                                                                }
                                                            }).create();
                                                    alertDialog.show();
                                                }else {
                                                    alertDialog = null;
                                                    builder = new AlertDialog.Builder(getContext());
                                                    alertDialog = builder.setMessage(R.string.lateOrder)
                                                            .setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                }
                                                            })
                                                            .setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    submitJsonOrder(uid, redeemPoints, lunch, pListCarts);
                                                                }
                                                            }).create();
                                                    alertDialog.show();
                                                }

                                                break;
                                            case R.id.radio3PM:
                                                if(comparetime(gate15)){
                                                    alertDialog = null;
                                                    builder = new AlertDialog.Builder(getContext());
                                                    alertDialog = builder.setMessage(R.string.ConfirmToProceed)
                                                            .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                }
                                                            })
                                                            .setPositiveButton(R.string.Confirm, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    submitJsonOrder(uid, redeemPoints, afternoon, pListCarts);
                                                                }
                                                            }).create();
                                                    alertDialog.show();
                                                }else {
                                                    alertDialog = null;
                                                    builder = new AlertDialog.Builder(getContext());
                                                    alertDialog = builder.setMessage(R.string.lateOrder)
                                                            .setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                }
                                                            })
                                                            .setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    submitJsonOrder(uid, redeemPoints, afternoon, pListCarts);
                                                                }
                                                            }).create();
                                                    alertDialog.show();
                                                }
                                                break;
                                        }
                                    }else{
                                        Toast.makeText(v.getContext(),R.string.PointNotEnough, Toast.LENGTH_LONG).show();
                                    }
                                    break;
                            }
                        } else {
                            Toast.makeText(v.getContext(),R.string.FillAddressFirst,Toast.LENGTH_LONG).show();
                            //Intent go to editAddress page
                            Intent intent = new Intent(getContext(),
                                    editAddress.class);
                            startActivity(intent);
                        }

                    }else {
                        Toast.makeText(v.getContext(),R.string.nothingIntheCart,Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(v.getContext(),R.string.please_login,Toast.LENGTH_LONG).show();
                }
            }
        });

        itemSum = (TextView)layout.findViewById(R.id.item_sum);
        totalAmount = (TextView)layout.findViewById(R.id.total);
        mRecyclerProducts = (RecyclerView) layout.findViewById(R.id.listCarts);
        //set the layout manager before trying to display data
        mRecyclerProducts.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new AdapterCart(getActivity());
        mRecyclerProducts.setAdapter(mAdapter);
        return layout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCartsLoaded(ArrayList<Cart> listCarts) {
        L.m("fg_cart: onCartsLoaded Fragment");
        mAdapter.setCarts(listCarts);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            pListCarts = ApiKey.getWritableDatabase().readCarts();
            mAdapter.setCarts(pListCarts);
            itemSum.setText(String.valueOf(getItemSum()));
            totalAmount.setText(String.valueOf(getTotalAmount()));
        }
    }
    public int getItemSum(){
        pListCarts = ApiKey.getWritableDatabase().readCarts();
        int i=0, itemSum=0;
        for(i=0;i<pListCarts.size();i++){
            itemSum=itemSum+pListCarts.get(i).getNum();
        }
        return itemSum;
    }
    public int getTotalAmount(){
        pListCarts = ApiKey.getWritableDatabase().readCarts();
        int i=0, total=0;
        for(i=0;i<pListCarts.size();i++){
            total=total+pListCarts.get(i).getPrice()*pListCarts.get(i).getNum();
        }
        return total;
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
            Toast.makeText(getContext(), R.string.timeout_error, Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getContext(), R.string.timeout_error, Toast.LENGTH_LONG).show();
        }
    }
    public void submitJsonOrder(String uid, String pm, String dt, ArrayList<Cart> op){
        JSONObject job =new JSONObject();
        try{
            job.put("UID", uid);
            job.put("PM", pm);
            job.put("DT", dt);
            JSONArray prds = new JSONArray();
            for(int i = 0; i<op.size();i++){
                JSONObject p = new JSONObject();
                p.put("PID", op.get(i).getPid());
                p.put("PNO", op.get(i).getNum());
                prds.put(p);
            }
            job.put("PRODUCTS",prds);
        }catch (JSONException ex){

        }
        pDialog.setMessage("Submitting your order ...");
        showDialog();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Url_SHOP.URL_SUBMIT_ORDER, job, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideDialog();
                try {
                    boolean error = response.getBoolean("error");
                    if(!error){
                        //if order successfully submitted, refresh Balance and points and use Toast to inform user.
                        JSONObject ubp = response.getJSONObject("UBP");
                        int bl = ubp.getInt("BALANCE");
                        int jf = ubp.getInt("POINTS");
                        session.setBalance(bl);
                        session.setPoints(jf);
                        //fg_me.userBalance.setText(Integer.valueOf(session.getBalance()));
                        //fg_me.userPoints.setText(Integer.valueOf(session.getPoint()));
                        Toast.makeText(getContext(),
                                R.string.OrderSubmitted, Toast.LENGTH_SHORT).show();
                    }else{
                        //let user try again.
                        String errorMsg = response.getString("error_msg");
                        Toast.makeText(getContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                handleVolleyError(error);
            }
        });
        requestQueue.add(jsonObjectRequest);

    }

    public boolean comparetime(String time){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        boolean sta;
        try{
            Date gate = sdf.parse(time);
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            Date ctime = sdf.parse(sdf.format(calendar.getTime()));
            if(ctime.compareTo(gate)>0){
                sta = false;
            }else {
                sta = true;
            }
        }catch (ParseException e){
            sta = false;
        }
        return sta;
    }
}