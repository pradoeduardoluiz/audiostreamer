package br.com.pradoeduardoluiz.spotifyclone.ui

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import br.com.pradoeduardoluiz.spotifyclone.MyApplication
import br.com.pradoeduardoluiz.spotifyclone.R
import br.com.pradoeduardoluiz.spotifyclone.client.MediaBrowserHelper
import br.com.pradoeduardoluiz.spotifyclone.client.MediaBrowserHelperCallback
import br.com.pradoeduardoluiz.spotifyclone.model.Artist
import br.com.pradoeduardoluiz.spotifyclone.services.MediaService
import br.com.pradoeduardoluiz.spotifyclone.ui.interfaces.MainActivityListener
import br.com.pradoeduardoluiz.spotifyclone.util.Constants
import br.com.pradoeduardoluiz.spotifyclone.util.MainActivityFragmentManager
import br.com.pradoeduardoluiz.spotifyclone.util.MyPreferenceManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainActivityListener, MediaBrowserHelperCallback {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var mediaBrowserHelper: MediaBrowserHelper
    private var isPlaying: Boolean = false
    private var application: MyApplication? = null
    private lateinit var preferenceManager: MyPreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mediaBrowserHelper = MediaBrowserHelper(this, MediaService::class.java)
        mediaBrowserHelper.setMediaBrowserHelperCallback(this)
        application = MyApplication.getInstance()
        preferenceManager = MyPreferenceManager(this)

        if (savedInstanceState == null) {
            loadFragment(HomeFragment.newInstance(), lateralMovement = true)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        MainActivityFragmentManager.getInstance()?.fragments?.size?.let {
            outState.putInt(
                "active_fragments",
                it
            )
        }
    }

    override fun onStart() {
        super.onStart()
        mediaBrowserHelper.onStart()
    }

    override fun onStop() {
        super.onStop()
        mediaBrowserHelper.onStop()
    }

    private fun loadFragment(fragment: Fragment, lateralMovement: Boolean) {
        val transaction = supportFragmentManager.beginTransaction()

        if (lateralMovement) {
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
        }

        val tag: String = when (fragment) {
            is HomeFragment -> getString(R.string.fragment_home)
            is CategoryFragment -> {
                val tagName = getString(R.string.fragment_category)
                transaction.addToBackStack(tagName)
                tagName
            }
            is PlaylistFragment -> {
                val tagName = getString(R.string.fragment_playlist)
                transaction.addToBackStack(tagName)
                tagName
            }
            else -> ""
        }

        transaction.replace(R.id.main_container, fragment, tag)
            .commit()

        MainActivityFragmentManager.getInstance()?.addFragment(fragment)

        showFragment(fragment, false)
    }

    private fun showFragment(fragment: Fragment, backswardsMovement: Boolean) {
        val transaction = supportFragmentManager.beginTransaction()

        if (backswardsMovement) {
            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
        }

        transaction.show(fragment)
        transaction.commit()

        MainActivityFragmentManager.getInstance()?.fragments?.forEach {
            if (!it.tag.equals(fragment.tag)) {
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.hide(it)
                fragmentTransaction.commit()
            }
        }
    }

    override fun onBackPressed() {
        val fragments: MutableList<Fragment>? = MainActivityFragmentManager.getInstance()?.fragments

        fragments?.let {
            if (it.size > 1) {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.remove(fragments.last())
                transaction.commit()

                MainActivityFragmentManager.getInstance()?.removeFragment(fragments.size - 1)
                showFragment(fragments.last(), true)
            }
        }

        super.onBackPressed()
    }

    override fun showProgressBar() {
        progress_bar.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progress_bar.visibility = View.GONE
    }

    override fun onCategorySelected(category: String) {
        loadFragment(CategoryFragment.newInstance(category), true)
    }

    override fun onArtistSelected(category: String, artist: Artist) {
        loadFragment(PlaylistFragment.newInstance(category, artist), true)
    }

    override fun setActionBarTitle(title: String) {
        supportActionBar?.title = title
    }

    override fun playPause() {
        if (isPlaying) {
            mediaBrowserHelper.getTransportControls()?.skipToNext()
        } else {
            mediaBrowserHelper.getTransportControls()?.play()
            isPlaying = true
        }
    }

    override fun getMyApplication(): MyApplication? {
        return application
    }

    override fun onMediaSelected(
        playlistId: String,
        mediaItem: MediaMetadataCompat?,
        queuePosition: Int
    ) {
        if (mediaItem != null) {
            Log.d(TAG, "[onMediaSelected]: Called ${mediaItem.description.mediaId}")

            val currentPlaylistId = getMyPreferenceManager().getPlayListId()

            val bundle = Bundle()
            bundle.putInt(Constants.MEDIA_QUEUE_POSITION, queuePosition)

            if (playlistId == currentPlaylistId) {
                mediaBrowserHelper.getTransportControls()
                    ?.playFromMediaId(mediaItem.description.mediaId, bundle)
            } else {
                bundle.putBoolean(Constants.QUEUE_NEW_PLAYLIST, true)
                mediaBrowserHelper.subscribeToNewPlayList(playlistId)
                mediaBrowserHelper.getTransportControls()
                    ?.playFromMediaId(mediaItem.description.mediaId, bundle)
            }
        } else {
            Toast.makeText(this, "Select something to play", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getMyPreferenceManager(): MyPreferenceManager {
        return preferenceManager
    }

    override fun onMetaDataChanged(metadata: MediaMetadataCompat) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
        isPlaying =
            state != null && state.state == PlaybackStateCompat.STATE_PLAYING
    }
}
