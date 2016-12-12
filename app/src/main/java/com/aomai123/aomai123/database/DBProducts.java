package com.aomai123.aomai123.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.aomai123.aomai123.logging.L;
import com.aomai123.aomai123.obj.Cart;
import com.aomai123.aomai123.obj.Product;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Nang Juann on 1/9/2016.
 */
public class DBProducts {
    private ProductsHelper pHelper;
    private SQLiteDatabase pDatabase;

    public DBProducts(Context context) {
        pHelper = new ProductsHelper(context);
        pDatabase = pHelper.getWritableDatabase();
    }

    public void insertCarts (String name, int price, int num, int pid){
        String sql = "INSERT INTO " + ProductsHelper.TABLE_CART + " VALUES (?,?,?,?,?);";
        SQLiteStatement statement = pDatabase.compileStatement(sql);
        pDatabase.beginTransaction();
        statement.bindLong(2,pid);
        statement.bindString(3, name);
        statement.bindLong(4, price);
        statement.bindLong(5, num);
        statement.execute();
        pDatabase.setTransactionSuccessful();
        pDatabase.endTransaction();
    }

    public void deletecartitem (int id){
        String sql = "DELETE FROM " + ProductsHelper.TABLE_CART + " WHERE " + ProductsHelper.COLUMN_PID + " =" + id;
        pDatabase.execSQL(sql);
    }

    public ArrayList<Cart> readCarts(){
        ArrayList<Cart> listCarts = new ArrayList<>();
        String[] columns = {ProductsHelper.COLUMN_PID,
                ProductsHelper.COLUMN_NAME,
                ProductsHelper.COLUMN_PRICE,
                ProductsHelper.PRODUCT_NUM
        };
        Cursor cursor = pDatabase.query(ProductsHelper.TABLE_CART, columns, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            L.m("loading entries " + cursor.getCount() + new Date(System.currentTimeMillis()));
            do {
                Cart product = new Cart();
                product.setPid(cursor.getInt(cursor.getColumnIndex(ProductsHelper.COLUMN_PID)));
                product.setName(cursor.getString(cursor.getColumnIndex(ProductsHelper.COLUMN_NAME)));
                product.setPrice(cursor.getInt(cursor.getColumnIndex(ProductsHelper.COLUMN_PRICE)));
                product.setNum(cursor.getInt(cursor.getColumnIndex(ProductsHelper.PRODUCT_NUM)));
                listCarts.add(product);
            }
            while (cursor.moveToNext());
        }
        return listCarts;
    }

    public void deleteCarts() {
        pDatabase.delete(ProductsHelper.TABLE_CART, null, null);
    }

    public int findByIDinCart(int ID){
        Cursor cursor = pDatabase.query(ProductsHelper.TABLE_CART, new String[]{ProductsHelper.PRODUCT_NUM,ProductsHelper.COLUMN_PID},ProductsHelper.COLUMN_PID + "=?",new String[]{String.valueOf(ID)},null,null,null,null);
        if(cursor.moveToFirst()){
            int num = cursor.getInt(cursor.getColumnIndex(ProductsHelper.PRODUCT_NUM));
            return num;
        }else {
            return 0;
        }

    }

    public void increaseItemNum(int num, int PID){
        int n = findByIDinCart(PID)+ num;
        String sql = "UPDATE " + ProductsHelper.TABLE_CART + " SET " + ProductsHelper.PRODUCT_NUM + "="+ n + " WHERE "+ ProductsHelper.COLUMN_PID + "="+PID;
        pDatabase.execSQL(sql);

    }



    public void insertProducts(ArrayList<Product> listProducts, boolean clearPrevious) {
        if (clearPrevious) {
            deleteProducts();
        }

        //create a sql prepared statement
        String sql = "INSERT INTO " + ProductsHelper.TABLE_PRODUCT + " VALUES (?,?,?,?,?,?);";
        //compile the statement and start a transaction
        SQLiteStatement statement = pDatabase.compileStatement(sql);
        pDatabase.beginTransaction();
        for (int i = 0; i < listProducts.size(); i++) {
            Product currentProduct = listProducts.get(i);
            statement.clearBindings();
            //for a given column index, simply bind the data to be put inside that index
            statement.bindLong(1, currentProduct.getId());
            statement.bindString(2, currentProduct.getName());
            statement.bindLong(3, currentProduct.getQuantity());
            statement.bindLong(4, currentProduct.getPrice());
            statement.bindString(5, currentProduct.getDescription());
            statement.bindString(6, currentProduct.getUrlThumbnail());

            statement.execute();
        }
        //set the transaction as successful and end the transaction
        L.m("inserting entries " + listProducts.size() + new Date(System.currentTimeMillis()));
        pDatabase.setTransactionSuccessful();
        pDatabase.endTransaction();
    }

    public ArrayList<Product> readProducts() {
        ArrayList<Product> listProducts = new ArrayList<>();

        //get a list of columns to be retrieved, we need all of them
        String[] columns = {ProductsHelper.COLUMN_UID,
                ProductsHelper.COLUMN_NAME,
                ProductsHelper.COLUMN_QUANTITY,
                ProductsHelper.COLUMN_PRICE,
                ProductsHelper.COLUMN_DESCRIPTION,
                ProductsHelper.COLUMN_URL_THUMBNAIL
        };
        Cursor cursor = pDatabase.query(ProductsHelper.TABLE_PRODUCT, columns, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            L.m("loading entries " + cursor.getCount() + new Date(System.currentTimeMillis()));
            do {

                //create a new movie object and retrieve the data from the cursor to be stored in this movie object
                Product product = new Product();
                //each step is a 2 part process, find the index of the column first, find the data of that column using
                //that index and finally set our blank movie object to contain our data
                product.setId(cursor.getInt(cursor.getColumnIndex(ProductsHelper.COLUMN_UID)));
                product.setName(cursor.getString(cursor.getColumnIndex(ProductsHelper.COLUMN_NAME)));
                product.setQuantity(cursor.getInt(cursor.getColumnIndex(ProductsHelper.COLUMN_QUANTITY)));
                product.setPrice(cursor.getInt(cursor.getColumnIndex(ProductsHelper.COLUMN_PRICE)));
                product.setDescription(cursor.getString(cursor.getColumnIndex(ProductsHelper.COLUMN_DESCRIPTION)));
                product.setUrlThumbnail(cursor.getString(cursor.getColumnIndex(ProductsHelper.COLUMN_URL_THUMBNAIL)));
                //add the movie to the list of movie objects which we plan to return
                listProducts.add(product);
            }
            while (cursor.moveToNext());
        }
        return listProducts;
    }

    public void deleteProducts() {
        pDatabase.delete(ProductsHelper.TABLE_PRODUCT, null, null);
    }

    private static class ProductsHelper extends SQLiteOpenHelper {
        public static final String TABLE_CART = "table_carts";
        public static final String PRODUCT_NUM = "number";
        public static final String TABLE_PRODUCT = "table_products";
        public static final String COLUMN_UID = "id";
        public static final String COLUMN_PID = "pid";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_URL_THUMBNAIL = "url_thumbnail";

        private static final String CREATE_TABLE_PRODUCT = "CREATE TABLE " + TABLE_PRODUCT + " (" +
                COLUMN_UID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME + " TEXT," +
                COLUMN_QUANTITY + " INTEGER," +
                COLUMN_PRICE + " INTEGER," +
                COLUMN_DESCRIPTION + " TEXT," +
                COLUMN_URL_THUMBNAIL + " TEXT" +
                ");";

        private static final String CREATE_TABLE_CART = "CREATE TABLE " + TABLE_CART + " (" +
                COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_PID + " INTEGER," +
                COLUMN_NAME + " TEXT," +
                COLUMN_PRICE + " INTEGER," +
                PRODUCT_NUM + " INTEGER" +
                ");";

        private static final String DB_NAME = "products_db";
        private static final int DB_VERSION = 1;
        private Context pContext;

        public ProductsHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            pContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_PRODUCT);
            db.execSQL(CREATE_TABLE_CART);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                L.m("upgrade table products executed");
                db.execSQL(" DROP TABLE " + TABLE_PRODUCT + " IF EXISTS;");
                db.execSQL(" DROP TABLE " + TABLE_CART + " IF EXISTS;");
                onCreate(db);
            } catch (SQLiteException exception) {
                L.t(pContext, exception + "");
            }
        }
    }
}
