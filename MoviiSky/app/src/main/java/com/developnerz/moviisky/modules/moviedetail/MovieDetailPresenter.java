package com.developnerz.moviisky.modules.moviedetail;

/**
 * Created by Rych Emrycho on 8/27/2018 at 9:17 AM.
 * Updated by Rych Emrycho on 8/27/2018 at 9:17 AM.
 */
public class MovieDetailPresenter implements MovieDetailContract.Presenter{

    private MovieDetailContract.View view;

    @Override
    public void setView(MovieDetailContract.View view) {
        this.view = view;
    }
}
