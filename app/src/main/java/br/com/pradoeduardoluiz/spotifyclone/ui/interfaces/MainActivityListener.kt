package br.com.pradoeduardoluiz.spotifyclone.ui.interfaces

import br.com.pradoeduardoluiz.spotifyclone.model.Artist

interface MainActivityListener {
    fun showProgressBar()
    fun hideProgressBar()
    fun onCategorySelected(category: String)
    fun onArtistSelected(category: String, artist: Artist)

}