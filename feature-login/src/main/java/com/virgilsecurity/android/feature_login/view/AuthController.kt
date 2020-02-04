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

import LoginDiConst.VM_AUTH
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.virgilsecurity.android.base.data.model.User
import com.virgilsecurity.android.base.extension.observe
import com.virgilsecurity.android.base.view.controller.BaseController
import com.virgilsecurity.android.common.util.ImageStorage
import com.virgilsecurity.android.common.util.UiUtils
import com.virgilsecurity.android.feature_login.R
import com.virgilsecurity.android.feature_login.viewmodel.login.AuthVM
import com.virgilsecurity.android.feature_login.viewslice.login.list.ViewPagerSlice
import com.virgilsecurity.android.feature_login.viewslice.login.list.adapter.UsersPagerAdapter
import com.virgilsecurity.android.feature_login.viewslice.login.state.StateSliceLogin
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.inject

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
class AuthController() : BaseController() {

    override val layoutResourceId: Int = R.layout.controller_login

    private val imageStorage: ImageStorage by inject()
    private val stateSlice: StateSliceLogin by inject()
    private val vmAuth: AuthVM by getKoin().getScope(VM_AUTH).viewModel(this)

    private lateinit var mutableLiveData: MutableLiveData<ViewPagerSlice.Action>

    private lateinit var login: (User) -> Unit
    private lateinit var registration: () -> Unit
    private lateinit var usersPagerAdapter: UsersPagerAdapter
    private lateinit var viewPagerSlice: ViewPagerSlice

    constructor(login: (User) -> Unit, registration: () -> Unit) : this() {
        this.login = login
        this.registration = registration
    }

    override fun init(containerView: View) {
        containerView.findViewById<TextView>(R.id.tvCreateAccount)
                .setOnClickListener { registration() }

        mutableLiveData = MutableLiveData()
        usersPagerAdapter = UsersPagerAdapter(imageStorage, mutableLiveData)
        viewPagerSlice = ViewPagerSlice(usersPagerAdapter, mutableLiveData)
    }

    override fun initViewSlices(window: Window) {
        viewPagerSlice.init(lifecycle, window)
        stateSlice.init(lifecycle, window)
    }

    override fun setupViewSlices(containerView: View) {}

    override fun setupVSActionObservers() =
            observe(viewPagerSlice.getAction(), ::onActionChanged)

    override fun setupVMStateObservers() = observe(vmAuth.getState(), ::onStateChanged)

    override fun initData() {}

    private fun onStateChanged(state: AuthVM.State) = when (state) {
        is AuthVM.State.UsersLoaded -> {
            viewPagerSlice.showUsers(state.users)
            if (state.users.size > 4)
                viewPagerSlice.updateIndicator()
            stateSlice.showContent()
        }
        AuthVM.State.ShowLoading -> stateSlice.showLoading()
        AuthVM.State.ShowError -> stateSlice.showError()
        AuthVM.State.LoginError -> {
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
