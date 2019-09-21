package wolfheros.life.home.interfaceActivity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.ads.MobileAds;

import wolfheros.life.home.R;


public abstract class DetailActivity extends AppCompatActivity {

    // abstract get Fragment()
    public abstract Fragment createFragment();
    @LayoutRes
    private int getResourceLayout(){
        return R.layout.detai_activity_movie;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResourceLayout());

        // 托管的Fragment
        FragmentManager fm =getSupportFragmentManager();    //创建一个兼容的FragmentManager对象。
        /**
         *  注意此处的是R.id.detail_contain.是属于R.layout.detai_activity_movie。不是fragment 的 layout内的id。
         * */
        Fragment fragment = fm.findFragmentById(R.id.detail_contain);

        // 判断fragment是不是为null。
        if(fragment == null) {
            // 继承得到的新类需要实现的方法。
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.detail_contain , fragment)
                    .commit();
        }



        Toolbar myChildToolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(myChildToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        if (ab !=null){
            // Enable the Up button
            ab.setDisplayHomeAsUpEnabled(true);
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


        // 初始化广告设置。用于继承。
        MobileAds.initialize(this,"ca-app-pub-1459318169701878~6479952525");
    }


}
