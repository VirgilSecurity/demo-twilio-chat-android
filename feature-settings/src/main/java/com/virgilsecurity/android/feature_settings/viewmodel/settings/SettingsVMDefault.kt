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

package com.virgilsecurity.android.feature_settings.viewmodel.settings

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import com.virgilsecurity.android.feature_settings.domain.DeleteAccountDo
import com.virgilsecurity.android.feature_settings.domain.LogoutDo

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    7/25/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * SettingsVMDefault
 */
class SettingsVMDefault(
        private val state: MediatorLiveData<State>,
        private val logoutDo: LogoutDo
) : SettingsVM() {

    init {
        state.addSource(logoutDo.getLiveData(), ::onLogoutResult)
    }

    override fun onCleared() {
        logoutDo.cleanUp()
    }

    override fun getState(): LiveData<State> = state

    override fun logout() {
        state.value = State.ShowLoading
        logoutDo.execute()
    }

    private fun onLogoutResult(result: LogoutDo.Result?) {
        when (result) {
            is LogoutDo.Result.OnSuccess -> {
                state.value = State.LogoutSuccessful
                state.value = State.Idle
            }
            is LogoutDo.Result.OnError -> {
                state.value = State.ShowError
                state.value = State.Idle
            }
        }
    }
}