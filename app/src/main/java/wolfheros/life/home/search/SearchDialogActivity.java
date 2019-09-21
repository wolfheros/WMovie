package wolfheros.life.home.search;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import wolfheros.life.home.R;
import wolfheros.life.home.WMovieActivity;

public class SearchDialogActivity extends AppCompatActivity{

    String mString; //  获取的用户输入的字符串
    String preString;
    private Fragment getFragment(){

        return SearchFragment.newInstance(mString);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mString = intent.getStringExtra(SearchManager.QUERY);

            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.search_base_activity);

            if (fragment == null){
                fragment = getFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.search_base_activity,fragment,mString)
                        .commit();
                preString = mString;
            }else if (fragmentManager.findFragmentByTag(mString)  == null){

                fragmentManager.beginTransaction()
                        .remove(fragmentManager.findFragmentByTag(preString)).commit();

                fragment = getFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.search_base_activity,fragment,mString)
                        .commit();
                preString = mString;
            }
        }
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.base_contain);
        handleIntent(getIntent());

        Toolbar myChildToolbar = (Toolbar)findViewById(R.id.search_toolbar);
        setSupportActionBar(myChildToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        // set Up Color White
        ab.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24);
        Window window = getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary));
    }

    // 设置一个 SearchView 搜索
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_view, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_view).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        ImageView searchIcon = (ImageView)searchView
                .findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
        searchIcon.setImageResource(R.drawable.baseline_search_white_24);
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
       super.onBackPressed();
    }

}
