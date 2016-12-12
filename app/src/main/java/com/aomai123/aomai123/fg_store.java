package com.aomai123.aomai123;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.aomai123.aomai123.API.ApiKey;
import com.aomai123.aomai123.adapters.AdapterProducts;
import com.aomai123.aomai123.callbacks.ProductsLoadedListener;
import com.aomai123.aomai123.logging.L;
import com.aomai123.aomai123.obj.Product;
import com.aomai123.aomai123.task.TaskLoadProducts;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class fg_store extends Fragment implements ProductsLoadedListener, SwipeRefreshLayout.OnRefreshListener{
    private static final String STATE_PRODUCTS = "state_products";
    private ArrayList<Product> pListProducts = new ArrayList<>();
    private AdapterProducts mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerProducts;
    private TextView mTextError;
    public fg_store() {
        // Required empty public constructor
    }

    public static fg_store newInstance(String param1, String param2) {
        fg_store fragment = new fg_store();
        Bundle args = new Bundle();
        //put any extra arguments that you may want to supply to this fragment
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
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fg_store, container, false);
        mTextError = (TextView) layout.findViewById(R.id.textVolleyError);
        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeMovieHits);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerProducts = (RecyclerView) layout.findViewById(R.id.listProducts);
        //set the layout manager before trying to display data
        mRecyclerProducts.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new AdapterProducts(getActivity());
        mRecyclerProducts.setAdapter(mAdapter);
        if (savedInstanceState != null) {
            //if this fragment starts after a rotation or configuration change, load the existing products from a parcelable
            pListProducts = savedInstanceState.getParcelableArrayList(STATE_PRODUCTS);
        } else {
            //if this fragment starts for the first time, load the list of movies from a database
            pListProducts = ApiKey.getWritableDatabase().readProducts();
            //if the database is empty, trigger an AsycnTask to download movie list from the web
            if (pListProducts.isEmpty()) {
                //L.m("fg_store: executing task from fragment");
                new TaskLoadProducts(this).execute();
            }
        }
        //update your Adapter to containg the retrieved products
        mAdapter.setProducts(pListProducts);
        return layout;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save the movie list to a parcelable prior to rotation or configuration change
        outState.putParcelableArrayList(STATE_PRODUCTS, pListProducts);
    }

    @Override
    public void onProductsLoaded(ArrayList<Product> listProducts) {
        //L.m("fg_store: onProductsLoaded Fragment");
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        mAdapter.setProducts(listProducts);
    }

    @Override
    public void onRefresh() {
        //L.t(getActivity(), "onRefresh");
        new TaskLoadProducts(this).execute();

    }


}
