package wolfheros.life.home.search.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;

import wolfheros.life.home.MovieItem;
import wolfheros.life.home.R;
import wolfheros.life.home.interfaceActivity.DetailActivity;

public class SearchDetailActivity extends DetailActivity {

    private FloatingActionButton mFloatingActionButton;
    private FloatingActionButton mFloatingActionButtonBoarder;

    private static final String SEARCH_DETAIL_ACTIVITY = "search_detail_activity";
    @Override
    public Fragment createFragment(){
        MovieItem movieItem =(MovieItem)getIntent().getSerializableExtra(SEARCH_DETAIL_ACTIVITY);
        return SearchDetailFragment.newInstance(movieItem);
    }

    public static Intent newIntent(Context context , MovieItem movieItem){
        Intent intent = new Intent(context , SearchDetailActivity.class);
        intent.putExtra(SEARCH_DETAIL_ACTIVITY , movieItem);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFloatingActionButtonBoarder =
                (FloatingActionButton)findViewById(R.id.floatingactionbutton);
        mFloatingActionButtonBoarder.hide();
        mFloatingActionButton =
                (FloatingActionButton) findViewById(R.id.floatingactionbutton_noboarder);
        mFloatingActionButton.hide();
    }

    @Override
    public boolean onSupportNavigateUp() {
        //This method is called when the up button is pressed. Just the pop back stack.
        getSupportFragmentManager().popBackStack();
        this.finish();
        return true;
    }
}
