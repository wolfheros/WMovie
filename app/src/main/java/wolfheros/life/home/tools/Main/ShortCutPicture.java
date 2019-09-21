package wolfheros.life.home.tools.Main;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

import wolfheros.life.home.MovieItem;

/**
 *  这个类是为了裁剪图片,作为保存类。
 * */

public class ShortCutPicture {

    private ShortCutPicture(){

    }

    public static byte[] getCutDrawable(Bitmap bitmap){

        int width = (bitmap.getWidth());
        int heigh = (bitmap.getHeight())/2;

        // 将图片数据写成byte[] 数组输出
        Bitmap newBitmap = Bitmap.createBitmap(bitmap,0,0 ,width,heigh);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        newBitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byteArrayOutputStream.toByteArray();
        return byteArrayOutputStream.toByteArray();
    }
}
