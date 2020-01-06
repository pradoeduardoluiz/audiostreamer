package br.com.pradoeduardoluiz.spotifyclone.client

import android.content.ComponentName
import android.content.Context
import android.os.RemoteException
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.util.Log
import androidx.media.MediaBrowserServiceCompat


class MediaBrowserHelper {

    private var context: Context
    private var mediaBrowserServiceClass: Class<out MediaBrowserServiceCompat?>

    private var mediaBrowser: MediaBrowserCompat? = null
    private var mediaController: MediaControllerCompat? = null
    private var mediaBrowserConnectionCallback: MediaBrowserConnectionCallback
    private var mediaBrowserSubscriptionCallback: MediaBrowserSubscriptionCallback

    constructor(context: Context, mediaBrowserServiceClass: Class<out MediaBrowserServiceCompat?>) {
        this.context = context
        this.mediaBrowserServiceClass = mediaBrowserServiceClass

        mediaBrowserConnectionCallback = MediaBrowserConnectionCallback()
        mediaBrowserSubscriptionCallback = MediaBrowserSubscriptionCallback()
    }

    fun onStart() {
        if (mediaBrowser == null) {
            mediaBrowser = MediaBrowserCompat(
                context,
                ComponentName(context, mediaBrowserServiceClass),
                mediaBrowserConnectionCallback,
                null
            )

            mediaBrowser?.connect()
        }

        Log.d(TAG, "[onStart]: connecting the service")
    }

    fun onStop() {
        mediaController?.let {
            mediaController = null
        }

        mediaBrowser?.let {
            if (it.isConnected) {
                it.disconnect()
                mediaBrowser = null
            }
        }

        Log.d(TAG, "[onStop]: disconnecting from the service")
    }

    private inner class MediaBrowserConnectionCallback : MediaBrowserCompat.ConnectionCallback() {

        override fun onConnected() {
            super.onConnected()

            mediaBrowser?.let {
                Log.d(TAG, "[onConnected]: ")
                try {
                    mediaController = MediaControllerCompat(context, it.sessionToken)
                } catch (e: RemoteException) {
                    Log.d(TAG, "[onConnected]: connection problem: $e")
                }

                it.subscribe(it.root, mediaBrowserSubscriptionCallback)
            }
        }
    }

    private inner class MediaBrowserSubscriptionCallback :
        MediaBrowserCompat.SubscriptionCallback() {

        override fun onChildrenLoaded(
            parentId: String,
            children: MutableList<MediaBrowserCompat.MediaItem>
        ) {
            Log.d(TAG, "[onChildrenLoaded]: called $parentId , $children")

            children.forEach { mediaItem ->
                mediaController?.addQueueItem(mediaItem.description)
            }
        }
    }

    companion object {
        private const val TAG = "MediaBrowserHelper"
    }

}