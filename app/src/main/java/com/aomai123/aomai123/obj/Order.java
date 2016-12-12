package com.aomai123.aomai123.obj;

/**
 * Created by Singto on 3/19/2016 AD.
 */
public class Order {
    private int oid, total, outstanding;
    private String pm, dt, op, status, date;
     public Order(){

     }

    public int getOid() {
        return oid;
    }
    public int getTotal(){
        return total;
    }
    public int getOutstanding(){
        return outstanding;
    }
    public String getPm(){
        return pm;
    }
    public String getDt(){
        return dt;
    }
    public String getOp(){
        return op;
    }

    public void setOid(int id){
        this.oid = id;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setOutstanding(int outstanding) {
        this.outstanding = outstanding;
    }

    public void setPm(String pm) {
        this.pm = pm;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getDate(){
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
