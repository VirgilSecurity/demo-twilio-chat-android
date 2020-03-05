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

package com.virgilsecurity.android.feature_channels_list.view

import android.view.View
import android.view.Window
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.virgilsecurity.android.base.data.model.ChannelMeta
import com.virgilsecurity.android.base.data.properties.UserProperties
import com.virgilsecurity.android.base.extension.observe
import com.virgilsecurity.android.base.view.adapter.BaseViewHolder
import com.virgilsecurity.android.base.view.adapter.DelegateAdapter
import com.virgilsecurity.android.base.view.adapter.DelegateAdapterItem
import com.virgilsecurity.android.base.view.adapter.DiffCallback
import com.virgilsecurity.android.base.view.controller.BaseController
import com.virgilsecurity.android.bcommon.di.CommonDiConst
import com.virgilsecurity.android.bcommon.util.currentScopeViewModel
import com.virgilsecurity.android.feature_channels_list.R
import com.virgilsecurity.android.feature_channels_list.viewmodel.list.ChannelsVM
import com.virgilsecurity.android.feature_channels_list.viewslice.list.ChannelsSlice
import com.virgilsecurity.android.feature_channels_list.viewslice.list.adapter.ChannelItem
import com.virgilsecurity.android.feature_channels_list.viewslice.state.StateSliceChannels
import com.virgilsecurity.android.feature_channels_list.viewslice.toolbar.ToolbarSliceChannelsList
import org.koin.core.inject
import org.koin.core.qualifier.named


/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    7/5/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * ChannelsListController
 */
class ChannelsListController() : BaseController() {

    override val layoutResourceId: Int = R.layout.controller_channels_list

    private val userProperties: UserProperties by inject()
    private val diffCallback: DiffCallback<ChannelMeta>
            by inject(named(CommonDiConst.KEY_DIFF_CALLBACK_CHANNEL_META))
    private val itemDecorator: RecyclerView.ItemDecoration by inject()
    private val viewModel: ChannelsVM by currentScopeViewModel()

    private lateinit var openDrawer: () -> Unit
    private lateinit var openChannel: (ChannelMeta) -> Unit
    private lateinit var mldToolbarSlice: MutableLiveData<ToolbarSliceChannelsList.Action>
    private lateinit var toolbarSlice: ToolbarSliceChannelsList
    private lateinit var mldChannelsSlice: MutableLiveData<ChannelsSlice.Action>
    private lateinit var channelsSlice: ChannelsSlice
    private lateinit var stateSlice: StateSliceChannels
    private lateinit var adapter: DelegateAdapter<ChannelMeta>

    constructor(openDrawer: () -> Unit,
                openChannel: (interlocutor: ChannelMeta) -> Unit) : this() {
        this.openDrawer = openDrawer
        this.openChannel = openChannel
    }

    override fun init(containerView: View) {
        this.mldToolbarSlice = MutableLiveData()
        this.mldChannelsSlice = MutableLiveData()
        val channelItem = ChannelItem(mldChannelsSlice, userProperties)
                as DelegateAdapterItem<BaseViewHolder<ChannelMeta>, ChannelMeta>
        this.adapter = DelegateAdapter.Builder<ChannelMeta>()
                .add(channelItem)
                .diffCallback(diffCallback)
                .build()
    }

    override fun initViewSlices(window: Window) {
        this.toolbarSlice = ToolbarSliceChannelsList(mldToolbarSlice)
        val layoutManager = LinearLayoutManager(activity)
        this.channelsSlice = ChannelsSlice(mldChannelsSlice, adapter, itemDecorator, layoutManager)
        this.stateSlice = StateSliceChannels()

        toolbarSlice.init(lifecycle, window)
        channelsSlice.init(lifecycle, window)
        stateSlice.init(lifecycle, window)
    }

    override fun setupViewSlices(view: View) {}

    override fun setupVSActionObservers() {
        observe(toolbarSlice.getAction(), ::onToolbarActionChanged)
        observe(channelsSlice.getAction(), ::onChannelsActionChanged)
    }

    private fun onChannelsActionChanged(action: ChannelsSlice.Action) = when (action) {
        is ChannelsSlice.Action.ChannelClicked -> openChannel(action.channel)
        ChannelsSlice.Action.Idle -> Unit
    }

    override fun setupVMStateObservers() {
        observe(viewModel.getState(), ::onStateChanged)
    }

    override fun initData() {
        viewModel.channels()
    }

    private fun onToolbarActionChanged(action: ToolbarSliceChannelsList.Action) = when (action) {
        ToolbarSliceChannelsList.Action.HamburgerClicked -> openDrawer()
        ToolbarSliceChannelsList.Action.Idle -> Unit
    }

    private fun onStateChanged(state: ChannelsVM.State): Unit = when (state) {
        is ChannelsVM.State.ChannelsLoaded -> channelsSlice.showChannels(state.channels)
        is ChannelsVM.State.ChannelAdded -> Unit
        ChannelsVM.State.ShowEmpty -> stateSlice.showEmpty()
        ChannelsVM.State.ShowContent -> stateSlice.showContent()
        ChannelsVM.State.ShowLoading -> stateSlice.showLoading()
        ChannelsVM.State.ShowError -> stateSlice.showError()
    }

    companion object {
        const val KEY_CHANNELS_LIST_CONTROLLER = "KEY_CHANNELS_LIST_CONTROLLER"
        private val TAG = ChannelsListController::class.java.simpleName
    }
}
