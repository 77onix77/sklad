package com.onix77.sklad

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class RecAdCat(private val elements: List<ElementDB>, private val parent: Context) :
    RecyclerView.Adapter<RecAdCat.ElViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElViewHolder {
        return ElViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_category, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ElViewHolder, position: Int) {
        val el = elements[position]
        holder.name.text = el.nameEl
        holder.number.text = el.number.toString()
        val color = R.color.pink
        if (el.number <= el.criticalRest) holder.number.setBackgroundResource(color)
        holder.itemView.setOnClickListener {
            val intent = Intent(parent, ElementActivity::class.java)
            intent.putExtra("el", el)
            ContextCompat.startActivity(parent, intent, null)
        }
    }

    override fun getItemCount(): Int {
        return elements.size
    }


    class ElViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.nameElTV)
        val number: TextView = view.findViewById(R.id.numberElTV)

    }


}