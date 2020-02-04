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

package com.virgilsecurity.android.feature_drawer_navigation.view

import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import androidx.lifecycle.MutableLiveData
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.bluelinelabs.conductor.changehandler.SimpleSwapChangeHandler
import com.virgilsecurity.android.base.data.model.ChannelMeta
import com.virgilsecurity.android.base.data.model.User
import com.virgilsecurity.android.base.extension.hasNoRootController
import com.virgilsecurity.android.base.extension.observe
import com.virgilsecurity.android.base.view.ScreenRouter
import com.virgilsecurity.android.base.view.activity.BActivityControllerSlices
import com.virgilsecurity.android.common.util.ImageStorage
import com.virgilsecurity.android.common.view.ScreenChat
import com.virgilsecurity.android.feature_channel.view.ChannelController
import com.virgilsecurity.android.feature_channels_list.view.ChannelsListController
import com.virgilsecurity.android.feature_contacts.view.AddContactController
import com.virgilsecurity.android.feature_contacts.view.ContactsController
import com.virgilsecurity.android.feature_drawer_navigation.R
import com.virgilsecurity.android.feature_drawer_navigation.viewslice.navigation.drawer.SliceDrawer
import com.virgilsecurity.android.feature_drawer_navigation.viewslice.navigation.state.StateSliceDrawer
import com.virgilsecurity.android.feature_settings.view.AboutController
import com.virgilsecurity.android.feature_settings.view.SettingsController
import com.virgilsecurity.android.feature_settings.view.SettingsEditController
import com.virgilsecurity.android.feature_settings.view.VersionHistoryController
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
) : BActivityControllerSlices() {

    override fun provideContainer(): ViewGroup = controllerContainer

    private val screenRouter: ScreenRouter by inject()
    private val imageStorage: ImageStorage by inject()

    private lateinit var user: User
    private lateinit var mldSliceDrawer: MutableLiveData<SliceDrawer.Action>
    private lateinit var sliceDrawer: SliceDrawer
    private lateinit var stateSlice: StateSliceDrawer

    override fun init(savedInstanceState: Bundle?) {
        user = intent.getParcelableExtra(User.KEY)

        initRouter()
    }

    override fun initViewSlices(window: Window) {
        this.mldSliceDrawer = MutableLiveData() // TODO move all slices init to this method
        this.sliceDrawer = SliceDrawer(mldSliceDrawer, imageStorage)
        this.stateSlice = StateSliceDrawer()

        sliceDrawer.init(lifecycle, window)
        stateSlice.init(lifecycle, window)
    }

    override fun setupViewSlices() {
        sliceDrawer.setHeader(user.identity, user.picturePath)
    }

    override fun setupVSActionObservers() {
        observe(sliceDrawer.getAction()) { onActionChanged(it) }
    }

    override fun setupVMStateObservers() {}

    private fun onActionChanged(action: SliceDrawer.Action,
                                pushChangeHandler: ControllerChangeHandler? = null,
                                popChangeHandler: ControllerChangeHandler? = null) = when (action) {
        SliceDrawer.Action.ContactsClicked -> {
            stateSlice.unLockDrawer()
            stateSlice.closeDrawer()
            replaceTopController(contactsController(), ContactsController.KEY_CONTACTS_CONTROLLER,
                                 pushChangeHandler,
                                 popChangeHandler)
        }
        SliceDrawer.Action.ChannelsListClicked -> {
            stateSlice.unLockDrawer()
            stateSlice.closeDrawer()
            replaceTopController(channelsController(),
                                 ChannelsListController.KEY_CHANNELS_LIST_CONTROLLER,
                                 pushChangeHandler,
                                 popChangeHandler)
        }
        SliceDrawer.Action.SettingsClicked -> {
            stateSlice.lockDrawer()
            stateSlice.closeDrawer()
            pushController(settingsController(),
                           SettingsController.KEY_SETTINGS_CONTROLLER,
                           pushChangeHandler,
                           popChangeHandler)
        }
        SliceDrawer.Action.SameItemClicked -> stateSlice.closeDrawer()
        SliceDrawer.Action.Idle -> Unit
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
            SettingsController(user,
                               {
                                   pushController(settingsEditController(),
                                                  SettingsEditController.KEY_EDIT_SETTINGS_CONTROLLER)
                               },
                               {
                                   screenRouter.getScreenIntent(this, ScreenChat.Login).run {
                                       startActivity(this)
                                       overridePendingTransition(R.anim.animation_slide_from_start_activity,
                                                                 R.anim.animation_slide_to_end_activity)
                                       finish()
                                   }
                               },
                               {
                                   pushController(AboutController(),
                                                  AboutController.KEY_ABOUT_CONTROLLER)
                               },
                               {
                                   pushController(VersionHistoryController(),
                                                  VersionHistoryController.KEY_VERSIONS_CONTROLLER)
                               })

    private fun settingsEditController() =
            SettingsEditController(user)
            {
                screenRouter.getScreenIntent(this, ScreenChat.Login).run {
                    startActivity(this)
                    overridePendingTransition(R.anim.animation_slide_from_start_activity,
                                              R.anim.animation_slide_to_end_activity)
                    finish()
                }
            }

    private fun replaceTopController(controller: Controller,
                                     tag: String,
                                     pushChangeHandler: ControllerChangeHandler? = SimpleSwapChangeHandler(),
                                     popChangeHandler: ControllerChangeHandler? = null) =
            routerRoot.replaceTopController(RouterTransaction
                                                    .with(controller.apply {
                                                        retainViewMode = Controller.RetainViewMode.RETAIN_DETACH
                                                    })
                                                    .pushChangeHandler(pushChangeHandler)
                                                    .popChangeHandler(popChangeHandler)
                                                    .tag(tag))

    private fun pushController(controller: Controller,
                               tag: String,
                               pushChangeHandler: ControllerChangeHandler? = HorizontalChangeHandler(),
                               popChangeHandler: ControllerChangeHandler? = HorizontalChangeHandler()) =
            routerRoot.pushController(RouterTransaction
                                              .with(controller.apply {
                                                  retainViewMode = Controller.RetainViewMode.RETAIN_DETACH
                                              })
                                              .pushChangeHandler(pushChangeHandler)
                                              .popChangeHandler(popChangeHandler)
                                              .tag(tag))

    private fun openChannelNotFromChannels(channel: ChannelMeta) {
        stateSlice.lockDrawer()
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

    private fun openChannel(channel: ChannelMeta) {
        stateSlice.lockDrawer()
        pushController(ChannelController(channel),
                       ChannelController.KEY_CHANNEL_CONTROLLER)
    }

    private fun initRouter() {
        if (routerRoot.hasNoRootController())
            routerRoot.setRoot(RouterTransaction
                                       .with(SmackInitController(user)
                                             {
                                                 onActionChanged(SliceDrawer.Action.ChannelsListClicked,
                                                                 HorizontalChangeHandler())
                                             })
                                       .pushChangeHandler(HorizontalChangeHandler())
                                       .popChangeHandler(HorizontalChangeHandler())
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

                if (to is ContactsController) {
                    sliceDrawer.setItemSelected(0)
                    stateSlice.unLockDrawer()
                } else if (to is ChannelsListController) {
                    sliceDrawer.setItemSelected(1)
                    stateSlice.unLockDrawer()
                }
            }

        })
    }
}
