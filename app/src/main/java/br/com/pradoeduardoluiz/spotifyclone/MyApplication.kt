package br.com.pradoeduardoluiz.spotifyclone

import android.app.Application
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import java.util.*
import kotlin.collections.ArrayList


class MyApplication : Application() {

    private val mediaItems: MutableList<MediaBrowserCompat.MediaItem> = ArrayList()
    private val treeMap: TreeMap<String, MediaMetadataCompat> = TreeMap()

    companion object {
        private const val TAG = "MyApplication"
        private var instance: MyApplication? = null

        fun getInstance(): MyApplication? {
            if (instance == null) {
                instance = MyApplication()
            }

            return instance
        }
    }

    fun setMediaItems(items: MutableList<MediaMetadataCompat>) {
        mediaItems.clear()

        items.forEach {
            mediaItems.add(
                MediaBrowserCompat.MediaItem(
                    it.description,
                    MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
                )
            )

            it.description.mediaId?.let { item ->
                treeMap.put(item, it)
            }
        }
    }

    fun getMediaItems(): MutableList<MediaBrowserCompat.MediaItem> {
        return mediaItems
    }

    fun getTreeMap(): TreeMap<String, MediaMetadataCompat> {
        return treeMap
    }

    fun getMediaItem(mediaId: String): MediaMetadataCompat? {
        return treeMap[mediaId]
    }

}