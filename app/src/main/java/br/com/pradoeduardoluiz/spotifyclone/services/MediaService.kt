package br.com.pradoeduardoluiz.spotifyclone.services

import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.text.TextUtils
import androidx.media.MediaBrowserServiceCompat

class MediaService : MediaBrowserServiceCompat() {

    private var session: MediaSessionCompat

    init {
        session = MediaSessionCompat(this, TAG)
        session.setFlags(MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS)

        session.setCallback(MediaSessionCallback())

        sessionToken = session.sessionToken
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
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

        result.sendResult(null)
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {

        if (clientPackageName == applicationContext.packageName) {
            // allowed to browser media
        }

        return BrowserRoot("empty_media", null)
    }


    inner class MediaSessionCallback : MediaSessionCompat.Callback() {

        override fun onPrepare() {
            super.onPrepare()
        }

        override fun onPlay() {
            super.onPlay()
        }

        override fun onStop() {
            super.onStop()
        }

        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {

        }

        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
        }

        override fun onSkipToNext() {
            super.onSkipToNext()
        }

        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
        }

        override fun onAddQueueItem(description: MediaDescriptionCompat?) {
            super.onAddQueueItem(description)
        }

        override fun onRemoveQueueItem(description: MediaDescriptionCompat?) {
            super.onRemoveQueueItem(description)
        }

    }

    companion object {
        private const val TAG = "MediaService"
    }
}