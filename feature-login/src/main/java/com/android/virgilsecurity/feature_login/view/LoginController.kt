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

package com.android.virgilsecurity.feature_login.view

import android.view.View
import com.android.virgilsecurity.base.data.model.User
import com.android.virgilsecurity.base.extension.inject
import com.android.virgilsecurity.base.extension.observe
import com.android.virgilsecurity.base.view.BaseController
import com.android.virgilsecurity.common.viewslice.StateSlice
import com.android.virgilsecurity.feature_login.R
import com.android.virgilsecurity.feature_login.viewmodel.login.LoginVM
import com.android.virgilsecurity.feature_login.viewslice.login.list.ViewPagerSlice
import com.android.virgilsecurity.feature_login.viewslice.login.list.ViewPagerSlice.Action

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    7/4/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * LoginController
 */
class LoginController() : BaseController() {

    override val layoutResourceId: Int = R.layout.controller_login

    private val viewPagerSlice: ViewPagerSlice by inject()
    private val stateSlice: StateSlice by inject()
    private val viewModel: LoginVM by inject()

    private lateinit var login: (User) -> Unit

    constructor(login: (User) -> Unit) : this() {
        this.login = login
    }

    override fun init() {}

    override fun initViewSlices(view: View) {
        viewPagerSlice.init(lifecycle, view)
        stateSlice.init(lifecycle, view)
    }

    override fun setupViewSlices(view: View) {
        // TODO Implement body or it will be empty ):
    }

    override fun setupVSActionObservers() =
            observe(viewPagerSlice.getAction()) { onActionChanged(it) }

    override fun setupVMStateObservers()
            = observe(viewModel.getState()) { onStateChanged(it) }

    private fun onStateChanged(state: LoginVM.State) = when (state) {
        is LoginVM.State.UsersLoaded -> viewPagerSlice.showUsers(state.users)
        LoginVM.State.ShowLoading -> stateSlice.showLoading()
        LoginVM.State.ShowContent -> stateSlice.showContent()
        LoginVM.State.ShowError -> stateSlice.showError()
        else -> stateSlice.showError()
    }

    private fun onActionChanged(action: Action) = when (action) {
        is Action.UserClicked -> login(action.user)
        Action.Idle -> Unit
    }

    companion object {
        const val KEY_LOGIN_CONTROLLER = "KEY_LOGIN_CONTROLLER"
    }
}