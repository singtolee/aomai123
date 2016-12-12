package com.aomai123.aomai123.task;

import android.os.AsyncTask;

import com.android.volley.RequestQueue;

import com.aomai123.aomai123.Extra.ProductUtils;
import com.aomai123.aomai123.callbacks.ProductsLoadedListener;
import com.aomai123.aomai123.network.VolleySingleton;
import com.aomai123.aomai123.obj.Product;

import java.util.ArrayList;

/**
 * Created by Nang Juann on 1/9/2016.
 */
public class TaskLoadProducts extends AsyncTask<Void, Void, ArrayList<Product>>{
    private ProductsLoadedListener myComponent;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;


    public TaskLoadProducts(ProductsLoadedListener myComponent) {

        this.myComponent = myComponent;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }


    @Override
    protected ArrayList<Product> doInBackground(Void... params) {

        ArrayList<Product> listProducts = ProductUtils.loadProducts(requestQueue);
        return listProducts;
    }

    @Override
    protected void onPostExecute(ArrayList<Product> listProducts) {
        if (myComponent != null) {
            myComponent.onProductsLoaded(listProducts);
        }
    }
}
