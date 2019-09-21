package wolfheros.life.home.kind.detailmovie;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import wolfheros.life.home.MovieItem;
import wolfheros.life.home.R;
import wolfheros.life.home.database.MovieDbSchema.SQLiteDataBaseHelper;
import wolfheros.life.home.interfaceActivity.DetailActivity;


/**
 *
 * 这个类是详情页的主类。为的是可以与主程序进行分离
 * */

public class LasteMoviesDetailActivity extends DetailActivity {

    private String keyVaule;
    private MovieItem movieItem;
    FloatingActionButton mFloatingActionButton;
    FloatingActionButton mFloatingActionButtonBoarder;
    boolean hasFavoNoBoarder;

    public static final String MOVIE_DETAIL_ACTIVITY = "detail_activity";

    public Fragment createFragment() {
        movieItem = (MovieItem) getIntent().getSerializableExtra(MOVIE_DETAIL_ACTIVITY);
        // 数据库中先查找一下。
        if (SQLiteDataBaseHelper.getInstance(this).getMovieItemDB(movieItem.getUri()) != null){
            // 用数据库中的实例替换传递过来的实例。
            movieItem =SQLiteDataBaseHelper.getInstance(this).getMovieItemDB(movieItem.getUri());
            return LasteMovieDetailFragment.newInstance(movieItem);
        }
        return LasteMovieDetailFragment.newInstance(movieItem);
    }

    public static Intent newIntent(Context context, MovieItem movieItem) {
        Intent intent = new Intent(context, LasteMoviesDetailActivity.class);
        intent.putExtra(MOVIE_DETAIL_ACTIVITY, movieItem);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // FloatingAction
        mFloatingActionButtonBoarder =
                (FloatingActionButton) findViewById(R.id.floatingactionbutton);
        mFloatingActionButton =
                (FloatingActionButton) findViewById(R.id.floatingactionbutton_noboarder);
        if (movieItem.isMyFavoMovie().equals("true")) {
            mFloatingActionButton.show();
            mFloatingActionButtonBoarder.hide();
            hasFavoNoBoarder = true;
        } else {
            mFloatingActionButtonBoarder.show();
            mFloatingActionButton.hide();
            hasFavoNoBoarder = false;
        }

        mFloatingActionButtonBoarder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onSearchRequested();
                movieItem.setMyFavoMovie("true");
                SQLiteDataBaseHelper.getInstance(LasteMoviesDetailActivity.this)
                        .updateMovieItemDB(movieItem);
                mFloatingActionButtonBoarder.hide();
                mFloatingActionButton.show();
                hasFavoNoBoarder = true;
            }
        });
        // FloatingAction_NoBoarder

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onSearchRequested();
                movieItem.setMyFavoMovie("false");
                SQLiteDataBaseHelper.getInstance(LasteMoviesDetailActivity.this)
                        .updateMovieItemDB(movieItem);
                mFloatingActionButton.hide();
                mFloatingActionButtonBoarder.show();
                hasFavoNoBoarder = false;
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        //This method is called when the up button is pressed. Just the pop back stack.
        getSupportFragmentManager().popBackStack();
        this.finish();
        return true;
    }

}
