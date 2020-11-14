package com.ecualac.zuuapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AuthActivity extends AppCompatActivity {

    private DatabaseReference reffBuscar ;
    private TextView  registro,responseText;
    private EditText email, pass;
    private Button ingresar, gotobodega, gotosup;
    Intent showReg;
    Intent showSup;
    Intent showTanque;
    int lvl;
    String ApeAux;
    static String postUrl = "https://127.0.0.1:5000";
    JSONObject json;
    JSONParser parser;
    JSONArray jsonarr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        email = findViewById(R.id.email_text);
        pass = findViewById(R.id.pass_text);
        registro = findViewById(R.id.Reg_btn);
        ingresar = findViewById(R.id.LogIn_Btn);
        reffBuscar = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        registro.setKeyListener(null);
        registro.setFocusableInTouchMode(false);
        showReg = new Intent(this, RegistroActivity.class);
        showSup = new Intent(this, SupActivity.class);
        showTanque = new Intent(this, TanqActivity.class);

        responseText = findViewById(R.id.responseText);


        //go to registro page
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(showReg);
            }
        });

        //API
        ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authGrafana(email.getText().toString(),pass.getText().toString());
            }
        });

    }

    private void authGrafana(String user, String password) {

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthInterceptor(user, password))
                .build();

        Request request = new Request.Builder()
                .url(PubVariable.postUrl+"/grafana/api/user")
                .addHeader("custom-key", "mkyong")  // add request headers
                .addHeader("User-Agent", "OkHttp Bot")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        responseText.setText("El usuario o contraseña no es valido, Inténtelo de nuevo");
                        responseText.setTextColor(Color.RED);
                        throw new IOException("Unexpected code " + response);
                    }
                    // Get response headers
                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        System.out.println(responseHeaders.name(i) + ":-- " + responseHeaders.value(i));
                    }

                    // Get response body
                    assert response.body() != null;
                    String respstring = responseBody.string();
                    System.out.println("++"+ respstring);
                    parser = new JSONParser();
                    assert response.body() != null;
                    json = (JSONObject) parser.parse(respstring);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        PubVariable.idOrg =  Math.toIntExact((Long) json.get("orgId"));
                    }
                    PubVariable.username = (String) json.get("login");
                    PubVariable.email = (String) json.get("email");


                    assert PubVariable.username != null;
                    if(PubVariable.idOrg!=0 && !PubVariable.username.equals("")){
                        if(PubVariable.idOrg==3){
                            try {
                                startActivity(showTanque);
                            }catch (Exception e){
                                Toast.makeText(getApplicationContext(), "Algo salió mal, intentelo de nuevo", Toast.LENGTH_LONG).show();
                            }

                        }else if(PubVariable.idOrg==2){
                            startActivity(showSup);
                        }
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) { // Login
            responseText.setText("Successful Login.");
        } else {
            responseText.setText("Invalid or no data entered. Please try again.");
        }
    }

    public void onClick(View view) {
        startActivity(showReg);
    }
}