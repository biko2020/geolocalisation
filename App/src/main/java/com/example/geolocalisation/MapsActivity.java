package com.example.geolocalisation;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;  // l'appel de l'autorisation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLocation();
    }

    private void fetchLocation() {
        //verification de l'autorisation
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        // definir la derniere emplacement
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {

            @Override
            public void onSuccess(Location location) {
                if (location != null) {

                    currentLocation = location;
                    Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(MapsActivity.this);
                }
            } });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            // Dans le cas lorsque vous cliquez sur le bouton "autoriser" du pop up , il y aura un deuxième appel de la méthode fetchLocation() pour obtenir la dernière localisation

            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation();
                }
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // ajouter Marker pour ENSET Mohammedia

       // LatLng enset = new LatLng(33.70468, -7.36045);
       // mMap.addMarker(new MarkerOptions().position(enset).title("ENSET Mohammedia"));
       // mMap.moveCamera(CameraUpdateFactory.newLatLng(enset));
       // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(enset,10));
       // mMap.getUiSettings().setZoomControlsEnabled(true);

        // latitude  actuelle
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        // Ajouter un Marker de la localisation actuelle dans la carte
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Ma position !");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8));
        googleMap.addMarker(markerOptions);



        //ajouter un cercle sur la position

        CircleOptions circleOptions = new CircleOptions();
       // circleOptions.center(enset);
        circleOptions.center(latLng);
        circleOptions.radius(700);
        circleOptions.fillColor(Color.TRANSPARENT);
        circleOptions.strokeWidth(6);
        mMap.addCircle(circleOptions);

        //modifier le type de la carte

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        //googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            final GoogleMap gmap = mMap;
            @Override
            public void onMapClick(LatLng latLng) {

                gmap.addMarker(new MarkerOptions().position(latLng));
                Toast.makeText(MapsActivity.this, latLng.latitude+"-"
                        +latLng.longitude, Toast.LENGTH_SHORT).show();

            }
        });

    }
}
