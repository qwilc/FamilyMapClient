package com.example.familymap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.util.logging.Logger;

import model.Event;
import model.Person;

public class MapFragment extends Fragment {
    private Logger logger = Logger.getLogger("MapFragment");
    private View mapView;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            logger.info("In onMapReady");
            for(String eventID : DataCache.getEvents().keySet()) { //TODO: Better way to iterate?
                Event event = DataCache.getEvents().get(eventID);
                assert event != null;
                Float eventColor = DataCache.getEventColors().get(event.getEventType().toLowerCase() );
                assert eventColor != null;

                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(event.getLatitude(), event.getLongitude() ) )
                        .icon(BitmapDescriptorFactory.defaultMarker(eventColor) ) );

                marker.setTag(event);
            }

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {
                    Event event = (Event) marker.getTag();
                    assert event != null;
                    LatLng eventLatLng = new LatLng(event.getLatitude(), event.getLongitude() );
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(eventLatLng) );

                    String personID = event.getPersonID();
                    Person person = DataCache.getPersonByID(personID);
                    String personName = DataCache.getPersonFullName(personID);
                    String personGender = person.getGender();
                    String eventType = event.getEventType().toUpperCase();
                    String eventYear = Integer.toString(event.getYear() );
                    String eventLocation = event.getCity() + ", " + event.getCountry();

                    String eventDetails = personName + " (" + personGender + ")\n"
                            + eventType + ": " + eventYear + "\n"
                            + eventLocation;

                    mapView.findViewById(R.id.view_select_marker_prompt).setVisibility(View.GONE);
                    TextView eventDetailsView = ( (TextView) mapView.findViewById(R.id.view_event_details) );
                    eventDetailsView.setText(eventDetails);
                    eventDetailsView.setVisibility(View.VISIBLE);

                    return true;
                }
            });
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Iconify.with(new FontAwesomeModule());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.search_menu_item);
        searchMenuItem.setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_search)
                .colorRes(R.color.white)
                .actionBarSize() );

        MenuItem settingsMenuItem = menu.findItem(R.id.settings_menu_item);
        settingsMenuItem.setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_gear)
                .colorRes(R.color.white)
                .actionBarSize() );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
        Intent intent;
        switch(menu.getItemId()) {
            case R.id.search_menu_item:
                intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                return true;
            case R.id.settings_menu_item:
                intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(menu);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mapView = inflater.inflate(R.layout.fragment_map, container, false);

        TextView eventDetailsText = mapView.findViewById(R.id.view_event_details);

        eventDetailsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PersonActivity.class);
                startActivity(intent);
            }
        });

        return mapView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}