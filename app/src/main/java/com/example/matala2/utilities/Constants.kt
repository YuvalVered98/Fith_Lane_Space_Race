package com.example.matala2.utilities

class Constants {

    object Timer {
        var DELAY: Long = 1_000L
    }

    object GameLogic {
        const val SCORE_DEFAULT: Int = 10
        const val NUM_COLUMNS: Int = 5
        const val NUM_ROWS: Int = 5
        const val TOTAL_CHICKENS: Int = NUM_COLUMNS * NUM_ROWS
        const val TIME_SCORE = 1
        const val SLOW_SPEED = 1000L
        const val FAST_SPEED = 400L

    }

    object GameMode {
        const val SENSOR = "SENSOR"
        const val BUTTON_SLOW = "BUTTON_SLOW"
        const val BUTTON_FAST = "BUTTON_FAST"
    }


    object BundleKeys {
        const val MESSAGE_KEY: String = "MESSAGE_KEY"
        const val SCORE_KEY: String = "SCORE_KEY"
        const val GAME_MODE = "GAME_MODE"
    }

    object SP_KEYS {
        const val PLAYLIST_KEY = "PLAYLIST_KEY"
    }
}
