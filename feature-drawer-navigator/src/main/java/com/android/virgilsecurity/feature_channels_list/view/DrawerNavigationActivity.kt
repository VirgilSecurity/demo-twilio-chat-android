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

package com.android.virgilsecurity.feature_channels_list.view

import android.os.Bundle
import android.view.ViewGroup
import com.android.virgilsecurity.base.data.model.User
import com.android.virgilsecurity.base.extension.getContentView
import com.android.virgilsecurity.base.extension.hasNoRootController
import com.android.virgilsecurity.base.extension.observe
import com.android.virgilsecurity.base.view.BaseActivityController
import com.android.virgilsecurity.feature_channel.ChannelController
import com.android.virgilsecurity.feature_channels_list.viewslice.drawer.DrawerSlice
import com.android.virgilsecurity.feature_channels_list.viewslice.state.DrawerStateSlice
import com.android.virgilsecurity.feature_drawer_navigator.R
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import kotlinx.android.synthetic.main.activity_channels_list.*
import org.koin.android.ext.android.inject

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    7/5/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * DrawerNavigationActivity
 */
class DrawerNavigationActivity(
        override val layoutResourceId: Int = R.layout.activity_channels_list
) : BaseActivityController() {

    private val drawerSlice: DrawerSlice by inject()
    private val stateSlice: DrawerStateSlice by inject()
    private lateinit var user: User

    override fun provideContainer(): ViewGroup = controllerContainer

    override fun init(savedInstanceState: Bundle?) {
        user = intent.getParcelableExtra(User.EXTRA_USER)

        if (router.hasNoRootController())
            router.setRoot(RouterTransaction
                                   .with(ChannelsListController(user) { openChannel(it) })
                                   .pushChangeHandler(FadeChangeHandler())
                                   .popChangeHandler(FadeChangeHandler()))
    }

    override fun initViewSlices() {
        drawerSlice.init(lifecycle, getContentView())
        stateSlice.init(lifecycle, getContentView())
    }

    override fun setupVSActionObservers() {
        observe(drawerSlice.getAction()) { onActionChanged(it) }
    }

    private fun onActionChanged(action: DrawerSlice.Action) = when (action) {
        DrawerSlice.Action.ChannelsListClicked -> {
            stateSlice.unLockDrawer()
            changeController(ChannelsListController(user) {
                openChannel(it)
            })
        }
        DrawerSlice.Action.ContactsClicked -> {
            stateSlice.unLockDrawer()
            changeController(ContactsController())
        }
        DrawerSlice.Action.SettingsClicked -> {
            stateSlice.unLockDrawer()
            changeController(SettingsController(user))
        }
    }

    private fun changeController(controller: Controller) {
        router.pushController(RouterTransaction
                                      .with(controller)
                                      .pushChangeHandler(FadeChangeHandler())
                                      .popChangeHandler(FadeChangeHandler()))
    }

    private fun openChannel(user: User) {
        stateSlice.lockDrawer()
        changeController(ChannelController(user))
    }

    override fun setupVMStateObservers() {}
}