package com.example.familymap;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.List;

import model.Event;
import model.Person;

public class SearchActivity extends UpNavigatingActivity {
    private static final int PERSON_ITEM_VIEW_TYPE = 0;
    private static final int EVENT_ITEM_VIEW_TYPE = 1;

    private SearchView searchView;
    private RecyclerView recyclerView;
    private List<Person> people = new ArrayList<>(); //TODO: Should I really initialize these here?
    private List<Event> events = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setTitle("Family Map: Search");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        searchView = findViewById(R.id.search_view);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search();
                return false;
            }
        });

        SearchAdapter adapter = new SearchAdapter(people, events);
        recyclerView.setAdapter(adapter);
    }

    private void search() {
        String query = String.valueOf(searchView.getQuery()).toLowerCase();

        if(!query.equals("")) {
            people = DataCache.filterPeopleByQuery(query);
            events = DataCache.filterEventsByQuery(query);
            SearchAdapter adapter = new SearchAdapter(people, events);
            recyclerView.setAdapter(adapter);
        }
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
        private final List<Person> people;
        private final List<Event> events;

        private SearchAdapter(List<Person> people, List<Event> events) {
            this.people = people;
            this.events = events;
        }

        @Override
        public int getItemViewType(int position) {
            return position < people.size() ? PERSON_ITEM_VIEW_TYPE : EVENT_ITEM_VIEW_TYPE;
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;

            if (viewType == PERSON_ITEM_VIEW_TYPE) {
                view = getLayoutInflater().inflate(R.layout.person_item, parent, false);
            }
            else {
                view = getLayoutInflater().inflate(R.layout.event_item, parent, false);
            }

            return new SearchViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            if(position < people.size()) {
                holder.bind(people.get(position));
            }
            else {
                holder.bind(events.get(position - people.size()));
            }
        }

        @Override
        public int getItemCount() {
            return people.size() + events.size();
        }
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView name;
        private final TextView details;
        private final ImageView icon;

        private final int viewType;
        private Person person;
        private Event event;

        SearchViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;

            itemView.setOnClickListener(this);

            if(viewType == PERSON_ITEM_VIEW_TYPE) {
                name = itemView.findViewById(R.id.person_item_name);
                details = null;
                icon = itemView.findViewById(R.id.person_item_icon);
            }
            else {
                name = itemView.findViewById(R.id.event_item_details);
                details = itemView.findViewById(R.id.event_item_person);
                icon = itemView.findViewById(R.id.event_item_icon);
            }
        }

        private void bind(Person person) {
            this.person = person;
            name.setText(DataCache.getFullName(person.getPersonID()));
            icon.setImageDrawable(DataCache.getGenderIcon(person, SearchActivity.this));

            itemView.setTag(person);
        }

        private void bind(Event event) {
            this.event = event;
            name.setText(DataCache.eventInfoString(event));
            details.setText(DataCache.getFullName(event.getPersonID()));
            icon.setImageDrawable(DataCache.getEventIcon(SearchActivity.this));

            itemView.setTag(event);
        }

        @Override
        public void onClick(View view) {
            Intent intent;

            if(viewType == PERSON_ITEM_VIEW_TYPE) {
                Person person = (Person) view.getTag();
                DataCache.setSelectedPerson(person);
                DataCache.setSelectedEvent(null);

                intent = new Intent(SearchActivity.this, PersonActivity.class);
            }
            else {
                Event event = (Event) view.getTag();
                DataCache.setSelectedEvent(event);
                DataCache.setSelectedPerson(event.getPersonID());

                intent = new Intent(SearchActivity.this, EventActivity.class);
            }

            startActivity(intent);
        }
    }
}