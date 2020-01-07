package br.com.pradoeduardoluiz.spotifyclone.ui


import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.pradoeduardoluiz.spotifyclone.R
import br.com.pradoeduardoluiz.spotifyclone.adapters.MediaSelectorListener
import br.com.pradoeduardoluiz.spotifyclone.adapters.PlaylistRecyclerAdapter
import br.com.pradoeduardoluiz.spotifyclone.model.Artist
import br.com.pradoeduardoluiz.spotifyclone.ui.interfaces.MainActivityListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.android.synthetic.main.fragment_home.*


class PlaylistFragment : Fragment(), MediaSelectorListener {

    private lateinit var adapter: PlaylistRecyclerAdapter
    private lateinit var mainActivityListener: MainActivityListener
    private var mediaList = mutableListOf<MediaMetadataCompat>()
    private var selectedCategory: String = " "
    private var selectedArtist: Artist? = null
    private var selectedMedia: MediaMetadataCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        arguments?.let {
            selectedCategory = it.getString(ARGUMENT_SELECTED_CATEGORY, "")
            selectedArtist = it.getParcelable(ARGUMENT_SELECTED_ARTIST)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivityListener.setActionBarTitle(selectedArtist?.title ?: "")

        initRecyclerView()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivityListener = requireActivity() as MainActivityListener
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(requireActivity())
        recycler_view.layoutManager = layoutManager

        adapter = PlaylistRecyclerAdapter(requireContext(), this)
        recycler_view.adapter = adapter

        if (mediaList.size == 0) {
            retrieveMedia()
        } else {
            updateDataSet()
        }
    }

    private fun retrieveMedia() {
        mediaList.clear()

        mainActivityListener.showProgressBar()

        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

        selectedArtist?.let { selectedArtist ->
            val query = firestore
                .collection(getString(R.string.collection_audio))
                .document(getString(R.string.document_categories))
                .collection(selectedCategory)
                .document(selectedArtist.artist_id)
                .collection(getString(R.string.collection_content))
                .orderBy(
                    getString(R.string.field_date_added),
                    Query.Direction.ASCENDING
                )

            query.get().addOnCompleteListener {
                if (it.isSuccessful) {

                    print(it.result?.size())

                    it.result?.forEach { document ->
                        addToMediaList(document)
                    }
                } else {
                    Log.d(TAG, "onComplete: error getting documents: " + it.exception)
                }
                updateDataSet()
            }
        }
    }

    private fun addToMediaList(document: QueryDocumentSnapshot?) {

        val media = MediaMetadataCompat.Builder()
            .putString(
                MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
                document?.getString(getString(R.string.field_media_id))
            )
            .putString(
                MediaMetadataCompat.METADATA_KEY_ARTIST,
                document?.getString(getString(R.string.field_artist))
            )
            .putString(
                MediaMetadataCompat.METADATA_KEY_TITLE,
                document?.getString(getString(R.string.field_title))
            )
            .putString(
                MediaMetadataCompat.METADATA_KEY_MEDIA_URI,
                document?.getString(getString(R.string.field_media_url))
            )
            .putString(
                MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION,
                document?.getString(getString(R.string.field_description))
            )
            .putString(
                MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION,
                document?.getDate(getString(R.string.field_date_added)).toString()
            )
            .putString(
                MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI,
                selectedArtist?.image
            )
            .build()

        mediaList.add(media)
    }


    private fun updateDataSet() {
        mainActivityListener.hideProgressBar()
        adapter.setList(mediaList)
    }

    override fun onMediaSelected(position: Int) {
        mainActivityListener.getMyApplication()?.setMediaItems(mediaList)
        selectedMedia = mediaList[position]
        adapter.setSelectedIndex(position)

        selectedArtist?.artist_id?.let {
            mainActivityListener.onMediaSelected(it, selectedMedia, position)
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (!hidden) {
            mainActivityListener.setActionBarTitle(selectedArtist?.title ?: "")
        }
    }

    companion object {
        private const val ARGUMENT_SELECTED_CATEGORY = "ARGUMENT_SELECTED_CATEGORY"
        private const val ARGUMENT_SELECTED_ARTIST = "ARGUMENT_SELECTED_ARTIST"
        fun newInstance(selectedCategory: String, selectedArtist: Artist) =
            PlaylistFragment().apply {
                arguments = Bundle().apply {
                    putString(ARGUMENT_SELECTED_CATEGORY, selectedCategory)
                    putParcelable(ARGUMENT_SELECTED_ARTIST, selectedArtist)
                }
            }

    }
}
