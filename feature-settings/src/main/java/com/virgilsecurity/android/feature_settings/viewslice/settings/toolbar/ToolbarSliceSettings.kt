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

package com.virgilsecurity.android.feature_settings.viewslice.settings.toolbar

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import android.graphics.Point
import com.virgilsecurity.android.base.viewslice.BaseViewSlice
import com.virgilsecurity.android.common.util.UiUtils
import com.virgilsecurity.android.common.view.Toolbar
import com.virgilsecurity.android.feature_settings.R

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    7/17/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * ToolbarSliceSettings
 */
class ToolbarSliceSettings(
        private val actionLiveData: MutableLiveData<ToolbarSlice.Action>
) : BaseViewSlice(), ToolbarSlice {

    private lateinit var toolbar: Toolbar

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        this.toolbar = toolbarSettings as Toolbar
        setupToolbar()
    }

    private fun setupToolbar() {
        toolbar.background = context.getDrawable(R.color.colorPrimaryNight)

        toolbar.showBackButton()
        toolbar.showMenuButton()

        toolbar.setOnToolbarItemClickListener {
            when (it.id) {
                R.id.ivBack -> {
                    actionLiveData.value = ToolbarSlice.Action.BackClicked
                    actionLiveData.value = ToolbarSlice.Action.Idle
                }
                R.id.ivMenu -> {
                    actionLiveData.value = ToolbarSlice.Action.MenuClicked(
                        Point(it.x.toInt() + UiUtils.pixelsToDp(it.width, context),
                              it.y.toInt())
                    )
                    actionLiveData.value = ToolbarSlice.Action.Idle
                }
            }
        }
    }

    override fun getAction(): LiveData<ToolbarSlice.Action> = actionLiveData
}
