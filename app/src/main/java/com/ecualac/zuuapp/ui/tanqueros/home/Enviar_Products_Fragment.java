package com.ecualac.zuuapp.ui.tanqueros.home;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ecualac.zuuapp.PubVariable;
import com.ecualac.zuuapp.R;

import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Enviar_Products_Fragment extends Fragment {

    static class Rute{
        String Nombre, Ruta;
        int id;

        Rute(String Nombre, String Ruta, int id) {
            this.Nombre = Nombre;
            this.Ruta = Ruta;
            this.id = id;
        }
    }
    int Ruta_id;
    Button min_btn, plus_btn, sumar_btn, restar_btn;
    EditText ProdNumber;
    TextView ProdPres,ProdName,stock_prod_enviar;
    String Nombre, Presentacion, Stock;
    String public_id;
    private ArrayList<Rute> rutasarr;
    Spinner s_rutes;
    private ArrayList<String> lista = new ArrayList<String>();;
    org.json.simple.JSONObject json;
    JSONParser parser;
    JSONArray jsonarr;

    private final OkHttpClient httpClient = new OkHttpClient();
    ArrayAdapter adapter ;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_enviar_products, container, false);
        min_btn = root.findViewById(R.id.min_btn);
        plus_btn = root.findViewById(R.id.plus_btn);
        sumar_btn = root.findViewById(R.id.anadir_btn);
        restar_btn = root.findViewById(R.id.restar_btn);
        ProdPres = root.findViewById(R.id.ProdPresText);
        ProdName = root.findViewById(R.id.ProdNameText);
        ProdNumber = root.findViewById(R.id.ProdNumber);
        s_rutes = root.findViewById(R.id.spinner);
        //stock_prod_enviar = root.findViewById(R.id.stock_prod_enviar);
        ProdNumber.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "999999")});
        s_rutes.getBackground().setColorFilter(getResources().getColor(R.color.colorarrow), PorterDuff.Mode.SRC_ATOP);
        assert getArguments() != null;
        Nombre = getArguments().getString("Nombre");
        Presentacion = getArguments().getString("Presentacion");
        Stock = getArguments().getString("Stock");
        public_id = getArguments().getString("public_id");
        restar_btn.setEnabled(false);
        ProdNumber.setText("0");
        ProdName.setText(Nombre);
        ProdPres.setText(Presentacion);

        try {
            sendGET3();
        } catch (IOException e) {
            e.printStackTrace();
        }

        rutasarr =new ArrayList<>();
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, lista);
       // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_rutes.setAdapter(adapter);
        s_rutes.setSelection(0);
        adapter.notifyDataSetChanged();

        s_rutes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    restar_btn.setEnabled(false);
                }else{
                    PubVariable.idRute = rutasarr.get(position).id;
                    Ruta_id = rutasarr.get(position).id;
                    s_rutes.setSelection(position);
                    restar_btn.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        min_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentNumber = Integer.parseInt(ProdNumber.getText().toString());
                if(currentNumber==0){
                    ProdNumber.setText("0");
                }else{
                    ProdNumber.setText(String.valueOf(currentNumber-1));
                }
            }
        });

        plus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentNumber = Integer.parseInt(ProdNumber.getText().toString());
                if(currentNumber==999999){
                    ProdNumber.setText("999999");
                }else{
                    ProdNumber.setText(String.valueOf(currentNumber+1));
                }
            }
        });

        sumar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_transaccion(1);
                gotoHomeTanque();
            }
        });

        restar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_transaccion(-1);
                gotoHomeTanque();
            }
        });


        return root;
    }


    private void sendGET3() throws IOException {

        Request request = new Request.Builder()
                .url(PubVariable.postUrl+":5000/api/rutas")
                .addHeader("custom-key", "mkyong")  // add request headers
                .addHeader("User-Agent", "OkHttp Bot")
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

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
                    json = (org.json.simple.JSONObject) parser.parse(respstring);
                    jsonarr = new JSONArray();
                    jsonarr = (JSONArray) json.get("rutas");
                    System.out.println("LENGHT++"+json.size());
                    System.out.println("ARRAY++"+jsonarr.toString());

                    popList();
//
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            myAdapter.notifyDataSetChanged();
//                        }
//                    });

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void popList() {
              lista.add(0, "Seleccione una ruta");
              rutasarr.add(0,new Rute("SISTEMA","SISTEMA",0));
        for (int i = 1; i < jsonarr.size(); i++) {
            org.json.simple.JSONObject c = (org.json.simple.JSONObject) jsonarr.get(i);// Used JSON Object from Android
            System.out.println("++" + c.size());
            int c_id=0;
            int product_id = 0;
            //Storing each Json in a string variable
            try {
                String Nombre = (String) c.get("driver_name");
                String Ruta = (String) c.get("rute");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    c_id = Math.toIntExact((Long) c.get("id"));
                }

                Rute newprod = new Rute(Nombre,Ruta,c_id);
                rutasarr.add(newprod);
                lista.add(Ruta +" | "+Nombre);
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                Log.e("POPLIST", "Error al obtener los datos: ", e);
            }
        }
    }

    private void gotoHomeTanque() {
        PubVariable.idRute=0;
        Bundle args = new Bundle();
        Fragment someFragment = new Home_Tanqueros_Fragment();
        assert getFragmentManager() != null;
        someFragment.setArguments(args);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_tanqueros, someFragment ); // give your fragment container id in first parameter
        transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
        transaction.commit();
    }


    public static String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(PubVariable.date_format);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-5"));
        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }


    public void send_transaccion(final int op) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(PubVariable.postUrl+":5000/api/transactions");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                   // transaccion = Transaccion(operation=0, usuario_tran="sistema", datetime=dt.datetime.now(),
                   //         product_id=producto.id)
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("operation", op*Integer.parseInt(ProdNumber.getText().toString()));
                    jsonParam.put("usuario_tran", PubVariable.username);
                    jsonParam.put("datetime", getCurrentTime());
                    jsonParam.put("public_id", public_id);
                    if(op==-1){
                        jsonParam.put("rute_id",Ruta_id);
                    }else if (op==1){
                        jsonParam.put("rute_id",1);
                    }


                    Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());

                    if(conn.getResponseMessage().equals("OK") && String.valueOf(conn.getResponseCode()).equals("200")){
                        Thread thread = new Thread(){
                            public void run(){
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getActivity(), "Enviado Correctamente", Toast.LENGTH_LONG).show();

                                    }
                                });
                            }
                        };
                        thread.start();
                    }else{
                        Thread thread = new Thread(){
                            public void run(){
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        try {
                                            Toast.makeText(getActivity(), "No se ha podido enviar la información, Error Code: "+ conn.getResponseCode(), Toast.LENGTH_LONG).show();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        };
                        thread.start();
                    }

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }




}