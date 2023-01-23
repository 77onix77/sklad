package com.onix77.sklad

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecAdHis(private val listHis: List<EntryHistory>):
    RecyclerView.Adapter<RecAdHis.HisViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HisViewHolder {
        return HisViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_history, parent, false)
        )
    }

    override fun onBindViewHolder(holder: HisViewHolder, position: Int) {
        val item = listHis[position]
        holder.date.text = item.date
        holder.time.text = item.time
        holder.cat.text = item.nameCat
        holder.name.text = item.nameEl
        holder.change.text = item.changeRest
        holder.rest.text = item.rest.toString()
        if (item.changeRest[0] == '-') holder.change.setTextColor(Color.RED) else holder.change.setTextColor(Color.GREEN)
    }

    override fun getItemCount(): Int {
        return listHis.size
    }

    class HisViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val date: TextView = view.findViewById(R.id.date)
        val time: TextView = view.findViewById(R.id.time)
        val cat: TextView = view.findViewById(R.id.cat)
        val name: TextView = view.findViewById(R.id.name)
        val change: TextView = view.findViewById(R.id.change)
        val rest: TextView = view.findViewById(R.id.rest)
    }
}