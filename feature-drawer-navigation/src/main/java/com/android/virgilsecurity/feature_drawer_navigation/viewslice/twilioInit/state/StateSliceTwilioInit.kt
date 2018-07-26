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

package com.android.virgilsecurity.feature_drawer_navigation.viewslice.twilioInit.state

import android.view.View
import com.android.virgilsecurity.base.viewslice.BaseViewSlice
import com.android.virgilsecurity.common.viewslice.StateSlice
import kotlinx.android.synthetic.main.controller_twilio_init.*

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    7/26/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * StateSliceTwilioInit
 */
class StateSliceTwilioInit : BaseViewSlice(), StateSlice {

    override fun showLoading() = show(LOADING)

    override fun showContent() = show(CONTENT)

    override fun showError() = show(ERROR)

    private fun show(state: Int) {
        when (state) {
            CONTENT, ERROR -> { // For now our content is showing up that something happened
                clContentRoot.visibility = View.VISIBLE
                pbLoading.visibility = View.INVISIBLE
                tvInitializing.visibility = View.INVISIBLE
            }
            LOADING -> {
                clContentRoot.visibility = View.INVISIBLE
                pbLoading.visibility = View.VISIBLE
                tvInitializing.visibility = View.VISIBLE
            }
        }
    }

    companion object {
        const val CONTENT = 0
        const val LOADING = 1
        const val ERROR = 2
    }
}