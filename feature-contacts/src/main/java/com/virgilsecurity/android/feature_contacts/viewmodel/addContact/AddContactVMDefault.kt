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

package com.virgilsecurity.android.feature_contacts.viewmodel.addContact

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.databinding.ObservableField
import com.virgilsecurity.android.base.data.properties.UserProperties
import com.virgilsecurity.android.base.extension.addOnPropertyChanged
import com.virgilsecurity.android.feature_contacts.domain.addContact.AddContactDo
import io.reactivex.disposables.Disposable

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    8/3/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * AddContactVMDefault
 */
class AddContactVMDefault(
        private val state: MediatorLiveData<State>,
        private val addContactDo: AddContactDo,
        private val userProperties: UserProperties
) : AddContactVM() {

    val usernameField = ObservableField<String>()
    private val propertyChangedCallback: Disposable

    init {
        state.addSource(addContactDo.getLiveData(), ::onAddContactResult)
        propertyChangedCallback = usernameField.addOnPropertyChanged {
            onUsernameChanged(usernameField.get())
        }
    }

    override fun getState(): LiveData<State> = state

    override fun addContact(identity: String) {
        state.value = State.ShowLoading
        addContactDo.execute(identity)
    }

    override fun onCleared() {
        propertyChangedCallback.dispose()
        addContactDo.cleanUp()
    }

    private fun onAddContactResult(result: AddContactDo.Result?) {
        when (result) {
            is AddContactDo.Result.OnSuccess -> state.value = State.ContactAdded(result.channel)
            AddContactDo.Result.NoSuchUser -> state.value = State.NoSuchUser
            is AddContactDo.Result.OnError -> state.value = State.ShowError
            AddContactDo.Result.UserAlreadyAdded -> state.value = State.UserAlreadyAdded
        }
    }

    private fun onUsernameChanged(username: String?) {
        username?.toLowerCase()?.run {
            when {
                this.isEmpty() ->
                    state.value = State.UsernameInvalid(AddContactVM.KEY_USERNAME_EMPTY)
                this.length < AddContactVM.MIN_LENGTH ->
                    state.value = State.UsernameInvalid(AddContactVM.KEY_USERNAME_SHORT)
                this.length > AddContactVM.MAX_LENGTH ->
                    state.value = State.UsernameInvalid(AddContactVM.KEY_USERNAME_LONG)
                this == userProperties.currentUser!!.identity ->
                    state.value = State.UsernameInvalid(AddContactVM.KEY_YOUR_OWN_USERNAME)
                else -> state.value = State.UsernameConsistent
            }
        }
    }
}
