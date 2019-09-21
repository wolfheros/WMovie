package wolfheros.life.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import wolfheros.life.home.aboutme.About;
import wolfheros.life.home.database.MovieDbSchema.MovieDbSchema;
import wolfheros.life.home.database.MovieDbSchema.SQLiteDataBaseHelper;
import wolfheros.life.home.kind.LasteMovieActivity;
import wolfheros.life.home.search.SearchDialogActivity;
import wolfheros.life.home.setting.AppSettings;
import wolfheros.life.home.tools.Main.ExternalStoreFile;

import static android.support.v4.view.ViewCompat.*;


/**
 * Created by james_huker on 8/6/17.
 *
 */

public class WMovieActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private static final int NEW_MOVIE_FRAGMENT = 0;
    private static final int MY_FAVORITE_FRAGMENT = 1;
    private static final int RECENT_DOWNLOAD = 2;
    public static final String LASTE_MOVIE_URI =
            "http://www.dytt8.net/html/gndy/dyzz/list_23_1.html";
    public static final String CHINA_MOVIE =
            "http://www.ygdy8.net/html/gndy/china/index.html";
    public static final String UN_MOVIE =
            "http://www.ygdy8.net/html/gndy/oumei/index.html";
    public static final String KOREAN_MOVIE =
            "http://www.dytt8.net/html/gndy/rihan/index.html";


    private FloatingActionButton mFloatingActionButton;
    private CustomViewPager mCustomViewPager;
    private MenuItem preMenu;
    private BottomNavigationView mBottomNavigationView;
    private DrawerLayout drawerLayout;

    // 需要继承的抽象方法。
    public Fragment createFragment(int code) {
        if (code == NEW_MOVIE_FRAGMENT) {
            return MovieFragment.newInstance();
        } else if (code == MY_FAVORITE_FRAGMENT) {
            return MovieFavoFragment.newInstance(getApplicationContext(), MY_FAVORITE_FRAGMENT);
        } else if (code == RECENT_DOWNLOAD) {
            return MovieRectFragment.newInstance(getApplicationContext(), RECENT_DOWNLOAD);
        }
        return null;
    }

    // 重写此方法后调用的将会是子类的相对应的此方法。
    @LayoutRes
    protected int getLayoutResId() {
        return R.layout.top_activity_movie;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutResId());
        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
        // 托管的Fragment
        mCustomViewPager = (CustomViewPager) findViewById(R.id.view_pager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(createFragment(NEW_MOVIE_FRAGMENT));
        viewPagerAdapter.addFragment(createFragment(MY_FAVORITE_FRAGMENT));
        viewPagerAdapter.addFragment(createFragment(RECENT_DOWNLOAD));

        mCustomViewPager.setAdapter(viewPagerAdapter);
        mCustomViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // 用来刷新数据。当从详细页面返回时，更新Favorite 和 Recent Download
                mCustomViewPager.getAdapter().notifyDataSetChanged();
                if (preMenu != null) {
                    preMenu.setChecked(false);
                } else {
                    mBottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                mBottomNavigationView.getMenu().getItem(position).setChecked(true);
                preMenu = mBottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //Disable ViewPager Swipe
        mCustomViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });


        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // FloatingAction
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.floatingactionbutton);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WMovieActivity.this,
                        SearchDialogActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //onSearchRequested();
            }
        });


        // DrawLayOut
        drawerLayout = (DrawerLayout) findViewById(R.id.draw_layout);
        ActionBarDrawerToggle abdt = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(abdt);
        abdt.syncState();

        // NavigationDraw
        NavigationView navigationView = (NavigationView) findViewById(R.id.toolbar_navigation_view);
        navigationView.setNavigationItemSelectedListener(this);  // 监听器的实现在Activity 类上。

        // BottomNavigationView
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        CoordinatorLayout.LayoutParams layoutParams =
                (CoordinatorLayout.LayoutParams) mBottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationScrollBehavior());

        //  setting statusbar color
        Window window = getWindow();
        /*// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);*/
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary));

    }

    // Setting BottomNavigationView Behavior.
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mFloatingActionButton.show();
                    mCustomViewPager.setCurrentItem(0);
                    break;
                case R.id.navigation_favorite:
                    mFloatingActionButton.hide();
                    mCustomViewPager.setCurrentItem(1);
                    break;
                case R.id.navigation_notification:
                    mFloatingActionButton.hide();
                    mCustomViewPager.setCurrentItem(2);
                    break;

            }
            return false;
        }
    };

    //  BottomNavigation 布局界面交互操作。
    public class BottomNavigationScrollBehavior extends
            CoordinatorLayout.Behavior<BottomNavigationView> {

        public BottomNavigationScrollBehavior() {
            super();
        }
        @Override
        public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                           @NonNull BottomNavigationView child,
                                           @NonNull View directTargetChild,
                                           @NonNull View target, int axes, int type) {
            return axes == SCROLL_AXIS_VERTICAL;
        }

        @Override
        public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                      @NonNull BottomNavigationView child,
                                      @NonNull View target, int dx, int dy,
                                      @NonNull int[] consumed, int type) {
            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);

            child.setTranslationY(Math.max(0f, Math.min(child.getHeight(),
                    child.getTranslationY() + dy)));

            if (dy > 0) {
                mFloatingActionButton.hide();
            } else {
                mFloatingActionButton.show();
            }
        }
    }

    /**
     * 此处的几个方法牢记，
     */

    // 返回键按钮的使用。
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.draw_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
/*
    // 创建右上角的选项（三个点）
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.main, menu);
       return true;
    }*/

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

    // 选中导航栏内的选项后的操作。
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.last_movie:
                startActivity(LasteMovieActivity.newIntent(this,LASTE_MOVIE_URI));
                break;
            case R.id.last_china_movie:
                startActivity(LasteMovieActivity.newIntent(this,CHINA_MOVIE));
                break;
            case R.id.last_tv_usa:
                startActivity(LasteMovieActivity.newIntent(this,UN_MOVIE));
                break;
            case R.id.last_tv_korean:
                startActivity(LasteMovieActivity.newIntent(this,KOREAN_MOVIE));
                break;
            case R.id.settings:
                startActivity(new Intent(this, AppSettings.class));
                break;
            case R.id.about_me:
                startActivity(new Intent(this, About.class));
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.draw_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }
    // 实现ViewPager 和 ViewAdapter类
    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mfragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }
        public void addFragment(Fragment fragment) {
            mfragmentList.add(fragment);
        }
        @Override
        public Fragment getItem(int position) {
            return mfragmentList.get(position);
        }
        @Override
        public int getCount() {
            return mfragmentList.size();
        }
        // 设置可以用来刷新数据。
        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}



