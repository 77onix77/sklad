package com.onix77.sklad


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class RecAdStat(private val ListStat: List<ItemStatistic>):
    RecyclerView.Adapter<RecAdStat.StatViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatViewHolder {
            return StatViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_statistic, parent, false)
            )
        }

        override fun onBindViewHolder(holder: StatViewHolder, position: Int) {
            val item = ListStat[position]
            holder.cat.text = item.cat
            holder.name.text = item.el
            val plus = "+${item.sumPlus}"
            holder.plus.text = plus
            holder.minus.text = item.sumMinus.toString()
        }

        override fun getItemCount(): Int {
            return ListStat.size
        }

        class StatViewHolder(view: View): RecyclerView.ViewHolder(view) {

            val cat: TextView = view.findViewById(R.id.ItStCat)
            val name: TextView = view.findViewById(R.id.ItStName)
            val plus: TextView = view.findViewById(R.id.ItStPlus)
            val minus: TextView = view.findViewById(R.id.ItStMinus)
        }
    }

class StatDiffUtils(private val oldList:List<ItemStatistic>, private val newList:List<ItemStatistic>): DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItemPosition == newItemPosition
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}