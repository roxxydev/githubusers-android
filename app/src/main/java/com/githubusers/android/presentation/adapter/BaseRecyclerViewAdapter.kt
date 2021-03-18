package com.githubusers.android.presentation.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseRecyclerViewAdapter<T, V: ViewBinding>
    : RecyclerView.Adapter<BaseViewHolder<T, V>>() {

    internal var items: ArrayList<T> = arrayListOf()
    lateinit var viewBindingInitializer: ViewHolderInitializer<T, V>

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T, V> {
        return viewBindingInitializer.generateViewHolder(parent)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T, V>, position: Int) {
        val listItem = items[position]
        holder.setViews(listItem)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    open fun submitList(listItems: List<T>) {
        items = listItems as ArrayList<T>
        notifyDataSetChanged()
    }

    fun getListItems(): List<T> {
        return items
    }
}

interface ViewHolderInitializer<T, V : ViewBinding> {

    fun generateViewHolder(parent: ViewGroup): BaseViewHolder<T, V>
}

