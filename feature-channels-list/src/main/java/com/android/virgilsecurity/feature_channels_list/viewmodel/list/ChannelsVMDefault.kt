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

package com.android.virgilsecurity.feature_channels_list.viewmodel.list

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import com.android.virgilsecurity.base.data.api.ChannelsApi
import com.android.virgilsecurity.feature_channels_list.domain.list.GetChannelsDo
import com.android.virgilsecurity.feature_channels_list.domain.list.JoinChannelDo
import com.android.virgilsecurity.feature_channels_list.domain.list.ObserveChannelsChangeDo
import com.twilio.chat.Channel

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
 * ChannelsVMDefault
 */
class ChannelsVMDefault(
        private val state: MediatorLiveData<State>,
        private val getChannelsDo: GetChannelsDo,
        private val observeChannelsChangeDo: ObserveChannelsChangeDo,
        private val joinChannelDo: JoinChannelDo
) : ChannelsVM() {

    init {
        state.addSource(getChannelsDo.getLiveData(), ::onLoadContactsResult)
        state.addSource(observeChannelsChangeDo.getLiveData(), ::onContactsChanged)
        state.addSource(joinChannelDo.getLiveData(), ::onJoinChannelResult)
    }

    override fun getState(): LiveData<State> = state

    override fun channels() {
        state.value = State.ShowLoading
        getChannelsDo.execute()
    }

    override fun observeChannelsChanges() = observeChannelsChangeDo.execute()

    override fun joinChannel(channel: Channel) {
        joinChannelDo.execute(channel)
    }

    override fun onCleared() {
        getChannelsDo.cleanUp()
        observeChannelsChangeDo.cleanUp()
    }

    private fun onLoadContactsResult(result: GetChannelsDo.Result?) {
        when (result) {
            is GetChannelsDo.Result.OnSuccess -> {
                state.value = State.ChannelsLoaded(result.contacts)

                if (result.contacts.isNotEmpty()) {
                    state.value = State.ShowContent
                }
            }
            is GetChannelsDo.Result.OnError -> state.value = State.ShowError
            GetChannelsDo.Result.OnEmpty -> state.value = State.ShowEmpty
        }
    }

    private fun onContactsChanged(result: ChannelsApi.ChannelsChanges?) {
        state.value = State.ChannelChanged(result!!)
    }

    private fun onJoinChannelResult(result: JoinChannelDo.Result?) {
        when (result) {
            is JoinChannelDo.Result.OnSuccess -> state.value = State.OnJoinSuccess(result.channel)
            is JoinChannelDo.Result.OnError -> state.value = State.ShowError
        }
    }
}