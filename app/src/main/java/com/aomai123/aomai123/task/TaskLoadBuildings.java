package com.aomai123.aomai123.task;

import android.os.AsyncTask;
import android.view.View;

import com.android.volley.RequestQueue;
import com.aomai123.aomai123.Extra.ProductUtils;
import com.aomai123.aomai123.callbacks.BuildingsLoadedListener;
import com.aomai123.aomai123.network.VolleySingleton;

import java.util.ArrayList;

/**
 * Created by Nang Juann on 2/23/2016.
 */
public class TaskLoadBuildings extends AsyncTask<Void,Void,ArrayList<String>>{
    private BuildingsLoadedListener myComponent;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;

    public TaskLoadBuildings(BuildingsLoadedListener myComponent){
        this.myComponent = myComponent;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }

    @Override
    protected ArrayList<String> doInBackground(Void... params) {

        ArrayList<String> listBuildings = ProductUtils.loadBuildings(requestQueue);
        return listBuildings;
    }

    @Override
    protected void onPostExecute(ArrayList<String> listBuildings) {
        if (myComponent != null) {
            myComponent.onBuildingsLoaded(listBuildings);
        }
    }

}
