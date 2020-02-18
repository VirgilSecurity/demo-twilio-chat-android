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

package com.virgilsecurity.android.feature_channels_list.viewslice.list

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.RecyclerView
import com.virgilsecurity.android.base.data.model.ChannelMeta
import com.virgilsecurity.android.base.view.adapter.DelegateAdapter
import com.virgilsecurity.android.base.viewslice.BaseViewSlice
import com.virgilsecurity.android.feature_channels_list.R

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    8/8/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * ChannelsSliceDefault
 */
class ChannelsSlice(
        private val actionLiveData: MutableLiveData<Action>,
        private val adapter: DelegateAdapter<ChannelMeta>,
        private val itemDecoratorBottomDivider: RecyclerView.ItemDecoration,
        private val layoutManager: RecyclerView.LayoutManager
) : BaseViewSlice() {

    private lateinit var rvChannels: RecyclerView

    override fun setupViews() {
        with(window) {
            this@ChannelsSlice.rvChannels = findViewById(R.id.rvChannels)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        rvChannels.adapter = adapter
        rvChannels.layoutManager = layoutManager
        rvChannels.addItemDecoration(itemDecoratorBottomDivider)
    }

    fun getAction(): LiveData<Action> = actionLiveData

    fun showChannels(channels: List<ChannelMeta>) = adapter.swapData(channels)

    fun addChannel(channel: ChannelMeta) = adapter.addItem(channel)

    sealed class Action {
        data class ChannelClicked(val channel: ChannelMeta) : Action()
        object Idle : Action()
    }
}
