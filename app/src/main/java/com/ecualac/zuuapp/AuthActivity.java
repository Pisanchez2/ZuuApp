package com.ecualac.zuuapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.util.Objects;

public class AuthActivity extends AppCompatActivity {

    private DatabaseReference reffBuscar ;
    private TextView email, password, registro;
    private Button ingresar;
    Intent showReg;
    Intent showSup;
    Intent showTanque;
    int lvl;
    String ApeAux;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        email = findViewById(R.id.email_text);
        password = findViewById(R.id.pass_text);
        registro = findViewById(R.id.Reg_btn);
        ingresar = findViewById(R.id.LogIn_Btn);
        reffBuscar = FirebaseDatabase.getInstance().getReference().child("Usuarios");

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

            }

    public void onClick(View view) {
        startActivity(showReg);
    }
}