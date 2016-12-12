package com.aomai123.aomai123.callbacks;

import com.aomai123.aomai123.obj.Product;

import java.util.ArrayList;

/**
 * Created by Nang Juann on 1/9/2016.
 */
public interface ProductsLoadedListener {
    public void onProductsLoaded(ArrayList<Product> listProducts);
}
