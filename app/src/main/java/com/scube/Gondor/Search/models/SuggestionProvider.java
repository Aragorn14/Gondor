package com.scube.Gondor.Search.models;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by vashoka on 05/30/15.
 */
public class SuggestionProvider extends SearchRecentSuggestionsProvider
{
    public final static String AUTHORITY = "com.scube.Gondor.Search.models.SuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SuggestionProvider()
    {
        setupSuggestions(AUTHORITY, MODE);
    }
}
