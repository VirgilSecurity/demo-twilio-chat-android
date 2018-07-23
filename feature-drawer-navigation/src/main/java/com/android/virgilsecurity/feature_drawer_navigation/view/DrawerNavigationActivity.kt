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

package com.android.virgilsecurity.feature_drawer_navigation.view

import android.os.Bundle
import android.view.ViewGroup
import com.android.virgilsecurity.base.data.model.User
import com.android.virgilsecurity.base.extension.getContentView
import com.android.virgilsecurity.base.extension.hasNoRootController
import com.android.virgilsecurity.base.extension.observe
import com.android.virgilsecurity.base.view.BaseActivityController
import com.android.virgilsecurity.feature_channel.view.ChannelController
import com.android.virgilsecurity.feature_channels_list.view.ChannelsListController
import com.android.virgilsecurity.feature_contacts.view.ContactsController
import com.android.virgilsecurity.feature_drawer_navigation.R
import com.android.virgilsecurity.feature_drawer_navigation.viewslice.drawer.DrawerSlice
import com.android.virgilsecurity.feature_drawer_navigation.viewslice.state.DrawerStateSlice
import com.android.virgilsecurity.feature_settings.view.SettingsController
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.*
import kotlinx.android.synthetic.main.activity_drawer_navigation.*
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
        override val layoutResourceId: Int = R.layout.activity_drawer_navigation
) : BaseActivityController() {

    private val drawerSlice: DrawerSlice by inject()
    private val stateSlice: DrawerStateSlice by inject()
    private lateinit var user: User

    override fun provideContainer(): ViewGroup = controllerContainer

    override fun init(savedInstanceState: Bundle?) {
        user = intent.getParcelableExtra(User.EXTRA_USER)

        if (router.hasNoRootController())
            router.setRoot(RouterTransaction
                                   .with(ChannelsListController(user,
                                                                {
                                                                    openChannel(it)
                                                                },
                                                                {
                                                                    stateSlice.openDrawer()
                                                                }))
                                   .pushChangeHandler(FadeChangeHandler())
                                   .popChangeHandler(FadeChangeHandler())
                                   .tag(ChannelsListController.KEY_CHANNELS_LIST_CONTROLLER))
    }

    override fun initViewSlices() {
        drawerSlice.init(lifecycle, getContentView())
        stateSlice.init(lifecycle, getContentView())

        drawerSlice.setHeader(user.identity, user.picturePath)
    }

    override fun setupVSActionObservers() {
        observe(drawerSlice.getAction()) { onActionChanged(it) }
    }

    private fun onActionChanged(action: DrawerSlice.Action) = when (action) {
        DrawerSlice.Action.ContactsClicked -> {
            stateSlice.unLockDrawer()
            stateSlice.closeDrawer()
            replaceTopController(ContactsController {
                stateSlice.openDrawer()
            }, ContactsController.KEY_CONTACTS_CONTROLLER)
        }
        DrawerSlice.Action.ChannelsListClicked -> {
            stateSlice.unLockDrawer()
            stateSlice.closeDrawer()
            replaceTopController(ChannelsListController(user,
                                                        {
                                                            openChannel(it)
                                                        },
                                                        {
                                                            stateSlice.openDrawer()
                                                        }),
                                 ChannelsListController.KEY_CHANNELS_LIST_CONTROLLER)
        }
        DrawerSlice.Action.SettingsClicked -> {
            stateSlice.unLockDrawer()
            stateSlice.closeDrawer()
            pushController(SettingsController(user)
                           {
                               // Quick fix for toolbar view restoring issue
                               router.popController(router.getControllerWithTag(
                                   SettingsController.KEY_SETTINGS_CONTROLLER)!!
                               )
                               stateSlice.closeDrawer()

                               if (router.getControllerWithTag(ContactsController.KEY_CONTACTS_CONTROLLER) != null) {
                                   replaceTopController(ContactsController {
                                       stateSlice.openDrawer()
                                   }, ContactsController.KEY_CONTACTS_CONTROLLER)
                                   drawerSlice.setItemSelected(0)
                               } else if (router.getControllerWithTag(ChannelsListController.KEY_CHANNELS_LIST_CONTROLLER) != null) {
                                   replaceTopController(ChannelsListController(user,
                                                                               {
                                                                                   openChannel(it)
                                                                               },
                                                                               {
                                                                                   stateSlice.openDrawer()
                                                                               }),
                                                        ChannelsListController.KEY_CHANNELS_LIST_CONTROLLER)
                                   drawerSlice.setItemSelected(1)
                               }
                           },
                           SettingsController.KEY_SETTINGS_CONTROLLER)
        }
        DrawerSlice.Action.SameItemClicked -> {
            stateSlice.closeDrawer()
        }
    }

    private fun replaceTopController(controller: Controller, tag: String) =
            router.replaceTopController(RouterTransaction
                                                .with(controller)
                                                .pushChangeHandler(SimpleSwapChangeHandler())
                                                .tag(tag))

    private fun pushController(controller: Controller, tag: String) =
            router.pushController(RouterTransaction
                                          .with(controller)
                                          .pushChangeHandler(SimpleSwapChangeHandler())
                                          .popChangeHandler(SimpleSwapChangeHandler())
                                          .tag(tag))

    private fun openChannel(user: User) {
        stateSlice.lockDrawer()
        replaceTopController(ChannelController(user), ChannelController.KEY_CHANNEL_CONTROLLER)
    }

    override fun setupVMStateObservers() {}
}