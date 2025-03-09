package com.example.android24;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

import android.util.Log;

public class SearchActivity extends AppCompatActivity {

    private EditText editTextSearch;
    private GridView gridViewSearchResults;
    private SearchResultAdapter adapter;
    private ArrayList<Picture> searchResults;
    private ListView listViewSuggestions;
    private ArrayAdapter<String> suggestionsAdapter;
    private ArrayList<String> suggestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        editTextSearch = findViewById(R.id.editTextSearch);
        gridViewSearchResults = findViewById(R.id.gridViewSearchResults);
        searchResults = new ArrayList<>();
        adapter = new SearchResultAdapter(this, searchResults);
        gridViewSearchResults.setAdapter(adapter);

        listViewSuggestions = findViewById(R.id.listViewSuggestions);
        suggestions = new ArrayList<>();
        suggestionsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, suggestions);
        listViewSuggestions.setAdapter(suggestionsAdapter);

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString().trim().toLowerCase();
                updateSuggestions(searchText);
                searchPhotos();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        listViewSuggestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedSuggestion = suggestions.get(position);
                editTextSearch.setText(selectedSuggestion);
                listViewSuggestions.setVisibility(View.GONE);
                searchPhotos();
            }
        });
    }

    private void searchPhotos() {
        String searchText = editTextSearch.getText().toString().trim();
        searchResults.clear();
        adapter.notifyDataSetChanged();

        String[] orCriteria = searchText.split("OR");
        boolean matchFound = false;

        for (Album album : MainActivity.getAlbums()) {
            for (Picture picture : album.getPictures()) {
                for (String orCriterion : orCriteria) {
                    String[] andCriteria = orCriterion.trim().split("AND");
                    boolean andMatchFound = true;

                    for (String andCriterion : andCriteria) {
                        String[] tagValue = andCriterion.trim().split("=");
                        if (tagValue.length == 2) {
                            String tagName = tagValue[0].trim().toLowerCase();
                            String tagValueText = tagValue[1].trim().toLowerCase();
                            boolean tagMatchFound = false;

                            for (Tag tag : picture.getTags()) {
                                if (tag.getName().toLowerCase().equals(tagName)) {
                                    for (String value : tag.getValues()) {
                                        if (value.toLowerCase().startsWith(tagValueText)) {
                                            tagMatchFound = true;
                                            break;
                                        }
                                    }
                                    if (tagMatchFound) {
                                        break;
                                    }
                                }
                            }

                            if (!tagMatchFound) {
                                andMatchFound = false;
                                break;
                            }
                        }
                    }

                    if (andMatchFound) {
                        matchFound = true;
                        break;
                    }
                }

                if (matchFound) {
                    searchResults.add(picture);
                    matchFound = false;
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void updateSuggestions(String searchText) {
        Set<String> suggestionsSet = new HashSet<>();
        String[] orCriteria = searchText.split("OR");
        String lastOrCriterion = orCriteria[orCriteria.length - 1].trim();

        if (lastOrCriterion.isEmpty()) {
            Log.d("SearchActivity", "Last OR criterion is empty, no suggestions to show");
            return;
        }

        String[] andCriteria = lastOrCriterion.split("AND");
        String newSearchText = andCriteria[andCriteria.length - 1].trim();

        if (newSearchText.isEmpty()) {
            Log.d("SearchActivity", "New search text is empty after 'AND' or 'OR' operator, no suggestions to show");
            return;
        }

        Log.d("SearchActivity", "New search text after 'AND' or 'OR' operator: " + newSearchText);

        List<Picture> filteredPictures = new ArrayList<>();

        for (Album album : MainActivity.getAlbums()) {
            for (Picture picture : album.getPictures()) {
                boolean matchFound = true;

                for (int i = 0; i < andCriteria.length - 1; i++) {
                    String andCriterion = andCriteria[i].trim();
                    String[] tagValue = andCriterion.split("=");
                    if (tagValue.length == 2) {
                        String tagName = tagValue[0].trim().toLowerCase();
                        String tagValueText = tagValue[1].trim().toLowerCase();
                        boolean tagMatchFound = false;

                        for (Tag tag : picture.getTags()) {
                            if (tag.getName().toLowerCase().equals(tagName)) {
                                for (String value : tag.getValues()) {
                                    if (value.toLowerCase().equals(tagValueText)) {
                                        tagMatchFound = true;
                                        break;
                                    }
                                }
                                if (tagMatchFound) {
                                    break;
                                }
                            }
                        }

                        if (!tagMatchFound) {
                            matchFound = false;
                            break;
                        }
                    }
                }

                if (matchFound) {
                    filteredPictures.add(picture);
                }
            }
        }

        for (Picture picture : filteredPictures) {
            for (Tag tag : picture.getTags()) {
                for (String value : tag.getValues()) {
                    String suggestion = tag.getName() + "=" + value;
                    if (suggestion.toLowerCase().contains(newSearchText.toLowerCase())) {
                        suggestionsSet.add(suggestion);
                        Log.d("SearchActivity", "Adding suggestion: " + suggestion);
                    }
                }
            }
        }

        suggestions.clear();
        suggestions.addAll(suggestionsSet);
        suggestionsAdapter.notifyDataSetChanged();
        listViewSuggestions.setVisibility(suggestions.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private class SearchResultAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<Picture> pictures;

        public SearchResultAdapter(Context context, ArrayList<Picture> pictures) {
            this.context = context;
            this.pictures = pictures;
        }

        @Override
        public int getCount() {
            return pictures.size();
        }

        @Override
        public Object getItem(int position) {
            return pictures.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(context);
                imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            Picture picture = pictures.get(position);
            Bitmap bitmap = BitmapFactory.decodeFile(picture.getName());
            imageView.setImageBitmap(bitmap);

            return imageView;
        }
    }
}