package com.example.bakingtime.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.bakingtime.R;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {

    public static final String RECIPE_SHARED_PREFERENCE_NAME = "RECIPE";
    public static final String SP_INGREDIENT_JSON = "SP_IngredientJson";
    public static final String SP_RECIPE_NAME_STRING = "SP_RecipeNameString";

    private static String recipeName;


    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget_list);

        recipeName = context.getSharedPreferences(RECIPE_SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).getString(SP_RECIPE_NAME_STRING, "");
        views.setTextViewText(R.id.widget_recipe_name, recipeName);

        // Set the ListWidgetService intent to act as the adapter for the ListView
        Intent intent = new Intent(context, ListWidgetService.class);
        views.setRemoteAdapter(R.id.list_view_widget, intent);
        // Handle when first use widget
        views.setEmptyView(R.id.list_view_widget, R.id.empty_view);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

