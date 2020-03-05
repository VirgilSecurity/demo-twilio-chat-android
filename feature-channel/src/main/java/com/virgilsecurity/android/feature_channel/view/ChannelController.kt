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

package com.virgilsecurity.android.feature_channel.view

import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.virgilsecurity.android.base.data.model.ChannelMeta
import com.virgilsecurity.android.base.data.model.MessageMeta
import com.virgilsecurity.android.base.data.properties.UserProperties
import com.virgilsecurity.android.base.extension.observe
import com.virgilsecurity.android.base.view.adapter.BaseViewHolder
import com.virgilsecurity.android.base.view.adapter.DelegateAdapter
import com.virgilsecurity.android.base.view.adapter.DelegateAdapterItem
import com.virgilsecurity.android.base.view.adapter.DiffCallback
import com.virgilsecurity.android.base.view.controller.BaseController
import com.virgilsecurity.android.bcommon.data.helper.virgil.VirgilHelper
import com.virgilsecurity.android.bcommon.di.CommonDiConst.KEY_DIFF_CALLBACK_MESSAGE_META
import com.virgilsecurity.android.bcommon.util.UiUtils
import com.virgilsecurity.android.bcommon.util.currentScope
import com.virgilsecurity.android.feature_channel.R
import com.virgilsecurity.android.feature_channel.data.interactor.model.ChannelItem
import com.virgilsecurity.android.feature_channel.viewmodel.ChannelVM
import com.virgilsecurity.android.feature_channel.viewslice.channel.ChannelSlice
import com.virgilsecurity.android.feature_channel.viewslice.channel.adapter.DateItem
import com.virgilsecurity.android.feature_channel.viewslice.channel.adapter.MessageItemInDevelopment
import com.virgilsecurity.android.feature_channel.viewslice.channel.adapter.MessageItemMe
import com.virgilsecurity.android.feature_channel.viewslice.channel.adapter.MessageItemYou
import com.virgilsecurity.android.feature_channel.viewslice.state.StateSliceChannel
import com.virgilsecurity.android.feature_channel.viewslice.toolbar.ToolbarSliceChannel
import org.koin.core.inject
import org.koin.core.qualifier.named

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

    private val diffCallback: DiffCallback<ChannelItem> by inject(named(
        KEY_DIFF_CALLBACK_MESSAGE_META))
    private val userProperties: UserProperties by inject()
    private val virgilHelper: VirgilHelper by inject()
    private val viewModel: ChannelVM by currentScope.inject()

    private lateinit var interlocutor: String
    private lateinit var channel: ChannelMeta
    private lateinit var mldToolbarSlice: MutableLiveData<ToolbarSliceChannel.Action>
    private lateinit var toolbarSlice: ToolbarSliceChannel
    private lateinit var mldChannelSlice: MutableLiveData<ChannelSlice.Action>
    private lateinit var adapter: DelegateAdapter<ChannelItem>
    private lateinit var channelSlice: ChannelSlice
    private lateinit var stateSlice: StateSliceChannel

    private lateinit var etMessage: EditText
    private lateinit var ivSend: ImageView

    constructor(channel: ChannelMeta) : this() {
        this.interlocutor = channel.interlocutor
        this.channel = channel
    }

    override fun init(containerView: View) {
        with(containerView) {
            this@ChannelController.etMessage = findViewById(R.id.etMessage)
            this@ChannelController.ivSend = findViewById(R.id.ivSend)

            ivSend.setOnClickListener {
                if (etMessage.text.isNotBlank()) {
                    viewModel.showMessagePreview(etMessage.text.toString()) // To improve user experience
                    viewModel.sendMessage(etMessage.text.toString(), channel)
                }
            }
        }

        this.mldToolbarSlice = MutableLiveData()
        this.toolbarSlice = ToolbarSliceChannel(mldToolbarSlice)
        this.mldChannelSlice = MutableLiveData()
        val messageMe = MessageItemMe(mldChannelSlice, userProperties, virgilHelper)
                as DelegateAdapterItem<BaseViewHolder<ChannelItem>, ChannelItem>
        val messageYou = MessageItemYou(mldChannelSlice, userProperties, virgilHelper)
                as DelegateAdapterItem<BaseViewHolder<ChannelItem>, ChannelItem>
        val messageInDevelopment = MessageItemInDevelopment()
                as DelegateAdapterItem<BaseViewHolder<ChannelItem>, ChannelItem>
        val dateItem = DateItem()
                as DelegateAdapterItem<BaseViewHolder<ChannelItem>, ChannelItem>
        this.adapter = DelegateAdapter.Builder<ChannelItem>()
                .add(messageMe)
                .add(messageYou)
                .add(messageInDevelopment)
                .add(dateItem)
                .diffCallback(diffCallback)
                .build()
        val layoutManager = LinearLayoutManager(activity)
        this.channelSlice = ChannelSlice(mldChannelSlice, adapter, layoutManager)
        this.stateSlice = StateSliceChannel()
    }

    override fun initViewSlices(window: Window) {
        toolbarSlice.init(lifecycle, window)
        channelSlice.init(lifecycle, window)
        stateSlice.init(lifecycle, window)
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
        viewModel.messages(channel)
    }

    private fun onToolbarActionChanged(action: ToolbarSliceChannel.Action) = when (action) {
        ToolbarSliceChannel.Action.BackClicked -> {
            hideKeyboard()
            backPressed()
        }
        ToolbarSliceChannel.Action.Idle -> Unit
    }

    private fun onStateChanged(state: ChannelVM.State): Unit = when (state) {
        is ChannelVM.State.MessageLoaded -> channelSlice.showMessages(state.messages)
        ChannelVM.State.ShowEmpty -> stateSlice.showEmpty()
        ChannelVM.State.ShowContent -> stateSlice.showContent()
        ChannelVM.State.ShowLoading -> stateSlice.showLoading()
        ChannelVM.State.ShowError -> stateSlice.showError()
        is ChannelVM.State.MessageSent -> Unit
        is ChannelVM.State.MessagePreviewAdded -> {
            // TODO add message preview without blinking (msgs update right after preview is shown)
            etMessage.text.clear()
//            channelSlice.addMessage(state.message)
//            stateSlice.showContent()
        }
        ChannelVM.State.MessageCopied -> UiUtils.toast(this, "Message copied")
        ChannelVM.State.MessageIsTooLong -> UiUtils.toast(this, "Message is too long")
    }


    private fun backPressed() {
        router.popCurrentController()
    }

    companion object {
        const val KEY_CHANNEL_CONTROLLER = "KEY_CHANNEL_CONTROLLER"
        private val TAG = ChannelController::class.java.simpleName
    }
}
