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
import android.os.Bundle
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.bluelinelabs.conductor.changehandler.SimpleSwapChangeHandler
import com.virgilsecurity.android.base.data.model.User
import com.virgilsecurity.android.base.extension.hasNoRootController
import com.virgilsecurity.android.base.extension.observe
import com.virgilsecurity.android.base.view.ScreenRouter
import com.virgilsecurity.android.base.view.activity.BActivityController
import com.virgilsecurity.android.common.util.DoubleBack
import com.virgilsecurity.android.common.util.UiUtils
import com.virgilsecurity.android.common.view.ScreenChat
import com.virgilsecurity.android.feature_login.R
import com.virgilsecurity.android.feature_login.viewmodel.login.AuthVM
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.android.scope.currentScope
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named

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
) : BActivityController() {

    override fun provideContainer(): ViewGroup = controllerContainer

    private val doubleBack: DoubleBack by inject()
    private val screenRouter: ScreenRouter by inject()
    private val vmAuth: AuthVM by getKoin().getOrCreateScope("someid", named(VM_AUTH)).inject()

    override fun init(savedInstanceState: Bundle?) {
        vmAuth.users()
    }

    override fun setupVMStateObservers() = observe(vmAuth.getState(), ::onStateChanged)

    private fun onStateChanged(state: AuthVM.State) = when (state) {
        is AuthVM.State.UsersLoaded -> {
            AuthController(::login, ::registration).run {
                if (routerRoot.hasNoRootController())
                    initRouter(this, AuthController.KEY_LOGIN_CONTROLLER)
                else
                    replaceTopController(this, AuthController.KEY_LOGIN_CONTROLLER)
            }

        }
        AuthVM.State.ShowNoUsers -> {
            NoUsersController(::registration).run {
                if (routerRoot.hasNoRootController())
                    initRouter(this, NoUsersController.KEY_NO_USERS_CONTROLLER)
                else
                    replaceTopController(this, NoUsersController.KEY_NO_USERS_CONTROLLER)
            }
        }
        AuthVM.State.ShowLoading -> Unit
        AuthVM.State.ShowContent -> Unit
        AuthVM.State.LoginError -> Unit
        AuthVM.State.ShowError -> {
            AuthController(::login, ::registration).run {
                if (routerRoot.hasNoRootController())
                    initRouter(this, AuthController.KEY_LOGIN_CONTROLLER)
                else
                    replaceTopController(this, AuthController.KEY_LOGIN_CONTROLLER)
            }
        }
    }

    fun login(user: User) =
            screenRouter.getScreenIntent(this,
                                         ScreenChat.DrawerNavigation,
                                         User.KEY,
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

    private fun initRouter(controller: Controller, tag: String) = routerRoot.setRoot(
        RouterTransaction
                .with(controller.apply {
                    retainViewMode = Controller.RetainViewMode.RETAIN_DETACH
                })
                .pushChangeHandler(SimpleSwapChangeHandler())
                .popChangeHandler(SimpleSwapChangeHandler())
                .tag(tag)
    )

    private fun replaceTopController(controller: Controller, tag: String) =
            routerRoot.replaceTopController(
                RouterTransaction
                        .with(controller.apply {
                            retainViewMode = Controller.RetainViewMode.RETAIN_DETACH
                        })
                        .pushChangeHandler(SimpleSwapChangeHandler())
                        .tag(tag)
            )

    private fun pushController(controller: Controller, tag: String) =
            routerRoot.pushController(RouterTransaction
                                              .with(controller)
                                              .pushChangeHandler(HorizontalChangeHandler())
                                              .popChangeHandler(HorizontalChangeHandler())
                                              .tag(tag))

    override fun onBackPressed() {
        if (routerRoot.backstackSize > 1) {
            routerRoot.popToRoot()
        } else {
            if (doubleBack.tryToPress())
                super.onBackPressed()
            else
                UiUtils.toast(this, getString(R.string.press_exit_once_more))
        }
    }
}
