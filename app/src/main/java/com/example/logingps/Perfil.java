package com.example.logingps;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Perfil extends AppCompatActivity {

    private TextView textEmail, textId;
    private Button btnLogout;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        inicializaComponentes();
        eventoCliks();


    }

    private void eventoCliks() {

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Conexao.LogOut();
                finish();
            }
        });
    }


    public void inicializaComponentes() {
        textEmail = (TextView) findViewById(R.id.textPerfilEmail);
        textId = (TextView) findViewById(R.id.textPerfilId);
        btnLogout = (Button) findViewById(R.id.btnPerfilLogout);
    }


    protected void  onStart(){
        super.onStart();
        auth = Conexao.getFirebaseAuth();
        user = Conexao.getFirebaseUser();

        verificaUser();

    }

    private void verificaUser() {
        if(user == null){
            finish();
        }else{
            textEmail.setText("Email:"+user.getEmail());
            textId.setText("ID:"+user.getUid());
        }


    }

}



