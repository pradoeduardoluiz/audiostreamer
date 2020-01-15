package br.com.pradoeduardoluiz.spotifyclone.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import br.com.pradoeduardoluiz.spotifyclone.services.MediaService

class MediaNotificationManager constructor(mediaService: MediaService) {

    private val notificationManager: NotificationManager =
        mediaService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


    public fun getNotificationManager() = notificationManager

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            val name: CharSequence = "MediaSession"
            val description = "MediaSession for the MediaPlayer"
            val important = NotificationManager.IMPORTANCE_LOW

            val channel = NotificationChannel(CHANNEL_ID, name, important)
            channel.apply {
                this.description = description
                enableLights(true)
                enableVibration(true)
                vibrationPattern = (
                        longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                        )
            }
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "[createChannel]: new notification created")
        }
        Log.d(TAG, "[createChannel]: notification channell already exists!")
    }

    private fun isAndroidOorHigher() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    companion object {
        private const val TAG = "MediaNotificationManage"

        private const val CHANNEL_ID = "br.com.pradoeduardoluiz.spotifyclone.musicplayer.channel"
    }
}