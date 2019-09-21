package wolfheros.life.home.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.gms.ads.MobileAds;

import wolfheros.life.home.*;
import wolfheros.life.home.database.MovieDbSchema.SQLiteDataBaseHelper;
import wolfheros.life.home.interfaceActivity.DetailActivity;

import static android.support.v4.view.ViewCompat.SCROLL_AXIS_VERTICAL;
import static android.support.v4.view.ViewCompat.cancelDragAndDrop;


/**
 *
 * 这个类是详情页的主类。为的是可以与主程序进行分离
 * */

public class MoviesDetailActivity extends DetailActivity {

    private MovieItem movieItem;
    FloatingActionButton mFloatingActionButton;
    FloatingActionButton mFloatingActionButtonBoarder;
    boolean hasFavoNoBoarder;

    public static final String MOVIE_DETAIL_ACTIVITY = "detail_activity";

    public Fragment createFragment() {
        movieItem = (MovieItem) getIntent().getSerializableExtra(MOVIE_DETAIL_ACTIVITY);
        return MovieDetailFragment.newInstance(movieItem.getUri());
    }

    public static Intent newIntent(Context context, MovieItem movieItem) {
        Intent intent = new Intent(context, MoviesDetailActivity.class);
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
                SQLiteDataBaseHelper.getInstance(MoviesDetailActivity.this)
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
                SQLiteDataBaseHelper.getInstance(MoviesDetailActivity.this)
                        .updateMovieItemDB(movieItem);
                mFloatingActionButton.hide();
                mFloatingActionButtonBoarder.show();
                hasFavoNoBoarder = false;
            }
        });

        // 初始化广告设置。
        MobileAds.initialize(this,"ca-app-pub-1459318169701878~6479952525");

        /*CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mFloatingActionButton.getLayoutParams();
        layoutParams.setBehavior(new FloatingButtonLayoutParmsSrollBehavior());*/
    }

    @Override
    public boolean onSupportNavigateUp() {
        //This method is called when the up button is pressed. Just the pop back stack.
        getSupportFragmentManager().popBackStack();
        this.finish();
        return true;
    }

   /* @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int offerX = x - lastX;
                int offerY = y - lastY;

                if (offerY > 0) {
                    mFloatingActionButton.setTranslationY(offerY);
                    // mFloatingActionButtonBoarder.hide();
                } else {
                    if (hasFavoNoBoarder) {
                        mFloatingActionButton.show();
                        mFloatingActionButtonBoarder.hide();
                    } else {
                        mFloatingActionButton.hide();
                        mFloatingActionButtonBoarder.show();
                    }
                }
                break;
        }
        return true;

    }*/

    // 用来检测屏幕滚动的内部类
/*
    public class FloatingButtonLayoutParmsSrollBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {

        public FloatingButtonLayoutParmsSrollBehavior(){
            super();
        }


        @Override
        public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout
                , @NonNull FloatingActionButton child
                , @NonNull View directTargetChild
                , @NonNull View target, int axes, int type) {

            return axes == SCROLL_AXIS_VERTICAL;
        }

        @Override
        public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout
                , @NonNull FloatingActionButton child
                , @NonNull View target, int dx, int dy
                , @NonNull int[] consumed, int type) {
            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);

            child.setTranslationY(Math.max(0f, Math.min(child.getHeight(), child.getTranslationY() + dy)));

            if (dy > 0) {
                mFloatingActionButton.hide();
                mFloatingActionButtonBoarder.hide();
            } else {
                if (hasFavoNoBoarder) {
                    mFloatingActionButton.show();
                    mFloatingActionButtonBoarder.hide();
                }else {
                    mFloatingActionButton.hide();
                    mFloatingActionButtonBoarder.show();
                }
            }
        }
    }
*/

}
