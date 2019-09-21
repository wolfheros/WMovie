package wolfheros.life.home;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import wolfheros.life.home.tools.Main.DownloadFiles;
import wolfheros.life.home.tools.Main.ImageFileStore;
import wolfheros.life.home.tools.Main.ImageZIP;

/**
 *  创建一个可以和主进程Handler沟通的类，用于图片资源下载和返回数据
 *  完成于 2018-5-28 23:33:44
 * */
public class HandlerRequest<T> extends HandlerThread {

    private static final String TAG = "HandlerRequest";
    private static final int MESSAGE_DOWNLOAD = 0;
    public static final int HOME_PICTURE_DOWNLOAD = 1;
    public static final int SHORT_CUT_PICTURE = 2;
    private Handler mHandler;
    // 为了储存，所以修改了这里的类型由 String -> MovieItem
    private ConcurrentHashMap<T , MovieItem> mConcurrentHashMap = new ConcurrentHashMap<>();
    private Handler mResponseHandler;
    private downLoaderListener<T> mDownLoaderListener;
    private Boolean mHasQuit = false;
    private Context mContext;
    /**
     *  写这个接口的目的是为了，传送数据变量，另外将此进程的handler和主进程的handler相联系。
     * */
    interface downLoaderListener<T> {
        void onDownloaderListener(T target , Bitmap bitmap );
    }
    // 将MovieFragment 中实现的downLoaderListener 实例传递过来给handleRequest 方法调用
    public void setDownLoaderListener(downLoaderListener<T> tdownLoaderListener) {
        mDownLoaderListener = tdownLoaderListener;
    }

    // 构造函数，现在的做法是为了关联主线程的 Handler 和 Looper。
    HandlerRequest(Handler mHandler , Context context) {
        super(TAG);
        mResponseHandler = mHandler;
        mContext = context;
    }

    // 图片下载函数。
    public void imageDownloader(@NonNull T target , @NonNull MovieItem movieItem) {
        // 防止movieItem 中没有PhotoUrl 值
        if ( movieItem.getPhotosUri() != null){
            mConcurrentHashMap.put(target,movieItem);
            mHandler.obtainMessage(MESSAGE_DOWNLOAD,target).sendToTarget();
        }else {
            mConcurrentHashMap.remove(target);
            Log.i(TAG , " URL is Null in imageDownloader()");
        }
    }

    /* 准备阶段 */
    @Override
    protected void onLooperPrepared() {
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message message) {
                if (message.what == MESSAGE_DOWNLOAD){
                    T target = (T) message.obj;
                    Log.i(TAG,"Got a request for URL" + mConcurrentHashMap.get(target));
                    if (mConcurrentHashMap.get(target) == null ){
                        mConcurrentHashMap.remove(target);
                        return;
                    }
                    handleRequest(target);
                }
            }
        };
    }

    // 这个方法才是要处理图片转换的地方。
    private void handleRequest(final T target) {
        try{
            final MovieItem movieItem = mConcurrentHashMap.get(target);

            byte[] bytes = getBytes(movieItem,HOME_PICTURE_DOWNLOAD);
            Bitmap mv;
            if (bytes != null && bytes.length!= 0){
                ImageFileStore.getImageFileStore()
                        .addImageBitmap(movieItem ,HOME_PICTURE_DOWNLOAD, bytes , mContext);
                mv = ImageZIP.getScaledBitmap(bytes);
            }else {
                ImageFileStore.getImageFileStore()
                        .addErrorBitmap(mContext,HOME_PICTURE_DOWNLOAD,movieItem);
                mv = ImageFileStore.getImageFileStore()
                        .getImageBitmap(movieItem ,mContext,1);
                Log.i(TAG, "封面截图已经被替换");
            }

            // 添加进LruCache 类中,并且储存到外部储存中。
            byte[] bytes1 = getBytes(movieItem,SHORT_CUT_PICTURE);

            if (bytes1 != null && bytes1.length != 0){
                ImageFileStore.getImageFileStore()
                        .addImageBitmap(movieItem ,SHORT_CUT_PICTURE, bytes1 , mContext);
            }else {
                ImageFileStore.getImageFileStore()
                        .addErrorBitmap(mContext,SHORT_CUT_PICTURE,movieItem);
                Log.i(TAG, "电影截图已经被替换");
            }

            final Bitmap bitmap = mv;
            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mConcurrentHashMap.get(target) != movieItem || mHasQuit) {
                        Log.e(TAG , "handleRequest is already quit");
                        return;
                    }
                    // 从集合中移除相应的元素
                    mConcurrentHashMap.remove(target);
                    mDownLoaderListener.onDownloaderListener( target , bitmap );
                }
            });
            Log.i(TAG,"Bitmap is Created");
        }catch (IOException ioe) {
            ioe.printStackTrace();
        }


    }

   /* *//**
     * 将未下载的照片进行替换。
     * 将照片转换成Bitmap后再转换成 byte[] 数组
     * *//*
    private byte[] bitmapToByte(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG , 100 ,byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    *//**
     * 未获取照片时候需要做的
     * *//*
    private void addErrorBitmap(int code , MovieItem movieItem){
        *//**
         * 如果下载的照片为空，就用ErrorImage 进行替换
         * *//*
        Resources resources = mContext.getResources();
        *//*获取资源中获取*//*
        Bitmap bitmap1 = BitmapFactory.decodeResource(resources, R.drawable.error_template);
        *//*压缩图片*//*
        Bitmap bitmap2 = ImageZIP.getScaledBitmap(bitmapToByte(bitmap1));
        byte[] bytes2 = bitmapToByte(bitmap2);
        ImageFileStore.getImageFileStore().addImageBitmap(movieItem ,code, bytes2,mContext);
        Log.e(TAG,"Home image is empty");
    }*/

    public void clearQuene(){
        mHandler.removeMessages(MESSAGE_DOWNLOAD);
    }
    @Override
    public boolean quit() {
        mHasQuit = true;
        return super.quit();
    }

    private byte[] getBytes(MovieItem movieItem,int downloadCode) throws IOException {

        if (downloadCode == HOME_PICTURE_DOWNLOAD) {
            return new DownloadFiles().downloadPhotoData(movieItem.getPhotosUri());
        } else if (downloadCode == SHORT_CUT_PICTURE) {
            return new DownloadFiles().downloadPhotoData(movieItem.getPhotoShortUri());
        }
        return null;
    }

}
