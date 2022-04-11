package com.example.familymap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import model.Event;
import model.FamilyMember;
import model.Person;

public class PersonActivity extends UpNavigatingActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        getSupportActionBar().setTitle("Family Map: Person Details");

        TextView firstNameView = findViewById(R.id.person_first_name);
        firstNameView.setText(DataCache.getSelectedPerson().getFirstName());

        TextView lastNameView = findViewById(R.id.person_last_name);
        lastNameView.setText(DataCache.getSelectedPerson().getLastName());

        TextView genderView = findViewById(R.id.person_gender);
        if(DataCache.getSelectedPerson().getGender().equals("m")) {
            genderView.setText(R.string.male_label);
        }
        else {
            genderView.setText(R.string.female_label);
        }

        ExpandableListView expandableListView = findViewById(R.id.expandableListView);

        DataCache.setSelectedPersonFamily(); //TODO: Should I combine set/get methods?
        List<FamilyMember> family = DataCache.getSelectedPersonFamily();
        List<Event> events = DataCache.getPersonEvents(DataCache.getSelectedPerson());

        expandableListView.setAdapter(new ExpandableListAdapter(family, events));
        expandableListView.expandGroup(0);
        expandableListView.expandGroup(1);
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private static final int FAMILY_GROUP_POSITION = 0;
        private static final int EVENT_GROUP_POSITION = 1;

        private List<FamilyMember> family;
        private List<Event> events;

        public ExpandableListAdapter(List<FamilyMember> family, List<Event> events) {
            this.family = family;
            this.events = events;
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch(groupPosition) {
                case FAMILY_GROUP_POSITION:
                    return family.size();
                case EVENT_GROUP_POSITION:
                    return events.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int i) {
            return null;
        }

        @Override
        public Object getChild(int i, int i1) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.expandable_list_group, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.list_title);

            switch (groupPosition) {
                case FAMILY_GROUP_POSITION:
                    titleView.setText(R.string.family_title);
                    break;
                case EVENT_GROUP_POSITION:
                    titleView.setText(R.string.events_title);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView;

            switch(groupPosition) {
                case FAMILY_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.person_item, parent, false);
                    initializeFamilyView(itemView, childPosition);
                    break;
                case EVENT_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.event_item, parent, false);
                    initializeEventView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return itemView;
        }

        private void initializeFamilyView(View personItemView, final int childPosition) {
            TextView personNameView = personItemView.findViewById(R.id.person_item_name);
            personNameView.setText(family.get(childPosition).getFullName());

            TextView personRelationView = personItemView.findViewById(R.id.person_item_relationship);
            personRelationView.setText(family.get(childPosition).getRelation());

            ImageView personIconView = personItemView.findViewById(R.id.person_item_icon);
            Person person = DataCache.getPersonByID(family.get(childPosition).getPersonID());
            personIconView.setImageDrawable(DataCache.getGenderIcon(person, PersonActivity.this));

            personItemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    //Toast.makeText(PersonActivity.this, "You clicked " + personNameView.getText(), Toast.LENGTH_SHORT).show();
                    DataCache.setSelectedPerson(family.get(childPosition).getPersonID());
                    Intent intent = new Intent(PersonActivity.this, PersonActivity.class);
                    startActivity(intent);
                }
            });
        }

        private void initializeEventView(View eventItemView, final int childPosition) {
            if(DataCache.isEventShown(events.get(childPosition))) {
                TextView eventDetailView = eventItemView.findViewById(R.id.event_item_details);
                eventDetailView.setText(DataCache.eventInfoString(events.get(childPosition)));

                TextView eventPersonView = eventItemView.findViewById(R.id.event_item_person);
                eventPersonView.setText(DataCache.getFullName(events.get(childPosition).getPersonID()));

                ImageView eventIconView = eventItemView.findViewById(R.id.event_item_icon);
                eventIconView.setImageDrawable(DataCache.getEventIcon(PersonActivity.this));

                eventItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(PersonActivity.this, "You clicked " + eventDetailView.getText(), Toast.LENGTH_SHORT).show();
                        DataCache.setSelectedEvent(events.get(childPosition));
                        Intent intent = new Intent(PersonActivity.this, EventActivity.class);
                        startActivity(intent);
                    }
                });
            }
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }
    }
}