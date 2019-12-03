package br.com.pradoeduardoluiz.spotifyclone.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Artist(
    val title: String = "",
    val image: String = "",
    val artist_id: String = ""
) : Parcelable