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
import br.com.pradoeduardoluiz.spotifyclone.adapters.HomeRecyclerAdapter
import br.com.pradoeduardoluiz.spotifyclone.adapters.HomeSelectorListener
import br.com.pradoeduardoluiz.spotifyclone.ui.interfaces.ProgressBarControl
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(), HomeSelectorListener {

    private lateinit var adapter: HomeRecyclerAdapter
    private var categories = mutableListOf<String>()
    private lateinit var progressBar: ProgressBarControl

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(requireActivity())
        recycler_view.layoutManager = layoutManager

        adapter = HomeRecyclerAdapter(requireContext(), this)
        recycler_view.adapter = adapter

        retrieveCategories()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        progressBar = requireActivity() as ProgressBarControl
    }

    private fun retrieveCategories() {
        progressBar.showProgressBar()

        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

        val reference: DocumentReference =
            firestore.collection(getString(R.string.collection_audio))
                .document(getString(R.string.document_categories))

        reference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                Log.d(TAG, "onComplete: $document")

                val categoriesMap: HashMap<String, String> =
                    document?.data?.get("categories") as HashMap<String, String>

                categories.addAll(categoriesMap.keys)
            }
            updateDataSet()
        }
    }

    private fun updateDataSet() {
        progressBar.hideProgressBar()
        adapter.setList(categories)
    }

    override fun onCategorySelected(position: Int) {

    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}
