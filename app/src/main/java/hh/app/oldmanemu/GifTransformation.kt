package hh.app.oldmanemu

import android.content.Context
import android.graphics.Bitmap
import com.blankj.utilcode.util.ImageUtils
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

class GifTransformation(var context: Context, var url: String) : BitmapTransformation() {
    override fun updateDiskCacheKey(messageDigest: MessageDigest) {

    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        if (url.lowercase().endsWith(".gif"))
            return ImageUtils.addImageWatermark(
                toTransform,
                ImageUtils.drawable2Bitmap(context.getDrawable(R.drawable.ic_baseline_gif_box_24)),
                0,
                0,
                0xff
            )
        else return toTransform
    }
}