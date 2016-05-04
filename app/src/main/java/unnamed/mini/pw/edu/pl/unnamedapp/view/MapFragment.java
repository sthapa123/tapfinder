package unnamed.mini.pw.edu.pl.unnamedapp.view;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;
import unnamed.mini.pw.edu.pl.unnamedapp.R;
import unnamed.mini.pw.edu.pl.unnamedapp.model.googleplaces.Place;
import unnamed.mini.pw.edu.pl.unnamedapp.model.googleplaces.PlacesResult;
import unnamed.mini.pw.edu.pl.unnamedapp.service.GoogleMapsApiService;
import unnamed.mini.pw.edu.pl.unnamedapp.view.place.PlaceFragment;

@SuppressWarnings("ResourceType")
public class MapFragment extends BaseFragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private HashMap<String, Place> markerPlacesIds = new HashMap<>();
    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private Location userLocation;
    private static LocationRequest locationRequest;
    private SupportMapFragment mapFragment;

    @Inject
    GoogleMapsApiService googleApiService;

    public MapFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activityComponent().inject(this);
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentManager fragmentManager = getChildFragmentManager();
        mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .addApi(Places.GEO_DATA_API)
                    .build();
        }

        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(20 * 1000)
                .setFastestInterval(10 * 1000);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
        map.setOnInfoWindowClickListener(marker -> {
            Place place = markerPlacesIds.get(marker.getId());
            PlaceFragment detailsFragment = PlaceFragment.newInstance(place.getPlaceId(), place.getName());
            ((BaseActivity)getActivity()).changeFragmentAndAddToStack(detailsFragment);
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        userLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
        if (userLocation == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } else {
            if(isAdded()) {
                handleNewLocation(userLocation);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onPause() {
        super.onPause();
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onResume() {
        googleApiClient.connect();
        super.onResume();
    }

    private void handleNewLocation(Location location) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 15));
        loadPubs(location);
    }

    private void loadPubs(Location location) {
        String locationString = location.getLatitude() + "," + location.getLongitude();
        googleApiService.getNearbyPubs(locationString, getString(R.string.google_places_key))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::showMarkers);
    }

    private void showMarkers(PlacesResult result) {
        markerPlacesIds.clear();
        for(Place place : result) {
            Place.Location location = place.getGeometry().getLocation();
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLat(), location.getLng()))
                    .title(place.getName())
                    .snippet(place.getFormattedAddress()));
            markerPlacesIds.put(marker.getId(), place);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        userLocation = location;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mapFragment != null && !getActivity().isFinishing()){
            getFragmentManager()
                    .beginTransaction()
                    .remove(mapFragment)
                    .commit();
        }
    }

    @Override
    protected int getTitleResId() {
        return R.string.nearby;
    }
}
