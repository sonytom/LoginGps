package com.example.logingps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Pesquisa extends AppCompatActivity {

    private EditText editPalavra;
    private ListView listVPesquisa;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private List<GpsCadastro> gpsCadastros = new ArrayList<GpsCadastro>();
    private ArrayAdapter<GpsCadastro> arrayAdaptergps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesquisa);
        inicializar();
        inicializaFB();
        eventEdit();
    }

    private void eventEdit() {
        editPalavra.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String palavra = editPalavra.getText().toString().trim();
                pesquisaPalavra(palavra);

            }
        });
    }

    private void pesquisaPalavra(String palavra) {
        Query query;
        if(palavra.equals("")){
            query = databaseReference.child("Gps").orderByChild("cordenadas");
        }else{
            query = databaseReference.child("Gps").orderByChild("cordenadas").startAt(palavra).endAt(palavra+"\uf8ff");

        }
//gpsCadastros.clear();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot objSnapshot:dataSnapshot.getChildren()){
                    GpsCadastro g = objSnapshot.getValue(GpsCadastro.class);
                    gpsCadastros.add(g);
                }
                arrayAdaptergps = new ArrayAdapter<GpsCadastro>(Pesquisa.this,android.R.layout.simple_list_item_1,gpsCadastros);
                listVPesquisa.setAdapter(arrayAdaptergps);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inicializaFB() {
        FirebaseApp.initializeApp(Pesquisa.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

    }

    private void inicializar() {
        editPalavra = (EditText) findViewById(R.id.editPalavra);
        listVPesquisa = (ListView) findViewById(R.id.listVPesquisa);
    }

    @Override
    protected void onResume(){
        super.onResume();
        pesquisaPalavra("");
    }
}
