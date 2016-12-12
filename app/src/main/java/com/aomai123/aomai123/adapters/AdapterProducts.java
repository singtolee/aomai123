package com.aomai123.aomai123.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import com.android.volley.toolbox.NetworkImageView;
import com.aomai123.aomai123.API.ApiKey;
import com.aomai123.aomai123.MainActivity;
import com.aomai123.aomai123.R;
import com.aomai123.aomai123.network.VolleySingleton;
import com.aomai123.aomai123.obj.Product;

import java.util.ArrayList;

/**
 * Created by Nang Juann on 1/9/2016.
 */
public class AdapterProducts extends RecyclerView.Adapter<AdapterProducts.ViewHolderProduct>{

    //contains the list of products
    private ArrayList<Product> pListProducts = new ArrayList<>();
    private LayoutInflater pInflater;
    private VolleySingleton pVolleySingleton;
    private ImageLoader pImageLoader;

    public Animation animation = null;

    public AdapterProducts(Context context) {
        pInflater = LayoutInflater.from(context);
        pVolleySingleton = VolleySingleton.getInstance();
        pImageLoader = pVolleySingleton.getImageLoader();
    }

    public void setProducts(ArrayList<Product> listProducts) {
        this.pListProducts = listProducts;
        //update the adapter to reflect the new set of products
        notifyDataSetChanged();
    }

    @Override
    public ViewHolderProduct onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = pInflater.inflate(R.layout.prd_cardview_relative, parent, false);
        ViewHolderProduct viewHolder = new ViewHolderProduct(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolderProduct holder, int position) {
        Product currentProduct = pListProducts.get(position);
        holder.productName.setText(currentProduct.getName());
        holder.productID.setText(String.valueOf(currentProduct.getId()));
        holder.productPrice.setText(String.valueOf(currentProduct.getPrice()));
        holder.productDescription.setText(currentProduct.getDescription());
        //holder.productDescription.setText(Html.fromHtml(currentProduct.getDescription()));
        //holder.productDescription.loadDataWithBaseURL(null,currentProduct.getDescription(),"text/html", "utf-8",null);
        holder.itemNum.setText("0");

        String urlThumnail = currentProduct.getUrlThumbnail();
        //loadImages(urlThumnail, holder);

        pImageLoader = pVolleySingleton.getImageLoader();
        pImageLoader.get(urlThumnail, ImageLoader.getImageListener(holder.productImage, R.mipmap.aomai123, R.mipmap.ic_launcher));
        holder.productImage.setImageUrl(urlThumnail, pImageLoader);

        holder.increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String num = holder.itemNum.getText().toString();
                int n = Integer.parseInt(num);
                n = n + 1;
                holder.itemNum.setText(String.valueOf(n));
            }


        });

        holder.decrease.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View view) {
                String num = holder.itemNum.getText().toString();
                int n = Integer.parseInt(num);
                n = n - 1;
                if (n < 0) {
                    n = 0;
                }
                holder.itemNum.setText(String.valueOf(n));
            }
        });
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ID = Integer.parseInt(holder.productID.getText().toString());
                String name = holder.productName.getText().toString();
                int price = Integer.parseInt(holder.productPrice.getText().toString());
                int num = Integer.parseInt(holder.itemNum.getText().toString());

                if(num>0){
                    if(existID(ID)!=0){
                        //如果购物车已经存在此商品,增加数量;
                        updateItemNum(num,ID);
                        Toast toast = Toast.makeText(view.getContext(), R.string.Addedtocart,Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL,0,0);
                        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                        v.setTextColor(Color.YELLOW);     //设置字体颜色
                        toast.show();
                    }else {
                        ApiKey.getWritableDatabase().insertCarts(name, price, num, ID);
                        //Start a Animation;
                        //animation = AnimationUtils.loadAnimation(view.getContext(),
                               // R.anim.anim_test);
                        //holder.productImage.startAnimation(animation);
                        Toast toast = Toast.makeText(view.getContext(), R.string.Addedtocart,Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL,0,0);
                        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                        v.setTextColor(Color.YELLOW);     //设置字体颜色
                        toast.show();

                    }
                }else {
                    Toast.makeText(view.getContext(),R.string.Num_is_zero, Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void loadImages(String urlThumbnail, final ViewHolderProduct holder) {
        pImageLoader.get(urlThumbnail, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                holder.productImage.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return pListProducts.size();
    }

    static class ViewHolderProduct extends RecyclerView.ViewHolder {

        NetworkImageView productImage;
        TextView productName;
        TextView productID;
        TextView productDescription;
        //WebView productDescription;
        TextView productPrice;

        ImageView increase;
        ImageView decrease;
        TextView itemNum;
        Button btn;

        public ViewHolderProduct(View itemView) {
            super(itemView);
            productImage = (NetworkImageView) itemView.findViewById(R.id.ProductThumbnail);
            productName = (TextView) itemView.findViewById(R.id.ProductName);
            productID = (TextView) itemView.findViewById(R.id.ProductID);
            productDescription = (TextView) itemView.findViewById(R.id.ProductDescription);
            //productDescription = (WebView) itemView.findViewById(R.id.ProductDescription);
            productPrice = (TextView) itemView.findViewById(R.id.ProductPrice);
            increase = (ImageView)itemView.findViewById(R.id.item_number_increase);
            decrease = (ImageView)itemView.findViewById(R.id.item_number_decrease);
            itemNum = (TextView)itemView.findViewById(R.id.item_number);
            btn = (Button)itemView.findViewById(R.id.btn_add_cart);
        }
    }

    public int existID(int ID){
        return ApiKey.getWritableDatabase().findByIDinCart(ID);

    }
    public void updateItemNum(int num, int ID){
        ApiKey.getWritableDatabase().increaseItemNum(num, ID);

    }
}
