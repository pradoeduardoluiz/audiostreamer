package br.com.pradoeduardoluiz.spotifyclone.players

import android.support.v4.media.session.PlaybackStateCompat

interface PlaybackInfoListener {

    fun onPlaybackStateChange(state: PlaybackStateCompat)

}