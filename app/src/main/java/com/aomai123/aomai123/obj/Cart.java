package com.aomai123.aomai123.obj;

/**
 * Created by Nang Juann on 1/21/2016.
 */
public class Cart {
    private String name;
    private int num, price, pid;

    public Cart(){

    }
    public Cart(String name, int num, int price){
        this.name = name;
        this.num = num;
        this.price = price;
    }

    public int getNum(){
        return num;
    }
    public void setNum(int num){
        this.num = num;
    }

    public int getPrice(){
        return price;
    }
    public void setPrice(int price){
        this.price = price;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }
}
