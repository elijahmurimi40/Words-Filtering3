package com.fortie40.words_filtering3.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fortie40.words_filtering3.R

class MainActivityAdapter(names: List<String>):
    RecyclerView.Adapter<MainActivityAdapter.MainActivityViewHolder>() {

    private val _names = names

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainActivityViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.name_layout, parent, false)
        return MainActivityViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return _names.size
    }

    override fun onBindViewHolder(holder: MainActivityViewHolder, position: Int) {
        val name = _names[position]
        holder.bind(name)
    }

    class MainActivityViewHolder(nItemView: View): RecyclerView.ViewHolder(nItemView) {
        private val name = nItemView.findViewById<TextView>(R.id.name)

        fun bind(nameA: String) {
            name.text = nameA
        }
    }
}