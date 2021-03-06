package com.developnerz.moviisky.modules.moviedetail;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.developnerz.moviisky.R;
import com.developnerz.moviisky.models.Movie;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.developnerz.moviisky.utils.db.DatabaseContract.CONTENT_URI;
import static com.developnerz.moviisky.utils.db.DatabaseContract.FavColumns.IS_ADULT;
import static com.developnerz.moviisky.utils.db.DatabaseContract.FavColumns.LANGUAGE;
import static com.developnerz.moviisky.utils.db.DatabaseContract.FavColumns.MOVIE_ID;
import static com.developnerz.moviisky.utils.db.DatabaseContract.FavColumns.OVERVIEW;
import static com.developnerz.moviisky.utils.db.DatabaseContract.FavColumns.POPULARITY;
import static com.developnerz.moviisky.utils.db.DatabaseContract.FavColumns.POSTER_PATH;
import static com.developnerz.moviisky.utils.db.DatabaseContract.FavColumns.RELEASE_DATE;
import static com.developnerz.moviisky.utils.db.DatabaseContract.FavColumns.TITLE;
import static com.developnerz.moviisky.utils.db.DatabaseContract.FavColumns.VOTE_AVERAGE;

public class MovieDetailActivity extends AppCompatActivity {

    public final static String EXTRA_MOVIE = "extra_position";

    public static int REQUEST_UPDATE = 100;
    public static int RESULT_DELETE = 201;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapseToolbar)
    CollapsingToolbarLayout collapseToolbar;
    @BindView(R.id.imageView_parralax)
    ImageView imageViewParralax;
    @BindView(R.id.tv_movie_title)
    TextView tv_movie_title;
    @BindView(R.id.tv_movie_description)
    TextView tv_movie_description;
    @BindView(R.id.tv_movie_lang)
    TextView tv_movie_lang;
    @BindView(R.id.tv_movie_vote)
    TextView tv_movie_vote;
    @BindView(R.id.tv_movie_adult)
    TextView tv_movie_adult;
    @BindView(R.id.tv_movie_popularity)
    TextView tv_movie_popularity;
    @BindView(R.id.tv_movie_release_date)
    TextView tv_movie_release_date;
    private Movie movie;
    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getIntent().getParcelableExtra(EXTRA_MOVIE) != null){
            movie = getIntent().getParcelableExtra(EXTRA_MOVIE);
            isFavorite = false;
        } else {
            Uri uri = getIntent().getData();
            if (uri != null){
                Cursor cursor = getContentResolver()
                        .query(uri, null, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        movie = new Movie(cursor);
                    }
                    cursor.close();
                }
            }
            isFavorite = true;
        }

        collapseToolbar.setTitle(movie.getTitle());
        collapseToolbar.setContentScrimResource(R.drawable.drawer_side_nav_bar);
        Picasso.get()
                .load("http://image.tmdb.org/t/p/w780/" + movie.getPosterPath())
                .placeholder(R.drawable.drawer_side_nav_bar)
                //.error(R.mipmap.ic_launcher_round)
                .into(imageViewParralax);

        tv_movie_title.setText(movie.getTitle());

        setEmptyField(movie);

        if (movie.getAdult()) {
            tv_movie_adult.setText(R.string.yes);
        } else {
            tv_movie_adult.setText(R.string.no);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fav_menu, menu);
        MenuItem item = menu.findItem(R.id.action_favorite);
        if (isFavorite) {
            item.setIcon(R.drawable.ic_favorite);
        } else {
            item.setIcon(R.drawable.ic_unfavorite);
        }
        item.setOnMenuItemClickListener( item1 -> {
            if (isFavorite) {
                item.setIcon(R.drawable.ic_unfavorite);
                getContentResolver().delete(getIntent().getData(), null, null);
                setResult(RESULT_DELETE, null);
                finish();
                isFavorite = false;
            } else {
                item.setIcon(R.drawable.ic_favorite);
                ContentValues values = new ContentValues();
                values.put(MOVIE_ID, movie.getId());
                values.put(TITLE, movie.getTitle());
                values.put(OVERVIEW, movie.getOverview());
                values.put(LANGUAGE, movie.getOriginalLanguage());
                values.put(VOTE_AVERAGE, movie.getVoteAverage());
                values.put(POPULARITY, movie.getPopularity());
                values.put(IS_ADULT, movie.getAdult());
                values.put(RELEASE_DATE, movie.getReleaseDate());
                values.put(POSTER_PATH, movie.getPosterPath());
                getContentResolver().insert(CONTENT_URI, values);
                isFavorite = true;
            }
            return true;
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void setEmptyField(Movie movie) {
        if (TextUtils.isEmpty(movie.getOverview())) {
            setNoData(tv_movie_description);
        } else {
            tv_movie_description.setText(movie.getOverview());
        }

        if (TextUtils.isEmpty(movie.getOriginalLanguage())) {
            setNoData(tv_movie_lang);
        } else {
            tv_movie_lang.setText(movie.getOriginalLanguage().toUpperCase());
        }

        if (TextUtils.isEmpty(String.valueOf(movie.getVoteAverage()))) {
            setNoData(tv_movie_vote);
        } else {
            tv_movie_vote.setText(String.valueOf(movie.getVoteAverage()));
        }

        if (TextUtils.isEmpty(String.valueOf(movie.getPopularity()))) {
            setNoData(tv_movie_popularity);
        } else {
            tv_movie_popularity.setText(String.valueOf(movie.getPopularity()));
        }

        if (TextUtils.isEmpty(movie.getReleaseDate())) {
            setNoData(tv_movie_release_date);
        } else {
            tv_movie_release_date.setText(movie.getReleaseDate());
        }
    }

    public void setNoData(TextView view) {
        view.setText("-");
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }
}
