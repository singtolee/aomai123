package com.aomai123.aomai123.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aomai123.aomai123.API.ApiKey;
import com.aomai123.aomai123.R;
import com.aomai123.aomai123.fg_cart;
import com.aomai123.aomai123.network.VolleySingleton;
import com.aomai123.aomai123.obj.Cart;

import java.util.ArrayList;

/**
 * Created by Nang Juann on 1/24/2016.
 */
public class AdapterCart extends RecyclerView.Adapter<AdapterCart.ViewHolderCart>{
    private ArrayList<Cart> pListCarts = new ArrayList<>();
    private LayoutInflater pInflater;
    private VolleySingleton pVolleySingleton;
    public AdapterCart(Context context) {
        pInflater = LayoutInflater.from(context);
        pVolleySingleton = VolleySingleton.getInstance();
    }

    public void setCarts(ArrayList<Cart> listCarts) {
        this.pListCarts = listCarts;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolderCart onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = pInflater.inflate(R.layout.cart, parent, false);
        ViewHolderCart viewHolder = new ViewHolderCart(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolderCart holder, final int position) {
        final Cart currentProduct = pListCarts.get(position);
        //holder.productID.setText(String.valueOf(currentProduct.getPid()));
        holder.productName.setText(currentProduct.getName());
        //holder.productPrice.setText(String.valueOf(currentProduct.getPrice()));
        holder.productNum.setText(String.valueOf(currentProduct.getNum()));
        holder.productTotal.setText(String.valueOf(currentProduct.getPrice() * currentProduct.getNum())+"฿");
        holder.premove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pid = currentProduct.getPid();
                ApiKey.getWritableDatabase().deletecartitem(pid);
                pListCarts.remove(position);
                notifyDataSetChanged();
                //notifyItemRemoved(position); I have to compare the difference.
                fg_cart.itemSum.setText(String.valueOf(getItemSum()));
                fg_cart.totalAmount.setText(String.valueOf(getTotalAmount()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return pListCarts.size();
    }

    static class ViewHolderCart extends RecyclerView.ViewHolder {
        TextView productName;
        TextView productID;
        TextView productPrice;
        TextView productNum;
        TextView productTotal;
        ImageView premove;
        //ImageView productRemove;

        public ViewHolderCart(View itemView) {
            super(itemView);
            //productID = (TextView) itemView.findViewById(R.id.cart_Product_ID);
            productName = (TextView) itemView.findViewById(R.id.cart_Product);
            //productPrice = (TextView) itemView.findViewById(R.id.cart_Product_price);
            productNum = (TextView) itemView.findViewById(R.id.cart_Product_num);
            productTotal = (TextView) itemView.findViewById(R.id.cart_Product_total);
            //productRemove = (ImageView) itemView.findViewById(R.id.remove_from_cart);
            premove = (ImageView)itemView.findViewById(R.id.remove);
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
}
