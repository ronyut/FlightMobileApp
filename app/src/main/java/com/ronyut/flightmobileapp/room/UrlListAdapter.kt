package com.ronyut.flightmobileapp.room

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ronyut.flightmobileapp.R

/*
    This class is responsible for the cached copy of the database
    Author: Rony Utevsky
    Date: 23-06-2020
 */
class UrlListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<UrlListAdapter.UrlViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    // This is the cached version of the urls
    private var urls = emptyList<Url>()

    // This inner class is small and cute
    inner class UrlViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val urlItemView: TextView = itemView.findViewById(R.id.urlTitle)
    }

    // create the holder of the url in the View
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UrlViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return UrlViewHolder(itemView)
    }

    // bind the url to the to the View
    override fun onBindViewHolder(holder: UrlViewHolder, position: Int) {
        val current = urls[position]
        holder.urlItemView.text = current.url
    }

    // Update the real db
    internal fun setUrls(urls: List<Url>) {
        this.urls = urls
        notifyDataSetChanged()
    }

    // count the items in the database
    override fun getItemCount() = urls.size
}