package com.example.musicapp

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore.Audio.Media
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    // variables
    var startTime = 0.0
    var finalTime = 0.0
    var forwardTime = 10000
    var backwardTime = 10000
    var oneTimeOnly = 0

    // Handler
    var handler: Handler = Handler()

    // Media Player
    var mediaPlayer = MediaPlayer()
    lateinit var time_txt: TextView
    lateinit var seekBar: SeekBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val play_btn : Button = findViewById(R.id.playBtn)
        val stop_btn : Button = findViewById(R.id.pauseBtn)
        val forward_btn: Button = findViewById(R.id.fowardBtn)
        val back_btn : Button = findViewById(R.id.backBtn)

        val title_txt : TextView = findViewById(R.id.song_title)
        time_txt  =findViewById(R.id.timeLeft)

        seekBar = findViewById(R.id.timeLeft)
        mediaPlayer = MediaPlayer.create(this, R.raw.understand)
        seekBar.isClickable = false

        //Adding Functionality
        play_btn.setOnClickListener(){
            mediaPlayer.start()
            finalTime = mediaPlayer.duration.toDouble()
            startTime = mediaPlayer.currentPosition.toDouble()

            if(oneTimeOnly == 0){
                seekBar.max = finalTime.toInt()
                oneTimeOnly = 1
            }
            time_txt.text = startTime.toString()
            seekBar.setProgress(startTime.toInt())
            handler.postDelayed(UpdateSongTime, 100)
        }

        //Setting the Music title
        title_txt.text = ""+resources.getResourceEntryName(R.raw.understand)

        //Stop btn
        stop_btn.setOnClickListener(){
            mediaPlayer.pause()
        }

        //Forwards btn
        forward_btn.setOnClickListener(){
            var temp = startTime
            if ((temp + forwardTime) <= finalTime){
                startTime = startTime + forwardTime
                mediaPlayer.seekTo(startTime.toInt())
            }else{
                Toast.makeText(this, "Cannot jump foward", Toast.LENGTH_LONG).show()
            }
        }

        back_btn.setOnClickListener(){
            var temp = startTime.toInt()
            if((temp - backwardTime) > 0){
                startTime = startTime - backwardTime
                mediaPlayer.seekTo(startTime.toInt())
            }else{
                Toast.makeText(this, "Cannot jump backward", Toast.LENGTH_LONG).show()
            }
        }

    }

    //Create Runnable
    val UpdateSongTime: Runnable = object :Runnable{
        @SuppressLint("SetTextI18n")
        override fun run() {
            startTime = mediaPlayer.currentPosition.toDouble()
            time_txt.text = buildString {
                append("")
                append(
                    String.format(
                        "%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                        TimeUnit.MILLISECONDS.toSeconds(
                            startTime.toLong() - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(startTime.toLong())
                            )
                        )
                    )
                )
            }
            seekBar.progress = startTime.toInt()
            handler.postDelayed(this, 100)
        }
    }
}