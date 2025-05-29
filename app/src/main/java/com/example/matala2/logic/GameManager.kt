package com.example.matala2.logic

import com.example.matala2.model.Mob
import com.example.matala2.model.DataManager
import com.example.matala2.utilities.Constants
import kotlin.random.Random

class GameManager(private val lifeCount: Int = 3) {

    val allChickens: List<Mob> = DataManager.getAllChickens()
    val allRockets: List<Mob> = DataManager.getAllRockets()
    val allCoins = DataManager.getAllCoins()

    var score: Int = 0


    var currentIndex: Int = 0
        private set
    var rocketIndex: Int = 1
        private set

    var wrongAnswers: Int = 0
        private set

    val currentMob: Mob
        get() = allChickens[currentIndex]

    val isGameEnded: Boolean
        get() = currentIndex == allChickens.size

    val isGameOver: Boolean
        get() = wrongAnswers == lifeCount


//    fun getWrongAnswers(): Int {
//        return wrongAnswers
//    }




    fun isHit(): Boolean {
        val lastRowStart = Constants.GameLogic.TOTAL_CHICKENS - Constants.GameLogic.NUM_COLUMNS

        for (i in 0 until Constants.GameLogic.NUM_COLUMNS) {
            val rocket = allRockets[i]
            val chicken = allChickens[lastRowStart + i]
            if (rocket.isView && chicken.isView) {
                chicken.isView = false // ✅ מניעת פגיעה כפולה
                wrongAnswers++
                return true
            }
        }
        return false
    }

    fun resetGame(){
        for (chicken in allChickens){
            chicken.isView = false
        }
        for (i in allRockets.indices) {
            allRockets[i].isView = (i == 2)
        }
        for (coin in allCoins) {
            coin.isView = false
        }


        rocketIndex = 2
        currentIndex = 0
        wrongAnswers = 0
        score = 0
//        isGameEnded = false
//        isGameOver = false
    }

    fun checkAnswer(expected: Boolean){
        if (!expected && rocketIndex > 0){
            allRockets[rocketIndex].isView = false
            rocketIndex--
            allRockets[rocketIndex].isView = true
        }

        else if(expected && rocketIndex < allRockets.size - 1){
            allRockets[rocketIndex].isView = false
            rocketIndex++
            allRockets[rocketIndex].isView = true
        }

    }

    fun isCoinHit(): Boolean {
        val rowStart = Constants.GameLogic.TOTAL_CHICKENS - Constants.GameLogic.NUM_COLUMNS
        for (i in 0 until Constants.GameLogic.NUM_COLUMNS) {
            if (allRockets[i].isView && allCoins[rowStart + i].isView) {
                allCoins[rowStart + i].isView = false
                score += Constants.GameLogic.SCORE_DEFAULT
                return true
            }
        }
        return false
    }

    fun getScoreWithTime(timeMillis: Long): Int {
        val timeSeconds = (timeMillis / 1000).toInt()
        val timeScore = timeSeconds * Constants.GameLogic.TIME_SCORE
        return score + timeScore
    }

    fun updateMob() {
        // 1. הזזת תרנגולות ומטבעות שורה למטה
        for (i in Constants.GameLogic.NUM_ROWS - 2 downTo 0) {
            for (j in 0 until Constants.GameLogic.NUM_COLUMNS) {
                val currentIndex = i * Constants.GameLogic.NUM_COLUMNS + j
                val belowIndex = (i + 1) * Constants.GameLogic.NUM_COLUMNS + j

                allChickens[belowIndex].isView = allChickens[currentIndex].isView
                allCoins[belowIndex].isView = allCoins[currentIndex].isView
            }
        }

        // 2. ניקוי השורה הראשונה
        for (j in 0 until Constants.GameLogic.NUM_COLUMNS) {
            allChickens[j].isView = false
            allCoins[j].isView = false
        }

        val column = Random.nextInt(0, Constants.GameLogic.NUM_COLUMNS)
        val index = column
        val isCoin = Random.nextFloat() <= 0.2f

        if (isCoin) {
            allCoins[index].isView = true
        } else {
            allChickens[index].isView = true
        }
    }

//    fun updateChickens() {
//        val numCols = Constants.GameLogic.NUM_COLUMNS
//        val totalChickens = Constants.GameLogic.TOTAL_CHICKENS
//        val lastRowStart = totalChickens - numCols
//
//        for (i in lastRowStart - 1 downTo 0) {
//            allChickens[i + numCols].isView = allChickens[i].isView
//        }
//
//        for (i in 0 until numCols) {
//            allChickens[i].isView = false
//        }
//
//        var r1 = (0 until numCols).random()
//        while ((r1 == rocketIndex) ||
//            ((r1 < numCols - 1 && allChickens[r1 + 1].isView) ||
//                    (r1 > 0 && allChickens[r1 - 1].isView) ||
//                    allChickens[r1].isView)
//        ) {
//            r1 = (0 until numCols).random()
//        }
//
//        allChickens[r1].isView = true
//    }

//    fun updateCoins() {
//        for (i in Constants.GameLogic.TOTAL_CHICKENS - Constants.GameLogic.NUM_COLUMNS - 1 downTo 0) {
//            allCoins[i + Constants.GameLogic.NUM_COLUMNS].isView = allCoins[i].isView
//        }
//
//        for (i in 0 until Constants.GameLogic.NUM_COLUMNS) {
//            allCoins[i].isView = false
//        }
//
//        val coinChance = (1..10).random()
//        if (coinChance <= 3) { // 30% סיכוי להופעת מטבע
//            var col: Int
//            do {
//                col = (0 until Constants.GameLogic.NUM_COLUMNS).random()
//            } while (allChickens[col].isView)
//
//            allCoins[col].isView = true
//        }
//    }
}