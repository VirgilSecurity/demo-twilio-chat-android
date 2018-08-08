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

import LoginDiConst.CONTEXT_AUTH_ACTIVITY
import android.os.Bundle
import android.view.ViewGroup
import com.android.virgilsecurity.base.data.model.User
import com.android.virgilsecurity.base.extension.hasNoRootController
import com.android.virgilsecurity.base.extension.observe
import com.android.virgilsecurity.base.view.BaseActivityController
import com.android.virgilsecurity.base.view.ScreenRouter
import com.android.virgilsecurity.common.util.DoubleBack
import com.android.virgilsecurity.common.util.UiUtils
import com.android.virgilsecurity.common.view.ScreenChat
import com.android.virgilsecurity.feature_login.R
import com.android.virgilsecurity.feature_login.viewmodel.login.LoginVM
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.bluelinelabs.conductor.changehandler.SimpleSwapChangeHandler
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
) : BaseActivityController() {

    override fun provideContainer(): ViewGroup = controllerContainer
    override val koinContextName: String? = CONTEXT_AUTH_ACTIVITY

    private val doubleBack: DoubleBack by inject()
    private val screenRouter: ScreenRouter by inject()
    private val loginVM: LoginVM by inject()

    override fun init(savedInstanceState: Bundle?) {
        loginVM.users()
    }

    override fun initViewSlices() {}

    override fun setupViewSlices() {}

    override fun setupVSActionObservers() {}

    override fun setupVMStateObservers() = observe(loginVM.getState()) {
        onStateChanged(it)
    }

    private fun onStateChanged(state: LoginVM.State) = when (state) {
        is LoginVM.State.UsersLoaded -> {
            val controller = LoginController(::login, ::registration)
            if (router.hasNoRootController())
                initRouter(controller, LoginController.KEY_LOGIN_CONTROLLER)
            else
                replaceTopController(controller, LoginController.KEY_LOGIN_CONTROLLER)
        }
        LoginVM.State.ShowNoUsers -> {
            val controller = NoUsersController(::registration)
            if (router.hasNoRootController())
                initRouter(controller, NoUsersController.KEY_NO_USERS_CONTROLLER)
            else
                replaceTopController(controller, NoUsersController.KEY_NO_USERS_CONTROLLER)
        }
        LoginVM.State.ShowLoading -> Unit
        LoginVM.State.ShowContent -> Unit
        LoginVM.State.LoginError -> Unit
        LoginVM.State.ShowError -> {
            val controller = LoginController(::login, ::registration)
            if (router.hasNoRootController())
                initRouter(controller, LoginController.KEY_LOGIN_CONTROLLER)
            else
                replaceTopController(controller, LoginController.KEY_LOGIN_CONTROLLER)
        }
        is LoginVM.State.LoginSuccess -> login(state.user)
    }

    private fun replaceTopController(controller: Controller, tag: String) =
            router.replaceTopController(RouterTransaction
                                                .with(controller)
                                                .pushChangeHandler(SimpleSwapChangeHandler())
                                                .tag(tag))

    fun login(user: User) =
            screenRouter.getScreenIntent(this,
                                         ScreenChat.DrawerNavigation,
                                         User.EXTRA_USER,
                                         user)
                    .run {
                        startActivity(this)
                        overridePendingTransition(R.anim.animation_slide_from_end_activity,
                                                  R.anim.animation_slide_to_start_activity)
                        finish()
                    }

    fun registration() {
        pushController(RegistrationController(::login),
                             RegistrationController.KEY_REGISTRATION_CONTROLLER)
    }

    private fun initRouter(controller: Controller, tag: String) =
            router.setRoot(RouterTransaction
                                   .with(controller)
                                   .pushChangeHandler(SimpleSwapChangeHandler())
                                   .popChangeHandler(SimpleSwapChangeHandler())
                                   .tag(tag))

    private fun pushController(controller: Controller, tag: String) =
            router.pushController(RouterTransaction
                                          .with(controller)
                                          .pushChangeHandler(HorizontalChangeHandler())
                                          .popChangeHandler(HorizontalChangeHandler())
                                          .tag(tag))

    override fun onBackPressed() {
        if (router.backstackSize > 1) {
            router.popToRoot()
        } else {
            if (doubleBack.tryToPress())
                super.onBackPressed()
            else
                UiUtils.toast(this, getString(R.string.press_exit_once_more))
        }
    }
    // TODO add wait controller while users are loaded
}