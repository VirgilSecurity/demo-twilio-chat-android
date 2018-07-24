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

package com.android.virgilsecurity.feature_channel.view

import android.view.View
import com.android.virgilsecurity.base.data.model.User
import com.android.virgilsecurity.base.extension.inject
import com.android.virgilsecurity.base.extension.observe
import com.android.virgilsecurity.base.view.BaseController
import com.android.virgilsecurity.common.util.UiUtils
import com.android.virgilsecurity.feature_channel.R
import com.android.virgilsecurity.feature_channel.viewslice.toolbar.ToolbarSlice

class ChannelController() : BaseController() {

    override val layoutResourceId: Int = R.layout.controller_channel

    private val toolbarSlice: ToolbarSlice by inject()

    private lateinit var user: User

    constructor(user: User) : this() {
        this.user = user
    }

    override fun init() {

    }

    override fun initViewSlices(view: View) {
        toolbarSlice.init(lifecycle, view)
    }

    override fun setupViewSlices(view: View) {
        toolbarSlice.setTitle(user.identity)
    }

    override fun setupVSActionObservers() {
        observe(toolbarSlice.getAction()) { onActionChanged(it) }
    }

    override fun setupVMStateObservers() {
        // TODO Implement body or it will be empty ):
    }

    private fun onActionChanged(action: ToolbarSlice.Action) = when (action) {
        ToolbarSlice.Action.BackClicked -> activity!!.onBackPressed()
        ToolbarSlice.Action.Idle -> Unit
    }

    companion object {
        const val KEY_CHANNEL_CONTROLLER = "KEY_CHANNEL_CONTROLLER"
    }
}
