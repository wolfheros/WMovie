package wolfheros.life.home.tools.Main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 *  压缩和缩放，下载的图片。
 *  完成于 2018-5-27
 * */
public class ImageZIP {
    private static final int destWidth = 100;
    private static final int destHeight = 141;

    // 压缩算法
    public static Bitmap getScaledBitmap(byte[] bytes){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes,0,bytes.length,options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        // Figure out how much to scale down by
        int inSampleSize = 1;
        if (srcHeight>destHeight || srcWidth>destWidth){
            // 找最大边。用最小边的比
            if (srcWidth>srcHeight){
                inSampleSize = Math.round(srcHeight/destHeight);
            }else {
                inSampleSize = Math.round(srcWidth/destWidth);
            }
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        return BitmapFactory.decodeByteArray(bytes,0,bytes.length,options);
    }

}
