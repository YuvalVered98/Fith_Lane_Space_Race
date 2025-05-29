package com.example.matala2.utilities

import android.content.Context
import android.media.MediaPlayer

class SingleSoundPlayer(private val context: Context) {

    fun playSound(resId: Int) {
        val mediaPlayer = MediaPlayer.create(context, resId)
        mediaPlayer.setOnCompletionListener { it.release() }
        mediaPlayer.start()
    }
}
