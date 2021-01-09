package com.ecualac.zuuapp.ui.tanqueros.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ecualac.zuuapp.PubVariable;
import com.ecualac.zuuapp.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Home_Tanqueros_Fragment extends Fragment {

    private static final String TAG = "Home Tanqueros";

    static class Product{
        String Nombre, Presentacion, codebar ,product_public_id, public_id;
        int c_disponible;

        Product(String Nombre, String Presentacion, int c_disponible,String codebar, String product_public_id, String public_id) {
            this.Nombre = Nombre;
            this.Presentacion = Presentacion;
            this.c_disponible = c_disponible;
            this.codebar = codebar;
            this.product_public_id = product_public_id;
            this.public_id =public_id;
        }
    }
    MyAdapter_home_tanqueros myAdapter;
    private ArrayList<Product> productos;
    TextView CodeText;
    JSONObject json;
    JSONParser parser;
    JSONArray jsonarr;

    private final OkHttpClient httpClient = new OkHttpClient();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_home_tanqueros, container, false);
        final ImageButton QRbuscarBtn = root.findViewById(R.id.QRBuscarBtn);
        CodeText = root.findViewById(R.id.CodeText);
        ListView listview = (ListView) root.findViewById(R.id.product_listview);

        try {
            productos=new ArrayList<>();
            myAdapter= new MyAdapter_home_tanqueros(getContext(),R.layout.product_list_item,productos);
            myAdapter.updateReceiptsList(productos);
        }catch (Exception e){
            Log.e(TAG, "ANT ", e);
        }
        try {
            sendGET3();
            Thread.sleep(10);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        //se añade el header
        try {
            ViewGroup header = (ViewGroup) inflater.inflate(R.layout.product_list_header, listview, false);
            listview.addHeaderView(header);
            //myAdapter.updateReceiptsList(productos);
            listview.setAdapter(myAdapter);
            myAdapter.notifyDataSetChanged();
        }catch (Exception e){
            Log.e(TAG, "DES ",e );
        }

        listview.setOnItemClickListener((parent, view, position, id) -> {
            //Toast.makeText(getActivity(), "Se ha seleccionado el medidor: "+ productos.get(position-1).Presentacion, Toast.LENGTH_LONG).show();
            if(position==0){

            }else {
                gotoEnviarProducts(productos.get(position - 1).Nombre, productos.get(position - 1).Presentacion,
                        String.valueOf(productos.get(position - 1).c_disponible)
                        , productos.get(position - 1).public_id);
            }
        });

        QRbuscarBtn.setOnClickListener(view -> {
            // Funcion para actualizar los campos en el frame de Tomar Mediciones
            escanear();
        });

        return root;
    }

    private void sendGET3() throws IOException {

        Request request = new Request.Builder()
                .url(PubVariable.postUrl+":5000/api/productos")
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
                    json = (JSONObject) parser.parse(respstring);
                    jsonarr = new JSONArray();
                    jsonarr = (JSONArray) json.get("productos");
                    System.out.println("LENGHT++"+json.size());
                    System.out.println("ARRAY++"+jsonarr.toString());

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myAdapter.notifyDataSetChanged();
                        }
                    });
                    popList();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myAdapter.notifyDataSetChanged();
                        }
                    });

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void popList() {

        for (int i = 0; i < jsonarr.size(); i++) {
            JSONObject c = (JSONObject) jsonarr.get(i);// Used JSON Object from Android
            System.out.println("++" + c.size());
            int c_dispo=0;
            int product_id = 0;
            //Storing each Json in a string variable
            try {
                String public_id = (String) c.get("public_id");
                String product_name = (String) c.get("commercial_name");
                String code_bar = (String) c.get("code_bar");
                String presentacion = (String) c.get("presentation");
                String product_public_id = (String) c.get("public_id");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    c_dispo = Math.toIntExact((Long) c.get("cant_disponible_dia"));
                }

                Product newprod = new Product(product_name, presentacion, c_dispo, code_bar, product_public_id,public_id);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        productos.add(newprod);
                        myAdapter.notifyDataSetChanged();
                    }
                });
            } catch (Exception e) {
                Log.e("POPLIST", "Error al obtener los datos: ", e);
            }
        }
    }


    private void escanear() {
        IntentIntegrator intent=IntentIntegrator.forSupportFragment(Home_Tanqueros_Fragment.this);
        intent.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intent.setPrompt("ESCANEAR CODIGO");
        intent.setCameraId(0);
        intent.setBeepEnabled(false);
        intent.setOrientationLocked(false);
        intent.setBeepEnabled(true);
        intent.setBarcodeImageEnabled(false);
        intent.initiateScan();
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result= IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents()==null){
                Toast.makeText(getContext(),"Se cancelo el escaneo", Toast.LENGTH_SHORT).show();
            }else {
                //CodeText.setText(result.getContents());
                try{
                    String code = result.getContents();
                    List<Product> selectitem = productos.stream()
                            .filter(p -> p.codebar.equals((code)))
                            .collect(Collectors.toList());
                    String Nombre = selectitem.get(0).Nombre;
                    String Presentacion = selectitem.get(0).Presentacion;
                    int Stock = selectitem.get(0).c_disponible;
                    String public_id = selectitem.get(0).public_id;

                    Log.i("selectitem", "onActivityResult: " + selectitem);
                    gotoEnviarProducts(Nombre, Presentacion, String.valueOf(Stock), public_id);
                }catch (Exception e){
                    CodeText.setText("Error al encontrar el código: "+result.getContents()+" , revise su conexión o verifique si el código está registrado");
                    CodeText.setTextColor(Color.YELLOW);
                }
            }
        }else{
            super.onActivityResult(requestCode,resultCode,data);
        }
    }


    private void gotoEnviarProducts(String Nombre, String Presentacion, String Stock,String public_id) {
        Bundle args = new Bundle();
        args.putString("Nombre", Nombre);
        args.putString("Presentacion", Presentacion);
        args.putString("Stock", Stock);
        args.putString("public_id", public_id);
        Fragment someFragment = new Enviar_Products_Fragment();
        assert getFragmentManager() != null;
        someFragment.setArguments(args);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_tanqueros, someFragment ); // give your fragment container id in first parameter
        transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
        transaction.commit();
    }

}