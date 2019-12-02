package br.com.pradoeduardoluiz.spotifyclone.ui

import    androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import br.com.pradoeduardoluiz.spotifyclone.R
import br.com.pradoeduardoluiz.spotifyclone.ui.interfaces.ProgressBarControl
import br.com.pradoeduardoluiz.spotifyclone.util.MainActivityFragmentManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ProgressBarControl {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadFragment(HomeFragment.newInstance(), lateralMovement = true)
    }

    private fun loadFragment(fragment: Fragment, lateralMovement: Boolean) {
        val transaction = supportFragmentManager.beginTransaction()

        if (lateralMovement) {
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
        }

        val tag: String = when (fragment) {
            is HomeFragment -> getString(R.string.fragment_home)
            is CategoryFragment -> getString(R.string.fragment_category)
            is PlaylistFragment -> getString(R.string.fragment_playlist)
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
            if (it.tag.equals(fragment.tag)) {
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.hide(it)
                fragmentTransaction.commit()
            }
        }
    }

    override fun showProgressBar() {
        progress_bar.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progress_bar.visibility = View.GONE
    }
}
