// app/src/main/java/org/foss/lens/presentation/HistoryAdapter.kt
package org.foss.lens.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.foss.lens.R
import org.foss.lens.domain.Codex
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter : ListAdapter<Codex, HistoryAdapter.ViewHolder>(DiffCallback) {
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_codex, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val codex = getItem(position)
        holder.text1.text = codex.payload.take(30)
        holder.text2.text = dateFormat.format(codex.timestamp.toEpochMilli())
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text1: TextView = view.findViewById(android.R.id.text1)
        val text2: TextView = view.findViewById(android.R.id.text2)
    }

    internal object DiffCallback : DiffUtil.ItemCallback<Codex>() {
        override fun areItemsTheSame(oldItem: Codex, newItem: Codex): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Codex, newItem: Codex): Boolean =
            oldItem.id == newItem.id &&
                oldItem.payload == newItem.payload &&
                oldItem.timestamp == newItem.timestamp
    }
}
