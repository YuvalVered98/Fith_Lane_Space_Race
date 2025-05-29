package com.example.matala2.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.matala2.R
import com.example.matala2.model.ScoreRecord

class HighScoreAdapter(
    private val scores: List<ScoreRecord>,
    private val onItemClick: (ScoreRecord) -> Unit
) : RecyclerView.Adapter<HighScoreAdapter.ScoreViewHolder>() {

    inner class ScoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameLBL: TextView = itemView.findViewById(R.id.item_LBL_name)
        val scoreLBL: TextView = itemView.findViewById(R.id.item_LBL_score)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(scores[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_score, parent, false)
        return ScoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        val score = scores[position]
        holder.nameLBL.text = score.name
        holder.scoreLBL.text = score.score.toString()
    }

    override fun getItemCount(): Int = scores.size
}
