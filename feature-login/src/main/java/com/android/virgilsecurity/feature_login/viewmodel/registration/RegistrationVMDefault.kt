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

package com.android.virgilsecurity.feature_login.viewmodel.registration

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.databinding.Bindable
import android.databinding.Observable
import android.databinding.ObservableField
import com.android.virgilsecurity.common.util.UiUtils
import com.android.virgilsecurity.feature_login.domain.registration.SignUpDo

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    7/6/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * RegistrationVMDefault
 */
class RegistrationVMDefault(
        private val state: MediatorLiveData<State>,
        private val signUpDo: SignUpDo
) : RegistrationVM() {

    val usernameField = ObservableField<String>()
    private val propertyChangedCallback: Observable.OnPropertyChangedCallback

    init {
        state.addSource(signUpDo.getLiveData(), ::onRegistrationResult)
        propertyChangedCallback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                onUsernameChanged(usernameField.get())
            }
        }
        usernameField.addOnPropertyChangedCallback(propertyChangedCallback)
    }

    override fun getState(): LiveData<State> = state

    override fun registration(identity: String) {
//        state.value = State.ShowLoading
//        signUpDo.execute(identity)
        UiUtils.log("tag_tag", "reg test")
    }

    override fun onCleared() {
        usernameField.removeOnPropertyChangedCallback(propertyChangedCallback)
        signUpDo.cleanUp()
    }

    private fun onRegistrationResult(result: SignUpDo.Result?) {
        when (result) {
            is SignUpDo.Result.OnSuccess ->
                state.value = State.RegisteredSuccessfully(result.user)
            is SignUpDo.Result.OnError -> state.value = State.ShowError
        }
    }

    private fun onUsernameChanged(username: String?) {
        if (username != null) {
            when {
                username.isEmpty() ->
                    state.value = State.UsernameInvalid(RegistrationVM.KEY_USERNAME_EMPTY)
                username.length < RegistrationVM.MIN_LENGTH ->
                    state.value = State.UsernameInvalid(RegistrationVM.KEY_USERNAME_SHORT)
                username.length > RegistrationVM.MAX_LENGTH ->
                    state.value = State.UsernameInvalid(RegistrationVM.KEY_USERNAME_LONG)
                else -> state.value = State.UsernameConsistent
            }
        }
    }
}