package com.example.omarf.tourmate;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by omarf on 2/19/2017.
 */

public class MapFragment extends SupportMapFragment implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener,
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener {
    private static final int PERMISION_REQUEST_CODE = 1;
    private static final String TAG = "mapFragmentTag";
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 900;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest=LocationRequest.create()
                .setFastestInterval(1*1000)
                .setInterval(10*1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        getMapAsync(this);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {


        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
     //   LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void initListeners() {
       Log.i(TAG,"init Listeners");
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(this);

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

        setMarker(latLng);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        initListeners();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG,"on Connect is called");
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISION_REQUEST_CODE);

        } else {

                setCurrentLocation();
        }

    }

    private void setCurrentLocation() {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            handleNewLocation(location);
        } else {
            mCurrentLocation = location;
            Toast.makeText(getActivity(), mCurrentLocation.getLatitude() + " " + mCurrentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
            Log.i(TAG," setCurrent "+mCurrentLocation.getLatitude() + " " + mCurrentLocation.getLongitude());
            initCamera();
        }

    }

    private void handleNewLocation(Location location) {
        if (location==null){
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
        }
        else {
            mCurrentLocation=location;
            Toast.makeText(getActivity(), mCurrentLocation.getLatitude() + " " + mCurrentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
            Log.i(TAG," handle "+mCurrentLocation.getLatitude() + " " + mCurrentLocation.getLongitude());

            initCamera();
        }
    }

    private void initCamera() {
       /* CameraPosition cameraPosition=CameraPosition.builder()
                .zoom(16f)
                .bearing(0f)
                .tilt(0f)
                .build();*/
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude())));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16f));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    setCurrentLocation();
                }

            }
        }
    }

   private void  setMarker(LatLng latLng){
       MarkerOptions options=new MarkerOptions();
       options.position(latLng);
       options.icon(BitmapDescriptorFactory.defaultMarker());
       mMap.addMarker(options);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);

    }
}
