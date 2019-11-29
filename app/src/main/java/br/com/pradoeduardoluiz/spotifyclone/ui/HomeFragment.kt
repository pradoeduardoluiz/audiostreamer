package br.com.pradoeduardoluiz.spotifyclone.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.pradoeduardoluiz.spotifyclone.R
import br.com.pradoeduardoluiz.spotifyclone.adapters.HomeRecyclerAdapter
import br.com.pradoeduardoluiz.spotifyclone.adapters.HomeSelectorListener
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(), HomeSelectorListener {

    private lateinit var adapter: HomeRecyclerAdapter
    private var categories = mutableListOf<String>()

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
    }

    override fun onCategorySelected(position: Int) {

    }

}
