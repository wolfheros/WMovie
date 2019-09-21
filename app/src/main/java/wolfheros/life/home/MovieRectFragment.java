package wolfheros.life.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import wolfheros.life.home.detail.MoviesDetailActivity;
import wolfheros.life.home.database.MovieDbSchema.MovieDbSchema;
import wolfheros.life.home.database.MovieDbSchema.SQLiteDataBaseHelper;
import wolfheros.life.home.search.detail.SearchDetailActivity;
import wolfheros.life.home.setting.SettingFragment;
import wolfheros.life.home.tools.Main.ImageFileStore;

/**
 * 此类用来显示下载记录
 * */

public class MovieRectFragment extends Fragment {

    private static List<MovieItem> sMovieItems = new ArrayList<>();
    private static final int MOVIE_RECT_FRAGMENT = 2;

    private RecyclerView mRecyclerView;
    private TextView mTextView;
    private int position = 0;
    boolean mShowPhotos;

    public static Fragment newInstance(Context context,int code){
            sMovieItems = SQLiteDataBaseHelper.getInstance(context)
                    .getMovieItemsDB(MovieDbSchema.MovieTable.Cols.RECENT_DOWNLOAD
                            ,code
                            ,null);
            return new MovieRectFragment();
    }
    @Override
    public void onResume() {
        super.onResume();
        final List<MovieItem> list;
        list = SQLiteDataBaseHelper.getInstance(getContext())
                .getMovieItemsDB(MovieDbSchema.MovieTable.Cols.RECENT_DOWNLOAD,
                        MOVIE_RECT_FRAGMENT,null);

        if (!list.equals(sMovieItems)){
            sMovieItems = list;
        }
        updateUI();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 获取设置变量
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getContext());
        // 获取显示封面照片。
        mShowPhotos = sharedPreferences.getBoolean(SettingFragment.KEY_SHOW,true);
    }

    /**
     * 刷新视图
     * */
    public void updateUI(){
        ((LinearLayoutManager)mRecyclerView.getLayoutManager())
                .scrollToPositionWithOffset(position,position+1);
        position = 0;

       if (isAdded()){
           mRecyclerView.setAdapter(new MovieFavoAdapter(sMovieItems));
           if (sMovieItems.isEmpty()){
               mRecyclerView.setVisibility(View.GONE);
               mTextView.setVisibility(View.VISIBLE);
           }else {
               mRecyclerView.setVisibility(View.VISIBLE);
               mTextView.setVisibility(View.GONE);
           }
       }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.recent_fragment,container,false);
        mRecyclerView=(RecyclerView) view.findViewById(R.id.movie_fragment_recycle_view);
        mTextView = (TextView)view.findViewById(R.id.recyclerview_text);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return view;

    }

    private class MovieFavoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CardView mCardView;
        private MovieItem mMovieItem;
        private ImageView mImageView;
        private TextView mNameView;
        private TextView mPointView;
        private TextView mKindView;
        private TextView mActorView;
        private MovieItem mv;
        private String uriString;

        public MovieFavoHolder(View view) {
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
            mv = item;
            uriString = item.getUri();
        }
        public void bindDrawable(Drawable drawable) {
            mImageView.setImageDrawable(drawable);
            //  如果设置允许就瞎子照片。
            if (mShowPhotos) {
                Picasso.get().load(mv.getPhotosUri()).error(R.drawable.error_template).into(mImageView);
            }

        }
        @Override
        public void onClick(View view) {
            // 选择是启动搜索的结果还是，直接主页获取的结果。
            Intent intent;
            if (mv.getSearchWord() != null){
                intent = SearchDetailActivity.newIntent(getContext(),mv);
            }else {
                intent = MoviesDetailActivity.newIntent(getContext(),mv);
            }
            startActivity(intent);

        }
    }

    private class MovieFavoAdapter extends RecyclerView.Adapter<MovieRectFragment.MovieFavoHolder> {
        private List<MovieItem> mMovieItems ;
        @Override
        public MovieFavoHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.list_movie_cardview, viewGroup, false);
            return new MovieFavoHolder(view);
        }

        @Override
        public int getItemCount() {
            return mMovieItems.size();
        }

        @Override
        public void onBindViewHolder(MovieRectFragment.MovieFavoHolder movieHolder, int position) {
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
            }else {
                movieHolder.bindDrawable(null);
            }
        }
         MovieFavoAdapter(List<MovieItem> list) {
            mMovieItems = list;
        }
    }
    // 软禁退出后删除，最近下载记录。
    @Override
    public void onDestroy() {
        /*for (MovieItem movieItem : sMovieItems){
            movieItem.setReDownload("false");
            SQLiteDataBaseHelper.getInstance(getContext()).updateMovieItemDB(movieItem);
        }*/
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        position = ((LinearLayoutManager)mRecyclerView.getLayoutManager())
                .findFirstCompletelyVisibleItemPosition();
    }
}