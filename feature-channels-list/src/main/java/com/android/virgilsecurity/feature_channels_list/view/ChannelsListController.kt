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

import android.app.Activity
import android.content.Context
import android.view.View
import com.android.virgilsecurity.base.data.model.User
import com.android.virgilsecurity.base.extension.inject
import com.android.virgilsecurity.base.extension.observe
import com.android.virgilsecurity.base.extension.removeObservers
import com.android.virgilsecurity.base.view.BaseController
import com.android.virgilsecurity.common.util.UiUtils
import com.android.virgilsecurity.feature_channels_list.R
import com.android.virgilsecurity.feature_channels_list.viewslice.toolbar.ToolbarSlice
import kotlinx.android.synthetic.main.controller_channels_list.*


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
 * ChannelsListController
 */
class ChannelsListController() : BaseController() {

    override val layoutResourceId: Int = R.layout.controller_channels_list

    private val toolbarSlice: ToolbarSlice by inject()

    private lateinit var user: User
    private lateinit var onChatClick: (User) -> Unit
    private lateinit var openDrawer: () -> Unit

    constructor(user: User, onChatClick: (User) -> Unit, openDrawer: () -> Unit) : this() {
        this.user = user
        this.onChatClick = onChatClick
        this.openDrawer = openDrawer
    }

//    override fun onContextAvailable(context: Context) {
//        super.onContextAvailable(context)
//        rootActivity = context as D
//    }

    override fun init() {
//        tvUsername.text = user.identity
        clNoContacts.setOnClickListener { onChatClick(user) }
    }

    override fun initViewSlices(view: View) {
        toolbarSlice.init(lifecycle, view)
    }

    override fun setupViewSlices(view: View) {
        // TODO Implement body or it will be empty ):
    }

    override fun setupVSActionObservers() {
        removeObservers(toolbarSlice.getAction())
        observe(toolbarSlice.getAction()) { onActionChanged(it) }
    }

    override fun setupVMStateObservers() {
        // TODO Implement body or it will be empty ):
    }

//    override fun cleanUp() {
//        toolbarSlice.cleanViewIdsCache()
//    }

    private fun onActionChanged(action: ToolbarSlice.Action) = when (action) {
        ToolbarSlice.Action.HamburgerClicked -> openDrawer()
        ToolbarSlice.Action.Idle -> Unit
    }

    companion object {
        const val KEY_CHANNELS_LIST_CONTROLLER = "KEY_CHANNELS_LIST_CONTROLLER"
    }
}