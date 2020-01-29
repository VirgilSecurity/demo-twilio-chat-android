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

package com.virgilsecurity.android.feature_settings.view

import android.view.View
import com.virgilsecurity.android.base.extension.observe
import com.virgilsecurity.android.base.view.BaseController
import com.virgilsecurity.android.feature_settings.R
import com.virgilsecurity.android.feature_settings.viewslice.about.toolbar.ToolbarSlice
import org.koin.core.inject

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    9/24/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * AboutController
 */
class AboutController : BaseController() {

    override val layoutResourceId: Int = R.layout.controller_about

    private val toolbarSlice: ToolbarSlice by inject()

    override fun init() {}

    override fun initViewSlices(view: View) {
        toolbarSlice.init(lifecycle, view)
    }

    override fun setupViewSlices(view: View) {}

    override fun setupVSActionObservers() {
        observe(toolbarSlice.getAction(), ::onToolbarActionChanged)
    }

    override fun setupVMStateObservers() {}

    override fun initData() {}

    private fun onToolbarActionChanged(action: ToolbarSlice.Action) = when (action) {
        ToolbarSlice.Action.BackClicked -> {
            hideKeyboard()
            backPress()
        }
        ToolbarSlice.Action.Idle -> Unit
    }

    private fun backPress() {
        router.popCurrentController()
    }

    companion object {
        const val KEY_ABOUT_CONTROLLER = "KEY_ABOUT_CONTROLLER"
    }
}
