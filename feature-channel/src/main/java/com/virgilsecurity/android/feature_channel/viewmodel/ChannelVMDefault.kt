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

package com.virgilsecurity.android.feature_channel.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.virgilsecurity.android.base.data.api.MessagesApi
import com.virgilsecurity.android.base.data.model.ChannelMeta
import com.virgilsecurity.android.base.data.properties.UserProperties
import com.virgilsecurity.android.feature_channel.domain.*
import com.virgilsecurity.sdk.cards.Card
import com.virgilsecurity.sdk.crypto.VirgilPublicKey

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    8/9/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * ChannelVMDefault
 */
class ChannelVMDefault(
        private val state: MediatorLiveData<State>,
        private val getMessagesDo: GetMessagesDo,
        private val sendMessageDo: SendMessageDo,
        private val getCardDo: GetCardDo,
        private val userProperties: UserProperties,
        private val showMessagePreviewDo: ShowMessagePreviewDo,
        private val copyMessageDo: CopyMessageDo
) : ChannelVM() {

    private lateinit var channel: ChannelMeta
    private var cards = mutableListOf<Card>()

    init {
        state.addSource(getMessagesDo.getLiveData(), ::onLoadMessagesResult)
        state.addSource(sendMessageDo.getLiveData(), ::onMessageSentResult)
        state.addSource(getCardDo.getLiveData(), ::onLoadCardResult)
        state.addSource(showMessagePreviewDo.getLiveData(), ::onShowMessagePreviewResult)
        state.addSource(copyMessageDo.getLiveData(), ::onCopyMessageResult)
    }

    override fun getState(): LiveData<State> = state

    override fun messages(channelMeta: ChannelMeta) {
        state.value = State.ShowLoading
        getCardDo.execute(channelMeta.localizedInterlocutor(userProperties))
    }

    override fun sendMessage(body: String) =
            sendMessageDo.execute(channel,
                                  body,
                                  cards.map { it.publicKey as VirgilPublicKey })

    override fun showMessagePreview(body: String) =
            showMessagePreviewDo.execute(body)

    override fun copyMessage(body: String?, context: Context) =
            copyMessageDo.execute(body, context)

    override fun onCleared() {
        getMessagesDo.cleanUp()
        sendMessageDo.cleanUp()
        getCardDo.cleanUp()
        showMessagePreviewDo.cleanUp()
        copyMessageDo.cleanUp()
    }

    private fun onLoadMessagesResult(result: GetMessagesDo.Result?) {
        when (result) {
            is GetMessagesDo.Result.OnSuccess -> {
                state.value = State.MessageLoaded(result.messages)

                if (result.messages.isNotEmpty())
                    state.value = State.ShowContent
            }
            is GetMessagesDo.Result.OnError -> state.value = State.ShowError
            GetMessagesDo.Result.OnEmpty -> state.value = State.ShowEmpty
        }
    }

    private fun onMessageSentResult(result: SendMessageDo.Result?) {
        when (result) {
            is SendMessageDo.Result.OnSuccess -> state.value = State.MessageSent
            is SendMessageDo.Result.MessageIsTooLong -> state.value = State.MessageIsTooLong
            is SendMessageDo.Result.OnError -> state.value = State.ShowError
        }
    }

    private fun onLoadCardResult(result: GetCardDo.Result?) {
        when (result) {
            is GetCardDo.Result.OnSuccess -> {
                cards.add(userProperties.currentUser!!.card())
                cards.add(result.card)

                getMessagesDo.execute(channel)
            }
            is GetCardDo.Result.OnError -> State.ShowError
        }
    }

    private fun onShowMessagePreviewResult(result: ShowMessagePreviewDo.Result?) {
        when (result) {
            is ShowMessagePreviewDo.Result.OnSuccess -> {
                state.value = State.MessagePreviewAdded(result.message)
            }
            ShowMessagePreviewDo.Result.MessageIsTooLong -> state.value = State.MessageIsTooLong
            is ShowMessagePreviewDo.Result.OnError -> State.ShowError
        }
    }


    private fun onCopyMessageResult(result: CopyMessageDo.Result?) {
        when (result) {
            is CopyMessageDo.Result.OnSuccess -> state.value = State.MessageCopied
            is CopyMessageDo.Result.OnError -> State.ShowError
        }
    }
}