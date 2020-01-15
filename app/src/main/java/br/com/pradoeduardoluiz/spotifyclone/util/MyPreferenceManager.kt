package br.com.pradoeduardoluiz.spotifyclone.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager


class MyPreferenceManager {

    private val preferences: SharedPreferences

    constructor(context: Context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun getPlayListId(): String {
        return preferences.getString(Constants.PLAYLIST_ID, "") ?: ""
    }

    fun savePlaylistId(playlistId: String?) {
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString(
            Constants.PLAYLIST_ID,
            playlistId
        )
        editor.apply()
    }

    fun saveQueuePosition(position: Int) {
        Log.d(TAG, "saveQueuePosition: SAVING QUEUE INDEX: $position")
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putInt(Constants.MEDIA_QUEUE_POSITION, position)
        editor.apply()
    }

    fun getQueuePosition(): Int {
        return preferences.getInt(Constants.MEDIA_QUEUE_POSITION, -1)
    }

    fun saveLastPlayedArtistImage(url: String?) {
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString(Constants.LAST_ARTIST_IMAGE, url)
        editor.apply()
    }

    fun getLastPlayedArtistImage(): String? {
        return preferences.getString(Constants.LAST_ARTIST_IMAGE, "")
    }

    fun getLastPlayedArtist(): String? {
        return preferences.getString(Constants.LAST_ARTIST, "")
    }

    fun getLastCategory(): String? {
        return preferences.getString(Constants.LAST_CATEGORY, "")
    }

    fun saveLastPlayedMedia(mediaId: String?) {
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString(Constants.NOW_PLAYING, mediaId)
        editor.apply()
    }

    fun getLastPlayedMedia(): String? {
        return preferences.getString(Constants.NOW_PLAYING, "")
    }

    fun saveLastPlayedCategory(category: String?) {
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString(Constants.LAST_CATEGORY, category)
        editor.apply()
    }

    fun saveLastPlayedArtist(artist: String?) {
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString(Constants.LAST_ARTIST, artist)
        editor.apply()
    }

    companion object {
        private const val TAG = "MyPreferenceManager"
    }


}