package br.com.pradoeduardoluiz.spotifyclone.ui


import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.pradoeduardoluiz.spotifyclone.MediaSeekBar

import br.com.pradoeduardoluiz.spotifyclone.R
import br.com.pradoeduardoluiz.spotifyclone.ui.interfaces.MainActivityListener
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_media_controller.*

class MediaControllerFragment : Fragment() {

    private var listener: MainActivityListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_media_controller, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_play_pause.setOnClickListener {
            listener?.playPause()
        }
    }

    fun setMediaTitle(mediaItem: MediaMetadataCompat) {
        text_media_song_title.text = mediaItem.description.title
    }

    fun setIsPlaying(isPlaying: Boolean) {
        if (isPlaying) {
            Glide.with(requireActivity()).load(R.drawable.ic_pause_circle_outline_white_24dp)
                .into(button_play_pause)
        } else {
            Glide.with(requireActivity()).load(R.drawable.ic_play_circle_outline_white_24dp)
                .into(button_play_pause)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = activity as MainActivity
    }

    fun getMediaSeekBar(): MediaSeekBar {
        return seek_bar_audio
    }

}
