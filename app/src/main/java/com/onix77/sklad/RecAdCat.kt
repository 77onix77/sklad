package com.onix77.sklad

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
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
        val pink = R.color.pink
        val green = R.color.green
        if (el.number <= el.criticalRest) holder.number.setBackgroundResource(pink)
        else holder.number.setBackgroundResource(green)
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

class CatDiffUtils(private val oldList:List<ElementDB>, private val newList:List<ElementDB>): DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}