package br.com.pradoeduardoluiz.spotifyclone.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class MyPreferenceManager {

    private val preferences: SharedPreferences

    constructor(context: Context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun setPlayListId(playListId: String) {
        val editor: SharedPreferences.Editor = preferences.edit()

        editor.apply {
            putString(Constants.PLAYLIST_ID, playListId)
            apply()
        }
    }

    fun getPlayListId(): String {
        return preferences.getString(Constants.PLAYLIST_ID, "") ?: ""
    }

    companion object {
        private const val TAG = "MyPreferenceManager"
    }


}