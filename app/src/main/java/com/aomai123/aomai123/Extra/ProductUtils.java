package com.aomai123.aomai123.Extra;

import com.android.volley.RequestQueue;
import com.aomai123.aomai123.API.ApiKey;
import com.aomai123.aomai123.json.Parser;
import com.aomai123.aomai123.json.Requestor;
import com.aomai123.aomai123.obj.Product;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nang Juann on 1/9/2016.
 */
public class ProductUtils {
    public static ArrayList<Product> loadProducts(RequestQueue requestQueue) {
        JSONArray response = Requestor.requestProductsJSON(requestQueue, Url_SHOP.URL_PRODUCT);
        ArrayList<Product> listProducts = Parser.parseProductsJSON(response);
        ApiKey.getWritableDatabase().insertProducts(listProducts, true);
        return listProducts;
    }

    public static ArrayList<String> loadBuildings(RequestQueue requestQueue){
        JSONArray response = Requestor.requestProductsJSON(requestQueue,Url_SHOP.URL_OFFICEBUILDING);
        ArrayList<String> buildings = Parser.parseBuildingsJSON(response);
        return buildings;
    }
}
