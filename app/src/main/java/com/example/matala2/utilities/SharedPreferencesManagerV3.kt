package com.example.matala2.utilities

import android.content.Context
import com.example.matala2.model.ScoreRecord
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class SharedPreferencesManagerV3 private constructor(context: Context) {
    private val sharedPreferences =
        context.getSharedPreferences(
            Constants.SP_KEYS.PLAYLIST_KEY,
            Context.MODE_PRIVATE
        )

    companion object{
        @Volatile
        private var instance: SharedPreferencesManagerV3? = null
        private val MAX_SCORES = 10


        fun init(context: Context): SharedPreferencesManagerV3{
            return instance ?: synchronized(this){
                instance ?: SharedPreferencesManagerV3(context).also { instance = it }
            }
        }

        fun isEligibleForTop10(score: Int): Boolean {
            val scores = getHighScores()
            return scores.size < MAX_SCORES || scores.any { it.score < score }
        }


        fun getHighScores(): List<ScoreRecord> {
            val json = instance?.sharedPreferences?.getString("high_scores", "[]") ?: "[]"
            val type = object : TypeToken<List<ScoreRecord>>() {}.type
            return Gson().fromJson(json, type)
        }
        fun putHighScores(highScores: List<ScoreRecord>) {
            val json = Gson().toJson(highScores)
            instance?.sharedPreferences?.edit()?.putString("high_scores", json)?.apply()

        }

        fun addHighScoreIfTop10(newRecord: ScoreRecord) {
            if (newRecord.name.isBlank()) return
            val highScores = getHighScores().toMutableList()

            highScores.add(newRecord)
            highScores.sortByDescending { it.score }

            if (highScores.size > 10) {
                highScores.removeAt(highScores.lastIndex)

            }

            putHighScores(highScores)
        }


        fun getInstance(): SharedPreferencesManagerV3 {
            return instance ?: throw IllegalStateException(
                "SharedPreferencesManagerV3 must be initialized by calling init(context) before use."
            )
        }
    }


    fun putString(key: String, value: String) {
        with(sharedPreferences.edit()) {
            putString(key, value)
            apply()
        }
    }

    fun getString(key: String, defaultValue: String): String {
        return sharedPreferences
            .getString(
                key, defaultValue
            ) ?: defaultValue
    }
}