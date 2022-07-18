package hh.app.oldmanemu

import kotlin.jvm.JvmOverloads
import android.widget.TextView
import org.sufficientlysecure.htmltextview.GlideImageGetter
import android.text.Html.ImageGetter
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import android.graphics.drawable.BitmapDrawable
import android.graphics.Bitmap
import android.graphics.Canvas
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import java.lang.ref.WeakReference

/*
 * Based on code by https://github.com/yrajabi
 */

class CustomGlideImageGetter(textView: TextView,
private val matchParentWidth: Boolean = false,
densityAware: Boolean = false,
private val imagesHandler: HtmlImagesHandler? = null
) : ImageGetter {
    private val container: WeakReference<TextView> = WeakReference(textView)
    private var density = 1.0f

    init {
        if (densityAware) {
            container.get()?.let {
                density = it.resources.displayMetrics.density
            }
        }
    }

    override fun getDrawable(source: String): Drawable {
        imagesHandler?.addImage(source)

        val drawable = BitmapDrawablePlaceholder()

        // Load Image to the Drawable
        container.get()?.apply {
            post {
                GlideApp.with(context)
                    .asBitmap()
                    .load(source)
                    .transform(GifTransformation(context,source))
                    .override(800)
                    .into(drawable)
            }
        }

        return drawable
    }

    private inner class BitmapDrawablePlaceholder : BitmapDrawable(container.get()?.resources, Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)), Target<Bitmap> {
        private var drawable: Drawable? = null
            set(value) {
                field = value
                value?.let { drawable ->
                    val drawableWidth = (drawable.intrinsicWidth * density).toInt()
                    val drawableHeight = (drawable.intrinsicHeight * density).toInt()
                    val maxWidth = container.get()!!.measuredWidth
                    if (drawableWidth > maxWidth || matchParentWidth) {
                        val calculatedHeight = maxWidth * drawableHeight / drawableWidth
                        drawable.setBounds(0, 0, maxWidth, calculatedHeight)
                        setBounds(0, 0, maxWidth, calculatedHeight)
                    } else {
                        drawable.setBounds(0, 0, drawableWidth, drawableHeight)
                        setBounds(0, 0, drawableWidth, drawableHeight)
                    }
                    container.get()?.text = container.get()?.text
                }
            }

        override fun draw(canvas: Canvas) {
            drawable?.draw(canvas)
        }

        override fun onLoadStarted(placeholderDrawable: Drawable?) {
            placeholderDrawable?.let {
                drawable = it
            }
        }

        override fun onLoadFailed(errorDrawable: Drawable?) {
            errorDrawable?.let {
                drawable = it
            }
        }

        override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
            drawable = BitmapDrawable(container.get()!!.resources, bitmap)
        }

        override fun onLoadCleared(placeholderDrawable: Drawable?) {
            placeholderDrawable?.let {
                drawable = it
            }
        }

        override fun getSize(sizeReadyCallback: SizeReadyCallback) {
            sizeReadyCallback.onSizeReady(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
        }

        override fun removeCallback(cb: SizeReadyCallback) {}
        override fun setRequest(request: Request?) {}
        override fun getRequest(): Request? {
            return null
        }

        override fun onStart() {}
        override fun onStop() {}
        override fun onDestroy() {}
    }

    interface HtmlImagesHandler {
        fun addImage(uri: String?)
    }

}