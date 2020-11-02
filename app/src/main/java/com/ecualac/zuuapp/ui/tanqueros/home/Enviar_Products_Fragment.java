package com.ecualac.zuuapp.ui.tanqueros.home;

import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ecualac.zuuapp.PubVariable;
import com.ecualac.zuuapp.R;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Enviar_Products_Fragment extends Fragment {

    Button min_btn, plus_btn, sumar_btn, restar_btn;
    EditText ProdNumber;
    TextView ProdPres,ProdName,stock_prod_enviar;
    String Nombre, Presentacion, Stock;
    String public_id;


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
        stock_prod_enviar = root.findViewById(R.id.stock_prod_enviar);
        ProdNumber.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "500")});

        assert getArguments() != null;
        Nombre = getArguments().getString("Nombre");
        Presentacion = getArguments().getString("Presentacion");
        Stock = getArguments().getString("Stock");
        public_id = getArguments().getString("public_id");

        ProdNumber.setText("0");
        ProdName.setText(Nombre);
        ProdPres.setText(Presentacion);


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
                if(currentNumber==500){
                    ProdNumber.setText("500");
                }else{
                    ProdNumber.setText(String.valueOf(currentNumber+1));
                }
            }
        });

        sumar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_transaccion(1);
            }
        });

        restar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_transaccion(-1);
            }
        });


        return root;
    }



    public static String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(PubVariable.date_format);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
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
                    jsonParam.put("usuario_tran", "phone_user");
                    jsonParam.put("datetime", getCurrentTime());
                    jsonParam.put("public_id", public_id);

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




}