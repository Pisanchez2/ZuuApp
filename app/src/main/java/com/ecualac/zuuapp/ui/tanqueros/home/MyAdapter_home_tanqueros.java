package com.ecualac.zuuapp.ui.tanqueros.home;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ecualac.zuuapp.R;

import java.util.ArrayList;

public class MyAdapter_home_tanqueros extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<Home_Tanqueros_Fragment.Product> productos;

    public MyAdapter_home_tanqueros(Context context, int layout, ArrayList<Home_Tanqueros_Fragment.Product> productos){
        this.context=context;
        this.layout=layout;
        this.productos=productos;
    }

    public void clear()
    {
        productos.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.productos.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Copiamos la vista
        View v = convertView;

        //Inflamos la vista con nuestro propio layout
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);

        v= layoutInflater.inflate(R.layout.product_list_item, null);
        // Valor actual según la posición

        String currentName = productos.get(position).Nombre;
        String currentPresentacion  = productos.get(position).Presentacion;
        int currentc_disponible = productos.get(position).c_disponible;
        String current_codebar = productos.get(position).codebar;
        // Referenciamos el elemento a modificar y lo rellenamos
        TextView textView1 = (TextView) v.findViewById(R.id.product_item_text1);
        TextView textView2 = (TextView) v.findViewById(R.id.product_item_text2);
        TextView textView3 = (TextView) v.findViewById(R.id.product_item_text3);
        textView1.setText(currentName);
        textView2.setText(currentPresentacion);
        textView3.setText(String.valueOf(currentc_disponible));
        notifyDataSetChanged();
        //Devolvemos la vista inflada
        return v;
    }
}
