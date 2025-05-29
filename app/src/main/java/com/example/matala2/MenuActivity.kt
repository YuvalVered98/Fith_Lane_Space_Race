package com.example.matala2.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.matala2.KotlinCoroutinesActivity
import com.example.matala2.R
import com.example.matala2.TopScoresActivity
import com.example.matala2.utilities.Constants
import com.google.android.material.button.MaterialButton

class MenuActivity : AppCompatActivity() {

    private lateinit var menu_BTN_sensor: MaterialButton
    private lateinit var menu_BTN_slow: MaterialButton
    private lateinit var menu_BTN_fast: MaterialButton
    private lateinit var menu_BTN_high_scores: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        findViews()
        initClicks()
    }

    private fun findViews() {
        menu_BTN_sensor = findViewById(R.id.menu_BTN_sensor)
        menu_BTN_slow = findViewById(R.id.menu_BTN_slow)
        menu_BTN_fast = findViewById(R.id.menu_BTN_fast)
        menu_BTN_high_scores = findViewById(R.id.menu_BTN_high_scores)

    }

    private fun initClicks() {
        menu_BTN_sensor.setOnClickListener {
            launchGame(Constants.GameMode.SENSOR)
        }
        menu_BTN_slow.setOnClickListener {
            launchGame(Constants.GameMode.BUTTON_SLOW)
        }
        menu_BTN_fast.setOnClickListener {
            launchGame(Constants.GameMode.BUTTON_FAST)
        }
        menu_BTN_high_scores.setOnClickListener {
            startActivity(Intent(this, TopScoresActivity::class.java))
        }

    }

    private fun launchGame(mode: String) {
        val intent = Intent(this, KotlinCoroutinesActivity::class.java)
        intent.putExtra(Constants.BundleKeys.GAME_MODE, mode)
        startActivity(intent)
        finish()
    }
}
