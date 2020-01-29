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

package com.virgilsecurity.android.feature_login.view

import LoginDiConst.STATE_SLICE_LOGIN
import android.view.View
import com.virgilsecurity.android.base.data.model.User
import com.virgilsecurity.android.base.extension.observe
import com.virgilsecurity.android.base.view.BaseController
import com.virgilsecurity.android.common.util.UiUtils
import com.virgilsecurity.android.common.viewslice.StateSlice
import com.virgilsecurity.android.feature_login.R
import com.virgilsecurity.android.feature_login.viewmodel.login.LoginVM
import com.virgilsecurity.android.feature_login.viewslice.login.list.ViewPagerSlice
import kotlinx.android.synthetic.main.controller_login.*
import org.koin.core.inject
import org.koin.core.qualifier.named

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
    private val stateSlice: StateSlice by inject(named(STATE_SLICE_LOGIN))
    private val viewModel: LoginVM by inject()

    private lateinit var login: (User) -> Unit
    private lateinit var registration: () -> Unit

    constructor(login: (User) -> Unit, registration: () -> Unit) : this() {
        this.login = login
        this.registration = registration
    }

    override fun init() {
        initViewCallbacks()
    }

    private fun initViewCallbacks() {
        tvCreateAccount.setOnClickListener { registration() }
    }

    override fun initViewSlices(view: View) {
        viewPagerSlice.init(lifecycle, view)
        stateSlice.init(lifecycle, view)
    }

    override fun setupViewSlices(view: View) {}

    override fun setupVSActionObservers() =
            observe(viewPagerSlice.getAction(), ::onActionChanged)

    override fun setupVMStateObservers() = observe(viewModel.getState(), ::onStateChanged)

    override fun initData() {}

    private fun onStateChanged(state: LoginVM.State) = when (state) {
        is LoginVM.State.UsersLoaded -> {
            viewPagerSlice.showUsers(state.users)
            if (state.users.size > 4)
                viewPagerSlice.updateIndicator()
            stateSlice.showContent()
        }
        LoginVM.State.ShowLoading -> stateSlice.showLoading()
        LoginVM.State.ShowError -> stateSlice.showError()
        LoginVM.State.LoginError -> {
            UiUtils.toast(this, "Login error. Try again")
            stateSlice.showContent()
        }
        else -> Unit
    }

    private fun onActionChanged(action: ViewPagerSlice.Action) = when (action) {
        is ViewPagerSlice.Action.UserClicked -> login(action.user)
        ViewPagerSlice.Action.Idle -> Unit
    }

    companion object {
        const val KEY_LOGIN_CONTROLLER = "KEY_LOGIN_CONTROLLER"
    }
}
