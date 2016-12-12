package com.aomai123.aomai123;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.aomai123.aomai123.Extra.Url_SHOP;
import com.aomai123.aomai123.database.Session;
import com.aomai123.aomai123.network.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class logIn extends AppCompatActivity {

    private Button btnLogin;
    private Button btnGoBackStore;
    private Button btnLinkToRegister;
    private EditText inputEmail;
    private EditText inputPassword;
    private Session session;
    private ProgressDialog pDialog;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);

        session = new Session(getApplicationContext());

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        findViewById(R.id.loginLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();

        Map<String,String> eAndp = session.readUserPsw();
        inputEmail = (EditText) findViewById(R.id.email);
        inputEmail.setText(eAndp.get("email"));
        inputPassword = (EditText) findViewById((R.id.password));
        inputPassword.setText(eAndp.get("password"));

        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        btnGoBackStore = (Button) findViewById(R.id.btnLinkToStore);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        if(session.isLoggedIn()){
            Intent i = new Intent(getApplicationContext(),MainActivity.class);
            i.putExtra("pos", 2);
            startActivity(i);
            finish();
        }

        btnGoBackStore.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view){
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
                finish();
            }

        });

        btnLinkToRegister.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent i = new Intent(getApplicationContext(),register.class);
                startActivity(i);
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if(!email.isEmpty()&&!password.isEmpty()){
                    checkLogin(email,password);
                }else {
                    Toast.makeText(getApplicationContext(),R.string.EnterCredential,Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void checkLogin(final String email, final String password) {
        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Url_SHOP.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session
                        session.setLogin(true);

                        JSONObject yh = jObj.getJSONObject("user");
                        int uid = yh.getInt("user_ID");
                        int bla = yh.getInt("balance");
                        int jifen = yh.getInt("points");
                        String fn = yh.getString("firstname");
                        String ln = yh.getString("lastname");
                        String mobile = yh.getString("phone");
                        String office = yh.getString("building");
                        String flr = yh.getString("floor");
                        String roomNo = yh.getString("room");
                        String company = yh.getString("company");

                        session.saveUsernamePsw(uid, bla, jifen, fn, ln, email, password, mobile, office, flr, roomNo, company);
                        // Launch main activity
                        Intent intent = new Intent(logIn.this,
                                MainActivity.class);
                        intent.putExtra("pos", 2);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                handleVolleyError(error);
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        requestQueue.add(strReq);
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
        }else{
            Toast.makeText(this, R.string.timeout_error, Toast.LENGTH_LONG).show();
        }
    }

}
