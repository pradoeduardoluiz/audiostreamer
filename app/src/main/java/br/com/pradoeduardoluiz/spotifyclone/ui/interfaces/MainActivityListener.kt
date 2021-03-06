package br.com.pradoeduardoluiz.spotifyclone.ui.interfaces

import android.support.v4.media.MediaMetadataCompat
import br.com.pradoeduardoluiz.spotifyclone.MyApplication
import br.com.pradoeduardoluiz.spotifyclone.model.Artist
import br.com.pradoeduardoluiz.spotifyclone.util.MyPreferenceManager

interface MainActivityListener {
    fun showProgressBar()
    fun hideProgressBar()
    fun onCategorySelected(category: String)
    fun onArtistSelected(category: String, artist: Artist)
    fun setActionBarTitle(title: String)
    fun playPause()
    fun getMyApplication(): MyApplication?
    fun onMediaSelected(playlistId: String, mediaItem: MediaMetadataCompat?, queuePosition: Int)
    fun getMyPreferenceManager(): MyPreferenceManager
}