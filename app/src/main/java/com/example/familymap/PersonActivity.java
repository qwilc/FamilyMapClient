package com.example.familymap;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.List;
import java.util.SortedSet;

import model.Event;
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

        List<Person> family = DataCache.getSelectedPersonFamily();
        SortedSet<Event> events = DataCache.getPersonEvents(DataCache.getSelectedPerson());
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private static final int FAMILY_GROUP_POSITION = 0;
        private static final int EVENT_GROUP_POSITION = 1;

        private List<Person> family;
        private List<Event> events;

        public ExpandableListAdapter(List<Person> family, List<Event> events) {
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
            personNameView.setText(family.get(childPosition).getFirstName());
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }
    }
}