package wolfheros.life.home.tools.Main;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.io.ByteArrayOutputStream;

import wolfheros.life.home.MovieItem;
import wolfheros.life.home.R;

/**
 *  向外部储存中添加下载的图片资源。
 *  完成于 2018-05-27
 * */
public class ImageFileStore{
    private LruCache<String , byte[]> mLruCache;
    private static ImageFileStore mImageFileStore;
    private static final String TAG = "ImageFileStore";

    private ImageFileStore() {
        // mLruCache = new LruCache<>(4*1024*1024);

    }
    /**
     * download == 1 说明要下载的是，海报照片， download == 2 说明要下载的是截图照片。
     * */
    // 价加载的图片储存在内存缓存中或者储存在外部储存中。
    public void addImageBitmap (@NonNull MovieItem movieItem , int downloadCode, byte[] bytes , Context context) {

        if (downloadCode== 1){
           // mLruCache.put(movieItem.getPhotosUri(),bytes);
            ExternalStoreFile.get(movieItem,context).storeFile(bytes,movieItem.getPhotosUri());
        }else if (downloadCode == 2){
            //mLruCache.put(movieItem.getPhotoShortUri(),bytes);
            ExternalStoreFile.get(movieItem,context).storeFile(bytes,movieItem.getPhotoShortUri());

        }
    }
    /**
     * download == 1 说明要下载的是，海报照片， download == 2 说明要下载的是截图照片。
     * */
    // 从内存或者外部储存获取图片。
    public Bitmap getImageBitmap(@NonNull MovieItem movieItem , Context context,int downloadCode) {
            if ( downloadCode == 1) {
                String string = movieItem.getPhotosUri();
                // 防止搜索的结果没有涵盖图片 URL
                if (string != null) {
                    /*if (mLruCache.get(string) != null) {
                        Log.i(TAG, "数据来源于缓存");
                        byte[] bytes = mLruCache.get(string);
                        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    } else */if (ExternalStoreFile.get(movieItem, context).readFromFile(movieItem.getPhotosUri()) != null) {
                        Log.i(TAG, "数据来源于储存");
                        byte[] bytes = ExternalStoreFile.get(movieItem, context).readFromFile(movieItem.getPhotosUri());
                        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    }else {
                        Log.e(TAG, "没有存储数据");
                        return null;
                    }
                }
            }else if (downloadCode == 2 && ExternalStoreFile.get(movieItem, context).readFromFile(movieItem.getPhotoShortUri()) != null) {
                String string = movieItem.getPhotoShortUri();
               /* if (mLruCache.get(string) != null){
                    Log.i(TAG,"数据来源于缓存");
                    byte[] bytes = mLruCache.get(string);
                    return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                } else*/ if(ExternalStoreFile.get(movieItem, context).readFromFile(movieItem.getPhotoShortUri()) != null){
                    byte[] bytes = ExternalStoreFile.get(movieItem, context).readFromFile(movieItem.getPhotoShortUri());
                    return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                }else {
                    Log.e(TAG, "没有存储数据");
                    return null;
                }
            }
            return null;
    }
    /**
     * 未获取照片时候需要做的
     * */
    public void addErrorBitmap(Context mContext , int code , MovieItem movieItem){
        /**
         * 如果下载的照片为空，就用ErrorImage 进行替换
         * */
        Resources resources = mContext.getResources();
        /*获取资源中获取*/
        Bitmap bitmap1 = BitmapFactory.decodeResource(resources, R.drawable.error_template);
        /*压缩图片*/
        Bitmap bitmap2 = ImageZIP.getScaledBitmap(bitmapToByte(bitmap1));
        byte[] bytes2 = bitmapToByte(bitmap2);
        ImageFileStore.getImageFileStore().addImageBitmap(movieItem ,code, bytes2,mContext);
        Log.e(TAG,"Home image is empty");
    }

    /**
     * 将未下载的照片进行替换。
     * 将照片转换成Bitmap后再转换成 byte[] 数组
     * */
    private byte[] bitmapToByte(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG , 100 ,byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


    public static ImageFileStore getImageFileStore() {
        if (mImageFileStore == null){
            return mImageFileStore = new ImageFileStore();
        }else {
            return mImageFileStore;
        }
    }
}
