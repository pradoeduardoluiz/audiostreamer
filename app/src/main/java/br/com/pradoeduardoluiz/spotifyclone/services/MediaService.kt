package br.com.pradoeduardoluiz.spotifyclone.services

import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.text.TextUtils
import android.util.Log
import androidx.media.MediaBrowserServiceCompat
import br.com.pradoeduardoluiz.spotifyclone.players.MediaPlayerAdapter
import br.com.pradoeduardoluiz.spotifyclone.players.PlayerAdapter
import br.com.pradoeduardoluiz.spotifyclone.util.MediaLibrary

class MediaService : MediaBrowserServiceCompat() {

    private lateinit var session: MediaSessionCompat
    private lateinit var playback: PlayerAdapter
    private lateinit var mediaLibrary: MediaLibrary

    override fun onCreate() {
        super.onCreate()

        mediaLibrary = MediaLibrary()

        //Build the MediaSession

        session = MediaSessionCompat(this, TAG)
        session.setFlags(MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS)
        session.setCallback(MediaSessionCallback())
        sessionToken = session.sessionToken

        playback = MediaPlayerAdapter(this)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.d(TAG, "[onTaskRemoved]: ")
        super.onTaskRemoved(rootIntent)
        playback.stop()
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        session.release()
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        if (TextUtils.equals("empty_media", parentId)) {
            result.sendResult(null)
            return
        }

        result.sendResult(MediaLibrary.getMediaItems()?.toMutableList())
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {

        if (clientPackageName == applicationContext.packageName) {
            // allowed to browser media
            return BrowserRoot("some_fake_playlist", null)
        }

        return BrowserRoot("empty_media", null)
    }


    // Send Message from MediaPlayerAdapter to MediaService
    inner class MediaSessionCallback : MediaSessionCompat.Callback() {

        private var playList: MutableList<MediaSessionCompat.QueueItem> = arrayListOf()
        private var queueIndex: Int = -1
        private var preparedMedia: MediaMetadataCompat? = null

        override fun onPrepare() {
            if (queueIndex < 0 && playList.isEmpty()) {
                return
            }

            val mediaId = playList[queueIndex].description.mediaId
            preparedMedia = mediaLibrary.getTreeMap()?.get(mediaId)

            if (!session.isActive) {
                session.isActive = true
            }
        }

        override fun onPlay() {
            if (!isReadyToPlay()) {
                // Nothing to play
                return
            }

            if (preparedMedia == null) {
                onPrepare()
            }

            playback.playFromMedia(preparedMedia)
        }

        override fun onStop() {
            playback.stop()
            session.isActive = false
        }

        override fun onPause() {
            playback.pause()
        }

        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            Log.d(TAG, "[onPlayFromMediaId]: Called")
        }

        override fun onSeekTo(pos: Long) {
            playback.seekTo(pos)
        }

        override fun onSkipToNext() {
            Log.d(TAG, "[onSkipToNext]: SKIP TO NEXT")
            queueIndex = (++queueIndex % playList.size)
            Log.d(TAG, "[onSkipToNext]: queue Index $queueIndex")
            onPlay()
        }

        override fun onSkipToPrevious() {
            Log.d(TAG, "[onSkipToPrevious]: SKIP TO PREVIOUS")
            queueIndex = if (queueIndex < 0) queueIndex - 1 else playList.size - 1
            onPlay()
        }

        override fun onAddQueueItem(description: MediaDescriptionCompat?) {
            Log.d(TAG, "[onAddQueueItem]: called: position in list ${playList.size}")
            playList.add(MediaSessionCompat.QueueItem(description, description.hashCode().toLong()))
            queueIndex = if (queueIndex == -1) 0 else queueIndex
            session.setQueue(playList)
        }

        override fun onRemoveQueueItem(description: MediaDescriptionCompat?) {
            Log.d(TAG, "[onRemoveQueueItem]: ")
            playList.remove(
                MediaSessionCompat.QueueItem(
                    description,
                    description.hashCode().toLong()
                )
            )
            queueIndex = if (playList.isEmpty()) -1 else queueIndex
            session.setQueue(playList)
        }

        private fun isReadyToPlay() = (playList.isNotEmpty())
    }

    companion object {
        private const val TAG = "MediaService"
    }
}