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

package com.virgilsecurity.android.feature_channels_list.viewmodel.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.virgilsecurity.android.base.data.api.ChannelsApi
import com.virgilsecurity.android.feature_channels_list.domain.list.GetChannelsDo
import com.virgilsecurity.android.feature_channels_list.domain.list.ObserveChannelsListChangeDo

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
        private val observeChannelsListChangeDo: ObserveChannelsListChangeDo
) : ChannelsVM() {

    init {
        state.addSource(getChannelsDo.getLiveData(), ::onLoadContactsResult)
        state.addSource(observeChannelsListChangeDo.getLiveData(), ::onContactsChanged)
    }

    override fun getState(): LiveData<State> = state

    override fun channels() {
        state.value = State.ShowLoading
        getChannelsDo.execute()
    }

    override fun observeChannelsChanges() = observeChannelsListChangeDo.execute()

    override fun onCleared() {
        getChannelsDo.cleanUp()
        observeChannelsListChangeDo.cleanUp()
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
        state.value = State.ChannelsListChanged(result!!)
    }
}
