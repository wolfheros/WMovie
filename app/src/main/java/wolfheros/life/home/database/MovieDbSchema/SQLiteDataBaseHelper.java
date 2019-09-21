package wolfheros.life.home.database.MovieDbSchema;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import wolfheros.life.home.MovieItem;
import wolfheros.life.home.tools.Main.HashValue;

import static wolfheros.life.home.database.MovieDbSchema.MovieDbSchema.*;

/**
 *  创建操作DataBase 的类
 * */

public class SQLiteDataBaseHelper {

    private Context mContext;
    private SQLiteDatabase mSQLiteDatabase;
    private static SQLiteDataBaseHelper mSQLiteDataBaseHelper;
    private static final String TAG = "SQLiteDateBaseHelper";

    public  List<MovieItem> getMovieItems(){
        return new ArrayList<>();
    }


    // 创建单例类,
    public static SQLiteDataBaseHelper getInstance(Context context) {
        if (mSQLiteDataBaseHelper != null) {
            return mSQLiteDataBaseHelper;
        }
        return new SQLiteDataBaseHelper(context);
    }

    private SQLiteDataBaseHelper (Context context) {
        mContext = context;
        mSQLiteDatabase = new MovieBaseSQLite(mContext).getWritableDatabase();
    }

    private ContentValues getContentValues(MovieItem movieItem) {
        ContentValues values = new ContentValues();
        values.put(MovieTable.Cols.MOVIE_NAME,movieItem.getName());
        values.put(MovieTable.Cols.UUID,movieItem.getUUID());
        values.put(MovieTable.Cols.ACTOR,movieItem.getActor());
        values.put(MovieTable.Cols.DERECTOR,movieItem.getDerector());
        values.put(MovieTable.Cols.MOVIE_DETAIL,movieItem.getMovieDetail());
        values.put(MovieTable.Cols.PHOTOS_URL,movieItem.getPhotosUri());
        values.put(MovieTable.Cols.POINT,movieItem.getPoint());
        values.put(MovieTable.Cols.URI,movieItem.getUri());
        values.put(MovieTable.Cols.MOVIE_DATE,movieItem.getDate());
        values.put(MovieTable.Cols.MOVIE_TIME,movieItem.getMovieTime());
        values.put(MovieTable.Cols.MOVIE_LANGUAGE,movieItem.getLaguage());
        values.put(MovieTable.Cols.MOVIE_COUNTRY,movieItem.getCountry());
        values.put(MovieTable.Cols.MOVIE_KIND,movieItem.getKind());
        values.put(MovieTable.Cols.MOVIE_SHORTCUT_URI,movieItem.getPhotoShortUri());
        values.put(MovieTable.Cols.MOVIE_DOWNLOAD_URI,movieItem.getMovieDownloadUri());
        values.put(MovieTable.Cols.MY_FAVORITE_MOVIE,movieItem.isMyFavoMovie());
        values.put(MovieTable.Cols.RECENT_DOWNLOAD,movieItem.isReDownload());
        values.put(MovieTable.Cols.STORE_TIME,movieItem.getStoreTime());
        values.put(MovieTable.Cols.SEARCH_MOVIE,movieItem.getSearchMovie());
        values.put(MovieTable.Cols.SEARCH_LONG_NAME,movieItem.getSearchLongName());
        values.put(MovieTable.Cols.SEARCH_WORD,movieItem.getSearchWord());

        return values;
    }

    // 添加并插入数据库中。
    public void addMovieItemDB(MovieItem movieItem) {
        ContentValues values = getContentValues(movieItem);
        mSQLiteDatabase.insert(MovieTable.NAME,null,values);
    }

    /**
     * 添加更新方法为以后使用,这里有一个坑，就是要查询出你需要修改的行，
     * 最后对整行数据进行修改也就是，所有的字段值。所以需要重新创建一个新的ContentValues实例，
     *  还有一点，就是数据库不能进行整数的筛选。即使将整数转换成字符串形式也不行。
     */
    public void updateMovieItemDB(MovieItem movieItem  ) {
        ContentValues values = getContentValues(movieItem);
        mSQLiteDatabase.update(MovieTable.NAME ,
                values,
                MovieTable.Cols.UUID+ "= ?",
                new String[]{movieItem.getUUID()});
    }
    /**
     *  删除指定的数据库数据。
     * */
    public void deleteMovieItemDB(MovieItem movieItem){
        mSQLiteDatabase.delete(MovieTable.NAME
                ,MovieTable.Cols.UUID+"= ?"
                ,new String[]{movieItem.getUUID()});
    }

    // 获取所有的MovieItem
    public List<MovieItem> getMovieItemsDB(String string , int code , String searchWord){
        List<MovieItem> movieItems = new ArrayList<>();
        Cursor cursor ;
        if (code == 1 || code == 2){    //  Favo_RecentFrament DB
            cursor =new CursorWrapper(mSQLiteDatabase.query(MovieTable.NAME,
                    null,
                    string + "= ?",
                    new String[]{"true"},
                    null,
                    null,
                    null));
        }else if (code == 0){ // 返回所有主页加载的结果，非搜索结果。
             cursor = new CursorWrapper(mSQLiteDatabase.query(MovieTable.NAME,
                    null,
                    MovieTable.Cols.SEARCH_MOVIE + "= ?",
                    new String[]{"false"},
                    null,
                    null,
                    MovieTable.Cols.ORDER_BY));
        }else {
            cursor =new CursorWrapper(mSQLiteDatabase.query(MovieTable.NAME,
                    null,
                    string + "= ?",
                    new String[]{searchWord},
                    null,
                    null,
                    null));

        }

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
              movieItems.add(getMovieItem(cursor));
              cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
        Log.i(TAG ,"获取的数据库中数据数量 " + movieItems.size());
        return movieItems;
    }

    // 根据连接获取单个MovieItem
    public MovieItem getMovieItemDB(String  uri) {
        String uuid = HashValue.hashKeyForDisk(uri);
        CursorWrapper cursor =new CursorWrapper(mSQLiteDatabase.query(MovieTable.NAME,
                null,
                MovieTable.Cols.UUID + "= ?",
                new String[]{uuid},
                null,
                null,
                null));

        try {
            if (cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            return getMovieItem(cursor);
        }finally {
            cursor.close();
        }
    }

    private MovieItem getMovieItem(Cursor cursor) {
        String name = cursor.getString(cursor.getColumnIndex(MovieTable.Cols.MOVIE_NAME));
        String uuid = cursor.getString(cursor.getColumnIndex(MovieTable.Cols.UUID));
        String actor = cursor.getString(cursor.getColumnIndex(MovieTable.Cols.ACTOR));
        String derector = cursor.getString(cursor.getColumnIndex(MovieTable.Cols.DERECTOR));
        String movieDetail = cursor.getString(cursor.getColumnIndex(MovieTable.Cols.MOVIE_DETAIL));
        String photosuri = cursor.getString(cursor.getColumnIndex(MovieTable.Cols.PHOTOS_URL));
        String point = cursor.getString(cursor.getColumnIndex(MovieTable.Cols.POINT));
        String uri = cursor.getString(cursor.getColumnIndex(MovieTable.Cols.URI));
        String movieDate = cursor.getString(cursor.getColumnIndex(MovieTable.Cols.MOVIE_DATE));
        String movieTime = cursor.getString(cursor.getColumnIndex(MovieTable.Cols.MOVIE_TIME));
        String movieLanguage = cursor.getString(cursor.getColumnIndex(MovieTable.Cols.MOVIE_LANGUAGE));
        String country = cursor.getString(cursor.getColumnIndex(MovieTable.Cols.MOVIE_COUNTRY));
        String movieKind = cursor.getString(cursor.getColumnIndex(MovieTable.Cols.MOVIE_KIND));
        String shorturi = cursor.getString(cursor.getColumnIndex(MovieTable.Cols.MOVIE_SHORTCUT_URI));
        String moviedownloaduri = cursor.getString(cursor.getColumnIndex(MovieTable.Cols.MOVIE_DOWNLOAD_URI));
        String myFavoMovie = cursor.getString(cursor.getColumnIndex(MovieTable.Cols.MY_FAVORITE_MOVIE));
        String reDownload = cursor.getString(cursor.getColumnIndex(MovieTable.Cols.MOVIE_DOWNLOAD_URI));
        Integer storeTime = cursor.getInt(cursor.getColumnIndex(MovieTable.Cols.STORE_TIME));
        String searchmovie = cursor.getString(cursor.getColumnIndex(MovieTable.Cols.SEARCH_MOVIE));
        String searchlongname = cursor.getString(cursor.getColumnIndex(MovieTable.Cols.SEARCH_LONG_NAME));
        String searchword = cursor.getString(cursor.getColumnIndex(MovieTable.Cols.SEARCH_WORD));



        MovieItem movieItem = new MovieItem(uuid);
        movieItem.setActor(actor);
        movieItem.setDerector(derector);
        movieItem.setMovieDetail(movieDetail);
        movieItem.setName(name);
        movieItem.setPhotosUri(photosuri);
        movieItem.setUri(uri);
        movieItem.setPoint(point);
        movieItem.setCountry(country);
        movieItem.setLaguage(movieLanguage);
        movieItem.setKind(movieKind);
        movieItem.setMovieTime(movieTime);
        movieItem.setDate(movieDate);
        movieItem.setPhotoShortUri(shorturi);
        movieItem.setMovieDownloadUri(moviedownloaduri);
        movieItem.setMyFavoMovie(myFavoMovie);
        movieItem.setReDownload(reDownload);
        movieItem.setStoreTime(storeTime);
        movieItem.setSearchMovie(searchmovie);
        movieItem.setSearchLongName(searchlongname);
        movieItem.setSearchWord(searchword);

        return movieItem;
    }

}
