package com.macrobios.trovamiapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.internal.ConnectionCallbacks;
import com.google.android.gms.common.api.internal.OnConnectionFailedListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final int RANGE_TO_DISPLAY_TACO_MARKER_IN_METTERS = 50;
    //private GoogleApiClient googleApiClient;
    private static final int ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE = 100;

    //Google MAP
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private Location userLocation;
    private ArrayList<Taco> tacoList;
    private ArrayList<Taco> filteredTacoList;

    private Button btnHereMaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnHereMaps = findViewById(R.id.btnHereMaps);
        requestLocationUser();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        tacoList = new ArrayList<>();
        filteredTacoList = new ArrayList<>();
        tacoList.add(new Taco(21.1026115, -101.7315783, "Arrachera"));
        tacoList.add(new Taco(21.1034275, -101.7309663, "Cerdo"));
        tacoList.add(new Taco(21.1037756, -101.731642, "Carne Asada"));
        tacoList.add(new Taco(21.102544, -101.7319527, "Pollo"));
        tacoList.add(new Taco(21.103064, -101.7348067, "Champiñones"));

        btnHereMaps.setOnClickListener(v -> {
            if(userLocation != null){
                Intent intent = new Intent(this, HereActivity.class);
                intent.putExtra("lat", userLocation.getLatitude());
                intent.putExtra("long", userLocation.getLongitude());
                intent.putParcelableArrayListExtra("list_tacos", tacoList);
                startActivity(intent);
            } else {
                Toast.makeText(
                        this,
                        "No se esta opteniendo la ubicacion, no podemos mostrarte este mapa",
                        Toast.LENGTH_SHORT).show();
            }

        });
    }




    //Se establece que este metodo se ejecutara en la version de Marshmallow en adelante
    @RequiresApi(api = Build.VERSION_CODES.M)
    //Metodo que se ejecuta cuando el usuario acepta o denega un permiso, optiene el resultado
    //final de la consulta del permiso
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //Se comprueba que el permiso con el que se genero la respuesta sea el mismo que usamos para
        //pedir el permiso
        if(requestCode == ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                requestLocationUser();
            } else if(shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)){
                //Se muestra mensaje al usuario de por que deberia aceptar el permiso
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Acceder a la ubicacion del telefono XD");
                builder.setMessage("Debes aceptar este permiso para poder usar Trovami weon qliao");
                builder.setPositiveButton("aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Array de string con los permisos que pediremos al usuario
                        final String[] permissions = new String[]{ACCESS_FINE_LOCATION};

                        //Muestra un dialogo para pedir los permisos al usuario
                        requestPermissions(permissions, ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE);
                    }
                });
                builder.show();
            }
        }
    }

    /**
     * Hace la peticion de permisos y obtiene las coordenadas si estos fueron concedidos.
     */
    private void requestLocationUser(){
        //Comprueba si la version de android es mayot a Marshmallow
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            //Revision de permisos
            if(checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
                client.getLastLocation().addOnSuccessListener(this, location -> {
                    if(location != null){
                        getUsesLastLocation(location);
                    };
                });
            } else {
                //Si no acepto el permiso
                final String[] permissions = new String[]{ACCESS_FINE_LOCATION};

                //Muestra un dialogo para pedir los permisos al usuario
                requestPermissions(permissions, ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE) ;
            }

        } else {
            FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
            client.getLastLocation().addOnSuccessListener(this, location -> {
                if(location != null){
                    getUsesLastLocation(location);
                }
            });
        }
    }

    /**
     * Muestra las coordenadas en sus views
     * @param userLocation Objeto Location
     */
    private void getUsesLastLocation(Location userLocation){
        if(userLocation != null){
            this.userLocation = userLocation;

            String longitude = String.valueOf(userLocation.getLongitude());
            String latitude = String.valueOf(userLocation.getLatitude());
            String coords = "Longitud: " + longitude + ", " + "Latitude: " + latitude;
            Toast.makeText(this, coords, Toast.LENGTH_LONG);
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        BitmapDescriptor tacoMarkerIcon = BitmapDescriptorFactory.fromResource(R.drawable.taco_marker);

        ArrayList<Taco> filteredTacoList = new ArrayList<>();

        for (Taco t: tacoList) {
            Location tacoLocation = new Location("");
            tacoLocation.setLatitude(t.getLatitude());
            tacoLocation.setLongitude(t.getLongitud());

            int distanceToTaco = Math.round(tacoLocation.distanceTo(userLocation));
            if(distanceToTaco  < RANGE_TO_DISPLAY_TACO_MARKER_IN_METTERS){
                filteredTacoList.add(t);
            }
        }

        // Add a marker in Sydney and move the camera
        LatLng userCoordinates = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(userCoordinates).title("Marker in my house"));

        for (Taco e: filteredTacoList) {
            mMap.addMarker(
                    new MarkerOptions()
                            .position(new LatLng(e.getLatitude(), e.getLongitud()))
                            .title(e.getFlavor()))
                            .setIcon(tacoMarkerIcon);
        }

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(userCoordinates));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userCoordinates, 15));
    }
}