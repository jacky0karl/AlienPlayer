package com.jk.alienplayer.data;

import android.content.Context;
import android.content.SearchRecentSuggestionsProvider;
import android.provider.SearchRecentSuggestions;

public class DiscoverSuggestionsProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.jk.alienplayer.data.DiscoverSuggestionsProvider";

    public DiscoverSuggestionsProvider() {
        setupSuggestions(AUTHORITY, DATABASE_MODE_QUERIES);
    }

    static public void saveRecentQuery(Context context, String query) {
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(context, AUTHORITY,
                DATABASE_MODE_QUERIES);
        suggestions.saveRecentQuery(query, null);
    }
}
