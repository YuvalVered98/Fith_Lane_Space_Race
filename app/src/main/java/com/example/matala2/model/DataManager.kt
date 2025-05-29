package com.example.matala2.model

class DataManager {
    companion object {
        private val chickens = Array(size = 25) { i -> "chicken$i" }
        private val isChickenViewed = BooleanArray(size = 25) { false }
        private val chickenIndex = IntArray(size = 25) { it }
        private val rockets = Array(size = 5) { i -> "rocket$i" }
        private val isRocketsViewed = BooleanArray(size = 5) { i -> i == 2 }
        private val rocketIndex = IntArray(size = 5) { it }
        private val coins = Array(size = 25) { i -> "coin$i" }
        private val isCoinViewed = BooleanArray(size = 25) { false }
        private val coinIndex = IntArray(size = 25) { it }


        fun getAllChickens(): List<Mob> {
            val allChickens = mutableListOf<Mob>()
            for (i in chickens.indices) {
                allChickens.add(
                    Mob(
                        name = chickens[i],
                        isView = isChickenViewed[i],
                        index = chickenIndex[i],
                        isCoin = false
                    )
                )
            }
            return allChickens
        }

        fun getAllCoins(): List<Mob> {
            val allCoins = mutableListOf<Mob>()
            for (i in coins.indices) {
                allCoins.add(
                    Mob(
                        name = coins[i],
                        isView = isCoinViewed[i],
                        index = coinIndex[i],
                        isCoin = true
                    )
                )
            }
            return allCoins
        }

        fun getAllRockets(): List<Mob> {
            val allRockets = mutableListOf<Mob>()
            for (i in rockets.indices) {
                allRockets.add(
                    Mob(
                        name = rockets[i],
                        isView = isRocketsViewed[i],
                        index = rocketIndex[i],
                        isCoin = false
                    )
                )
            }
            return allRockets
        }
    }
}
