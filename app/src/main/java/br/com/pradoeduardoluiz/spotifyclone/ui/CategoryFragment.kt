package br.com.pradoeduardoluiz.spotifyclone.ui


import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.pradoeduardoluiz.spotifyclone.R
import br.com.pradoeduardoluiz.spotifyclone.adapters.CategoryRecyclerAdapter
import br.com.pradoeduardoluiz.spotifyclone.adapters.CategorySelectorListener
import br.com.pradoeduardoluiz.spotifyclone.model.Artist
import br.com.pradoeduardoluiz.spotifyclone.ui.interfaces.MainActivityListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_home.*

class CategoryFragment : Fragment(), CategorySelectorListener {

    private lateinit var adapter: CategoryRecyclerAdapter
    private var artists = mutableListOf<Artist>()
    private var selectedCategory = ""
    private lateinit var mainActivityListener: MainActivityListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedCategory = it.getString(ARGUMENT_SELECTED_CATEGORY, "")
        }
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivityListener.setActionBarTitle(selectedCategory)

        initRecyclerView()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivityListener = requireActivity() as MainActivityListener
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(requireActivity())
        recycler_view.layoutManager = layoutManager

        adapter = CategoryRecyclerAdapter(requireContext(), this)
        recycler_view.adapter = adapter

        if (artists.size == 0) {
            retrieveArtists()
        }else{
            updateDataSet()
        }
    }

    private fun retrieveArtists() {
        mainActivityListener.showProgressBar()

        artists.clear()

        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

        val query: Query = firestore
            .collection(getString(R.string.collection_audio))
            .document(getString(R.string.document_categories))
            .collection(selectedCategory)

        query.get().addOnCompleteListener {
            if (it.isSuccessful) {
                it.result?.forEach { document ->
                    artists.add(document.toObject(Artist::class.java))
                }
            } else {
                Log.d(TAG, "onComplete: error getting documents: " + it.exception)
            }
            updateDataSet()
        }
    }

    private fun updateDataSet() {
        mainActivityListener.hideProgressBar()
        adapter.setList(artists)
    }

    override fun onArtistSelected(position: Int) {
        mainActivityListener.onArtistSelected(selectedCategory, artists[position])
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (!hidden) {
            mainActivityListener.setActionBarTitle(selectedCategory)
        }
    }

    companion object {
        private const val ARGUMENT_SELECTED_CATEGORY = "ARGUMENT_SELECTED_CATEGORY"
        fun newInstance(selectedCategory: String) = CategoryFragment().apply {
            arguments = Bundle().apply {
                putString(ARGUMENT_SELECTED_CATEGORY, selectedCategory)
            }
        }
    }

}
