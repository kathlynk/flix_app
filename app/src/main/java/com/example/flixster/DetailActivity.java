package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

import static com.google.android.youtube.player.YouTubePlayer.PlayerStyle.MINIMAL;

public class DetailActivity extends YouTubeBaseActivity {

    private static final String YOUTUBE_API_KEY = "AIzaSyCKB45IMU_xAhfdMkrB2qXmVuR-Lu9NIFg";
    public static final String VIDEOS_URL = "https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    TextView tvTitle;
    TextView tvOverview;
    RatingBar ratingBar;
    TextView tvVotes;
    TextView tvRelease;
    YouTubePlayerView youTubePlayerView;
    float rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Movie movie = Parcels.unwrap(getIntent().getParcelableExtra("movie"));

        tvTitle = findViewById(R.id.tvTitle);
        tvOverview = findViewById(R.id.tvOverview);
        ratingBar = findViewById(R.id.ratingBar);
        tvVotes = findViewById(R.id.tvVotes);
        tvRelease = findViewById(R.id.tvRelease);
        youTubePlayerView = findViewById(R.id.player);

        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());
        ratingBar.setRating(movie.getRating());
        tvVotes.setText(String.format("%s",movie.getVotes()));
        tvRelease.setText(movie.getRelease());
        int movieId = movie.getMovieId();
        rating = movie.getRating();

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(String.format(VIDEOS_URL, movieId), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    JSONArray results = json.jsonObject.getJSONArray("results");
                    if (results.length()==0)
                        return;
                    String youtubeKey = results.getJSONObject(0).getString("key");
                    String site = results.getJSONObject(0).getString("site");
                    Log.d("DetailActivity", youtubeKey);
                    initializeYoutube(youtubeKey, site);
                } catch (JSONException e) {
                    Log.e("DetailActivity","Failed to parse JSON");
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

            }
        });

    }

    private void initializeYoutube(final String youtubeKey, final String site) {
        youTubePlayerView.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Log.d("Detail Activity","onInitializationSuccess");
                //making sure youtube site is referenced
                if(site.compareTo("YouTube")==0) {
                    // AutoPlay for highly rated videos
                    if (rating >= 7) youTubePlayer.loadVideo(youtubeKey);
                    else youTubePlayer.cueVideo(youtubeKey);
                } //else youTubePlayer.... I don't know how to make it do much else
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d("Detail Activity","onInitializationFailure");

            }
        });
    }
}