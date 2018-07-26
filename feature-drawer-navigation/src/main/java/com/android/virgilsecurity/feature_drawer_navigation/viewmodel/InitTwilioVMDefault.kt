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

package com.android.virgilsecurity.feature_drawer_navigation.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import com.android.virgilsecurity.feature_drawer_navigation.domain.InitTwilioDo

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    7/26/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * InitTwilioVMDefault
 */
class InitTwilioVMDefault(
        private val state: MediatorLiveData<InitTwilioVM.State>,
        private val initTwilioDo: InitTwilioDo
) : InitTwilioVM() {

    init {
        state.addSource(initTwilioDo.getLiveData(), ::onInitTwilioResult)
    }

    override fun onCleared() = initTwilioDo.cleanUp()

    override fun getState(): LiveData<State> = state

    override fun initChatClient(identity: String) {
        state.value = InitTwilioVM.State.ShowLoading
        initTwilioDo.execute(identity)
    }

    private fun onInitTwilioResult(result: InitTwilioDo.Result?) {
        when (result) {
            is InitTwilioDo.Result.OnSuccess -> {
                    state.value = State.InitSuccess
                    state.value = State.ShowContent
            }
            is InitTwilioDo.Result.OnError -> state.value = State.ShowError
        }
    }
}