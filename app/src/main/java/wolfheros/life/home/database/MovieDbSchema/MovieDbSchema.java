package wolfheros.life.home.database.MovieDbSchema;

import android.graphics.drawable.Drawable;
import android.net.Uri;

/**
 * 此类是数据库类，用来储存获取的MovieItems 对象。
 * */
public class MovieDbSchema {
    /**
     * 内部类用来创建数据库表格。
     */

    public static final class MovieTable {
        public static final String NAME = "movie";

        /**
         *  内部类，数据库的内容
         * */

         public static final class Cols{
            public static final String UUID = "uuid";
            public static final String DERECTOR = "derector";
            public static final String ACTOR = "actor";
            public static final String POINT = "point";
            public static final String MOVIE_DETAIL ="moviedetail";
            public static final String URI ="uri";
            public static final String PHOTOS_URL ="photosUri";
            public static final String MOVIE_NAME ="movieName";
            public static final String MOVIE_DATE ="moviedate";
            public static final String MOVIE_TIME ="movietime";
            public static final String MOVIE_LANGUAGE ="movielanguage";
            public static final String MOVIE_COUNTRY ="moviecountry";
            public static final String MOVIE_KIND ="moviekind";
            public static final String MOVIE_SHORTCUT_URI ="movieshortcut";
            public static final String MOVIE_DOWNLOAD_URI ="moviedownloaduri";
            public static final String MY_FAVORITE_MOVIE ="myfavoritemovie";
            public static final String RECENT_DOWNLOAD ="recentdownload";
            public static final String SEARCH_MOVIE = "searchmovie";
            public static final String STORE_TIME = "storetime";
            public static final String SEARCH_LONG_NAME = "searchlongname";
            public static final String SEARCH_WORD = "searchword";
            public static final String ORDER_BY="datetime("+STORE_TIME+") DESC";
        }
    }
}
