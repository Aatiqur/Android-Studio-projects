package com.example.mapview4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends AppCompatActivity {

    private GoogleMap mMap;
    SupportMapFragment supportMapFragment;
    DatabaseReference dref1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        supportMapFragment  = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        dref1= FirebaseDatabase.getInstance().getReference().child("Test");
        dref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String latitude = snapshot.child("lat").getValue().toString();
                String longitude = snapshot.child("lng").getValue().toString();


                //Creating Map..............................................................

                final double latitude1 = Double.parseDouble(latitude);
                final double longitude1 = Double.parseDouble(longitude);
                supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap)
                    {
                        mMap = googleMap;
                        mMap.setMapType(mMap.MAP_TYPE_NORMAL);
                        LatLng Dhaka=new LatLng(latitude1,longitude1);
                        mMap.clear();
                        mMap.setTrafficEnabled(true);
                        mMap.addMarker(new MarkerOptions().position(Dhaka).title("My Car"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(Dhaka));



                    }
                });

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


}