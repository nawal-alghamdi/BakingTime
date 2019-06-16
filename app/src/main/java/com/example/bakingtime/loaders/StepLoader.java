package com.example.bakingtime.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.bakingtime.data.Step;
import com.example.bakingtime.utilities.NetworkJsonUtilities;

import java.util.List;

/**
 * To define the StepLoader class, we extend AsyncTaskLoader and specify List as the generic parameter,
 * which explains what type of data is expected to be loaded.
 * In this case, the loader is loading a list of Step objects.
 * <p>
 * Loads a list of steps by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class StepLoader extends AsyncTaskLoader<List<Step>> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = StepLoader.class.getName();

    /* This List will hold and help cache our step data */
    private List<Step> stepsData = null;

    /**
     * Query URL
     */
    private String url;

    /**
     * Position which is the adapter position to find out which recipe the user select and then download
     * the steps for the selected recipe
     */
    private int position;

    /**
     * Constructs a new {@link StepLoader}.
     *
     * @param context  of the activity
     * @param url      to load data from
     * @param position of the adapter
     */
    public StepLoader(Context context, String url, int position) {
        super(context);
        this.url = url;
        this.position = position;
    }

    // We override the onStartLoading() method to call forceLoad() which is a required
    // step to actually trigger the loadInBackground() method to execute.
    @Override
    protected void onStartLoading() {
        if (stepsData != null) {
            deliverResult(stepsData);
        } else {
            forceLoad();
        }
    }

    /**
     * This is the method of the AsyncTaskLoader that will load and parse the JSON data
     * from NetworkJsonUtilities in the background.
     *
     * @return Step data from NetworkJsonUtilities as a List of Steps.
     * null if an error occurs
     */
    @Override
    public List<Step> loadInBackground() {
        try {
            List<Step> jsonStepResponse = NetworkJsonUtilities.fetchStepData(url, position);
            return jsonStepResponse;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sends the result of the load to the registered listener.
     *
     * @param data The result of the load
     */
    @Override
    public void deliverResult(List<Step> data) {
        stepsData = data;
        super.deliverResult(data);
    }
}
