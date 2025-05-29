package com.example.matala2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.matala2.activities.MenuActivity
import com.example.matala2.interfaces.Callback_HighScoreItemClicked
import com.example.matala2.ui.ScoresFragment
import com.example.matala2.ui.MapFragment

class TopScoresActivity : AppCompatActivity(), Callback_HighScoreItemClicked {

    private lateinit var main_FRAME_list: FrameLayout
    private lateinit var main_FRAME_map: FrameLayout
    private lateinit var top_scores_BTN_back: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_top_scores)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViews()
        initViews()
        handleClicks()
    }

    private fun findViews() {
        main_FRAME_list = findViewById(R.id.main_FRAME_list)
        main_FRAME_map = findViewById(R.id.main_FRAME_map)
        top_scores_BTN_back = findViewById(R.id.top_scores_BTN_back)
    }

    private fun initViews() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_FRAME_list, ScoresFragment())
            .commit()

        // mapFragment נטען רק בלחיצה על שיא - לא מראש
    }

    private fun handleClicks() {
        top_scores_BTN_back.setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
            finish()
        }
    }

    override fun highScoreItemClicked(lat: Double, lon: Double) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_FRAME_map, MapFragment.newInstance(lat, lon))
            .commit()

        main_FRAME_map.visibility = View.VISIBLE
    }
}
