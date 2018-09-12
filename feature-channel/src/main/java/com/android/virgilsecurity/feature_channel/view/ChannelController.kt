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

package com.android.virgilsecurity.feature_channel.view

import android.view.View
import com.android.virgilsecurity.base.data.api.MessagesApi
import com.android.virgilsecurity.base.data.model.ChannelInfo
import com.android.virgilsecurity.base.data.model.MessageInfo
import com.android.virgilsecurity.base.data.properties.UserProperties
import com.android.virgilsecurity.base.extension.observe
import com.android.virgilsecurity.base.extension.toMessageInfo
import com.android.virgilsecurity.base.view.BaseController
import com.android.virgilsecurity.common.util.UiUtils
import com.android.virgilsecurity.common.viewslice.StateSliceEmptyable
import com.android.virgilsecurity.feature_channel.R
import com.android.virgilsecurity.feature_channel.di.Const.CONTEXT_CHANNEL
import com.android.virgilsecurity.feature_channel.di.Const.STATE_CHANNEL
import com.android.virgilsecurity.feature_channel.di.Const.TOOLBAR_CHANNEL
import com.android.virgilsecurity.feature_channel.viewmodel.ChannelVM
import com.android.virgilsecurity.feature_channel.viewslice.list.ChannelSlice
import com.android.virgilsecurity.feature_channel.viewslice.toolbar.ToolbarSlice
import kotlinx.android.synthetic.main.controller_channel.*
import org.koin.standalone.inject

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    8/2/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * ChannelController
 */
class ChannelController() : BaseController() {

    override val layoutResourceId: Int = R.layout.controller_channel
    override val koinContextName: String? = CONTEXT_CHANNEL

    private val toolbarSlice: ToolbarSlice by inject(TOOLBAR_CHANNEL)
    private val channelSlice: ChannelSlice by inject()
    private val stateSlice: StateSliceEmptyable by inject(STATE_CHANNEL)
    private val viewModel: ChannelVM by inject()
    private val userProperties: UserProperties by inject()

    private lateinit var interlocutor: String
    private lateinit var channel: ChannelInfo

    constructor(channel: ChannelInfo) : this() {
        this.interlocutor = channel.interlocutor
        this.channel = channel
    }

    override fun init() {
        initViewCallbacks()
    }

    private fun initViewCallbacks() {
        ivSend.setOnClickListener {
            if (etMessage.text.isNotBlank()) {
                viewModel.showMessagePreview(etMessage.text.toString()) // To improve user experience
                viewModel.sendMessage(etMessage.text.toString())
            }
        }
    }

    override fun initViewSlices(view: View) {
        toolbarSlice.init(lifecycle, view)
        channelSlice.init(lifecycle, view)
        stateSlice.init(lifecycle, view)
    }

    override fun setupViewSlices(view: View) {
        toolbarSlice.setTitle(channel.localizedInterlocutor(userProperties))
    }

    override fun setupVSActionObservers() {
        observe(toolbarSlice.getAction(), ::onToolbarActionChanged)
        observe(channelSlice.getAction(), ::onMessageActionChanged)
    }

    private fun onMessageActionChanged(action: ChannelSlice.Action) = when (action) {
        is ChannelSlice.Action.MessageClicked -> {
//            viewModel.copyMessage(action.message.body, activity!!)
        }
        is ChannelSlice.Action.MessageLongClicked -> Unit
        ChannelSlice.Action.Idle -> Unit
    }

    override fun setupVMStateObservers() {
        observe(viewModel.getState(), ::onStateChanged)
    }

    override fun initData() {
        viewModel.messages(channel.sid)
    }

    private fun onToolbarActionChanged(action: ToolbarSlice.Action) = when (action) {
        ToolbarSlice.Action.BackClicked -> {
            hideKeyboard()
            backPressed()
        }
        ToolbarSlice.Action.Idle -> Unit
    }

    private fun onStateChanged(state: ChannelVM.State): Unit = when (state) {
        is ChannelVM.State.MessageLoaded -> channelSlice.showMessages(state.messages)
        ChannelVM.State.ShowEmpty -> stateSlice.showEmpty()
        ChannelVM.State.ShowContent -> stateSlice.showContent()
        ChannelVM.State.ShowLoading -> stateSlice.showLoading()
        ChannelVM.State.ShowError -> stateSlice.showError()
        is ChannelVM.State.ChannelChanged -> onChannelChanged(state.change)
        is ChannelVM.State.MessageSent -> Unit
        is ChannelVM.State.MessagePreviewAdded -> {
            etMessage.text.clear()
            channelSlice.addMessage(state.message)
            stateSlice.showContent()
        }
        ChannelVM.State.MessageCopied -> UiUtils.toast(this, "Message copied")
        ChannelVM.State.MessageIsTooLong -> UiUtils.toast(this, "Message is too long")
    }

    private fun onChannelChanged(change: MessagesApi.ChannelChanges) = when (change) {
        is MessagesApi.ChannelChanges.MemberDeleted -> Unit
        is MessagesApi.ChannelChanges.TypingEnded -> Unit
        is MessagesApi.ChannelChanges.MessageAdded -> {
            if (change.message!!.toMessageInfo().sender != userProperties.currentUser!!.identity)
                channelSlice.addMessage(change.message!!.toMessageInfo())

            stateSlice.showContent()
        }
        is MessagesApi.ChannelChanges.MessageDeleted -> Unit
        is MessagesApi.ChannelChanges.MemberAdded -> Unit
        is MessagesApi.ChannelChanges.TypingStarted -> Unit
        is MessagesApi.ChannelChanges.SynchronizationChanged -> Unit
        is MessagesApi.ChannelChanges.MessageUpdated -> Unit
        is MessagesApi.ChannelChanges.MemberUpdated -> Unit
        is MessagesApi.ChannelChanges.Exception -> Unit
    }

    private fun backPressed() {
        router.popCurrentController()
    }

    companion object {
        const val KEY_CHANNEL_CONTROLLER = "KEY_CHANNEL_CONTROLLER"
        private val TAG = ChannelController::class.java.simpleName
    }
}
