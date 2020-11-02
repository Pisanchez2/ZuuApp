package com.ecualac.zuuapp.ui.main;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.ecualac.zuuapp.AuthActivity;
import com.ecualac.zuuapp.R;
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

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    EditText NombresText, ApellidosText, EmailText, ContraText, ConfirmContraText;
    Button Registrar, reg2;
    int userlevel;
    TextView responseText;
    private DatabaseReference reffBuscar;
    private PageViewModel pageViewModel;
    static String postUrl = "http://192.168.100.53";

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
            userlevel = index;
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        final TextView textView = root.findViewById(R.id.section_label);
        NombresText = root.findViewById(R.id.NombreRegText);
        ApellidosText = root.findViewById(R.id.ApellRegText);
        EmailText = root.findViewById(R.id.EmailRegText);
        ContraText = root.findViewById(R.id.ContraRegText);
        ConfirmContraText = root.findViewById(R.id.ConfirmContraText);
        Registrar = root.findViewById(R.id.Registrar_btn);
        responseText = root.findViewById(R.id.responseTextRegister);

        reffBuscar = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        reg2 = root.findViewById(R.id.registerButton);
        reg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPost();
            }
        });

        pageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        /*
        //FIREBASE
        Registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( !ApellidosText.getText().toString().isEmpty() && !NombresText.getText().toString().isEmpty() && !EmailText.getText().toString().isEmpty() &&
                        !ContraText.getText().toString().isEmpty() && ConfirmContraText.getText().toString().equals(ContraText.getText().toString())){
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(EmailText.getText().toString(),ContraText.getText().toString()).
                            addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        if (userlevel==1){
                                            reffBuscar.child("Tanqueros").child(ApellidosText.getText().toString()).child("Apellido").setValue(ApellidosText.getText().toString());
                                            reffBuscar.child("Tanqueros").child(ApellidosText.getText().toString()).child("Nombre").setValue(NombresText.getText().toString());
                                            reffBuscar.child("Tanqueros").child(ApellidosText.getText().toString()).child("Correo").setValue(EmailText.getText().toString());
                                            reffBuscar.child("Tanqueros").child(ApellidosText.getText().toString()).child("lvl").setValue(userlevel);
                                        }else if(userlevel==2){
                                            reffBuscar.child("Supervisores").child(ApellidosText.getText().toString()).child("Apellido").setValue(ApellidosText.getText().toString());
                                            reffBuscar.child("Supervisores").child(ApellidosText.getText().toString()).child("Nombre").setValue(NombresText.getText().toString());
                                            reffBuscar.child("Supervisores").child(ApellidosText.getText().toString()).child("Correo").setValue(EmailText.getText().toString());
                                            reffBuscar.child("Supervisores").child(ApellidosText.getText().toString()).child("lvl").setValue(userlevel);
                                        }
                                        Toast.makeText(getContext(),"Registrado Correctamente", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(getContext(),"Error al registrar usuario", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        }
                    }
                });
        */
        return root;

    }

    public void sendPost() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(postUrl+"/api/usuario");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("user_name", NombresText.getText().toString().trim());
                    jsonParam.put("email", EmailText.getText().toString().trim());
                    jsonParam.put("password", ContraText.getText().toString().trim());
                    jsonParam.put("is_blocked", false);

                    Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }



    public void register() {

        if (NombresText.length() == 0 || EmailText.length() == 0 || ContraText.length() == 0) {
            Toast.makeText(getContext(), "Something is wrong. Please check your inputs.", Toast.LENGTH_LONG).show();
        } else {
            JSONObject registrationForm = new JSONObject();
            try {
                //registrationForm.put("subject", "register");
                registrationForm.put("user_name", NombresText.getText().toString().trim());
                registrationForm.put("email", EmailText.getText().toString().trim());
                registrationForm.put("password", ContraText.getText().toString().trim());
                registrationForm.put("is_blocked", false);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), registrationForm.toString());

            postRequest(postUrl+"/api/products", body);

            Log.i("URL", "register: "+body );
            Log.i("URL-JSON", "register: "+ registrationForm );


        }

    }

    public void postRequest(String postUrl, RequestBody postBody) {
        OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();

        Log.i("URL2", "register: "+request);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                Log.d("FAIL", e.getMessage());

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        responseText.setText("Failed to Connect to Server. Please Try Again.");
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) {
                try {
                    final String responseString = response.body().string().trim();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (responseString.equals("success")) {
                                responseText.setText("Registration completed successfully.");
                            } else if (responseString.equals("username")) {
                                responseText.setText("Username already exists. Please chose another username.");
                            } else {
                                responseText.setText("Something went wrong. Please try again later.");
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}