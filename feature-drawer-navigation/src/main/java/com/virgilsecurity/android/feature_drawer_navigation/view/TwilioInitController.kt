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

package com.virgilsecurity.android.feature_drawer_navigation.view

import android.view.View
import com.virgilsecurity.android.base.data.model.User
import com.virgilsecurity.android.base.extension.observe
import com.virgilsecurity.android.base.view.BaseController
import com.virgilsecurity.android.common.viewslice.StateSlice
import com.virgilsecurity.android.feature_drawer_navigation.R
import com.virgilsecurity.android.feature_drawer_navigation.di.Const.CONTEXT_TWILIO_INIT
import com.virgilsecurity.android.feature_drawer_navigation.di.Const.STATE_SLICE_TWILIO_INIT
import com.virgilsecurity.android.feature_drawer_navigation.viewmodel.InitTwilioVM
import com.virgilsecurity.android.feature_drawer_navigation.viewslice.twilioInit.interaction.TwilioInitSlice
import org.koin.standalone.inject

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
 * TwilioInitController
 */
class TwilioInitController() : BaseController() {

    override val layoutResourceId: Int = R.layout.controller_twilio_init
    override val koinContextName: String? = CONTEXT_TWILIO_INIT

    private val twilioInitSlice: TwilioInitSlice by inject()
    private val stateSlice: StateSlice by inject(STATE_SLICE_TWILIO_INIT)
    private val viewModel: InitTwilioVM by inject()

    private lateinit var user: User
    private lateinit var initSuccess: () -> Unit

    constructor(user: User, initSuccess: () -> Unit) : this() {
        this.user = user
        this.initSuccess = initSuccess
    }

    override fun init() {
        viewModel.initChatClient(user.identity)
    }

    override fun initViewSlices(view: View) {
        twilioInitSlice.init(lifecycle, view)
        stateSlice.init(lifecycle, view)
    }

    override fun setupViewSlices(view: View) { }

    private fun onActionChanged(action: TwilioInitSlice.Action) = when (action) {
        TwilioInitSlice.Action.RetryClicked -> viewModel.initChatClient(user.identity)
        TwilioInitSlice.Action.Idle -> Unit
    }

    override fun setupVSActionObservers() {
        observe(twilioInitSlice.getAction(), ::onActionChanged)
    }

    override fun setupVMStateObservers() {
        observe(viewModel.getState(), ::onStateChanged)
    }

    override fun initData() {}

    private fun onStateChanged(state: InitTwilioVM.State) = when (state) {
        InitTwilioVM.State.InitSuccess -> initSuccess()
        InitTwilioVM.State.ShowLoading -> stateSlice.showLoading()
        InitTwilioVM.State.ShowContent -> stateSlice.showContent()
        InitTwilioVM.State.ShowError -> stateSlice.showError()
    }

    override fun handleBack(): Boolean {
        return if (viewModel.getState().value != InitTwilioVM.State.ShowLoading)
            true
        else
            super.handleBack()
    }
}