package br.com.pradoeduardoluiz.spotifyclone.util

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import java.util.*
import kotlin.collections.ArrayList

class MediaLibrary {

    var mediaMap: TreeMap<String?, MediaMetadataCompat?> = TreeMap()
    var mediaList: MutableList<MediaMetadataCompat> = ArrayList()

    init {
        initMap()
    }

    private fun initMap() {
        for (media in mediaLibrary) {
            val mediaId = media.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
            mediaMap.put(mediaId, media)
            mediaList.add(media)
        }
    }

    fun getTreeMap(): TreeMap<String?, MediaMetadataCompat?>? {
        return mediaMap
    }

    fun getMediaLibrary(): Array<MediaMetadataCompat>? {
        return mediaLibrary
    }

    companion object {

        private const val TAG = "MediaLibrary"

        fun getMediaItems(): List<MediaBrowserCompat.MediaItem>? {
            val result: MutableList<MediaBrowserCompat.MediaItem> = ArrayList()
            for (metadata in mediaLibrary) {
                result.add(
                    MediaBrowserCompat.MediaItem(
                        metadata.description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
                    )
                )
            }
            return result
        }

        fun getPlaylistMedia(mediaIds: Set<String>): List<MediaBrowserCompat.MediaItem>? {
            val result: MutableList<MediaBrowserCompat.MediaItem> = ArrayList()
            // VERY INEFFICIENT WAY TO DO THIS (BUT I NEED TO BECAUSE THE DATA STRUCTURE ARE NOT IDEAL)
            // RETRIEVING DATA FROM A SERVER WOULD NOT POSE THIS ISSUE
            for (id in mediaIds) {
                for (metadata in mediaLibrary) {
                    if (id == metadata.description.mediaId) {
                        result.add(
                            MediaBrowserCompat.MediaItem(
                                metadata.description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
                            )
                        )
                    }
                }
            }
            return result
        }

        private val mediaLibrary = arrayOf(
            MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "11111")
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "Mitch Tabian & Jim Wilson")
                .putString(
                    MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI,
                    "https://codingwithmitch.s3.amazonaws.com/static/profile_images/default_avatar.jpg"
                )
                .putString(
                    MediaMetadataCompat.METADATA_KEY_TITLE,
                    "CodingWithMitch Podcast #1 - Jim Wilson"
                )
                .putString(
                    MediaMetadataCompat.METADATA_KEY_MEDIA_URI,
                    "http://content.blubrry.com/codingwithmitch/Interview_audio_online-audio-converter.com_.mp3"
                )
                .build(),

            MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "11112")
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "Mitch Tabian & Justin Mitchel")
                .putString(
                    MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI,
                    "https://codingwithmitch.s3.amazonaws.com/static/profile_images/default_avatar.jpg"
                )
                .putString(
                    MediaMetadataCompat.METADATA_KEY_TITLE,
                    "CodingWithMitch Podcast #2 - Justin Mitchel"
                )
                .putString(
                    MediaMetadataCompat.METADATA_KEY_MEDIA_URI,
                    "http://content.blubrry.com/codingwithmitch/Justin_Mitchel_interview_audio_online-audio-converter.com_.mp3"
                )
                .build(),

            MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "11113")
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "Mitch Tabian & Matt Tran")
                .putString(
                    MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI,
                    "https://codingwithmitch.s3.amazonaws.com/static/profile_images/default_avatar.jpg"
                )
                .putString(
                    MediaMetadataCompat.METADATA_KEY_TITLE,
                    "CodingWithMitch Podcast #3 - Matt Tran"
                )
                .putString(
                    MediaMetadataCompat.METADATA_KEY_MEDIA_URI,
                    "http://content.blubrry.com/codingwithmitch/Matt_Tran_Interview_online-audio-converter.com_.mp3"
                )
                .build(),

            MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "11114")
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "Mitch Tabian")
                .putString(
                    MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI,
                    "https://codingwithmitch.s3.amazonaws.com/static/profile_images/default_avatar.jpg"
                )
                .putString(
                    MediaMetadataCompat.METADATA_KEY_TITLE,
                    "Some Random Test Audio"
                )
                .putString(
                    MediaMetadataCompat.METADATA_KEY_MEDIA_URI,
                    "https://s3.amazonaws.com/codingwithmitch-static-and-media/pluralsight/Processes+and+Threads/audio+test+1+(online-audio-converter.com).mp3"
                )
                .build()
        )
    }
}