package br.com.pradoeduardoluiz.spotifyclone.players

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class MediaPlayerAdapter(context: Context) : PlayerAdapter(context) {

    private val context: Context = context

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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPause() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun playFromMedia(metadata: MediaMetadataCompat?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val currentMedia: MediaMetadataCompat?
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override val isPlaying: Boolean
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun onStop() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun seekTo(position: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setVolume(volume: Float) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}