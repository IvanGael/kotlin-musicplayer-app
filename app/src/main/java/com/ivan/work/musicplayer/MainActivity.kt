package com.ivan.work.musicplayer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    private lateinit var listViewSongs: ListView

    private lateinit var songsArrayList: ArrayList<Song>

    private lateinit var adapter: SongsAdapter

    companion object{
        private const val REQUEST_PERMISSION = 99
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        initialize()

        songsArrayList = ArrayList()


        adapter = SongsAdapter(this@MainActivity, songsArrayList)
        listViewSongs.adapter = adapter

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSION)
            return
        } else {
            getSongs()
        }

    }

    override fun onStart() {
        super.onStart()
        getSongs()
        listViewSongs.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val selectedSong = songsArrayList[i]
            Intent(this@MainActivity, PlayMusicActivity::class.java).also {
                it.putExtra("selectedSong", selectedSong)
                startActivity(it)
            }
        }
    }

    private fun initialize(){
        listViewSongs = findViewById(R.id.list_view_songs)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_PERMISSION){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getSongs()
            }
        }
    }

    private fun getSongs(){
        val songCursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,null)

        if(songCursor != null && songCursor.moveToFirst()){
            val indexTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val indexArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val indexData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val indexIsMusic = songCursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)
            val indexIsDownload = songCursor.getColumnIndex(MediaStore.Audio.Media.IS_DOWNLOAD)

            do {
                val title = songCursor.getString(indexTitle)
                val artist = songCursor.getString(indexArtist)
                val path = songCursor.getString(indexData)
                val isMusic = songCursor.getInt(indexIsMusic)
                val isDownload = songCursor.getInt(indexIsDownload)

                if(isMusic == 1 || isDownload == 1){
                    songsArrayList.add(Song(title,artist,path))
                }
            } while (songCursor.moveToNext())

            songCursor.close()
        }
        adapter.notifyDataSetChanged()
        if(songsArrayList.isEmpty()){
            //songsArrayList.add(Song("default.mp3","default.mp3","default.mp3"))
            Toast.makeText(this@MainActivity, "No songs to display",Toast.LENGTH_LONG).show()
        }
    }

}