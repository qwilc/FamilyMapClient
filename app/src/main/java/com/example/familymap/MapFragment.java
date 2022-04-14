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
    private final DataCache dataCache = DataCache.getInstance();
    private View mapView;
    private GoogleMap map;

    private final float BASE_POLYLINE_WIDTH = 15;
    private final int FAMILY_TREE_LINE_COLOR = Color.BLUE;
    private final int LIFE_STORY_LINE_COLOR = Color.GREEN;
    private final int SPOUSE_LINE_COLOR = Color.RED;

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            logger.info("In onMapReady");

            map = googleMap;

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            dataCache.initializeSettings(preferences);

            if(dataCache.isEventActivity()) {
                Event centerEvent = dataCache.getSelectedEvent();

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
                dataCache.setSelectedEvent(event);
                dataCache.setSelectedPerson(event.getPersonID());

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
        String personName = dataCache.getFullName(personID);
        String personGender = dataCache.getPersonByID(personID).getGender();

        String eventDetails = personName + " (" + personGender + ")\n"
                + dataCache.eventInfoString(event);

        mapView.findViewById(R.id.view_select_marker_prompt).setVisibility(View.GONE);
        TextView eventDetailsView = (mapView.findViewById(R.id.view_event_details) );
        eventDetailsView.setText(eventDetails);
        eventDetailsView.setVisibility(View.VISIBLE);

        ImageView eventIconView = mapView.findViewById(R.id.event_details_icon);
        eventIconView.setImageDrawable(dataCache.getGenderIcon(dataCache.getSelectedPerson(), getContext()));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!dataCache.isEventActivity()) {
            setHasOptionsMenu(true);
        }

        Iconify.with(new FontAwesomeModule());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
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
        int itemId = menu.getItemId();

        if (itemId == R.id.search_menu_item) {
            intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
            return true;
        }
        else if (itemId == R.id.settings_menu_item) {
            intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else {
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
                dataCache.saveSelectedEvent();
                dataCache.setSelectedPerson(dataCache.getSelectedEvent().getPersonID());
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

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        dataCache.initializeSettings(preferences);

        if(map != null) {
            logger.finest(dataCache.getSelectedPerson().getPersonID());
            map.clear();
            addEventMarkers();

            Event selectedEvent = dataCache.getSelectedEvent();
            if(selectedEvent != null && dataCache.isEventShown(selectedEvent)) {
                drawPolyLines();
            }
        }
    }

    private void addEventMarkers() {
        for(Event event : dataCache.getEvents().values()) {
            assert event != null;

            if (dataCache.isEventShown(event)) {
                Float eventColor = dataCache.getEventColors().get(event.getEventType().toLowerCase());
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
        boolean showSpouseLines = dataCache.isSpouseLineEnabled();
        if(showSpouseLines) {
            drawSpouseLine();
        }

        boolean showLifeStoryLines = dataCache.isLifeStoryEnabled();
        if(showLifeStoryLines) {
            drawLifeStoryLines();
        }

        boolean showFamilyTreeLines = dataCache.isFamilyTreeEnabled();
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
        if(dataCache.isMaleEventsEnabled() && dataCache.isFemaleEventsEnabled()) {
            Event selectedEvent = dataCache.getSelectedEvent();
            String spouseID = dataCache.getSelectedPerson().getSpouseID();

            drawLineToFirstEvent(spouseID, selectedEvent, SPOUSE_LINE_COLOR);
        }
    }

    private void drawFamilyTreeLines() {
        boolean showMaleEvents = dataCache.isMaleEventsEnabled();
        boolean showFemaleEvents = dataCache.isFemaleEventsEnabled();
        boolean showFatherSide = dataCache.isFatherSideEnabled();
        boolean showMotherSide = dataCache.isMotherSideEnabled();

        Person person = dataCache.getSelectedPerson();
        Event event = dataCache.getSelectedEvent();

        String fatherID = person.getFatherID();
        if(fatherID != null && showFatherSide && showMaleEvents) {
            drawLineToFirstEvent(fatherID, event, FAMILY_TREE_LINE_COLOR);
            drawAncestorLinesHelper(fatherID, 2);
        }

        String motherID = person.getMotherID();
        if(motherID != null && showMotherSide && showFemaleEvents) {
            drawLineToFirstEvent(motherID, event, FAMILY_TREE_LINE_COLOR);
            drawAncestorLinesHelper(motherID, 2);
        }
    }

    private void drawAncestorLinesHelper(String personID, int generation) {
        boolean showMaleEvents = dataCache.isMaleEventsEnabled();
        boolean showFemaleEvents = dataCache.isFemaleEventsEnabled();

        Person person = dataCache.getPersonByID(personID);

        String fatherID = person.getFatherID();
        if(fatherID != null && showMaleEvents) {
            drawLineBetweenFirstEvents(personID, fatherID, BASE_POLYLINE_WIDTH/generation);
            drawAncestorLinesHelper(fatherID, generation + 1);
        }

        String motherID = person.getMotherID();
        if(motherID != null && showFemaleEvents) {
            drawLineBetweenFirstEvents(personID, motherID, BASE_POLYLINE_WIDTH/generation);
            drawAncestorLinesHelper(motherID, generation + 1);
        }
    }

    private void drawLifeStoryLines() {
        boolean showMaleEvents = dataCache.isMaleEventsEnabled();
        boolean showFemaleEvents = dataCache.isFemaleEventsEnabled();

        Person person = dataCache.getSelectedPerson();
        String gender = person.getGender();

        if(gender.equals("m") && showMaleEvents || gender.equals("f") && showFemaleEvents) {
            List<Event> personEvents = dataCache.getPersonEvents(person);
            for (int i = 0; i < personEvents.size() - 1; i++) {
                map.addPolyline(new PolylineOptions()
                        .add(eventToLatLng(personEvents.get(i)), eventToLatLng(personEvents.get(i + 1)))
                        .width(BASE_POLYLINE_WIDTH)
                        .color(LIFE_STORY_LINE_COLOR));
            }
        }
    }

    private void drawLineToFirstEvent(String personID, Event startingEvent, int color) {
        Event firstEvent = dataCache.getPersonFirstEvent(personID);
        map.addPolyline(new PolylineOptions()
                .add(eventToLatLng(startingEvent), eventToLatLng(firstEvent))
                .width(BASE_POLYLINE_WIDTH)
                .color(color));
    }

    private void drawLineBetweenFirstEvents(String personID1, String personID2, float width) {
        Event firstEvent1 = dataCache.getPersonFirstEvent(personID1);
        Event firstEvent2 = dataCache.getPersonFirstEvent(personID2);
        map.addPolyline(new PolylineOptions()
                .add(eventToLatLng(firstEvent1), eventToLatLng(firstEvent2))
                .width(width)
                .color(FAMILY_TREE_LINE_COLOR));
    }
}