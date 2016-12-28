package isel.yawa.connect

import android.graphics.Bitmap
import android.util.LruCache
import com.android.volley.toolbox.ImageLoader

/*
 * Wrapper around an LruCache designed to hold bitmaps obtained from the network in volatile memory
 * thus making them available in a cheap and fast manner
 */
class LruImageCache(maxSize : Int) : ImageLoader.ImageCache{

    private var bitmapCache: LruCache<String, Bitmap>

    init {
        bitmapCache = object : LruCache<String, Bitmap>(maxSize){
            // override LruCache sizeOf method to return the number of Kib a bitmap occupies
            override fun sizeOf(key: String?, bitmap: Bitmap): Int = bitmap.byteCount / 1024;
        }
    }

    override fun getBitmap(url: String?): Bitmap? = bitmapCache.get(url)

    override fun putBitmap(url: String?, bitmap: Bitmap?) {
        bitmapCache.put(url, bitmap)
    }
}