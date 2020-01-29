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
import com.twilio.chat.Channel
import com.virgilsecurity.android.base.data.api.ChannelsApi
import com.virgilsecurity.android.base.data.model.ChannelInfo
import com.virgilsecurity.android.base.extension.observe
import com.virgilsecurity.android.base.view.BaseControllerWithScope
import com.virgilsecurity.android.common.data.remote.channels.MapperToChannelInfo
import com.virgilsecurity.android.common.viewslice.StateSliceEmptyable
import com.virgilsecurity.android.feature_channels_list.R
import com.virgilsecurity.android.feature_channels_list.di.Const.STATE_CHANNELS
import com.virgilsecurity.android.feature_channels_list.di.Const.VM_CHANNELS_LIST
import com.virgilsecurity.android.feature_channels_list.viewmodel.list.ChannelsVM
import com.virgilsecurity.android.feature_channels_list.viewslice.list.ChannelsSlice
import com.virgilsecurity.android.feature_channels_list.viewslice.toolbar.ToolbarSlice
import io.reactivex.Single
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
class ChannelsListController() : BaseControllerWithScope() {

    override val layoutResourceId: Int = R.layout.controller_channels_list

    private val toolbarSlice: ToolbarSlice by inject()
    private val channelsSlice: ChannelsSlice by inject()
    private val stateSlice: StateSliceEmptyable by inject(named(STATE_CHANNELS))
    private val viewModel: ChannelsVM by inject(named(VM_CHANNELS_LIST))
    private val mapper: MapperToChannelInfo by inject()

    private lateinit var openDrawer: () -> Unit
    private lateinit var openChannel: (ChannelInfo) -> Unit

    constructor(openDrawer: () -> Unit,
                openChannel: (interlocutor: ChannelInfo) -> Unit) : this() {
        this.openDrawer = openDrawer
        this.openChannel = openChannel
    }

    override fun init() {}

    override fun initViewSlices(view: View) {
        toolbarSlice.init(lifecycle, view)
        channelsSlice.init(lifecycle, view)
        stateSlice.init(lifecycle, view)
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
        viewModel.observeChannelsChanges()
    }

    private fun onToolbarActionChanged(action: ToolbarSlice.Action) = when (action) {
        ToolbarSlice.Action.HamburgerClicked -> openDrawer()
        ToolbarSlice.Action.Idle -> Unit
    }

    private fun onStateChanged(state: ChannelsVM.State): Unit = when (state) {
        is ChannelsVM.State.ChannelsLoaded -> channelsSlice.showChannels(state.channels)
        ChannelsVM.State.ShowEmpty -> stateSlice.showEmpty()
        ChannelsVM.State.ShowContent -> stateSlice.showContent()
        ChannelsVM.State.ShowLoading -> stateSlice.showLoading()
        ChannelsVM.State.ShowError -> stateSlice.showError()
        is ChannelsVM.State.ChannelsListChanged -> onChannelChanged(state.change)
    }

    private fun onChannelChanged(change: ChannelsApi.ChannelsChanges) = when (change) {
        is ChannelsApi.ChannelsChanges.ChannelDeleted -> Unit
        is ChannelsApi.ChannelsChanges.InvitedToChannelNotification -> Unit
        is ChannelsApi.ChannelsChanges.ClientSynchronization -> Unit
        ChannelsApi.ChannelsChanges.NotificationSubscribed -> Unit
        is ChannelsApi.ChannelsChanges.UserSubscribed -> Unit
        is ChannelsApi.ChannelsChanges.ChannelUpdated -> Unit
        is ChannelsApi.ChannelsChanges.RemovedFromChannelNotification -> Unit
        is ChannelsApi.ChannelsChanges.NotificationFailed -> Unit
        is ChannelsApi.ChannelsChanges.ChannelJoined -> Unit
        is ChannelsApi.ChannelsChanges.ChannelAdded -> Unit
        is ChannelsApi.ChannelsChanges.ChannelSynchronizationChange -> Unit
        is ChannelsApi.ChannelsChanges.UserUnsubscribed -> Unit
        is ChannelsApi.ChannelsChanges.AddedToChannelNotification -> Unit
        is ChannelsApi.ChannelsChanges.ChannelInvited -> showChannel(change.channel!!)
        is ChannelsApi.ChannelsChanges.NewMessageNotification -> Unit
        is ChannelsApi.ChannelsChanges.ConnectionStateChange -> Unit
        is ChannelsApi.ChannelsChanges.Error -> Unit
        is ChannelsApi.ChannelsChanges.UserUpdated -> Unit
        is ChannelsApi.ChannelsChanges.Exception -> Unit
    }

    private fun showChannel(channel: Channel) {
        Single.just(channel)
                .map(mapper::mapChannel)
                .subscribe { channelInfo ->
                    channelsSlice.addChannel(channelInfo)
                    stateSlice.showContent()
                }
    }

    companion object {
        const val KEY_CHANNELS_LIST_CONTROLLER = "KEY_CHANNELS_LIST_CONTROLLER"
        private val TAG = ChannelsListController::class.java.simpleName
    }
}
