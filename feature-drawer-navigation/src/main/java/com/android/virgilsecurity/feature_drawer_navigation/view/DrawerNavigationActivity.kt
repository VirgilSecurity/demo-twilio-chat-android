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
import com.android.virgilsecurity.base.data.model.ChannelInfo
import com.android.virgilsecurity.base.data.model.User
import com.android.virgilsecurity.base.data.properties.UserProperties
import com.android.virgilsecurity.base.extension.getContentView
import com.android.virgilsecurity.base.extension.hasNoRootController
import com.android.virgilsecurity.base.extension.observe
import com.android.virgilsecurity.base.view.BaseActivityController
import com.android.virgilsecurity.base.view.ScreenRouter
import com.android.virgilsecurity.common.view.ScreenChat
import com.android.virgilsecurity.feature_channel.view.ChannelController
import com.android.virgilsecurity.feature_channels_list.view.ChannelsListController
import com.android.virgilsecurity.feature_contacts.view.AddContactController
import com.android.virgilsecurity.feature_contacts.view.ContactsController
import com.android.virgilsecurity.feature_drawer_navigation.R
import com.android.virgilsecurity.feature_drawer_navigation.di.Const.CONTEXT_DRAWER_NAVIGATION
import com.android.virgilsecurity.feature_drawer_navigation.viewslice.navigation.drawer.DrawerSlice
import com.android.virgilsecurity.feature_drawer_navigation.viewslice.navigation.state.DrawerStateSlice
import com.android.virgilsecurity.feature_settings.view.SettingsController
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.bluelinelabs.conductor.changehandler.SimpleSwapChangeHandler
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

    override fun provideContainer(): ViewGroup = controllerContainer
    override val koinContextName: String? = CONTEXT_DRAWER_NAVIGATION

    private val drawerSlice: DrawerSlice by inject()
    private val stateSlice: DrawerStateSlice by inject()
    private val screenRouter: ScreenRouter by inject()
    private val userProperties: UserProperties by inject()

    private lateinit var user: User

    override fun init(savedInstanceState: Bundle?) {
        user = intent.getParcelableExtra(User.EXTRA_USER)

        initRouter()
    }

    override fun initViewSlices() {
        drawerSlice.init(lifecycle, getContentView())
        stateSlice.init(lifecycle, getContentView())
    }

    override fun setupViewSlices() {
        drawerSlice.setHeader(user.identity, user.picturePath)
    }

    override fun setupVSActionObservers() {
        observe(drawerSlice.getAction()) { onActionChanged(it) }
    }

    override fun setupVMStateObservers() {}

    private fun onActionChanged(action: DrawerSlice.Action) = when (action) {
        DrawerSlice.Action.ContactsClicked -> {
            stateSlice.unLockDrawer()
            stateSlice.closeDrawer()
            replaceTopController(contactsController(), ContactsController.KEY_CONTACTS_CONTROLLER)
        }
        DrawerSlice.Action.ChannelsListClicked -> {
            stateSlice.unLockDrawer()
            stateSlice.closeDrawer()
            replaceTopController(channelsController(),
                                 ChannelsListController.KEY_CHANNELS_LIST_CONTROLLER)
        }
        DrawerSlice.Action.SettingsClicked -> {
            stateSlice.lockDrawer()
            stateSlice.closeDrawer()
            pushController(settingsController(), SettingsController.KEY_SETTINGS_CONTROLLER)
        }
        DrawerSlice.Action.SameItemClicked -> stateSlice.closeDrawer()
        DrawerSlice.Action.Idle -> Unit
    }

    private fun contactsController() =
            ContactsController({
                                   stateSlice.openDrawer()
                               },
                               {
                                   stateSlice.lockDrawer()
                                   pushController(AddContactController(::openChannelNotFromChannels),
                                                  AddContactController.KEY_ADD_CONTACT_CONTROLLER)
                               },
                               {
                                   openChannelNotFromChannels(it)
                               })

    private fun channelsController() =
            ChannelsListController({
                                       stateSlice.openDrawer()
                                   },
                                   {
                                       openChannel(it)
                                   })

    private fun settingsController() =
            SettingsController(user)
            {
                screenRouter.getScreenIntent(this, ScreenChat.Login).run {
                    startActivity(this)
                    overridePendingTransition(R.anim.animation_slide_from_start_activity,
                                              R.anim.animation_slide_to_end_activity)
                    finish()
                }
            }

    private fun replaceTopController(controller: Controller, tag: String) =
            routerRoot.replaceTopController(RouterTransaction
                                                    .with(controller.apply {
                                                        retainViewMode = Controller.RetainViewMode.RETAIN_DETACH
                                                    })
                                                    .pushChangeHandler(SimpleSwapChangeHandler())
                                                    .tag(tag))

    private fun pushController(controller: Controller, tag: String) =
            routerRoot.pushController(RouterTransaction
                                              .with(controller)
                                              .pushChangeHandler(HorizontalChangeHandler())
                                              .popChangeHandler(HorizontalChangeHandler())
                                              .tag(tag))

    private fun openChannelNotFromChannels(channel: ChannelInfo) {

            routerRoot.setBackstack(
                listOf(RouterTransaction
                               .with(channelsController().apply {
                                   retainViewMode = Controller.RetainViewMode.RETAIN_DETACH
                               })
                               .pushChangeHandler(SimpleSwapChangeHandler())
                               .tag(ChannelsListController.KEY_CHANNELS_LIST_CONTROLLER)),
                SimpleSwapChangeHandler())

            openChannel(channel)
    }

    private fun openChannel(channel: ChannelInfo) =
            pushController(ChannelController(channel),
                           ChannelController.KEY_CHANNEL_CONTROLLER)

    private fun initRouter() {
        if (routerRoot.hasNoRootController())
            routerRoot.setRoot(RouterTransaction
                                       .with(TwilioInitController(user)
                                             {
                                                 onActionChanged(DrawerSlice.Action.ChannelsListClicked)
                                             })
                                       .pushChangeHandler(SimpleSwapChangeHandler())
                                       .popChangeHandler(SimpleSwapChangeHandler())
                                       .tag(ChannelsListController.KEY_CHANNELS_LIST_CONTROLLER))

        routerRoot.addChangeListener(object : ControllerChangeHandler.ControllerChangeListener {
            override fun onChangeStarted(to: Controller?,
                                         from: Controller?,
                                         isPush: Boolean,
                                         container: ViewGroup,
                                         changeHandler: ControllerChangeHandler) {
            }

            override fun onChangeCompleted(to: Controller?,
                                           from: Controller?,
                                           isPush: Boolean,
                                           container: ViewGroup,
                                           changeHandler: ControllerChangeHandler) {

                if (to is ContactsController)
                    drawerSlice.setItemSelected(0)
                else if (to is ChannelsListController)
                    drawerSlice.setItemSelected(1)
            }

        })
    }
}