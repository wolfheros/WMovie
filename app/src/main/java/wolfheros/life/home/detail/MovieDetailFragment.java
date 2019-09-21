package wolfheros.life.home.detail;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import wolfheros.life.home.setting.SettingFragment;
import wolfheros.life.home.tools.Main.ImageFileStore;
import wolfheros.life.home.MovieItem;
import wolfheros.life.home.R;
import wolfheros.life.home.database.MovieDbSchema.SQLiteDataBaseHelper;
/**
 *  by James_Huker
 * */
public class MovieDetailFragment extends Fragment implements RewardedVideoAdListener{

    public static final String MOVIE_DETAIL_FRAGMENT = "Movie_Detail_Fragment";
    private static final int HOME_IMAGE = 1;
    private static final int SHORT_CUT = 2;

    private MovieItem movieItem;
    private Context mContext;

    RewardedVideoAd mRewardedVideoAd;
    AVLoadingIndicatorView mAVLoadingIndicatorView;
    ImageView mImageView;
    ImageView mShortCut;
    TextView movieDetailName;
    TextView movieDate;
    TextView movieTime;
    TextView setMovieDetailText;
    Button movieDownload;
    TextView mCountry;
    TextView mlanguage;
    AdView mAdView;
    boolean mShowPhotos;
    Intent intent;

    public static Fragment newInstance(@NonNull String string) {
        Bundle args = new Bundle();
        args.putString(MOVIE_DETAIL_FRAGMENT,string);
        MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
        movieDetailFragment.setArguments(args);
        return movieDetailFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        String keyVaule = getArguments().getString( MOVIE_DETAIL_FRAGMENT);
        movieItem = SQLiteDataBaseHelper.getInstance(mContext).getMovieItemDB(keyVaule);

        // 获取设置变量
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(mContext);
        // 获取显示封面照片。
        mShowPhotos = sharedPreferences.getBoolean(SettingFragment.KEY_SHOW,true);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.movie_detail_fragment, container,false);
        mAVLoadingIndicatorView = (AVLoadingIndicatorView) view.findViewById(R.id.avi_anim_wait);
        mImageView = (ImageView)view.findViewById(R.id.image_view);
        mCountry = (TextView)view.findViewById(R.id.movie_country);
        movieDate = (TextView)view.findViewById(R.id.movie_date);
        movieDetailName = (TextView)view.findViewById(R.id.movie_detail_name);
        movieTime = (TextView)view.findViewById(R.id.movie_time);
        setMovieDetailText = (TextView) view.findViewById(R.id.set_movie_detail_text);
        // movieDetailTextShowMore =(Button) view.findViewById(R.id.set_movie_detail_text_show_more);
        //playButton = (Button)view.findViewById(R.id.play_button);
        movieDownload = (Button)view.findViewById(R.id.movie_download);
        mShortCut = (ImageView) view.findViewById(R.id.movie_shortcut);
        mlanguage=(TextView)view.findViewById(R.id.movie_language);
        if (getDrawable(movieItem ,HOME_IMAGE) == null && mShowPhotos){
            Picasso.get().load(movieItem.getPhotosUri())
                    .error(R.drawable.error_template).into(mImageView);
        }else if (getDrawable(movieItem ,HOME_IMAGE) != null){
            mImageView.setImageDrawable(getDrawable(movieItem ,HOME_IMAGE));
        }else {
            mImageView.setImageResource(R.drawable.baseline_insert_photo_white_24);
        }

        // 海报照片
       // 电影名称
        movieDetailName.setText(movieItem.getName());
       // 电影国家
        mCountry.setText(movieItem.getCountry());
        // 语言
        mlanguage.setText(movieItem.getLaguage());
       // 电影上映日期
        movieDate.setText(movieItem.getDate());
       // 电影播放时间
        movieTime.setText(movieItem.getMovieTime());
       // 电影简介
        setMovieDetailText.setText(movieItem.getMovieDetail());
       // 电影截图
        if (mShowPhotos) {
            Picasso.get().load(movieItem.getPhotoShortUri())
                    .error(R.drawable.error_template).into(mShortCut);
        }
        if (mShortCut.getDrawable()!=null){
            mAVLoadingIndicatorView.smoothToHide();
        }
        // 显示视频广告。
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(getContext());
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardVidieoAd();

        // 添加广告。
        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // 电影下载按钮
        movieDownload.setText(R.string.download);
        movieDownload.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        // intent 实例化。
        intent = new Intent(Intent.ACTION_VIEW
                ,Uri.parse(movieItem.getMovieDownloadUri()));
        intent.addCategory("android.intent.category.DEFAULT");

        if (getActivity().getPackageManager().resolveActivity(intent,PackageManager.MATCH_DEFAULT_ONLY) == null){
            Toast.makeText(mContext, "检测到您未安装迅雷，请您先安装迅雷。",
                    Toast.LENGTH_LONG).show();
            movieDownload.setEnabled(false);
        }
        movieDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                movieItem.setReDownload("true");
                SQLiteDataBaseHelper.getInstance(mContext).updateMovieItemDB(movieItem);

                if (mRewardedVideoAd.isLoaded()){
                    mRewardedVideoAd.show();
                }else{
                    startIntent(intent);
                }
            }
        });

        return view;
    }

    private Drawable getDrawable(@NonNull MovieItem item , int code) {
        if (code == 1) {
            Bitmap bitmap = ImageFileStore.getImageFileStore()
                    .getImageBitmap(item, mContext, 1);
            return new BitmapDrawable(getResources(), bitmap);
        }else if (code ==2){
            Bitmap bitmap = ImageFileStore.getImageFileStore()
                    .getImageBitmap(item, mContext, 2);
            return new BitmapDrawable(getResources(), bitmap);
        }
        return null;
    }

    void startIntent(Intent intent){
        startActivity(intent);          // Loaded ad complete so start download program.
        loadRewardVidieoAd();
    }

    private void loadRewardVidieoAd(){
        mRewardedVideoAd.loadAd("ca-app-pub-1459318169701878/1599137228",new AdRequest.Builder().build());
    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        startIntent(intent);
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
    }

    @Override
    public void onResume() {
        mRewardedVideoAd.resume(getContext());
        super.onResume();
    }

    @Override
    public void onPause() {
        mRewardedVideoAd.pause(getContext());
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mRewardedVideoAd.destroy(getContext());
        super.onDestroy();
    }
}
