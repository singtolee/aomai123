package com.aomai123.aomai123.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aomai123.aomai123.R;
import com.aomai123.aomai123.obj.Order;

import java.util.ArrayList;

/**
 * Created by Singto on 3/23/2016 AD.
 */
public class AdapterHistory extends RecyclerView.Adapter<AdapterHistory.ViewHolderHistory> {
    private ArrayList<Order> orders = new ArrayList<>();
    public class ViewHolderHistory extends RecyclerView.ViewHolder {
        TextView dTime;
        TextView tPrice;
        TextView historyProduct;
        TextView oStatus;

        public ViewHolderHistory(View itemView) {
            super(itemView);
            dTime = (TextView) itemView.findViewById(R.id.dtime);
            tPrice = (TextView) itemView.findViewById(R.id.oTotal);
            historyProduct = (TextView) itemView.findViewById(R.id.historyPrds);
            oStatus = (TextView) itemView.findViewById(R.id.oStatus);
        }
    }

    public AdapterHistory(ArrayList<Order> orders) {
        this.orders = orders;
    }

    @Override
    public ViewHolderHistory onCreateViewHolder(ViewGroup parent, int viewType) {
        //View view = pInflater.inflate(R.layout.history, parent, false);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history, parent, false);
        return new ViewHolderHistory(view);
    }
    @Override
    public void onBindViewHolder(final ViewHolderHistory holder, final int position) {
        final Order currentProduct = orders.get(position);
        holder.dTime.setText(currentProduct.getDt());
        //holder.tPrice.setText("Total: "+String.valueOf(currentProduct.getTotal())+R.string.Thai_Baht_sign);
        holder.tPrice.setText(Integer.toString(currentProduct.getTotal())+"฿");
        holder.historyProduct.setText(currentProduct.getOp());
        holder.oStatus.setText(currentProduct.getStatus().toUpperCase());
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
}
