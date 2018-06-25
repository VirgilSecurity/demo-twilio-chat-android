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

package com.android.virgilsecurity.base.view.adapter

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
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
        diffCallback.setLists(this.data, data)
        val result = DiffUtil.calculateDiff(diffCallback)
        this.data.clear()
        this.data.addAll(data)
        result.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class Builder<T : Comparable<T>> {

        private var count: Int = 0
        private val typeToAdapterMap: SparseArray<DelegateAdapterItem<BaseViewHolder<T>, T>> =
                SparseArray()
        private lateinit var diffCallback: DiffCallback<T>

        fun setDiffCallback(diffCallback: DiffCallback<T>) {
            this.diffCallback = diffCallback
        }

        fun add(delegateAdapter: DelegateAdapterItem<BaseViewHolder<T>, T>): Builder<T> {
            typeToAdapterMap.put(count++, delegateAdapter)
            return this
        }

        fun build(): DelegateAdapter<T> {
            if (count == 0) throw IllegalArgumentException("Register at least one adapter")
            return DelegateAdapter(diffCallback, typeToAdapterMap)
        }
    }

    companion object {

        private val TAG = DelegateAdapter::class.java.simpleName
        private val FIRST_VIEW_TYPE = 0
    }
}