package com.onix77.sklad

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView


class RecyclerAdapter(private val category: List<String>,  private val parent: Context):
    RecyclerView.Adapter<RecyclerAdapter.CatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatViewHolder {
        return CatViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.element_category, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CatViewHolder, position: Int) {
        val cat = category[position]
        holder.name.text = cat
        holder.itemView.setOnClickListener {
            val intent = Intent(parent, CatActivity::class.java)
            intent.putExtra("cat", cat)
            startActivity(parent, intent, null)
        }
    }

    override fun getItemCount(): Int {
        return category.size
    }

    class CatViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.nameCat)
    }

}


class MainDiffUtils(private val oldList:List<String>, private val newList:List<String>): DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItemPosition == newItemPosition
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}