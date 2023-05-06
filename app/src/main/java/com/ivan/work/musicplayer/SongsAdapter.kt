package com.ivan.work.musicplayer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class SongsAdapter(
    val ctx: Context,
    val values: ArrayList<Song>
): ArrayAdapter<Song>(ctx,0,values){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        //LayoutInflater permet de convertir un fichier xml en View
        val itemView: View = LayoutInflater.from(ctx).inflate(R.layout.item_song,parent,false)

        val songTitle: TextView = itemView.findViewById(R.id.song_title)
        val songArtist: TextView = itemView.findViewById(R.id.song_artist)

        val song = getItem(position)
        song?.let {
            songTitle.text = it.title
            songArtist.text = it.artist
        }

        return itemView
    }
}