package wolfheros.life.home.search.detail;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import java.io.ByteArrayOutputStream;

import wolfheros.life.home.MovieItem;
import wolfheros.life.home.R;
import wolfheros.life.home.database.MovieDbSchema.SQLiteDataBaseHelper;
import wolfheros.life.home.tools.Main.ImageFileStore;
import wolfheros.life.home.tools.Main.ImageZIP;

public class SearchDetailFragment extends Fragment implements RewardedVideoAdListener{
    private static final String SEARCH_DETAIL_FRAGMENT ="search_detail_fragment";
    private static final String TAG = "SearchDetailFragment";
    private static final int HOME_IMAGE = 1;
    private static final int SHORT_CUT = 2;

    private Context mContext;
    private SQLiteDataBaseHelper mSQLiteDataBaseHelper;

    TextView mlangruage;
    MovieItem movieItem;
    ImageView mImageView;
    TextView movieDetailName;
    TextView movieDate;
    TextView movieTime;
    TextView setMovieDetailText;
    TextView text_more;
    Button movieDownload;
    TextView mCountry;
    AdView mAdView;
    RewardedVideoAd mRewardedVideoAd;

    public static SearchDetailFragment newInstance(MovieItem  movieItem) {
        Bundle args = new Bundle();
        args.putSerializable(SEARCH_DETAIL_FRAGMENT , movieItem);
        SearchDetailFragment fragment = new SearchDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        movieItem = (MovieItem) getArguments().getSerializable( SEARCH_DETAIL_FRAGMENT);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_detail_fragment, container,false);

        mImageView = (ImageView)view.findViewById(R.id.image_view);
        mCountry = (TextView)view.findViewById(R.id.movie_country);
        movieDate = (TextView)view.findViewById(R.id.movie_date);
        movieDetailName = (TextView)view.findViewById(R.id.movie_detail_name);
        movieTime = (TextView)view.findViewById(R.id.movie_time);
        setMovieDetailText = (TextView) view.findViewById(R.id.set_movie_detail_text);
        text_more = (TextView) view.findViewById(R.id.text_more);
        mlangruage = (TextView) view.findViewById(R.id.movie_language);
        // textMore = (Button)view.findViewById(R.id.text_more);
        /*textMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textMore.getLineCount() == 10){
                    textMore.setLines(100);
                }else if (textMore.getLineCount()>10){
                    textMore.setLines(10);
                }
            }
        });*/
        // movieDetailTextShowMore =(Button) view.findViewById(R.id.set_movie_detail_text_show_more);
        //playButton = (Button)view.findViewById(R.id.play_button);
        movieDownload = (Button)view.findViewById(R.id.movie_download);

        if (movieItem.getPhotosUri() != null){
            Picasso.get().load(movieItem.getPhotosUri()).into(mImageView);
        }else {
            // replacedByErrorImage();
            /*ImageFileStore.getImageFileStore().addErrorBitmap(mContext,HOME_IMAGE,movieItem);*/
           /* mImageView.setImageDrawable(new BitmapDrawable(getResources()
                    ,ImageFileStore.getImageFileStore().getImageBitmap(movieItem ,mContext,HOME_IMAGE)));*/
            Picasso.get().load(R.drawable.error_template).into(mImageView);
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
        // 电影语言
        mlangruage.setText(movieItem.getLaguage());

        // 设置简介太少就不用显示省略号
        if (movieItem.getMovieDetail()!= null) {
            if (movieItem.getMovieDetail().length() > 200) {
                text_more.setVisibility(View.VISIBLE);
            } else {
                text_more.setVisibility(View.INVISIBLE);
            }
        }else{
            text_more.setVisibility(View.INVISIBLE);
        }
        // 电影下载按钮
        movieDownload.setText(R.string.download);
        movieDownload.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        final Intent intent = new Intent(Intent.ACTION_VIEW , Uri.parse(movieItem.getMovieDownloadUri()));
        intent.addCategory("android.intent.category.DEFAULT");
        PackageManager packageManager = getActivity().getPackageManager();

        if (packageManager.resolveActivity(intent,PackageManager.MATCH_DEFAULT_ONLY) == null){
            Toast.makeText(mContext, "检测到您未安装迅雷，请您先安装迅雷。", Toast.LENGTH_LONG).show();
            movieDownload.setEnabled(false);
        }
        movieDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  更新数据库，显示这个最近访问或者下载的。
                movieItem.setReDownload("true");
                mSQLiteDataBaseHelper =  SQLiteDataBaseHelper.getInstance(mContext);
                if (mSQLiteDataBaseHelper.getMovieItemDB(movieItem.getUri()) == null) {
                   mSQLiteDataBaseHelper.addMovieItemDB(movieItem);
                }else {
                    mSQLiteDataBaseHelper.updateMovieItemDB(movieItem);
                }
                startActivity(intent);
            }
        });
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

    private Drawable getDrawable(@NonNull MovieItem item) {
        if (item.getPhotosUri() != null) {
            // 从缓存或者内存中获取图片。
            Bitmap bitmap = ImageFileStore.getImageFileStore().getImageBitmap(item, mContext, 1);
            return new BitmapDrawable(getResources(), bitmap);
        }
        return null;
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
