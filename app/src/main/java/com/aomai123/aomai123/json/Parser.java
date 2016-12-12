package com.aomai123.aomai123.json;

import com.aomai123.aomai123.obj.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nang Juann on 1/9/2016.
 */
public class Parser {
    public static ArrayList<Product> parseProductsJSON(JSONArray response) {
        ArrayList<Product> listProducts = new ArrayList<>();

        if(response != null && response.length() > 0){
            for(int i=0;i<response.length();i++){
                try{
                    JSONObject obj = response.getJSONObject(i);
                    Product product = new Product();
                    product.setId(obj.getInt("id"));
                    product.setName(obj.getString("name"));
                    product.setQuantity(obj.getInt("quantity"));
                    product.setPrice(obj.getInt("price"));
                    product.setDescription(obj.getString("description"));
                    product.setUrlThumbnail(obj.getString("image"));

                    listProducts.add(product);

                }catch (JSONException e){}
            }

        }
        return listProducts;
    }
    public static ArrayList<String> parseBuildingsJSON(JSONArray response){
        ArrayList<String> b = new ArrayList<>();
        if(response != null && response.length() > 0){
            for(int i=0;i<response.length();i++){
                try{
                    JSONObject obj = response.getJSONObject(i);
                    String xx = obj.getString("building");
                    b.add(xx);
                }catch (JSONException e){}
            }
        }
        return b;
    }
}
