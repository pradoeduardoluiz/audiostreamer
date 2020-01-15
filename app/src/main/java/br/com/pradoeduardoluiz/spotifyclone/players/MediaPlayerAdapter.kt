package br.com.pradoeduardoluiz.spotifyclone.players

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.SystemClock
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class MediaPlayerAdapter(context: Context, playInfoListener: PlaybackInfoListener) :
    PlayerAdapter(context) {

    private val context: Context = context.applicationContext
    private var currentMedia: MediaMetadataCompat? = null
    private var currentMediaPlayerCompletion: Boolean = false
    @PlaybackStateCompat.State
    private var state: Int = -1
    private var startTime: Long = -1
    private var playbackInfoListener: PlaybackInfoListener = playInfoListener

    //ExoPlayer objects
    private var exoPlayer: SimpleExoPlayer? = null
    private lateinit var trackSelector: TrackSelector
    private lateinit var renderer: DefaultRenderersFactory
    private lateinit var dataSource: DataSource.Factory


    private fun initializeExoPlayer() {
        if (exoPlayer == null) {
            trackSelector = DefaultTrackSelector()
            renderer = DefaultRenderersFactory(context)
            dataSource = DefaultDataSourceFactory(
                context,
                Util.getUserAgent(context, "AudioStreamer")
            )
            exoPlayer =
                ExoPlayerFactory.newSimpleInstance(renderer, trackSelector, DefaultLoadControl())

        }
    }

    private fun release() {
        exoPlayer?.let {
            it.release()
            exoPlayer = null
        }
    }


    override fun onPlay() {
        exoPlayer?.let {
            if (!it.playWhenReady) {
                it.playWhenReady = true
                setNewState(PlaybackStateCompat.STATE_PLAYING)
            }
        }
    }

    override fun onPause() {
        exoPlayer?.let {
            if (it.playWhenReady) {
                it.playWhenReady = false
                setNewState(PlaybackStateCompat.STATE_PAUSED)
            }
        }
    }

    override fun playFromMedia(metadata: MediaMetadataCompat?) {
        startTrackingPlayback()
        playFile(metadata)
    }

    override fun getCurrentMedia(): MediaMetadataCompat? = currentMedia

    override val isPlaying: Boolean
        get() = exoPlayer?.playWhenReady ?: false

    override fun onStop() {
        setNewState(PlaybackStateCompat.STATE_STOPPED)
        release()
    }

    override fun seekTo(position: Long) {
        exoPlayer?.let {
            it.seekTo(position)
            setNewState(state)
        }
    }

    override fun setVolume(volume: Float) {
        exoPlayer?.volume = volume
    }

    private fun playFile(metadata: MediaMetadataCompat?) {

        val mediaId: String? = metadata?.description?.mediaId
        var mediaChanged =
            (currentMedia == null) || !mediaId.equals(currentMedia?.description?.mediaId)

        if (currentMediaPlayerCompletion) {
            mediaChanged = true
            currentMediaPlayerCompletion = false
        }

        if (!mediaChanged) {
            if (!isPlaying) {
                play()
            }
            return
        } else {
            release()
        }

        currentMedia = metadata
        initializeExoPlayer()

        try {
            val audioSource = ExtractorMediaSource.Factory(dataSource)
                .createMediaSource(Uri.parse(currentMedia?.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI)))
            exoPlayer?.prepare(audioSource)
        } catch (e: Exception) {
            throw RuntimeException(
                "Failed to play media url: ${currentMedia?.getString(
                    MediaMetadataCompat.METADATA_KEY_MEDIA_URI
                )}", e
            )
        }

        play()
    }

    private fun startTrackingPlayback() {
        val handle = Handler()

        val runnable = object : Runnable {
            override fun run() {
                if (isPlaying) {
                    // send updates

                    exoPlayer?.let {
                        playbackInfoListener.seekTo(it.contentPosition, it.duration)
                    }

                    handle.postDelayed(this, 100)
                }

                exoPlayer?.let { exoPlayer ->

                    if (exoPlayer.contentPosition >= exoPlayer.duration
                        && exoPlayer.duration > 0
                    ) {
                        playbackInfoListener.onPlaybackComplete()
                    }
                }
            }
        }
        handle.postDelayed(runnable, 100)

    }

    private fun setNewState(newPlayerState: Int) {
        state = newPlayerState

        if (state == PlaybackStateCompat.STATE_STOPPED) {
            currentMediaPlayerCompletion = true
        }

        val reportPosition: Long = if (exoPlayer == null) 0 else exoPlayer?.currentPosition ?: 0
        publishStateBuilder(reportPosition)
    }


    @SuppressLint("WrongConstant")
    private fun publishStateBuilder(reportPosition: Long) {
        val stateBuilder = PlaybackStateCompat.Builder()
        stateBuilder.setActions(getAvailableActions())
        stateBuilder.setState(state, reportPosition, 1.0F, SystemClock.elapsedRealtime())
        playbackInfoListener.onPlaybackStateChange(stateBuilder.build())
        playbackInfoListener.updateUI(currentMedia?.description?.mediaId)
    }


    /**
     * Set the current capabilities available on this session. Note: If a capability is not
     * listed in the bitmask of capabilities then the MediaSession will not handle it. For
     * example, if you don't want ACTION_STOP to be handled by the MediaSession, then don't
     * included it in the bitmask that's returned.
     */
    @PlaybackStateCompat.Actions
    private fun getAvailableActions(): Long {
        var actions = (PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
                or PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH
                or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
        when (state) {
            PlaybackStateCompat.STATE_STOPPED -> {
                actions =
                    actions or PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PAUSE
            }
            PlaybackStateCompat.STATE_PLAYING -> {
                actions =
                    actions or (PlaybackStateCompat.ACTION_STOP
                            or PlaybackStateCompat.ACTION_PAUSE
                            or PlaybackStateCompat.ACTION_SEEK_TO)
            }
            PlaybackStateCompat.STATE_PAUSED -> {
                actions =
                    actions or PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_STOP
            }
            else -> actions = actions or (PlaybackStateCompat.ACTION_PLAY
                    or PlaybackStateCompat.ACTION_PLAY_PAUSE
                    or PlaybackStateCompat.ACTION_STOP
                    or PlaybackStateCompat.ACTION_PAUSE)
        }
        return actions
    }

    private inner class ExoPlayerEventListener : Player.EventListener {
        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onSeekProcessed() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onTracksChanged(
            trackGroups: TrackGroupArray?,
            trackSelections: TrackSelectionArray?
        ) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onPlayerError(error: ExoPlaybackException?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onLoadingChanged(isLoading: Boolean) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onPositionDiscontinuity(reason: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onShuffleModeEnabledChanged(shuxzffleModeEnabled: Boolean) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_ENDED -> setNewState(PlaybackStateCompat.STATE_PAUSED)
                Player.STATE_BUFFERING -> startTime = System.currentTimeMillis()
                Player.STATE_READY -> {
                    Log.d(
                        TAG,
                        "[onPlayerStateChanged]: TIME ELAPSED: " + (System.currentTimeMillis() - startTime)
                    )
                }
            }
        }

    }
}