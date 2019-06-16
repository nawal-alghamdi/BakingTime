package com.example.bakingtime.widgets;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.bakingtime.R;
import com.example.bakingtime.data.Ingredient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new ListRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context context;
    private List<Ingredient> ingredients = new ArrayList<>();

    public ListRemoteViewsFactory(Context applicationContext, Intent intent) {
        context = applicationContext;
    }

    @Override
    public void onCreate() {

    }

    //called on start and when notifyAppWidgetViewDataChanged is called
    @Override
    public void onDataSetChanged() {

        // Get selected recipe ingredients
        Gson gson = new Gson();
        String json = context.getSharedPreferences(RecipeWidgetProvider.RECIPE_SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE).getString(RecipeWidgetProvider.SP_INGREDIENT_JSON, "");
        if (!json.isEmpty()) {
            Type type = new TypeToken<List<Ingredient>>() {
            }.getType();
            ingredients = gson.fromJson(json, type);
        } else {
            ingredients = null;
        }

        Log.d(context.getPackageName(), "trying onDataSet ");

    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        Log.d(context.getPackageName(), "trying getCount  ");
        if (ingredients == null) return 0;
        return ingredients.size();
    }

    /**
     * This method acts like the onBindViewHolder method in an Adapter
     *
     * @param position The current position of the item in the ListView to be displayed
     * @return The RemoteViews object to display for the provided position
     */
    @Override
    public RemoteViews getViewAt(int position) {
        if (ingredients.isEmpty()) return null;
        Log.d(context.getPackageName(), "trying getViewAt  ");

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget_provider);

        Ingredient ingredient = ingredients.get(position);
        views.setTextViewText(R.id.appwidget_text, ingredient.getIngredient());

        return views;

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1; // Treat all items in the GridView the same
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

