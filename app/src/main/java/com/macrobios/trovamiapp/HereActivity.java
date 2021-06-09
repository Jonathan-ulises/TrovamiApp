package com.macrobios.trovamiapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.mapview.MapError;
import com.here.sdk.mapview.MapImage;
import com.here.sdk.mapview.MapImageFactory;
import com.here.sdk.mapview.MapMarker;
import com.here.sdk.mapview.MapScene;
import com.here.sdk.mapview.MapScheme;
import com.here.sdk.mapview.MapView;

import java.util.ArrayList;

public class HereActivity extends AppCompatActivity {

    private static final String TAG = "here_error";
    private MapView mapView;
    private ArrayList<Taco> tacoArrayListHereMaps;
    private GeoCoordinates geoCoordinatesUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_here);

        //Captura de datos del intent
        Bundle extras = getIntent().getExtras();
        tacoArrayListHereMaps = new ArrayList<>();
        geoCoordinatesUser = new GeoCoordinates(extras.getDouble("lat"), extras.getDouble("long"));
        tacoArrayListHereMaps = extras.getParcelableArrayList("list_tacos");


        //Crear y renderiza el mapa
        mapView = findViewById(R.id.mapHere);
        mapView.onCreate(savedInstanceState);

        loadMapScene();
    }

    private void loadMapScene() {
        MapImage mapImage = MapImageFactory.fromResource(this.getResources(), R.drawable.marker_map);
        MapMarker mapMarker = new MapMarker(geoCoordinatesUser, mapImage);

        if(tacoArrayListHereMaps.isEmpty()){
            Toast.makeText(
                    this,
                    "No se esta opteniendo la ubicacion, no podemos mostrarte este mapa",
                    Toast.LENGTH_SHORT).show();
        }

        mapView.getMapScene().loadScene(MapScheme.NORMAL_DAY, new MapScene.LoadSceneCallback() {
            @Override
            public void onLoadScene(@Nullable  MapError mapError) {
                if(mapError == null){
                    double distanceInMeters = 1000 * 10;
                    mapView.getCamera().lookAt(geoCoordinatesUser, distanceInMeters);
                } else {
                    Log.d(TAG, "Loading map failed: mapError: " + mapError.name());
                }
            }
        });

        //Asigna el marcador del usuario
        mapView.getMapScene().addMapMarker(mapMarker);

        //Asigna los marcadores de los puestos de tacos
        for (Taco t : tacoArrayListHereMaps) {
            MapImage mapImageTaco = MapImageFactory.fromResource(this.getResources(), R.drawable.taco_marker);
            MapMarker mapMarkerTaco = new MapMarker(geoCoordinatesUser, mapImage);
            mapView.getMapScene().addMapMarker(mapMarker);
        }


    }

    //Control de uso del MapView

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}