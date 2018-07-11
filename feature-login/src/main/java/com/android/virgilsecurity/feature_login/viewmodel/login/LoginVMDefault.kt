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

package com.android.virgilsecurity.feature_login.viewmodel.login

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import com.android.virgilsecurity.feature_login.domain.login.LoadUsersDo

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    6/25/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * LoginVMDefault
 */

class LoginVMDefault(
        private val state: MediatorLiveData<State>,
        private val loadUsersDo: LoadUsersDo
) : LoginVM() {

    init {
        state.addSource(loadUsersDo.getLiveData(), ::onLoadUsersResult)
    }

    override fun onCleared() = loadUsersDo.cleanUp()

    override fun getState(): LiveData<State> = state

    override fun users() {
        state.value = State.ShowLoading // TODO add debounce to avoid blinking if users are loaded fast
        loadUsersDo.execute()
    }

    private fun onLoadUsersResult(result: LoadUsersDo.Result?) {
        when (result) {
            is LoadUsersDo.Result.OnSuccess -> {
                if (result.users.isNotEmpty()) {
                    state.value = State.UsersLoaded(
                        result.users)
                    state.value = State.ShowContent
                } else {
                    state.value = State.ShowNoUsers
                }
            }
            is LoadUsersDo.Result.OnError -> state.value = State.ShowError
        }
    }
}