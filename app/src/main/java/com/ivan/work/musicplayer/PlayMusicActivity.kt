package com.ivan.work.musicplayer

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import java.io.IOException

class PlayMusicActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var txtSongTitle: TextView
    private lateinit var txtSongArtist: TextView
    private lateinit var txtSongStart: TextView
    private lateinit var txtSongEnd: TextView
    private lateinit var playButton: Button
    private lateinit var volumeSeekBar: SeekBar
    private lateinit var songSeekBar: SeekBar


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_music)

        val song = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("selectedSong") as? Song
        } else {
            intent.getSerializableExtra("selectedSong") as Song
        }

        // Display the selected song's title and artist
        txtSongTitle = findViewById(R.id.txt_song_title)
        txtSongTitle.text = song?.title
        txtSongArtist = findViewById(R.id.txt_song_artist)
        txtSongArtist.text = song?.artist

        txtSongStart = findViewById(R.id.txt_song_start)
        txtSongEnd = findViewById(R.id.txt_song_end)
        playButton = findViewById(R.id.play_button)
        songSeekBar = findViewById(R.id.seekbar_song_control)
        volumeSeekBar = findViewById(R.id.seekbar_volume_control)

        mediaPlayer = MediaPlayer()
        try {
            mediaPlayer.setDataSource(song?.path)
            mediaPlayer.prepare()
        } catch (e : IOException) {
            e.printStackTrace()
        }
        mediaPlayer.isLooping = true
        mediaPlayer.seekTo(0)
        mediaPlayer.setVolume(0.5f,0.5f)
        val duration = milliSecondsToString(mediaPlayer.duration)
        txtSongEnd.text = duration
        volumeSeekBar.progress = 50
        volumeSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val volume = progress / 100f
                mediaPlayer.setVolume(volume, volume)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        songSeekBar.max = mediaPlayer.duration
        songSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                }
                txtSongStart.text = milliSecondsToString(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        var isPlaying = false

        playButton.setOnClickListener {
            if (isPlaying) {
                mediaPlayer.pause()
                isPlaying = false
                playButton.setBackgroundResource(R.drawable.play_icon)
            } else {
                mediaPlayer.start()
                isPlaying = true
                playButton.setBackgroundResource(R.drawable.ic_pause)
            }
        }


        Thread {
            while (mediaPlayer != null) {
                if (mediaPlayer.isPlaying) {
                    try {
                        val current = mediaPlayer.currentPosition.toDouble()
                        runOnUiThread {
                            txtSongStart.text = milliSecondsToString(current.toInt())
                            songSeekBar.progress = current.toInt()
                        }
                        Thread.sleep(1000)
                    } catch (e: InterruptedException) {
                        break
                    }
                }
            }
        }.start()

    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    /*private fun milliSecondsToString(time: Int): String {
        var elapsedTime = ""
        val minutes = time / 1000 / 60
        val seconds = time / 1000 % 60
        elapsedTime = "$minutes:"
        if(seconds < 10) {
            elapsedTime += "0"
        }
        elapsedTime += seconds
        return elapsedTime
    }*/

    private fun milliSecondsToString(time: Int): String {
        val minutes = time / 1000 / 60
        val seconds = time / 1000 % 60

        return String.format("%d:%02d", minutes, seconds)
    }



}


