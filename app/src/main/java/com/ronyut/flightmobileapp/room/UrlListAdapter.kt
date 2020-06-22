package com.ronyut.flightmobileapp.room

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ronyut.flightmobileapp.R

class UrlListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<UrlListAdapter.UrlViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var urls = emptyList<Url>() // Cached copy of urls

    inner class UrlViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val urlItemView: TextView = itemView.findViewById(R.id.urlTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UrlViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return UrlViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UrlViewHolder, position: Int) {
        val current = urls[position]
        holder.urlItemView.text = current.url
    }

    internal fun setUrls(urls: List<Url>) {
        this.urls = urls
        notifyDataSetChanged()
    }

    override fun getItemCount() = urls.size
}