package wolfheros.life.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import wolfheros.life.home.detail.MoviesDetailActivity;
import wolfheros.life.home.database.MovieDbSchema.SQLiteDataBaseHelper;
import wolfheros.life.home.setting.SettingFragment;
import wolfheros.life.home.tools.Main.DownloadFiles;
import wolfheros.life.home.tools.Main.ExternalStoreFile;
import wolfheros.life.home.tools.Main.ImageFileStore;

/**
 *  此类是主视图类.
 *  2018-5-28 23:28:17
 * */

public class MovieFragment extends Fragment {

    private static final int MOVIE_FRAGMENT = 0;

    private RecyclerView mRecyclerView;
    private List<MovieItem> mMovieItems = new ArrayList<>();
    private HandlerRequest<MovieHolder> mMovieHolderHandlerRequest;
    private static final String TAG = "Movie Fragment";
    private MovieAdapter mMovieAdapter;
    private FetchMovieTask mFetchMovieTask;
    private int mMessage = 0;
    private boolean lostFacouse = false;
    private Context mContext;
    private AVLoadingIndicatorView mAVLoadingIndicatorView;
    private TextView lateText;
    private int position = 0;
    boolean isNetworkAvilable = true;

    TextView mTextView;
    String sharePreferenceValue;
    Ringtone mRingtone;
    boolean mShowPhotos;
    boolean mRingtoneSound;
    int mCacheDays;

    public static MovieFragment newInstance() {
        return new MovieFragment() ;
    }

    @Override
    public void onResume() {
        super.onResume();
        lostFacouse = false;
        mMessage =0;
        mMovieItems = SQLiteDataBaseHelper.getInstance(mContext)
                .getMovieItemsDB(null, MOVIE_FRAGMENT,null);
        updateAdapter();


    }

    @Override
    public void onCreate(Bundle savedInstanceBundle)    {
        super.onCreate(savedInstanceBundle);
        mContext = getActivity();
        // 获取设置变量
        SharedPreferences sharedPreferences= PreferenceManager
                .getDefaultSharedPreferences(mContext);
        // 打开铃声
        mRingtoneSound = sharedPreferences
                .getBoolean(SettingFragment.KEY_NOTIFICATION_SOUNDS,false);
        // 获取铃声
        sharePreferenceValue = sharedPreferences
                .getString(SettingFragment.KEY_NOTIFICATION,"");
        mRingtone = RingtoneManager.getRingtone(mContext, Uri.parse(sharePreferenceValue));
        // 获取显示封面照片。
        mShowPhotos = sharedPreferences.getBoolean(SettingFragment.KEY_SHOW,true);
        /**
         * 设置数据缓存天数。注意此处返回的是string 值， 而不是int值。
         * */
        mCacheDays = Integer.parseInt(sharedPreferences
                .getString(SettingFragment.KEY_DELETE,"3"));
        if ((SQLiteDataBaseHelper.getInstance(mContext).getMovieItemsDB(null,
                MOVIE_FRAGMENT, null).size() == 0) && isNetworkAvailable()) {
            mFetchMovieTask = new FetchMovieTask();
            mFetchMovieTask.execute();
            Toast.makeText(mContext, "第一次加载时间较长，请您耐心等待。",
                    Toast.LENGTH_LONG).show();
        } else if (isNetworkAvailable()) {
            mMovieItems = SQLiteDataBaseHelper.getInstance(mContext)
                    .getMovieItemsDB(null, MOVIE_FRAGMENT,null);
            mFetchMovieTask = new FetchMovieTask();
            mFetchMovieTask.execute();
        } else {
            mMovieItems = SQLiteDataBaseHelper.getInstance(mContext)
                    .getMovieItemsDB(null, MOVIE_FRAGMENT,null);
            Toast.makeText(getContext(), "网络好像出问题了，但您仍可以添加迅雷任务哦！",
                    Toast.LENGTH_LONG).show();
        }

            // 创建主线程的 handler 实例。
        Handler mainHandler = new Handler();
        mMovieHolderHandlerRequest = new HandlerRequest<>(mainHandler, getContext());
        mMovieHolderHandlerRequest.setDownLoaderListener(new HandlerRequest.downLoaderListener<MovieHolder>() {
                    @Override
                    public void onDownloaderListener(MovieHolder target, Bitmap bitmap) {
                        Drawable drawable;
                        if (bitmap != null){
                            drawable = new BitmapDrawable(getResources(), bitmap);
                            target.bindDrawable(drawable);
                            Log.e(TAG,"Home image is not empty");
                        }else {
                            Log.e(TAG,"Home image is empty");
                        }
                    }
                });
        mMovieHolderHandlerRequest.start();
        mMovieHolderHandlerRequest.getLooper();
        Log.i(TAG, " Downloader Image start");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup,
                             Bundle savedInstanceBundle) {
        View view = inflater.inflate(R.layout.movie_recycleview, viewGroup, false);

        mAVLoadingIndicatorView = (AVLoadingIndicatorView) view.findViewById(R.id.avi_anim);
        lateText = (TextView)view.findViewById(R.id.late_text);
        if (mMovieItems.size() == 0){
          mAVLoadingIndicatorView.smoothToShow();
          lateText.setVisibility(View.VISIBLE);
        }else {
            mAVLoadingIndicatorView.smoothToHide();
            lateText.setVisibility(View.GONE);
        }
        mRecyclerView = (RecyclerView) view.findViewById(R.id.movie_fragment_recycle_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // 设定循环视图。
        mTextView = (TextView)view.findViewById(R.id.recyclerview_text);
        updateAdapter();
        return view;
    }
    /**
     * 刷新数据，更新现有的视图，插入到现在的视图中。
     */
    private void updateAdapter() {
        // 返回当前选择的卡片。
        ((LinearLayoutManager)mRecyclerView.getLayoutManager())
                .scrollToPositionWithOffset(position,position+1);
        position = 0;
        if (isAdded()) {
            mMovieAdapter = new MovieAdapter(mMovieItems);
            mRecyclerView.setAdapter(mMovieAdapter);
            /*if (mMovieItems.isEmpty()){
                mRecyclerView.setVisibility(View.GONE);
                mTextView.setVisibility(View.VISIBLE);
            }else {
                mRecyclerView.setVisibility(View.VISIBLE);
                mTextView.setVisibility(View.GONE);
            }*/
           //mMovieAdapter.notifyItemRangeInserted(0,insertMovieItems.size());
           mMovieAdapter.notifyDataSetChanged();
        }
    }

    private class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CardView mCardView;
        private MovieItem mMovieItem;
        private ImageView mImageView;
        private TextView mNameView;
        private TextView mPointView;
        private TextView mKindView;
        private TextView mActorView;
        private MovieItem mv;
        private String uriString;

        public MovieHolder(View view) {
            super(view);
            mCardView = (CardView) view.findViewById(R.id.movie_cardview);
            mImageView = view.findViewById(R.id.movie_image);
            mNameView = view.findViewById(R.id.movie_name);
            mPointView = view.findViewById(R.id.movie_point);
            mKindView = view.findViewById(R.id.movie_kind);
            mActorView = view.findViewById(R.id.movie_actor);
            view.setOnClickListener(this);

        }
        public void bindMovieItem(@NonNull MovieItem item) {
            uriString = item.getUri();
            mv= item;
        }
        public void bindDrawable(Drawable drawable) {
                mImageView.setImageDrawable(drawable);

        }
        @Override
        public void onClick(View view) {
            Intent intent = MoviesDetailActivity.newIntent(getContext(),mv);
            startActivity(intent);

        }
    }

    private class MovieAdapter extends RecyclerView.Adapter<MovieHolder> {

        @Override
        public MovieHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.list_movie_cardview,
                    viewGroup, false);
            return new MovieHolder(view);
        }

        @Override
        public int getItemCount() {
            return mMovieItems.size();
        }

        @Override
        public void onBindViewHolder(MovieHolder movieHolder, int position) {
            MovieItem mItem = mMovieItems.get(position);
            movieHolder.bindMovieItem(mItem);

            movieHolder.mActorView.setText(mItem.getActor());
            movieHolder.mKindView.setText(mItem.getKind());
            movieHolder.mPointView.setText(mItem.getPoint());
            movieHolder.mNameView.setText(mItem.getName());
            // 设置照片。
            movieHolder.bindDrawable(getResources()
                    .getDrawable(R.drawable.ic_insert_photo_black_24dp, null));

            Bitmap bitmap = ImageFileStore.getImageFileStore()
                    .getImageBitmap(mItem ,getContext(),1);

            if (bitmap != null ) {
                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                movieHolder.bindDrawable(drawable);
            }else if(mShowPhotos){      // 如果设置允许，就下载照片。
                mMovieHolderHandlerRequest.imageDownloader(movieHolder, mItem);
            }
        }
        public MovieAdapter(List<MovieItem> list) {
            mMovieItems = list;
        }

    }
    public class FetchMovieTask extends AsyncTask<Void, Void, List<MovieItem>> {


        @Override
        protected List<MovieItem> doInBackground(Void... params) {
            // 需要传递的两个变量。
            SQLiteDataBaseHelper sqLiteDataBaseHelper = SQLiteDataBaseHelper
                    .getInstance(getContext());
            return new DownloadFiles(sqLiteDataBaseHelper).urlHomePageLink();
        }

        @Override
        protected void onPostExecute(List<MovieItem> items) {
            mAVLoadingIndicatorView.smoothToHide();
            lateText.setVisibility(View.GONE);
            // 获取数据就播放声音。
            if (mRingtoneSound){
                mRingtone.play();
            }
            if (items.size()> 0) {
                Toast.makeText(mContext, "为您更新 " +
                        items.size() + " 部新电影", Toast.LENGTH_LONG).show();

                mMessage +=1;
                if (mMovieItems.size() ==0) {
                    sortCollections(items);
                    mMovieItems = items;
                    updateAdapter();
                }else {
                    mMovieItems.addAll(0, items);
                    sortCollections(mMovieItems);
                    mMovieAdapter.notifyItemRangeInserted(0,items.size());
                    updateAdapter();
                }
            }
            if (mMessage == 0 && !lostFacouse) {
                Toast.makeText(getContext(), "当前已是最新数据", Toast.LENGTH_LONG).show();
                mMessage = 0;
            }
        }
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

    }

    public void sortCollections(List<MovieItem> list){
        // 系统版本大于7.1.1就使用特殊的排序方式。
        if (Build.VERSION.SDK_INT >= 25){
            Collections.sort(list, new Comparator<MovieItem>() {
                @Override
                public int compare(MovieItem o1, MovieItem o2) {
                    return -(o1.getStoreTime()-o2.getStoreTime());
                }
            });
        }else {
            Collections.sort(list);
        }
    }
  /*  // 给 mMovieItems 排序的方法
    public void orderBy(List<MovieItem> movieItems){
        for (int i = 0; i<movieItems.size()/2;i++){
            if (movieItems.get(i) != movieItems.get((movieItems.size()-i-1))){
                MovieItem movieItem;
                MovieItem movieItem1;

                movieItem = movieItems.get(i);
                movieItem1= movieItems.get(movieItems.size()-i-1);
                movieItems.set(i,movieItem1);
                movieItems.set(movieItems.size()-i-1,movieItem);

            }
        }
    }*/

    @Override
    public void onPause() {
        super.onPause();
        lostFacouse = true;
        mMessage +=1;
       position =  ((LinearLayoutManager)mRecyclerView.getLayoutManager())
               .findFirstCompletelyVisibleItemPosition();
       if (mAVLoadingIndicatorView.isShown()){
           mAVLoadingIndicatorView.smoothToHide();
           lateText.setVisibility(View.GONE);
       }
    }

    // 退出looper 循环。
    // 清除MEssage对列。
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMovieHolderHandlerRequest.clearQuene();
    }

    // 确保停止这个HandlerRequest 进程，防止它一直运行下去。
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFetchMovieTask != null) {
            mFetchMovieTask.cancel(false);
        }
        mMovieHolderHandlerRequest.quit();
        Log.i(TAG, "Downloader image Quite");

        Date date = new Date();
        Integer integer = (int) date.getTime();

        // 删除图片资源和数据库资源。
        SharedPreferences sharedPreferences= PreferenceManager
                .getDefaultSharedPreferences(mContext);
        int cacheDays = Integer.parseInt(sharedPreferences
                .getString(SettingFragment.KEY_DELETE,"3"));
        // 删除下载时间超过三天的电影列表。
        List<MovieItem> movieItems = SQLiteDataBaseHelper.getInstance(mContext)
                .getMovieItemsDB(null,0,null);
        for (MovieItem movie:movieItems
                ) {
            if (integer - movie.getStoreTime() > cacheDays*24*60*60*1000){
                SQLiteDataBaseHelper.getInstance(mContext).deleteMovieItemDB(movie);
                ExternalStoreFile.get(movie,mContext).deletePhotoFile();
            }
        }
    }

    private boolean isNetworkAvailable (){
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext()
                .getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager ==  null){
            isNetworkAvilable = false;
            return false;
        }

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isAvailable()){
            isNetworkAvilable = false;
            return false;
        }
        return true;
    }
}
