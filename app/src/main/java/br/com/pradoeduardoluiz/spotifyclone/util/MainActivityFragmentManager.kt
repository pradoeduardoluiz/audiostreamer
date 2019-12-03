package br.com.pradoeduardoluiz.spotifyclone.util

import androidx.fragment.app.Fragment

data class MainActivityFragmentManager(
    var fragments: MutableList<Fragment> = mutableListOf()
) {

    fun addFragment(fragment: Fragment) {
        fragments.add(fragment)
    }

    fun removeFragment(fragment: Fragment) {
        fragments.remove(fragment)
    }

    fun removeFragment(position: Int) {
        fragments.removeAt(position)
    }

    fun remoteAllFragments() {
        fragments.clear()
    }

    companion object {
        private var instance: MainActivityFragmentManager? = null
        fun getInstance(): MainActivityFragmentManager? {
            if (instance == null) {
                instance = MainActivityFragmentManager()
            }
            return instance
        }
    }
}