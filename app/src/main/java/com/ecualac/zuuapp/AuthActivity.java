package com.ecualac.zuuapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;

public class AuthActivity extends AppCompatActivity {

    private DatabaseReference reffBuscar ;
    private TextView  registro;
    private EditText email, pass;
    private Button ingresar, gotobodega, gotosup;
    Intent showReg;
    Intent showSup;
    Intent showTanque;
    int lvl;
    String ApeAux;
    static String postUrl = "https://127.0.0.1:5000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        email = findViewById(R.id.email_text);
        pass = findViewById(R.id.pass_text);
        registro = findViewById(R.id.Reg_btn);
        ingresar = findViewById(R.id.LogIn_Btn);
        reffBuscar = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        gotobodega = findViewById(R.id.gotoBodega);
        gotosup = findViewById(R.id.gotoSup);
        registro.setKeyListener(null);
        registro.setFocusableInTouchMode(false);
        showReg = new Intent(this, RegistroActivity.class);
        showSup = new Intent(this, SupActivity.class);
        showTanque = new Intent(this, TanqActivity.class);
        //go to registro page
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(showReg);
            }
        });

        gotobodega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(showTanque);
            }
        });


        gotosup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(showSup);
            }
        });

        //API

        //FIREBASE
        /*
        ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( !email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).
                            addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        PubVariable.correo=email.getText().toString();

                                        reffBuscar.child("Tanqueros").orderByChild("Correo").equalTo(email.getText().toString()).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                                                    PubVariable.Apellido= childSnapshot.getKey();
                                                    startActivity(showTanque);
                                                    Log.i("APEAUX1",PubVariable.Apellido);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        if(PubVariable.Apellido==null){
                                            reffBuscar.child("Supervisores").orderByChild("Correo").equalTo(email.getText().toString()).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                                                        PubVariable.Apellido= childSnapshot.getKey();
                                                        startActivity(showSup);
                                                        Log.i("APEAUX2",PubVariable.Apellido);
                                                    }
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                }
                                            });
                                        }

                                    }else{
                                        Toast.makeText(getBaseContext(),"Error al registrar usuario", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        }
                    }
                });
               */

    }

    public void submit(View v) {

        String username = email.getText().toString().trim();
        String password = pass.getText().toString().trim();

        if (username.length() == 0 || password.length() == 0) {
            Toast.makeText(getApplicationContext(), "Something is wrong. Please check your inputs.", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject loginForm = new JSONObject();
        try {
            //loginForm.put("subject", "login");
            loginForm.put("user_name", username);
            loginForm.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), loginForm.toString());
        Log.v("URL-body", "submit: "+body);
        postRequest(postUrl+"/api/productos/login", body);
    }

    public void postRequest(String postUrl, RequestBody postBody) {
        OkHttpClient client = new OkHttpClient();


        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();
        Log.i("URL", "postRequest: " + request);

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure.
                call.cancel();
                Log.d("FAIL", e.getMessage());

                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView responseTextLogin = findViewById(R.id.responseTextLogin);
                        responseTextLogin.setText("Failed to Connect to Server. Please Try Again.");
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView responseTextLogin = findViewById(R.id.responseTextLogin);
                        try {
                            String loginResponseString = response.body().string().trim();
                            Log.d("LOGIN", "Response from the server : " + loginResponseString);
                            if (loginResponseString.equals("success")) {
                                Log.d("LOGIN", "Successful Login");
                                finish();//finishing activity and return to the calling activity.
                            } else if (loginResponseString.equals("failure")) {
                                responseTextLogin.setText("Login Failed. Invalid username or password.");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            responseTextLogin.setText("Something went wrong. Please try again later.");
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TextView responseText = findViewById(R.id.responseText);
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