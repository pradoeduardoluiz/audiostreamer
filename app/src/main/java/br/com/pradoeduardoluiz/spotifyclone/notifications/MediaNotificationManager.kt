package br.com.pradoeduardoluiz.spotifyclone.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.media.session.MediaButtonReceiver
import br.com.pradoeduardoluiz.spotifyclone.R
import br.com.pradoeduardoluiz.spotifyclone.services.MediaService
import br.com.pradoeduardoluiz.spotifyclone.ui.MainActivity


class MediaNotificationManager constructor(private val mediaService: MediaService) {

    private val notificationManager: NotificationManager =
        mediaService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val playAction: NotificationCompat.Action = NotificationCompat.Action(
        R.drawable.ic_play_arrow_white_24dp,
        mediaService.getString(R.string.label_play),
        MediaButtonReceiver.buildMediaButtonPendingIntent(
            mediaService, PlaybackStateCompat.ACTION_PLAY
        )
    )
    private var pauseAction: NotificationCompat.Action = NotificationCompat.Action(
        R.drawable.ic_pause_circle_outline_white_24dp,
        mediaService.getString(R.string.label_play),
        MediaButtonReceiver.buildMediaButtonPendingIntent(
            mediaService, PlaybackStateCompat.ACTION_PAUSE
        )
    )
    private var nextAction: NotificationCompat.Action = NotificationCompat.Action(
        R.drawable.ic_skip_next_white_24dp,
        mediaService.getString(R.string.label_play),
        MediaButtonReceiver.buildMediaButtonPendingIntent(
            mediaService, PlaybackStateCompat.ACTION_SKIP_TO_NEXT
        )
    )
    private var previousAction: NotificationCompat.Action = NotificationCompat.Action(
        R.drawable.ic_skip_previous_white_24dp,
        mediaService.getString(R.string.label_play),
        MediaButtonReceiver.buildMediaButtonPendingIntent(
            mediaService, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
        )
    )

    init {
        notificationManager.cancelAll()
    }

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

    fun buildNotification(
        state: PlaybackStateCompat,
        token: MediaSessionCompat.Token?,
        description: MediaDescriptionCompat?,
        bitmap: Bitmap?
    ): Notification {

        var isPlaying: Boolean = state.state == PlaybackStateCompat.STATE_PLAYING

        if (isAndroidOorHigher()) {
            createChannel()
        }

        val builder = NotificationCompat.Builder(mediaService, CHANNEL_ID)
        builder.setStyle(
            androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(token)
                .setShowActionsInCompactView(0, 1, 2)
        )
            .setColor(ContextCompat.getColor(mediaService, R.color.notification_bg))
            .setSmallIcon(R.drawable.ic_audiotrack_grey_24dp) // Pending intent that is fired when user clicks on notification.
            .setContentIntent(createContentIntent()) // Title - Usually Song name.
            .setContentTitle(description?.title) // Subtitle - Usually Artist name.
            .setContentText(description?.subtitle)
            .setLargeIcon(bitmap) // When notification is deleted (when playback is paused and notification can be
// deleted) fire MediaButtonPendingIntent with ACTION_STOP.
            .setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    mediaService, PlaybackStateCompat.ACTION_STOP
                )
            ) // Show controls on lock screen even when user hides sensitive content.
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        if ((state.actions and PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS) != 0L) {
            builder.addAction(previousAction)
        }

        builder.addAction(if (isPlaying) pauseAction else playAction)

        if ((state.actions and PlaybackStateCompat.ACTION_SKIP_TO_NEXT) != 0L) {
            builder.addAction(nextAction)
        }

        return builder.build()
    }

    private fun createContentIntent(): PendingIntent {
        val openUI = Intent(mediaService, MainActivity.javaClass)
        openUI.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        return PendingIntent.getActivity(
            mediaService,
            501,
            openUI,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
    }

    companion object {
        const val NOTIFICATION_ID = 201
        private const val TAG = "MediaNotificationManage"

        private const val CHANNEL_ID = "br.com.pradoeduardoluiz.spotifyclone.musicplayer.channel"
    }
}