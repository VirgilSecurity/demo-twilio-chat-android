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

import android.app.Fragment
import android.os.Bundle
import com.android.virgilsecurity.base.data.model.User
import com.android.virgilsecurity.base.extension.observe
import com.android.virgilsecurity.base.view.BaseActivity
import com.android.virgilsecurity.base.view.ScreenRouter
import com.android.virgilsecurity.common.util.DoubleBack
import com.android.virgilsecurity.common.util.UiUtils
import com.android.virgilsecurity.common.view.ScreenChat
import com.android.virgilsecurity.feature_login.R
import com.android.virgilsecurity.feature_login.viewmodel.login.LoginVM
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.ext.android.inject

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    5/29/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

class AuthActivity(
        override val layoutResourceId: Int = R.layout.activity_login
) : BaseActivity() {

    private val doubleBack: DoubleBack by inject()
    private val screenRouter: ScreenRouter by inject()
    private val loginVM: LoginVM by inject()

    override fun init(savedInstanceState: Bundle?) {
        loginVM.users()
    }

    override fun initViewSlices() {}

    override fun setupVSObservers() {}

    override fun setupVMStateObservers() = observe(loginVM.getState()) {
        onStateChanged(it)
    }

    private fun onStateChanged(state: LoginVM.State) = when (state) {
        is LoginVM.State.ShowContent -> showFragment(LoginFragment.instance())
        LoginVM.State.ShowNoUsers -> showFragment(NoUsersFragment.instance())
        LoginVM.State.ShowError -> showFragment(LoginFragment.instance())
        else -> {
            // Waiting while users are loaded
        }
    }

    private fun showFragment(fragment: Fragment) {
        UiUtils.replaceFragmentNoTag(fragmentManager,
                                     flContainer.id,
                                     fragment)
        // TODO add nice transaction & remove white background after splash
    }

    fun login(user: User) =
            screenRouter.getScreenIntent(this,
                                         ScreenChat.ChannelsList,
                                         User.EXTRA_USER,
                                         user)
                    .run { startActivity(this) }

    fun registration() {
        showFragment(RegistrationFragment.instance())
    }

    override fun onBackPressed() {
        hideKeyboard()

        if (doubleBack.tryToPress())
            super.onBackPressed()
        else
            UiUtils.toast(this, getString(R.string.press_exit_once_more))
    }
}