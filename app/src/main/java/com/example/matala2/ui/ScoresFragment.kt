package com.example.matala2.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.matala2.R
import com.example.matala2.interfaces.Callback_HighScoreItemClicked
import com.example.matala2.model.ScoreRecord
import com.example.matala2.utilities.SharedPreferencesManagerV3

class ScoresFragment : Fragment() {

    private var callback: Callback_HighScoreItemClicked? = null
    private lateinit var scores_LST_highScores: RecyclerView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Callback_HighScoreItemClicked) {
            callback = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_high_score, container, false)
        scores_LST_highScores = view.findViewById(R.id.scores_LST_highScores)
        initRecyclerView()
        return view
    }

    private fun refreshHighScores() {
        val updatedScores = SharedPreferencesManagerV3.getHighScores()
        (scores_LST_highScores.adapter as? HighScoreAdapter)?.let { adapter ->
            // יוצרים adapter חדש עם הנתונים העדכניים
            scores_LST_highScores.adapter = HighScoreAdapter(updatedScores) { score ->
                callback?.highScoreItemClicked(score.lat, score.lon)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshHighScores()
    }


    private fun initRecyclerView() {
        val highScores: List<ScoreRecord> = SharedPreferencesManagerV3.getHighScores()

        val adapter = HighScoreAdapter(highScores) { score ->
            callback?.highScoreItemClicked(score.lat, score.lon)
        }

        scores_LST_highScores.layoutManager = LinearLayoutManager(requireContext())
        scores_LST_highScores.adapter = adapter
    }
}
