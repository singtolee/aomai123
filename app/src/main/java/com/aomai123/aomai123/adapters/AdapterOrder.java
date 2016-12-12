package com.aomai123.aomai123.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageView;
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
import com.android.volley.toolbox.StringRequest;
import com.aomai123.aomai123.Extra.Url_SHOP;
import com.aomai123.aomai123.MainActivity;
import com.aomai123.aomai123.R;
import com.aomai123.aomai123.network.VolleySingleton;
import com.aomai123.aomai123.obj.Order;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Singto on 3/19/2016 AD.
 */
public class AdapterOrder extends RecyclerView.Adapter<AdapterOrder.ViewHolderOrder> {
    private ArrayList<Order> orders = new ArrayList<>();
    private AlertDialog alertDialog = null;
    private AlertDialog.Builder builder = null;
    private LayoutInflater pInflater;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    public AdapterOrder(Context context) {
        pInflater = LayoutInflater.from(context);
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();

    }

    public void setOrders(ArrayList<Order> listOrders) {
        this.orders = listOrders;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolderOrder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = pInflater.inflate(R.layout.order, parent, false);
        ViewHolderOrder viewHolder = new ViewHolderOrder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolderOrder holder, final int position) {
        final Order currentProduct = orders.get(position);
        //Today or Tmr date
        holder.deliverTime.setText(tmr(currentProduct.getDt()));
        holder.totalPrice.setText("Total: "+String.valueOf(currentProduct.getTotal())+"฿");
        holder.outstandingPrice.setText("Cash: "+String.valueOf(currentProduct.getOutstanding())+"฿");
        holder.orderProduct.setText(currentProduct.getOp());
        holder.orderStatus.setText(currentProduct.getStatus().toUpperCase());
        holder.orderRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final int oid = currentProduct.getOid();
                alertDialog = null;
                builder = new AlertDialog.Builder(view.getContext());
                alertDialog = builder.setMessage(R.string.Doyouwanttocancel)
                        .setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final ProgressDialog pDialog = new ProgressDialog(view.getContext());
                                pDialog.setMessage("Submitting your Cancellation request...");
                                if (!pDialog.isShowing()) {
                                    pDialog.show();
                                }
                                StringRequest strReq = new StringRequest(Request.Method.POST,
                                        Url_SHOP.URL_CANCEL_ORDER, new Response.Listener<String>() {

                                    @Override
                                    public void onResponse(String response) {
                                        if (pDialog.isShowing()) {
                                            pDialog.dismiss();
                                        }

                                        try {
                                            JSONObject jObj = new JSONObject(response);
                                            //boolean error = jObj.getBoolean("error");
                                            String errorMsg = jObj.getString("message");
                                            Toast.makeText(view.getContext(),
                                                    errorMsg, Toast.LENGTH_LONG).show();
                                        } catch (JSONException e) {
                                            // JSON error
                                            e.printStackTrace();
                                            Toast.makeText(view.getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }

                                    }
                                }, new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        handleVolleyError(error, view);
                                        if (pDialog.isShowing()) {
                                            pDialog.dismiss();
                                        }

                                    }
                                }) {

                                    @Override
                                    protected Map<String, String> getParams() {
                                        // Posting orderID to cancel
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("oid", Integer.toString(oid));
                                        return params;
                                    }
                                };
                                // Adding request to request queue
                                requestQueue.add(strReq);
                            }
                        }).create();
                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class ViewHolderOrder extends RecyclerView.ViewHolder {
        TextView deliverTime;
        TextView totalPrice;
        TextView outstandingPrice;
        TextView orderProduct;
        TextView orderStatus;
        ImageView orderRemove;

        public ViewHolderOrder(View itemView) {
            super(itemView);
            deliverTime = (TextView) itemView.findViewById(R.id.dt);
            totalPrice = (TextView) itemView.findViewById(R.id.orderTotal);
            outstandingPrice = (TextView) itemView.findViewById(R.id.orderOutstanding);
            orderProduct = (TextView) itemView.findViewById(R.id.orderProduct);
            orderStatus = (TextView) itemView.findViewById(R.id.orderStatus);
            orderRemove = (ImageView)itemView.findViewById(R.id.btnCancelOrder);
        }
    }
    public void handleVolleyError(VolleyError error, View v){
        if(error instanceof TimeoutError || error instanceof NoConnectionError){
            Toast.makeText(v.getContext(), R.string.timeout_error, Toast.LENGTH_LONG).show();
        }else if(error instanceof AuthFailureError){
            Toast.makeText(v.getContext(), "Auth fail", Toast.LENGTH_LONG).show();
        }else if(error instanceof ServerError){
            Toast.makeText(v.getContext(), "Server error", Toast.LENGTH_LONG).show();
        }else if(error instanceof NetworkError){
            Toast.makeText(v.getContext(), "NetWork error", Toast.LENGTH_LONG).show();
        }else if(error instanceof ParseError){
            Toast.makeText(v.getContext(), "Parse error", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(v.getContext(), "Something Error", Toast.LENGTH_LONG).show();
        }
    }

    public String tmr(String date){
        String a;
        SimpleDateFormat sdftime = new SimpleDateFormat("HH:mm");
        SimpleDateFormat sdffull = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
        try{
            Date odfull = sdffull.parse(date);
            Date oddate = sdfdate.parse(date);
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            Date today = sdfdate.parse(sdfdate.format(calendar.getTime()));
            if(oddate.compareTo(today)==0){
                a = "Today "+sdftime.format(odfull);
            }else {
                a="Tomorrow "+sdftime.format(odfull);
            }
        }catch (ParseException e){
            a =date;
        }
        return a;
    }


}
