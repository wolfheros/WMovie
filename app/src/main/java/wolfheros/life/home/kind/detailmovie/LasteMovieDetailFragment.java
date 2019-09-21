package wolfheros.life.home.kind.detailmovie;

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

import wolfheros.life.home.MovieItem;
import wolfheros.life.home.R;
import wolfheros.life.home.database.MovieDbSchema.SQLiteDataBaseHelper;
import wolfheros.life.home.setting.SettingFragment;
import wolfheros.life.home.tools.Main.ImageFileStore;

public class LasteMovieDetailFragment extends Fragment implements RewardedVideoAdListener{

    public static final String MOVIE_DETAIL_FRAGMENT = "Movie_Detail_Fragment";/*
    private static final String XUNLEI_DOWNLOAD = "life.wolfheros.wmovie.DOWNLOAD";
    private static final int HOME_IMAGE = 1;
    private static final int SHORT_CUT = 2;*/
    private MovieItem movieItem;
    private Context mContext;

    ImageView mImageView;
    ImageView mShortCut;
    TextView movieDetailName;
    TextView movieDate;
    TextView movieTime;
    TextView setMovieDetailText;
    Button movieDownload;
    TextView mCountry;
    AdView mAdView;
    RewardedVideoAd mRewardedVideoAd;

    boolean mShowPhotos;
    public static Fragment newInstance(@NonNull MovieItem movieItem) {
        Bundle args = new Bundle();
        args.putSerializable(MOVIE_DETAIL_FRAGMENT,movieItem);
        LasteMovieDetailFragment movieDetailFragment = new LasteMovieDetailFragment();
        movieDetailFragment.setArguments(args);
        return movieDetailFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        movieItem =(MovieItem) getArguments().getSerializable( MOVIE_DETAIL_FRAGMENT);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

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
        mImageView.setImageResource(R.drawable.baseline_insert_photo_white_24);

        if (mShowPhotos) {
            Picasso.get().load(movieItem.getPhotosUri())
                    .error(R.drawable.error_template).into(mImageView);
        }

        // 海报照片
       // 电影名称
       movieDetailName.setText(movieItem.getName());
       // 电影国家
        mCountry.setText(movieItem.getCountry());
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
        // 电影下载按钮
        movieDownload.setText(R.string.download);
        movieDownload.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        // Download URL not null
        if (movieItem.getMovieDownloadUri() != null){
            final Intent intent = new Intent(Intent.ACTION_VIEW
                    , Uri.parse(movieItem.getMovieDownloadUri()));
            intent.addCategory("android.intent.category.DEFAULT");
            if (getActivity().getPackageManager().resolveActivity(intent
                    ,PackageManager.MATCH_DEFAULT_ONLY) == null){
                Toast.makeText(mContext, "检测到您未安装迅雷，请您先安装迅雷。",
                        Toast.LENGTH_LONG).show();
                movieDownload.setEnabled(false);
            }
            movieDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //  更新数据库，显示这个最近访问或者下载的。
                    movieItem.setReDownload("true");
                    SQLiteDataBaseHelper.getInstance(mContext).updateMovieItemDB(movieItem);
                    startActivity(intent);
                }
            });
        }

        // 显示视频广告。
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(getContext());
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardVidieoAd();

        // 添加广告。
        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        return view;
    }

    private void loadRewardVidieoAd(){
        mRewardedVideoAd.loadAd("ca-app-pub-1459318169701878/5046493499",new AdRequest.Builder().build());
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
