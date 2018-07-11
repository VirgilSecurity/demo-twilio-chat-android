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

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.virgilsecurity.base.extension.observe
import com.android.virgilsecurity.base.view.BaseFragment
import com.android.virgilsecurity.common.viewslice.StateSlice
import com.android.virgilsecurity.feature_login.R
import com.android.virgilsecurity.feature_login.viewmodel.login.LoginVM
import com.android.virgilsecurity.feature_login.viewslice.login.list.ViewPagerSlice
import com.android.virgilsecurity.feature_login.viewslice.login.list.ViewPagerSlice.Action
import org.koin.android.ext.android.inject

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
 * LoginFragment
 */
class LoginFragment @SuppressLint("ValidFragment") constructor(
        override val layoutResourceId: Int = R.layout.fragment_login
) : BaseFragment<AuthActivity>() {

    private val viewPagerSlice: ViewPagerSlice by inject()
    private val stateSlice: StateSlice by inject()
    private val viewModel: LoginVM by inject()

    override fun init(view: View, savedInstanceState: Bundle?) { }

    override fun initViewSlices(view: View) {
        viewPagerSlice.init(rootActivity!!.lifecycle, view)
        stateSlice.init(rootActivity!!.lifecycle, view)
        onStateChanged(viewModel.getState().value!!)
    }

    override fun setupVSActionObservers() =
            observe(viewPagerSlice.getAction()) { onActionChanged(it) }

    override fun setupVMStateObservers() = observe(viewModel.getState()) {
        onStateChanged(it)
    }

    private fun onStateChanged(state: LoginVM.State) = when (state) {
        is LoginVM.State.UsersLoaded -> viewPagerSlice.showUsers(state.users)
        LoginVM.State.ShowLoading -> stateSlice.showLoading()
        LoginVM.State.ShowContent -> stateSlice.showContent()
        LoginVM.State.ShowError -> stateSlice.showError()
        else -> stateSlice.showError()
    }

    private fun onActionChanged(action: Action) = when (action) {
        is Action.UserClicked -> rootActivity?.login(action.user)
    }

    companion object {
        fun instance() = LoginFragment()
    }
}