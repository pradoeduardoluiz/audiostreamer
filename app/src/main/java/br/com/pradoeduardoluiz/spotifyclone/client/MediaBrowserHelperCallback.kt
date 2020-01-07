package br.com.pradoeduardoluiz.spotifyclone.client

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat

interface MediaBrowserHelperCallback {
    fun onMetaDataChanged(metadata: MediaMetadataCompat)
    fun onPlaybackStateChanged(state: PlaybackStateCompat?)
}