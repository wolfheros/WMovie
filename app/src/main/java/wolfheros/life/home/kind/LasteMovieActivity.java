package wolfheros.life.home.kind;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;

import wolfheros.life.home.R;

import static wolfheros.life.home.WMovieActivity.*;

public class LasteMovieActivity extends AppCompatActivity {

    public static final String LASTE_MOVIE_ACTIVITY = "lastemovieactivity";

    private String uri;

    public static Intent newIntent(Context context, String uri) {
        Intent intent = new Intent(context, LasteMovieActivity.class);
        intent.putExtra(LASTE_MOVIE_ACTIVITY, uri);
        return intent;
    }
    public Fragment createFragment() {
        return LasteMovieFragment.newInstance(uri);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kind_base_activity);
        uri = getIntent().getStringExtra(LASTE_MOVIE_ACTIVITY);

        // 托管的Fragment
        FragmentManager fm =getSupportFragmentManager();    //创建一个兼容的FragmentManager对象。
        /**
         *  注意此处的是R.id.detail_contain.是属于R.layout.detai_activity_movie。不是fragment 的 layout内的id。
         * */
        Fragment fragment = fm.findFragmentById(R.id.kind_base_movie);

        // 判断fragment是不是为null。
        if(fragment == null) {
            // 继承得到的新类需要实现的方法。
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.kind_base_movie , fragment)
                    .commit();
        }

        Toolbar myChildToolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(myChildToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        if (ab != null){
            ab.setDisplayHomeAsUpEnabled(true);

            // Setting title for kind actionbar
            switch (uri){
                case LASTE_MOVIE_URI:
                    ab.setTitle(R.string.last_movie);
                    break;
                case CHINA_MOVIE:
                    ab.setTitle(R.string.last_china_movie);
                    break;
                case KOREAN_MOVIE:
                    ab.setTitle(R.string.last_TV_Korean);
                    break;
                case UN_MOVIE:
                    ab.setTitle(R.string.last_TV_USA);
                    break;
            }
            // Set Up button color to white;
            ab.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24);
        }



        Window window = getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary));
    }

    @Override
    public boolean onSupportNavigateUp() {
        //This method is called when the up button is pressed. Just the pop back stack.
        getSupportFragmentManager().popBackStack();
        this.finish();
        return true;
    }

    // 创建右上角的选项（三个点）
  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
*/
    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        *//*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.draw_layout);
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
            default:
        }*//*
        return super.onOptionsItemSelected(item);
    }*/


}
