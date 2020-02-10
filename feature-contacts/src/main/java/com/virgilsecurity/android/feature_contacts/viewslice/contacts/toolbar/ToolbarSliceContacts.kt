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

package com.virgilsecurity.android.feature_contacts.viewslice.contacts.toolbar

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import com.virgilsecurity.android.base.viewslice.BaseViewSlice
import com.virgilsecurity.android.common.view.Toolbar
import com.virgilsecurity.android.feature_contacts.R

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    7/17/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * ToolbarSliceContacts
 */
class ToolbarSliceContacts(
        private val actionLiveData: MutableLiveData<Action>
) : BaseViewSlice() {

    private lateinit var toolbar: Toolbar

    override fun setupViews() {
        with(window) {
            this@ToolbarSliceContacts.toolbar = findViewById(R.id.toolbarContacts)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        setupToolbar()
    }

    private fun setupToolbar() {
        toolbar.setTitle(resources.getString(R.string.messenger))

        toolbar.showHamburgerButton()
        toolbar.showAddPersonButton()

        toolbar.setOnToolbarItemClickListener {
            when (it.id) {
                R.id.ivHamburger -> {
                    actionLiveData.value = Action.HamburgerClicked
                    actionLiveData.value = Action.Idle
                }
                R.id.ivAddPerson -> {
                    actionLiveData.value = Action.AddClicked
                    actionLiveData.value = Action.Idle
                }
            }
        }
    }

    fun getAction(): LiveData<Action> = actionLiveData

    sealed class Action {
        object HamburgerClicked : Action()
        object AddClicked : Action()
        object Idle : Action()
    }
}