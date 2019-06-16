package com.example.bakingtime.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bakingtime.R;
import com.example.bakingtime.data.Step;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StepFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StepFragment extends Fragment {


    private static final String TAG = StepFragment.class.getSimpleName();

    private static final String ARG_SELECTED_STEP_POSITION = "selectedStepPosition";
    private static final String ARG_STEP_LIST = "argStepList";
    private static final String PLAYBACK_POSITION = "thePlaybackPosition";
    private static final String WINDOW_INDEX = "windowIndex";
    private static final String IS_PLAYER_READY = "isPlayerReady";
    Step step;
    @BindView(R.id.video_view)
    PlayerView videoView;
    // Text view for step Short description
    @BindView(R.id.short_description_textView)
    TextView shortDescriptionTextView;
    // Text view for step description
    @BindView(R.id.description_textView)
    TextView descriptionTextView;
    private int position;
    private List<Step> steps;
    private String videoUrl;
    private ExoPlayer exoPlayer;
    private long playbackPosition;
    private int currentWindowIndex;
    private boolean playWhenReady = false;

    public StepFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param position Parameter 1.
     * @return A new instance of fragment StepFragment.
     */
    public static StepFragment newInstance(int position, List<Step> steps) {
        StepFragment fragment = new StepFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SELECTED_STEP_POSITION, position);
        args.putParcelableArrayList(ARG_STEP_LIST, (ArrayList<? extends Parcelable>) steps);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (exoPlayer != null && !isVisibleToUser) {
            exoPlayer.setPlayWhenReady(false);
        } else if (exoPlayer != null && isVisibleToUser) {
            // * Handling orientation change
            // This else is so important because when you rotate the device it's first
            // set play when ready to false. Then the app will be visible to the user
            // so it will came to this else and then instead of stopping the video
            // the video will still playing even after device rotation
            exoPlayer.setPlayWhenReady(true);
        }
        Log.d(TAG, "setUserVisibleHint" + " playbackPosition " + playbackPosition + " " + currentWindowIndex + " " + playWhenReady);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // * Handling orientation change
        // This will save all the variable no need to use onSaveInstanceState
        this.setRetainInstance(true);

        if (getArguments() != null) {
            position = getArguments().getInt(ARG_SELECTED_STEP_POSITION);
            steps = getArguments().getParcelableArrayList(ARG_STEP_LIST);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_step, container, false);

        if (savedInstanceState != null) {
            playbackPosition = savedInstanceState.getLong(PLAYBACK_POSITION);
            currentWindowIndex = savedInstanceState.getInt(WINDOW_INDEX);
            playWhenReady = savedInstanceState.getBoolean(IS_PLAYER_READY);
            Log.d(TAG, "onCreateView" + " playbackPosition " + playbackPosition + " " + currentWindowIndex + " " + playWhenReady);

        }

        if (position != -1) {

            ButterKnife.bind(this, view);

            step = steps.get(position);

            videoUrl = step.getVideoUrl();

            shortDescriptionTextView.setText(step.getShortDescription());

            descriptionTextView.setText(step.getDescription());
        }


        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (!videoUrl.isEmpty() && Util.SDK_INT > 23) {
            videoView.setVisibility(View.VISIBLE);
            initializePlayer(Uri.parse(videoUrl));
            Log.d(TAG, "onStart" + " playbackPosition " + playbackPosition + " " + currentWindowIndex + " " + playWhenReady);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!videoUrl.isEmpty() && (Util.SDK_INT <= 23 || exoPlayer == null)) {
            videoView.setVisibility(View.VISIBLE);
            initializePlayer(Uri.parse(videoUrl));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
            Log.d(TAG, "onStop" + " playbackPosition " + playbackPosition + " " + currentWindowIndex + " " + playWhenReady);
        }
    }

    /**
     * Initialize ExoPlayer.
     *
     * @param mediaUri The URI of the sample to play.
     */
    private void initializePlayer(Uri mediaUri) {
        if (exoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            RenderersFactory renderersFactory = new DefaultRenderersFactory(getContext());
            exoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), renderersFactory, trackSelector, loadControl);
            videoView.setPlayer(exoPlayer);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getContext(), getContext().getString(R.string.app_name));
            MediaSource mediaSource = new ExtractorMediaSource.Factory(new DefaultDataSourceFactory(getContext(), userAgent))
                    .setExtractorsFactory(new DefaultExtractorsFactory())
                    .createMediaSource(mediaUri);
            exoPlayer.prepare(mediaSource, true, false);
            exoPlayer.setPlayWhenReady(playWhenReady);
            exoPlayer.seekTo(currentWindowIndex, playbackPosition);
            Log.d(TAG, "initializePlayer" + " playbackPosition " + playbackPosition + " " + currentWindowIndex + " " + playWhenReady);

        }
    }


    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        if (exoPlayer != null) {
            playbackPosition = exoPlayer.getCurrentPosition();
            currentWindowIndex = exoPlayer.getCurrentWindowIndex();
            playWhenReady = exoPlayer.getPlayWhenReady();
            exoPlayer.release();
            exoPlayer = null;
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (exoPlayer != null) {
            super.onSaveInstanceState(outState);
            outState.putLong(PLAYBACK_POSITION, Math.max(0, exoPlayer.getCurrentPosition()));
            outState.putInt(WINDOW_INDEX, exoPlayer.getCurrentWindowIndex());
            outState.putBoolean(IS_PLAYER_READY, exoPlayer.getPlayWhenReady());
            Log.d(TAG, "onSaveInstanceState" + " playbackPosition " + exoPlayer.getCurrentPosition() + " " + exoPlayer.getCurrentWindowIndex() + " " + exoPlayer.getPlayWhenReady());
        }
    }
}
