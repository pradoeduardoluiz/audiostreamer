package br.com.pradoeduardoluiz.spotifyclone.ui

import    androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import br.com.pradoeduardoluiz.spotifyclone.R
import br.com.pradoeduardoluiz.spotifyclone.model.Artist
import br.com.pradoeduardoluiz.spotifyclone.ui.interfaces.ProgressBarControl
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ProgressBarControl {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //testHomeFragment()
        // testCategoryFragment()
        testPlaylistFragment()
    }

    private fun testHomeFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, HomeFragment.newInstance()).commit()
    }

    private fun testCategoryFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, CategoryFragment.newInstance("Music")).commit()
    }

    private fun testPlaylistFragment() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.main_container,
                PlaylistFragment.newInstance(
                    "Podcasts",
                    Artist(
                        title = "CodingWithMitch Podcast",
                        image = "https://assets.blubrry.com/coverart/orig/654497-584077.png",
                        artistId = "m2BE0t4z0raEqqqgHXj4"
                    )
                )
            ).commit()
    }

    override fun showProgressBar() {
        progress_bar.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progress_bar.visibility = View.GONE
    }
}
