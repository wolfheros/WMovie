package wolfheros.life.home.kind;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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

import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import wolfheros.life.home.*;
import wolfheros.life.home.database.MovieDbSchema.SQLiteDataBaseHelper;
import wolfheros.life.home.kind.detailmovie.LasteMoviesDetailActivity;
import wolfheros.life.home.setting.SettingFragment;
import wolfheros.life.home.tools.Main.*;

/**
 *  此类是主视图类.
 *  2018-5-28 23:28:17
 * */

public class LasteMovieFragment extends Fragment {

    private static final int MOVIE_FRAGMENT = 0;
    private static final String LASTE_MOVIE_SITE = "lastmovie";

    private String uri;
    private RecyclerView mRecyclerView;
    private List<MovieItem> mMovieItems = new ArrayList<>();
    private static final String TAG = "Movie Fragment";
    private MovieAdapter mMovieAdapter;
    private int mMessage = 0;
    private boolean lostFacouse = false;
    private Context mContext;
    private AVLoadingIndicatorView mAVLoadingIndicatorView;

    int recyclerPosition = 0;
    boolean isNetworkAvilable = true;
    TextView lateText;
    FetchMovieTask mFetchMovieTask;
    String sharePreferenceValue;
    Ringtone mRingtone;
    boolean mShowPhotos;
    boolean mRingtoneSound;
    View mView;

    public static LasteMovieFragment newInstance(String string) {
        Bundle bundle = new Bundle();
        bundle.putString(LASTE_MOVIE_SITE,string);
        LasteMovieFragment lasteMovieFragment = new LasteMovieFragment() ;
        lasteMovieFragment.setArguments(bundle);
        return lasteMovieFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        lostFacouse = false;
        mMessage =0;
        updateAdapter();
    }

    @Override
    public void onCreate(Bundle savedInstanceBundle)    {
        super.onCreate(savedInstanceBundle);
        mContext = getActivity();

        // 获取设置变量
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(mContext);
        // 打开铃声
        mRingtoneSound = sharedPreferences
                .getBoolean(SettingFragment.KEY_NOTIFICATION_SOUNDS,false);
        // 获取铃声
        sharePreferenceValue = sharedPreferences
                .getString(SettingFragment.KEY_NOTIFICATION,"");
        mRingtone = RingtoneManager.getRingtone(mContext, Uri.parse(sharePreferenceValue));
        // 获取显示封面照片。
        mShowPhotos = sharedPreferences.getBoolean(SettingFragment.KEY_SHOW,true);

        uri = (String) getArguments().get(LASTE_MOVIE_SITE);

        if (isNetworkAvailable()) {
            mFetchMovieTask = new FetchMovieTask();
            mFetchMovieTask.execute();
        } else {
            Toast.makeText(getContext(), "网络好像出问题了，请检查您的网络连接！",
                    Toast.LENGTH_LONG).show();
        }




    }
   /* // 恢复 recyclerView 实例。
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null){
            savedRecycleView = savedInstanceState.getParcelable(LASTE_MOVIE_SITE);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecycleView);
        }
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup
            , Bundle savedInstanceBundle) {
        mView = inflater.inflate(R.layout.kind_recyclerview, viewGroup, false);

        mRecyclerView = (RecyclerView) mView.findViewById(R.id.movie_fragment_recycle_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAVLoadingIndicatorView = (AVLoadingIndicatorView)mView.findViewById(R.id.avi_anim);
        lateText = (TextView)mView.findViewById(R.id.late_text);
        // 设定循环视图。
        // mTextView = (TextView)mView.findViewById(R.id.recyclerview_text);
        updateAdapter();
        return mView;
    }
    /**
     * 刷新数据，更新现有的视图，插入到现在的视图中。
     */
    private void updateAdapter() {
        ((LinearLayoutManager)mRecyclerView.getLayoutManager())
                .scrollToPositionWithOffset(recyclerPosition,recyclerPosition+1);
        recyclerPosition = 0;
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
            mv= item;
        }
        public void bindDrawable(Drawable drawable) {
            mImageView.setImageDrawable(drawable);
            //  如果设置允许就下载照片。
            if (mShowPhotos) {
                Picasso.get().load(mv.getPhotosUri()).error(R.drawable.error_template).into(mImageView);
            }
        }
        @Override
        public void onClick(View view) {
            if(SQLiteDataBaseHelper.getInstance(mContext).getMovieItemDB(mv.getUri()) != null){
                Intent intent = LasteMoviesDetailActivity.newIntent(getContext(),mv);
                startActivity(intent);
            }else {
                mv.setSearchMovie("true");
                SQLiteDataBaseHelper.getInstance(mContext).addMovieItemDB(mv);
                Intent intent = LasteMoviesDetailActivity.newIntent(getContext(),mv);
                startActivity(intent);
            }


        }
    }

    private class MovieAdapter extends RecyclerView.Adapter<MovieHolder> {

        @Override
        public MovieHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.list_movie_cardview, viewGroup, false);
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
        }
        public MovieAdapter(List<MovieItem> list) {
            mMovieItems = list;
        }

    }
    public class FetchMovieTask extends AsyncTask<Void, Void, List<MovieItem>> {
        @Override
        protected List<MovieItem> doInBackground(Void... params) {
            // 需要传递的两个变量。
            SQLiteDataBaseHelper sqLiteDataBaseHelper = SQLiteDataBaseHelper.getInstance(getContext());
            SearchDownload searchDownload =  new SearchDownload(sqLiteDataBaseHelper,mContext,null);
            searchDownload.getSearchHomeElements(uri);

            return searchDownload.getEachUrl(MOVIE_FRAGMENT);
        }

        @Override
        protected void onPostExecute(List<MovieItem> items) {
            mAVLoadingIndicatorView.smoothToHide();
            lateText.setVisibility(View.GONE);
            if (items.size()> 0) {
                if (mRingtoneSound){
                    mRingtone.play();
                }
                // Toast.makeText(mContext, "为您更新 " + items.size() + " 部新电影", Toast.LENGTH_LONG).show();
                Snackbar.make(mView,"为您加载了 " + items.size() + " 部新电影",
                        Snackbar.LENGTH_LONG).show();
                mMessage +=1;
                if (mMovieItems.size() ==0) {
                    mMovieItems.addAll(0, items);
                    updateAdapter();
                }else {
                    mMovieItems.addAll(0, items);
                    mMovieAdapter.notifyItemRangeInserted(0,items.size());
                    updateAdapter();
                }

            }
            if (mMessage == 0 && !lostFacouse) {
                // Toast.makeText(getContext(), "当前已是最新数据", Toast.LENGTH_LONG).show();
                Snackbar.make(mView,"未获取到电影数据，请稍后再试。",Snackbar.LENGTH_LONG).show();
                mMessage = 0;
            }

        }
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        lostFacouse = true;
        mMessage +=1;
        // 保存recyclerView 视图的位置。
        recyclerPosition = ((LinearLayoutManager)mRecyclerView.getLayoutManager())
                .findFirstCompletelyVisibleItemPosition();
    }
    /*// 保存 RecyclerView 实例
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(LASTE_MOVIE_SITE , mRecyclerView.getLayoutManager().onSaveInstanceState());
    }*/

    // 退出looper 循环。
    // 清除MEssage对列。
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    // 确保停止这个HandlerRequest 进程，防止它一直运行下去。
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Downloader image Quite");
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
