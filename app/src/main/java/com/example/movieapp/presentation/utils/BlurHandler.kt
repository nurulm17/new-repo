package com.example.movieapp.presentation.utils



import android.graphics.Bitmap
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import jp.wasabeef.glide.transformations.internal.FastBlur
import java.security.MessageDigest

class BlurHandler(private val radius: Int) : BitmapTransformation() {

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update("blur transformation".toByteArray(Charsets.UTF_8))
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        return if (radius == 0) {
            toTransform
        } else {
            FastBlur.blur(toTransform, radius, true)
        }
    }
}
