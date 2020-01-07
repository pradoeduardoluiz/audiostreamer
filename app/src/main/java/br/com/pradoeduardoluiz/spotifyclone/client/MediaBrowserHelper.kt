package br.com.pradoeduardoluiz.spotifyclone.client

import android.content.ComponentName
import android.content.Context
import android.os.RemoteException
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.media.MediaBrowserServiceCompat
import java.lang.IllegalStateException


class MediaBrowserHelper(
    private var context: Context,
    private var mediaBrowserServiceClass: Class<out MediaBrowserServiceCompat?>
) {

    private var mediaBrowser: MediaBrowserCompat? = null
    private var mediaController: MediaControllerCompat? = null
    private var mediaBrowserConnectionCallback: MediaBrowserConnectionCallback
    private var mediaBrowserSubscriptionCallback: MediaBrowserSubscriptionCallback
    private var mediaControllerCallback: MediaControllerCallback
    private var mediaHelperControllerCallback: MediaBrowserHelperCallback? = null

    init {
        mediaBrowserConnectionCallback = MediaBrowserConnectionCallback()
        mediaBrowserSubscriptionCallback = MediaBrowserSubscriptionCallback()
        mediaControllerCallback = MediaControllerCallback()
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
            mediaController?.unregisterCallback(mediaControllerCallback)
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

                    mediaController?.registerCallback(mediaControllerCallback)

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
                Log.d(TAG, "onChildrenLoaded: CALLED: queue item: " + mediaItem.mediaId)
                mediaController?.addQueueItem(mediaItem.description)
            }
        }
    }

    fun getTransportControls(): MediaControllerCompat.TransportControls? {
        if (mediaController == null) {
            throw IllegalStateException("Media controller  is null")
        }

        return mediaController?.transportControls
    }

    fun subscribeToNewPlayList(playlistId: String) {
        mediaBrowser?.subscribe(playlistId, mediaBrowserSubscriptionCallback)
    }

    fun setMediaBrowserHelperCallback(browserHelperCallback: MediaBrowserHelperCallback) {
        mediaHelperControllerCallback = browserHelperCallback
    }


    companion object {
        private const val TAG = "MediaBrowserHelper"
    }

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            Log.d(TAG, "[onPlaybackStateChanged]: called")
            mediaHelperControllerCallback?.let { mediaHelperControllerCallback ->
                state?.let {
                    mediaHelperControllerCallback.onPlaybackStateChanged(it)
                }
            }
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {

        }
    }

}