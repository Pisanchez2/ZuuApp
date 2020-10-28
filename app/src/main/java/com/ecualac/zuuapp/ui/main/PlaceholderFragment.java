package com.ecualac.zuuapp.ui.main;

import android.graphics.Color;
import android.os.Bundle;
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

import java.util.Objects;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    EditText NombresText, ApellidosText, EmailText, ContraText, ConfirmContraText;
    Button Registrar;
    int userlevel;
    private DatabaseReference reffBuscar ;
    private PageViewModel pageViewModel;

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
            userlevel= index;
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
        ConfirmContraText= root.findViewById(R.id.ConfirmContraText);
        Registrar = root.findViewById(R.id.Registrar_btn);
        reffBuscar = FirebaseDatabase.getInstance().getReference().child("Usuarios");

        pageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

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

        return root;
    }

}