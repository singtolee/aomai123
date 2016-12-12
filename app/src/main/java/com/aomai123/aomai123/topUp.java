package com.aomai123.aomai123;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.aomai123.aomai123.Extra.Url_SHOP;
import com.aomai123.aomai123.database.Session;
import com.aomai123.aomai123.json.Parser;
import com.aomai123.aomai123.network.VolleySingleton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

public class topUp extends AppCompatActivity {
    private Button goBackme, openGallery,btnUpload;
    private ImageView receipt;
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private Session session;
    private ProgressDialog pDialog;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.top_up);
        session = new Session(this);
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        receipt = (ImageView)findViewById(R.id.imageBill);
        openGallery = (Button) findViewById(R.id.btnOpenGallary);
        btnUpload = (Button) findViewById(R.id.btnuploadpic);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadImage();
            }
        });
        openGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openpic = new Intent();
                openpic.setType("image/*");
                openpic.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(openpic, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
        goBackme = (Button) findViewById(R.id.btnreturn);
        goBackme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("pos", 2);
                startActivity(i);
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                receipt.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public String getStringImage(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage(){
        pDialog.setMessage("Uploading, please wait...");
        showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Url_SHOP.URL_TOPUP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        hideDialog();
                        Toast.makeText(getApplication(), s, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        hideDialog();
                        handleVolleyError(volleyError);
                        //Toast.makeText(getApplication(), volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String image = getStringImage(bitmap);
                String uid = Integer.toString(session.getUserID());
                Map<String,String> params = new Hashtable<String, String>();
                params.put("IMAGE", image);
                params.put("UID", uid);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
    public void handleVolleyError(VolleyError error){
        if(error instanceof TimeoutError || error instanceof NoConnectionError){
            Toast.makeText(this, R.string.timeout_error, Toast.LENGTH_LONG).show();
        }else if(error instanceof AuthFailureError){
            Toast.makeText(this, "Auth fail", Toast.LENGTH_LONG).show();
        }else if(error instanceof ServerError){
            Toast.makeText(this, "Server error", Toast.LENGTH_LONG).show();
        }else if(error instanceof NetworkError){
            Toast.makeText(this, "NetWork error", Toast.LENGTH_LONG).show();
        }else if(error instanceof ParseError){
            Toast.makeText(this, "Parse error", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Something Error", Toast.LENGTH_LONG).show();
        }
    }
}
