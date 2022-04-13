package com.example.familymap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.util.List;
import java.util.logging.Logger;

import model.Event;
import model.Person;

public class MapFragment extends Fragment {
    private final Logger logger = Logger.getLogger("MapFragment");
    private View mapView;
    private GoogleMap map;

    private final float BASE_POLYLINE_WIDTH = 15;
    private final int FAMILY_TREE_LINE_COLOR = Color.BLUE;
    private final int LIFE_STORY_LINE_COLOR = Color.GREEN;
    private final int SPOUSE_LINE_COLOR = Color.RED;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            logger.info("In onMapReady");

            map = googleMap;

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            DataCache.initializeSettings(preferences);

            if(DataCache.isEventActivity()) {
                Event centerEvent = DataCache.getSelectedEvent();

                centerCamera(centerEvent);
                updateEventDetailsView(centerEvent);

                drawPolyLines();
            }

            addEventMarkers();
            createMarkerClickListener(googleMap);
        }
    };

    private void createMarkerClickListener(GoogleMap map) {
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                Event event = (Event) marker.getTag();
                assert event != null;
                DataCache.setSelectedEvent(event);
                DataCache.setSelectedPerson(event.getPersonID());

                map.clear();
                addEventMarkers();
                drawPolyLines();

                centerCamera(event);
                updateEventDetailsView(event);

                return true;
            }
        });
    }

    private void centerCamera(Event event) {
        LatLng eventLatLng = new LatLng(event.getLatitude(), event.getLongitude() );
        map.animateCamera(CameraUpdateFactory.newLatLng(eventLatLng) );
    }

    private void updateEventDetailsView(Event event) {
        String personID = event.getPersonID();
        String personName = DataCache.getFullName(personID);
        String personGender = DataCache.getPersonByID(personID).getGender();

        String eventDetails = personName + " (" + personGender + ")\n"
                + DataCache.eventInfoString(event);

        mapView.findViewById(R.id.view_select_marker_prompt).setVisibility(View.GONE);
        TextView eventDetailsView = (mapView.findViewById(R.id.view_event_details) );
        eventDetailsView.setText(eventDetails);
        eventDetailsView.setVisibility(View.VISIBLE);

        ImageView eventIconView = mapView.findViewById(R.id.event_details_icon);
        eventIconView.setImageDrawable(DataCache.getGenderIcon(DataCache.getSelectedPerson(), getContext()));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!DataCache.isEventActivity()) {
            setHasOptionsMenu(true);
        }

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
                DataCache.saveSelectedEvent();
                DataCache.setSelectedPerson(DataCache.getSelectedEvent().getPersonID());
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

    @Override
    public void onResume() {
        super.onResume();
        if(map != null) {
            map.clear();
            addEventMarkers();

            Event selectedEvent = DataCache.getSelectedEvent();
            if(selectedEvent != null && DataCache.isEventShown(selectedEvent)) {
                drawPolyLines();
            }
        }
    }

    private void addEventMarkers() {
        for(Event event : DataCache.getEvents().values()) {
            assert event != null;

            if (DataCache.isEventShown(event)) {
                Float eventColor = DataCache.getEventColors().get(event.getEventType().toLowerCase());
                assert eventColor != null;

                Marker marker = map.addMarker(new MarkerOptions()
                        .position(new LatLng(event.getLatitude(), event.getLongitude()))
                        .icon(BitmapDescriptorFactory.defaultMarker(eventColor)));

                assert marker != null;
                marker.setTag(event);
            }
        }
    }

    private void drawPolyLines() {
        boolean showSpouseLines = DataCache.showSpouseLines();
        if(showSpouseLines) {
            drawSpouseLine();
        }

        boolean showLifeStoryLines = DataCache.showLifeStoryLines();
        if(showLifeStoryLines) {
            drawLifeStoryLines();
        }

        boolean showFamilyTreeLines = DataCache.showAncestorLines();
        if(showFamilyTreeLines) {
            drawFamilyTreeLines();
        }
    }

    private LatLng eventToLatLng(Event event) {
        double lat = event.getLatitude();
        double lng = event.getLongitude();
        return new LatLng(lat, lng);
    }

    private void drawSpouseLine() {
        if(DataCache.showMaleEvents() && DataCache.showFemaleEvents()) {
            Event selectedEvent = DataCache.getSelectedEvent();
            String spouseID = DataCache.getSelectedPerson().getSpouseID();

            drawLineToFirstEvent(spouseID, selectedEvent, SPOUSE_LINE_COLOR, BASE_POLYLINE_WIDTH);
        }
    }

    private void drawFamilyTreeLines() {
        boolean showMaleEvents = DataCache.showMaleEvents();
        boolean showFemaleEvents = DataCache.showFemaleEvents();
        boolean showFatherSide = DataCache.showFatherSide();
        boolean showMotherSide = DataCache.showMotherSide();

        Person person = DataCache.getSelectedPerson();
        Event event = DataCache.getSelectedEvent();

        String fatherID = person.getFatherID();
        if(fatherID != null && showFatherSide && showMaleEvents) {
            drawLineToFirstEvent(fatherID, event, FAMILY_TREE_LINE_COLOR, BASE_POLYLINE_WIDTH);
            drawAncestorLinesHelper(fatherID, 2);
        }

        String motherID = person.getMotherID();
        if(motherID != null && showMotherSide && showFemaleEvents) {
            drawLineToFirstEvent(motherID, event, FAMILY_TREE_LINE_COLOR, BASE_POLYLINE_WIDTH);
            drawAncestorLinesHelper(motherID, 2);
        }
    }

    private void drawAncestorLinesHelper(String personID, int generation) {
        boolean showMaleEvents = DataCache.showMaleEvents();
        boolean showFemaleEvents = DataCache.showFemaleEvents();

        Person person = DataCache.getPersonByID(personID);

        String fatherID = person.getFatherID();
        if(fatherID != null && showMaleEvents) {
            drawLineBetweenFirstEvents(personID, fatherID, FAMILY_TREE_LINE_COLOR, BASE_POLYLINE_WIDTH/generation);
            drawAncestorLinesHelper(fatherID, generation + 1);
        }

        String motherID = person.getMotherID();
        if(motherID != null && showFemaleEvents) {
            drawLineBetweenFirstEvents(personID, motherID, FAMILY_TREE_LINE_COLOR, BASE_POLYLINE_WIDTH/generation);
            drawAncestorLinesHelper(motherID, generation + 1);
        }
    }

    private void drawLifeStoryLines() {
        boolean showMaleEvents = DataCache.showMaleEvents();
        boolean showFemaleEvents = DataCache.showFemaleEvents();

        Person person = DataCache.getSelectedPerson();
        String gender = person.getGender();

        if(gender.equals("m") && showMaleEvents || gender.equals("f") && showFemaleEvents) {
            List<Event> personEvents = DataCache.getPersonEvents(person);
            for (int i = 0; i < personEvents.size() - 1; i++) {
                map.addPolyline(new PolylineOptions()
                        .add(eventToLatLng(personEvents.get(i)), eventToLatLng(personEvents.get(i + 1)))
                        .width(BASE_POLYLINE_WIDTH)
                        .color(Color.GREEN));
            }
        }
    }

    private void drawLineToFirstEvent(String personID, Event startingEvent, int color, float width) {
        Event firstEvent = DataCache.getPersonFirstEvent(personID);
        map.addPolyline(new PolylineOptions()
                .add(eventToLatLng(startingEvent), eventToLatLng(firstEvent))
                .width(width)
                .color(color));
    }

    private void drawLineBetweenFirstEvents(String personID1, String personID2, int color, float width) {
        Event firstEvent1 = DataCache.getPersonFirstEvent(personID1);
        Event firstEvent2 = DataCache.getPersonFirstEvent(personID2);
        map.addPolyline(new PolylineOptions()
                .add(eventToLatLng(firstEvent1), eventToLatLng(firstEvent2))
                .width(width)
                .color(color));
    }
}