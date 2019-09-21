package wolfheros.life.home.search;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import wolfheros.life.home.MovieItem;
import wolfheros.life.home.R;
import wolfheros.life.home.database.MovieDbSchema.SQLiteDataBaseHelper;
import wolfheros.life.home.search.detail.SearchDetailActivity;
import wolfheros.life.home.tools.Main.CharToHex;
import wolfheros.life.home.tools.Main.SearchDownload;

import static wolfheros.life.home.database.MovieDbSchema.MovieDbSchema.MovieTable.Cols.SEARCH_WORD;

public class SearchFragment extends Fragment {
    private static final String SEARCH_QUERY = "Movie_Search";
    private static final String SEARCH_URI =
            "http://s.ygdy8.com/plus/so.php?kwtype=0&searchtype=title&keyword=";
    private static final String TAG ="SearchFragment";
     static final int SEARCH_FRAGMENT = 3;
    private String  stringQuery;
    private RecyclerView mRecyclerView;
    private TextView mTextView;
    private List<MovieItem> mMovieItems = new ArrayList<>();
    private SearchAdapter mSearchAdapter;
    private String stringHex;
    private String urlString;
    SearchTask searchTask;
    private Context mContext;
    private boolean hasFacous = true;

    private AVLoadingIndicatorView mAVLoadingIndicatorView;
    private TextView lateText;
    int position = 0;

    public static Fragment newInstance(@NonNull String string){
        Bundle args = new Bundle();
        args.putString(SEARCH_QUERY , string);
        SearchFragment searchFragment = new SearchFragment();
        searchFragment.setArguments(args);
        return searchFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        hasFacous = true;
        ((LinearLayoutManager)mRecyclerView.getLayoutManager())
                .scrollToPositionWithOffset(position,position+1);
        position =0;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

        stringQuery = (String )getArguments().get(SEARCH_QUERY);
        stringHex = CharToHex.stringToHex(stringQuery);
        urlString = SEARCH_URI + stringHex;

        // 启动进程
        searchTask = new SearchTask();
        searchTask.execute();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment,container,false);
        // 加载动画。
        mAVLoadingIndicatorView = (AVLoadingIndicatorView)view.findViewById(R.id.avi_anim);
        lateText = (TextView)view.findViewById(R.id.late_text);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.movie_fragment_recycle_view);
        // 设定循环视图。
        mTextView = (TextView)view.findViewById(R.id.recyclerview_text);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSearchAdapter = new SearchAdapter(mMovieItems);
        updateUI(mMovieItems);
        return view;

    }

    private void updateUI(List<MovieItem> list) {
        if (isAdded()) {
            mSearchAdapter = new SearchAdapter(list);
            mRecyclerView.setAdapter(mSearchAdapter);
            /*if (list == null || list.isEmpty()) {
                mRecyclerView.setVisibility(View.GONE);
                mTextView.setVisibility(View.VISIBLE);
            } else {
                mRecyclerView.setVisibility(View.VISIBLE);
                mTextView.setVisibility(View.GONE);
            }*/
            mSearchAdapter.notifyDataSetChanged();
        }

    }

    private class SearchHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView nameView;
        TextView kindView;
        TextView languageView;
        TextView dateView;
        TextView countryView;
        String uriString;
        MovieItem mv;

        public SearchHolder(View itemView) {
            super(itemView);
            nameView =(TextView) itemView.findViewById(R.id.name_text);
            nameView.setTypeface(null, Typeface.BOLD);
            kindView = (TextView) itemView.findViewById(R.id.movie_search_kind);
            languageView =(TextView) itemView.findViewById(R.id.movie_language);
            dateView = (TextView)itemView.findViewById(R.id.movie_time);
            countryView =(TextView) itemView.findViewById(R.id.movie_country);
            itemView.setOnClickListener(this);
        }
        public void bindItem(MovieItem movieItem){
            uriString = movieItem.getUri();
            mv = movieItem;
        }
        @Override
        public void onClick(View v) {
            mv.setSearchMovie("true");
            Intent intent = SearchDetailActivity.newIntent(mContext,mv);
            startActivity(intent);
        }
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchHolder>{

        private List<MovieItem> itemList;

        SearchAdapter(List<MovieItem> movieItems){
            itemList = movieItems;
        }

        @NonNull
        @Override
        public SearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.search_fragment_cardview,parent,false);
            return new SearchHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchHolder holder, int position) {
            MovieItem movieItem = itemList.get(position);
            holder.bindItem(movieItem);

            holder.countryView.setText(movieItem.getCountry());
            holder.dateView.setText(movieItem.getDate());
            holder.languageView.setText(movieItem.getLaguage());
            holder.kindView.setText(movieItem.getKind());
            holder.nameView.setText(movieItem.getSearchLongName());
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }
    }
    public class SearchTask extends AsyncTask<Void,Void,List<MovieItem>>{
        SQLiteDataBaseHelper sqLiteDataBaseHelper;
        @Override
        protected List<MovieItem> doInBackground(Void... voids) {
            sqLiteDataBaseHelper = SQLiteDataBaseHelper.getInstance(mContext);
            SearchDownload searchDownload = new SearchDownload(sqLiteDataBaseHelper
                    , mContext , stringQuery);
            searchDownload.getSearchHomeElements(urlString);
            return searchDownload.getEachUrl(SEARCH_FRAGMENT);
        }

        @Override
        protected void onPostExecute(List<MovieItem> list) {
            mAVLoadingIndicatorView.smoothToHide();
            lateText.setVisibility(View.GONE);
            if (list.size() != 0){
                for (int i = 0 ; i<list.size();i++){
                    if (list.get(i) == null){
                       list.remove(i);
                    }else if(!hasSearch(mMovieItems,list.get(i))){
                        mMovieItems.add(list.get(i));
                        updateUI(mMovieItems);
                    }
                }
            }else {
                if (mMovieItems.size() == 0) {
                    if (hasFacous) {
                        Toast.makeText(mContext, "未搜索到相关电影，尝试其他关键词",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    updateUI(SQLiteDataBaseHelper.getInstance(mContext)
                            .getMovieItemsDB(SEARCH_WORD, SEARCH_FRAGMENT, stringQuery));

                }
            }
        }
        /**
         * 判断是否数据库中已经含有
         * */
        private boolean hasSearch(List<MovieItem> list , MovieItem item){
            String stringuri = item.getUUID();
            for (MovieItem movieItem : list){
                if (stringuri.equals( movieItem.getUUID())) {
                    return true;
                }
            }
            return false;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        hasFacous=false;
        position = ((LinearLayoutManager)mRecyclerView
                .getLayoutManager()).findFirstCompletelyVisibleItemPosition();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG , "Task is canceled");
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
