package com.example.logingps;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Gps extends AppCompatActivity {



    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ListView listV_dados;
    private List<GpsCadastro> listgpsCadastros = new ArrayList<GpsCadastro>();
    private ArrayAdapter<GpsCadastro> arrayAdapterGps;



    private static final int REQUEST_CHECK_SETTINGS = 613;
    private LocationRequest mLocationRequest;

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        listV_dados = (ListView)findViewById(R.id.ListV_dados);



        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        for(String provider : locationManager.getAllProviders()){
            Toast.makeText(getApplicationContext(), provider, Toast.LENGTH_LONG).show();
        }


        createLocationRequest();


        Button fab = findViewById(R.id.gps);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                pedirPermissoes();
                askForLocationChange();
                configurarServico();
            }
        });
inicializarFirebase();
eventoDatabase();


    }
int test = 5;

    private void eventoDatabase() {
        databaseReference.child("Gps").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listgpsCadastros.clear();
                for (DataSnapshot test: dataSnapshot.getChildren()){
                    GpsCadastro p = test.getValue(GpsCadastro.class);
                    listgpsCadastros.add(p);

                }
                arrayAdapterGps = new ArrayAdapter<GpsCadastro>(Gps.this,android.R.layout.simple_list_item_1,listgpsCadastros);
                listV_dados.setAdapter(arrayAdapterGps);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void inicializarFirebase() {
        FirebaseApp.initializeApp(Gps.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();


    }




    private void askForLocationChange() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Toast.makeText(Gps.this, "Location is already on", Toast.LENGTH_SHORT).show();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(Gps.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException ignored) {
                    }
                }
            }
        });
    }






    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(100000);
        mLocationRequest.setFastestInterval(50000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void pedirPermissoes() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else
            configurarServico();
    }

    private void configurarServico() {

        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    atualizar(location);
                }

                public void onStatusChanged(String provider, int status, Bundle extras) { }

                public void onProviderEnabled(String provider) { }

                public void onProviderDisabled(String provider) { }
            };
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }catch(SecurityException ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void atualizar(Location location)
    {

        SimpleDateFormat dateFormatbra = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss");
        SimpleDateFormat dateFormat_hora = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat dateFormat_minutos = new SimpleDateFormat("mm");
        SimpleDateFormat dateFormat_seg = new SimpleDateFormat("ss");
        Date data = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        Date data_atual = cal.getTime();

        String data_completa = dateFormat.format(data_atual);
        String hora_atual = dateFormat_hora.format(data_atual);
        String min_atual = dateFormat_minutos.format(data_atual);
        String segs = dateFormat_seg.format(data_atual);
        String databra = dateFormatbra.format(data_atual);


        Log.i("data_completa", data_completa);
        Log.i("data_atual", data_atual.toString());
        Log.i("hora_atual", hora_atual); // Esse é o que você quer


        Double latPoint = location.getLatitude();
        Double lngPoint = location.getLongitude();

        String latitude = latPoint.toString();
        // Log.i("GPSLATITUDE:", latitude);
        String longitude = lngPoint.toString();
        // Log.i("GPSLONGITUDE:", longitude);

        String coordenadas = latitude + "## E ##" + longitude;

        int min = Integer.parseInt(String.valueOf(min_atual));
        int ss =  Integer.parseInt(String.valueOf(segs));

        Log.i("GPSLONGITUDE:", coordenadas);
        Log.i("GPSLONGITUDE:", data_completa);

        //final long TEMPO = (5000 * 60); //executa a cada 5 min
       // Timer timer = new Timer();
      //  TimerTask timerTask = new TimerTask() {
        //    @Override
        //    public void run() {
        //        System.out.println("executando...");
        //    }
      //  };
     //   timer.scheduleAtFixedRate(timerTask, TEMPO, TEMPO);



        GpsCadastro ps = new GpsCadastro();
        ps.setId(UUID.randomUUID().toString());
        ps.setCordenadas(latitude.toString());
        ps.setCordendas2(longitude.toString());
        ps.setDatahora(data_completa.toString());
        databaseReference.child("Gps").child(ps.getId()).setValue(ps);

    }


}







