package br.com.pradoeduardoluiz.spotifyclone.services

import android.R.attr.bitmap
import android.app.Notification
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.TextUtils
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.test.orchestrator.junit.BundleJUnitUtils.getDescription
import br.com.pradoeduardoluiz.spotifyclone.MyApplication
import br.com.pradoeduardoluiz.spotifyclone.R
import br.com.pradoeduardoluiz.spotifyclone.notifications.MediaNotificationManager
import br.com.pradoeduardoluiz.spotifyclone.players.MediaPlayerAdapter
import br.com.pradoeduardoluiz.spotifyclone.players.PlaybackInfoListener
import br.com.pradoeduardoluiz.spotifyclone.players.PlayerAdapter
import br.com.pradoeduardoluiz.spotifyclone.util.Constants
import br.com.pradoeduardoluiz.spotifyclone.util.MyPreferenceManager
import sun.jvm.hotspot.utilities.IntArray


class MediaService : MediaBrowserServiceCompat() {

    private lateinit var session: MediaSessionCompat
    private lateinit var playback: PlayerAdapter
    private var application: MyApplication? = null
    private lateinit var preferenceManager: MyPreferenceManager
    private lateinit var mediaNotificationManager: MediaNotificationManager
    private var isServiceRunning: Boolean = false

    override fun onCreate() {
        super.onCreate()

        //Build the MediaSession

        session = MediaSessionCompat(this, TAG)
        session.setFlags(MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS)
        session.setCallback(MediaSessionCallback())
        sessionToken = session.sessionToken

        application = MyApplication.getInstance()

        preferenceManager = MyPreferenceManager(this)

        playback = MediaPlayerAdapter(this, MediaPlayerListener())
        mediaNotificationManager = MediaNotificationManager(this)
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

        result.sendResult(application?.getMediaItems()) // return all available media
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {

        if (clientPackageName == applicationContext.packageName) {
            // allowed to browser media
            return BrowserRoot("some_real_playlist", null)
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

            mediaId?.let {
                preparedMedia = application?.getMediaItem(it)
                session.setMetadata(preparedMedia)
            }

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
            preferenceManager.saveQueuePosition(queueIndex)
            preferenceManager.saveLastPlayedMedia(preparedMedia?.description?.mediaId)
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

            var newQueuePosition: Int = -1
            var isNewPlayList: Boolean = false

            extras?.let {
                newQueuePosition = it.getInt(Constants.MEDIA_QUEUE_POSITION)
                isNewPlayList = it.getBoolean(Constants.QUEUE_NEW_PLAYLIST)
            }

            mediaId?.let {
                preparedMedia = application?.getMediaItem(it)
                session.setMetadata(preparedMedia)
                if (!session.isActive) {
                    session.isActive = true
                }
                playback.playFromMedia(preparedMedia)
            }

            if (isNewPlayList) {
                resetPlaylist()
            }

            if (newQueuePosition == 1) {
                queueIndex++
            } else {
                queueIndex = newQueuePosition
            }

            preferenceManager.saveQueuePosition(queueIndex)
            preferenceManager.saveLastPlayedMedia(preparedMedia?.description?.mediaId)
        }

        override fun onSeekTo(pos: Long) {
            playback.seekTo(pos)
        }

        override fun onSkipToNext() {
            Log.d(TAG, "[onSkipToNext]: SKIP TO NEXT")
            queueIndex = (++queueIndex % playList.size)
            Log.d(TAG, "[onSkipToNext]: queue Index $queueIndex")
            preparedMedia = null
            onPlay()
        }

        override fun onSkipToPrevious() {
            Log.d(TAG, "[onSkipToPrevious]: SKIP TO PREVIOUS")
            queueIndex = if (queueIndex < 0) queueIndex - 1 else playList.size - 1
            preparedMedia = null
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

        private fun resetPlaylist() {
            playList.clear()
            queueIndex = -1
        }
    }

    companion object {
        private const val TAG = "MediaService"
    }

    private inner class MediaPlayerListener : PlaybackInfoListener {

        override fun onPlaybackStateChange(state: PlaybackStateCompat) {
            // Report the state to the MediaSession
            session.setPlaybackState(state)
        }

        override fun seekTo(progress: Long, max: Long) {
            val intent = Intent().apply {
                action = getString(R.string.broadcast_seekbar_update)
                putExtra(Constants.SEEK_BAR_PROGRESS, progress)
                putExtra(Constants.SEEK_BAR_MAX, max)
            }
            sendBroadcast(intent)
        }

        override fun onPlaybackComplete() {
            Log.d(TAG, "[onPlaybackComplete]: SKIPPING TO NEXT")
            session.controller.transportControls.skipToNext()

        }

        override fun updateUI(mediaId: String?) {
            val intent = Intent().apply {
                action = getString(R.string.broadcast_update_ui)
                putExtra(getString(R.string.broadcast_new_media_id), mediaId)
            }
            sendBroadcast(intent)
        }

        inner class ServiceMananger {

            fun displayNotification(state: PlaybackStateCompat) {
                var notification: Notification? = null

                when (state.state) {
                    PlaybackStateCompat.STATE_PLAYING -> {
                        notification = mediaNotificationManager.buildNotification(
                            state,
                            sessionToken,
                            playback.getCurrentMedia()?.description,
                            null
                        )
                        if (!isServiceRunning) {
                            ContextCompat.startForegroundService(
                                this@MediaService,
                                Intent(this@MediaService, MediaService::class.java)
                            )
                            isServiceRunning = true
                        }
                        startForeground(MediaNotificationManager.NOTIFICATION_ID, notification)
                    }
                    PlaybackStateCompat.STATE_PAUSED -> {
                        stopForeground(false)
                        notification = mediaNotificationManager.buildNotification(
                            state,
                            sessionToken,
                            playback.getCurrentMedia().getDescription(),
                            null
                        )
                        mediaNotificationManager.getNotificationManager()
                            .notify(MediaNotificationManager.NOTIFICATION_ID, notification)
                    }
                }

            }

            private fun moveServiceOutOfStartedState() {
                stopForeground(true)
                stopSelf()
            }
        }
    }
}