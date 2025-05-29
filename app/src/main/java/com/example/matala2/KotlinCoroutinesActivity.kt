package com.example.matala2

import android.content.Intent
import android.os.Bundle
import android.util.Log

import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textview.MaterialTextView
import com.example.matala2.logic.GameManager
import com.example.matala2.utilities.Constants
import com.example.matala2.utilities.SignalManager
import com.example.matala2.utilities.SingleSoundPlayer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.matala2.utilities.TiltCallback
import com.example.matala2.utilities.TiltDetector


class KotlinCoroutinesActivity : AppCompatActivity() {

    private lateinit var chickens: Array<AppCompatImageView>
    private lateinit var rockets: Array<AppCompatImageView>
    private lateinit var main_LBL_score: MaterialTextView
    private lateinit var main_IMG_hearts: Array<AppCompatImageView>
    private lateinit var main_FAB_left: ExtendedFloatingActionButton
    private lateinit var main_FAB_right: ExtendedFloatingActionButton
    private lateinit var tiltDetector: TiltDetector
    private lateinit var coins: Array<AppCompatImageView>
    private lateinit var gameManager: GameManager
    private var isGameActive = true
    private lateinit var main_FAB_stop: ExtendedFloatingActionButton
    private lateinit var main_FAB_play: ExtendedFloatingActionButton
    private var startTime: Long = 0
    private var stopTime: Long = 0
    private var timerOn: Boolean = false
    private lateinit var timerJob: Job


    private fun updateTimerUI(timeInMillis: Long) {
        val fullScore = gameManager.getScoreWithTime(timeInMillis)
        main_LBL_score.text = "Score: $fullScore"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViews()
        val gameMode =
            intent.getStringExtra(Constants.BundleKeys.GAME_MODE) ?: Constants.GameMode.BUTTON_SLOW
        when (gameMode) {
            Constants.GameMode.SENSOR -> {
                initTiltDetector()
                main_FAB_left.visibility = View.GONE
                main_FAB_right.visibility = View.GONE
            }

            Constants.GameMode.BUTTON_SLOW -> {
                initButtonControls(speed = Constants.GameLogic.SLOW_SPEED)
            }

            Constants.GameMode.BUTTON_FAST -> {
                initButtonControls(speed = Constants.GameLogic.FAST_SPEED)
            }
        }
        gameManager = GameManager(main_IMG_hearts.size)
        initViews()
        gameManager.resetGame()
    }

    private fun initTiltDetector() {
        tiltDetector = TiltDetector(
            context = this,
            tiltCallback = object : TiltCallback {
                override fun onTiltLeft() {
                    if (!isGameActive) return
                    answerClicked(true)
                }

                override fun onTiltRight() {
                    if (!isGameActive) return
                    answerClicked(false)
                }

            }
        )
    }

    private fun findViews() {
        rockets = arrayOf(
            findViewById(R.id.Rocket0),
            findViewById(R.id.Rocket1),
            findViewById(R.id.Rocket2),
            findViewById(R.id.Rocket3),
            findViewById(R.id.Rocket4)
        )
        chickens = Array(Constants.GameLogic.TOTAL_CHICKENS) { index ->
            findViewById(
                resources.getIdentifier("Chicken$index", "id", packageName)
            )
        }
        main_LBL_score = findViewById(R.id.main_LBL_score)
        main_IMG_hearts = arrayOf(
            findViewById(R.id.main_IMG_heart0),
            findViewById(R.id.main_IMG_heart1),
            findViewById(R.id.main_IMG_heart2),
        )
        main_FAB_left = findViewById(R.id.main_FAB_left)
        main_FAB_right = findViewById(R.id.main_FAB_right)
        coins = Array(Constants.GameLogic.TOTAL_CHICKENS) { index ->
            findViewById(
                resources.getIdentifier("Coin$index", "id", packageName)
            )
        }
        main_FAB_stop = findViewById(R.id.main_FAB_stop)
        main_FAB_play = findViewById(R.id.main_FAB_play)
    }

    private fun initButtonControls(speed: Long) {
        main_FAB_left.setOnClickListener {
            gameManager.checkAnswer(false)
            refreshUI()
        }

        main_FAB_right.setOnClickListener {
            gameManager.checkAnswer(true)
            refreshUI()
        }
        Constants.Timer.DELAY = speed
    }


    private fun initViews() {
        main_LBL_score.text = "0"
        startTime = System.currentTimeMillis()
        main_FAB_play.setOnClickListener { v: View -> startTimer() }
        main_FAB_stop.setOnClickListener { v: View -> stopTimer() }
        main_FAB_left.setOnClickListener { view: View -> answerClicked(false) }
        main_FAB_right.setOnClickListener { view: View -> answerClicked(true) }
        refreshUI()
    }

    private fun refreshUI() {
        if (gameManager.isGameOver) {
            val finalScore = gameManager.getScoreWithTime(System.currentTimeMillis() - startTime)
            changeActivity("😓 Game Over", finalScore.toString())

        }
        else { // Ongoing game:
            for (rocket in gameManager.allRockets) {
                if (rocket.isView == false) {
                    rockets[rocket.index].visibility = View.INVISIBLE
                } else
                    rockets[rocket.index].visibility = View.VISIBLE
            }
            for (chicken in gameManager.allChickens) {
                if (!chicken.isView) {
                    chickens[chicken.index].visibility = View.INVISIBLE
                } else
                    chickens[chicken.index].visibility = View.VISIBLE
            }
            for (coin in gameManager.allCoins) {
                coins[coin.index].visibility = if (coin.isView) View.VISIBLE else View.INVISIBLE
            }

            if (gameManager.isHit()) {
                SingleSoundPlayer(this).playSound(R.raw.explosion)
                SignalManager.getInstance().toast("God burned!!!")
                SignalManager.getInstance().vibrate()
            }
            if (gameManager.isCoinHit()) {
                SignalManager.getInstance().toast("💰 Coin collected!")
            }
            if (gameManager.wrongAnswers != 0) {
                main_IMG_hearts[main_IMG_hearts.size - gameManager.wrongAnswers]
                    .visibility = View.INVISIBLE
            }
            val elapsedMillis = System.currentTimeMillis() - startTime
            val fullScore = gameManager.getScoreWithTime(elapsedMillis)
            main_LBL_score.text = "Score: $fullScore"
            main_IMG_hearts
        }
    }

    fun resetLives() {
        for (heart in main_IMG_hearts) {
            heart.visibility = View.VISIBLE
        }
    }

    private fun changeActivity(message: String, score: CharSequence) {
        val intent = Intent(this, ScoreActivity::class.java)
        var bundle = Bundle()
        bundle.putString(Constants.BundleKeys.MESSAGE_KEY, message)
        bundle.putCharSequence(Constants.BundleKeys.SCORE_KEY, score)
        intent.putExtras(bundle)
        startActivity(intent)
        finish()
    }

    private fun answerClicked(expected: Boolean) {
        if (!isGameActive) return

        gameManager.checkAnswer(expected)
        refreshUI()
    }


    private fun startTimer() {
        if (!timerOn) {
            timerOn = true
            if (stopTime == 0L) {
                startTime = System.currentTimeMillis()
            } else {
                startTime = System.currentTimeMillis() - stopTime
            }

            timerJob = lifecycleScope.launch {
                while (timerOn) {
                    val elapsed = System.currentTimeMillis() - startTime
                    updateTimerUI(elapsed)
                    gameManager.updateMob()
                    refreshUI()
                    delay(Constants.Timer.DELAY)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::tiltDetector.isInitialized) {
            tiltDetector.start()
        }
        if (stopTime != 0L) {
            startTime = System.currentTimeMillis() - stopTime
        }
    }

    override fun onPause() {
        super.onPause()
        if (::tiltDetector.isInitialized) {
            tiltDetector.stop()
        }
        stopTime = System.currentTimeMillis() - startTime
        timerOn = false
        timerJob?.cancel()

    }

    private fun stopTimer() {
        if (timerOn) {
            timerOn = false
            timerJob.cancel()
            val currentTime: Long = System.currentTimeMillis()
            stopTime = currentTime - startTime
            isGameActive = false
        } else {
            stopTime = 0L
            startTime = System.currentTimeMillis()
            resetLives()
            gameManager.resetGame()
            refreshUI()
            isGameActive = true
        }
    }
}