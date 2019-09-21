package wolfheros.life.home.tools.Main;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import wolfheros.life.home.MovieItem;

/**
 *  创建外部储存专用类。
 *  完成于 2018-5-28 23:31:30
 *
 */
public class ExternalStoreFile {

    private static final String TAG ="ExternalStoreFile";
    private Context mContext;
    private MovieItem mMovieItem;
    private static ExternalStoreFile sExternalStoreFile;

    private ExternalStoreFile(MovieItem movieItem , Context context) {
        mContext = context;
        mMovieItem = movieItem;
    }

    public static ExternalStoreFile get(MovieItem movieItem , Context context) {
        if (sExternalStoreFile == null){
            sExternalStoreFile = new ExternalStoreFile(movieItem,context);
            return sExternalStoreFile;
        }else {
            sExternalStoreFile.setContext(context);
            sExternalStoreFile.setMovieItem(movieItem);
            return sExternalStoreFile;
        }
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public MovieItem getMovieItem() {
        return mMovieItem;
    }

    public void setMovieItem(MovieItem movieItem) {
        mMovieItem = movieItem;
    }

    // 储存文件到手机外部储存
    public void storeFile(@NonNull byte[] bytes , String string) {
        String uuid;
        if (string == mMovieItem.getPhotosUri()){
            uuid = mMovieItem.getUUID();
        } else {
            uuid = HashValue.hashKeyForDisk(string);
        }
        try {
            if (extraStorageNotReady()) {
                File path = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),uuid);
                FileOutputStream fileOutputStream = new FileOutputStream(path,true);
                fileOutputStream.write(bytes);
                fileOutputStream.close();
                Log.i(TAG,"写入储存文件成功");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            Log.e(TAG,"Got a ERROR when write File to Extral storage");
        }
    }

    public byte[] readFromFile(@NonNull String uri) {
        String uuid;
        if (uri == mMovieItem.getPhotosUri()){
            uuid = mMovieItem.getUUID();
        } else {
            uuid = HashValue.hashKeyForDisk(uri);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        File path = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),uuid);
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            int byteRead = 0;
            byte[] buffer = new byte[1024];
            while ((byteRead = fileInputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, byteRead);
            }
            Log.i(TAG,"Read File succuses");
            outputStream.close();
            fileInputStream.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
        return outputStream.toByteArray();
    }

    /**
     * delete photo file when quiet app.
     * */
    public void deletePhotoFile(){
        if (mMovieItem.getUri() != null && mMovieItem.getPhotoShortUri() != null) {
            new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), HashValue.hashKeyForDisk(mMovieItem.getUri())).delete();
            new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), HashValue.hashKeyForDisk(mMovieItem.getPhotoShortUri())).delete();
        } else if (mMovieItem.getUri() != null){
            new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), HashValue.hashKeyForDisk(mMovieItem.getUri())).delete();
        }

    }
    // 判断储存状态是否可用。
    public static boolean extraStorageNotReady() {
        String state = Environment.getExternalStorageState();
        Log.e(TAG, "储存权限 " + state.equals(Environment.MEDIA_MOUNTED));
        return state.equals(Environment.MEDIA_MOUNTED);
    }
}
