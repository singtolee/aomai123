package com.aomai123.aomai123.obj;

import android.os.Parcel;
import android.os.Parcelable;

import com.aomai123.aomai123.logging.L;

/**
 * Created by Nang Juann on 1/16/2016.
 */
public class Product implements Parcelable {
    public static final Parcelable.Creator<Product> CREATOR
            = new Parcelable.Creator<Product>() {
        public Product createFromParcel(Parcel in) {
            L.m("create from parcel :Product");
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
    private int id, quantity;
    private String name, description;
    private int price;
    private String urlThumbnail;

    public Product() {

    }

    public Product(Parcel input) {
        id = input.readInt();
        name = input.readString();
        quantity = input.readInt();
        price = input.readInt();
        description = input.readString();
        urlThumbnail = input.readString();
    }

    public Product(int id, int quantity, String name, String description, int price, String urlThumbnail) {
        this.id = id;
        this.quantity = quantity;
        this.name = name;
        this.description = description;
        this.price = price;
        this.urlThumbnail = urlThumbnail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice(){
        return price;
    }

    public void setPrice(int price){
        this.price = price;
    }

    public String getUrlThumbnail() {
        return urlThumbnail;
    }

    public void setUrlThumbnail(String urlThumbnail) {
        this.urlThumbnail = urlThumbnail;
    }

    @Override
    public String toString() {
        return "\nID: " + id +
                "\nName " + name +
                "\nQuantity " + quantity +
                "\nPrice " + price +
                "\nDescription " + description +
                "\nurlThumbnail " + urlThumbnail +
                "\n";
    }

    @Override
    public int describeContents() {
//        L.m("describe Contents Product");
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        L.m("writeToParcel Product");
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(quantity);
        dest.writeLong(price);
        dest.writeString(description);
        dest.writeString(urlThumbnail);
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
