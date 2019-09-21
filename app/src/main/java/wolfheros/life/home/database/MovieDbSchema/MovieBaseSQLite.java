package wolfheros.life.home.database.MovieDbSchema;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static wolfheros.life.home.database.MovieDbSchema.MovieDbSchema.*;

/**
 * 操作数据库类
 * */
public class MovieBaseSQLite extends SQLiteOpenHelper {

    private static final int VERSION = 4;
    private static final String DATABASE_NAME = "movieItemSQLite.db";

    MovieBaseSQLite(Context context) {
        super(context, DATABASE_NAME ,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + MovieTable.NAME
                +"(" +"_id integer primary key autoincrement, "
                +MovieTable.Cols.MOVIE_NAME+", "
                +MovieTable.Cols.UUID+", "
                +MovieTable.Cols.ACTOR+", "
                +MovieTable.Cols.DERECTOR+", "
                +MovieTable.Cols.MOVIE_DETAIL+", "
                +MovieTable.Cols.PHOTOS_URL+", "
                +MovieTable.Cols.POINT+", "
                +MovieTable.Cols.URI+", "
                +MovieTable.Cols.MOVIE_DATE+", "
                +MovieTable.Cols.MOVIE_TIME+", "
                +MovieTable.Cols.MOVIE_LANGUAGE+", "
                +MovieTable.Cols.MOVIE_COUNTRY+", "
                +MovieTable.Cols.MOVIE_KIND+", "
                +MovieTable.Cols.MOVIE_SHORTCUT_URI+", "
                +MovieTable.Cols.MOVIE_DOWNLOAD_URI+", "
                +MovieTable.Cols.MY_FAVORITE_MOVIE+", "
                +MovieTable.Cols.RECENT_DOWNLOAD+", "
                +MovieTable.Cols.STORE_TIME+", "
                +MovieTable.Cols.SEARCH_MOVIE+", "
                +MovieTable.Cols.SEARCH_LONG_NAME+", "
                +MovieTable.Cols.SEARCH_WORD
                +")"
        );
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                db.execSQL("ALTER TABLE " + MovieTable.NAME+ " ADD COLUMN "
                        + MovieTable.Cols.STORE_TIME + " NOT NULL DEFAULT 0;");
                db.execSQL("ALTER TABLE " + MovieTable.NAME+ " ADD COLUMN "
                        + MovieTable.Cols.SEARCH_MOVIE + " NOT NULL DEFAULT 'false';");
                db.execSQL("ALTER TABLE " + MovieTable.NAME+ " ADD COLUMN "
                        + MovieTable.Cols.SEARCH_LONG_NAME );
                db.execSQL("ALTER TABLE " + MovieTable.NAME+ " ADD COLUMN "
                        + MovieTable.Cols.SEARCH_WORD );
                break;
            case 2:
                db.execSQL("ALTER TABLE " + MovieTable.NAME+ " ADD COLUMN "
                        + MovieTable.Cols.SEARCH_LONG_NAME );
                db.execSQL("ALTER TABLE " + MovieTable.NAME+ " ADD COLUMN "
                        + MovieTable.Cols.SEARCH_WORD );
                break;
            case 3:
                db.execSQL("ALTER TABLE " + MovieTable.NAME+ " ADD COLUMN "
                        + MovieTable.Cols.SEARCH_WORD );
                break;
        }
    }

    @Override
    public synchronized void close() {
        super.close();
    }
}
