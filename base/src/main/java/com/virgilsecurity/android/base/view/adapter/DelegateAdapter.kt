/*
 * Copyright (c) 2015-2018, Virgil Security, Inc.
 *
 * Lead Maintainer: Virgil Security Inc. <support@virgilsecurity.com>
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     (1) Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 *
 *     (2) Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *     (3) Neither the name of virgil nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.virgilsecurity.android.base.view.adapter

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import android.util.SparseArray
import android.view.ViewGroup

class DelegateAdapter<T : Comparable<T>> constructor(
        private val diffCallback: DiffCallback<T>,
        private val typeToAdapterMap: SparseArray<DelegateAdapterItem<BaseViewHolder<T>, T>>
) : RecyclerView.Adapter<BaseViewHolder<T>>() {

    private val data: MutableList<T> = mutableListOf()

    override fun getItemViewType(position: Int): Int {
        for (i in FIRST_VIEW_TYPE until typeToAdapterMap.size()) {
            val delegate = typeToAdapterMap.valueAt(i)

            if (delegate.isForViewType(data, position)) {
                return typeToAdapterMap.keyAt(i)
            }
        }
        throw NullPointerException("Can not get viewType for position $position")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {
        return typeToAdapterMap[viewType].onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        val delegateAdapter = typeToAdapterMap[getItemViewType(position)]

        if (delegateAdapter != null)
            delegateAdapter.onBindViewHolder(holder, data, position)
        else
            throw NullPointerException("can not find adapter for position $position")
    }

    override fun onViewRecycled(holder: BaseViewHolder<T>) {
        typeToAdapterMap[holder.itemViewType].onRecycled(holder)
    }

    fun swapData(data: List<T>) {
        if (data.isNotEmpty()) {
            diffCallback.setLists(this.data, data)
            val result = DiffUtil.calculateDiff(diffCallback)
            this.data.clear()
            this.data.addAll(data)

            result.dispatchUpdatesTo(this)
        }
    }

    fun setItems(data: List<T>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    fun addItems(data: List<T>) {
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    fun addItemToEnd(data: T) {
        this.data.add(data)
        notifyDataSetChanged()
    }

    fun addItem(data: T) {
        this.data.toMutableList()
                .apply {
                    add(data)
                    this.sort()
                }.run {
                    swapData(this)
                }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    /**
     * Adapter must have at least one item added and diff callback.
     * Otherwise exception will be thrown.
     */
    class Builder<T : Comparable<T>> {

        private var count: Int = 0
        private val typeToAdapterMap: SparseArray<DelegateAdapterItem<BaseViewHolder<T>, T>> =
                SparseArray()
        private var diffCallback: DiffCallback<T>? = null

        fun diffCallback(diffCallback: DiffCallback<T>): Builder<T> {
            this.diffCallback = diffCallback

            return this
        }

        fun add(delegateAdapter: DelegateAdapterItem<BaseViewHolder<T>, T>): Builder<T> {
            typeToAdapterMap.put(count++, delegateAdapter)
            return this
        }

        fun build(): DelegateAdapter<T> {
            if (count == 0) throw IllegalArgumentException("Register at least one adapter item")

            if (diffCallback == null)
                throw IllegalArgumentException("This adapter requires diff callback provided")

            return DelegateAdapter(diffCallback!!, typeToAdapterMap)
        }
    }

    companion object {

        private val TAG = DelegateAdapter::class.java.simpleName
        private const val FIRST_VIEW_TYPE = 0
    }
}
