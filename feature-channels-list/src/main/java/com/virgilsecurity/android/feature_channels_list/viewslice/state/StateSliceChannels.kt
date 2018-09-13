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

package com.virgilsecurity.android.feature_channels_list.viewslice.state

import android.view.View
import com.virgilsecurity.android.base.viewslice.BaseViewSlice
import com.virgilsecurity.android.common.viewslice.StateSliceEmptyable
import kotlinx.android.synthetic.main.controller_channels_list.*

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    8/8/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * StateSliceChannels
 */
class StateSliceChannels : BaseViewSlice(), StateSliceEmptyable {

    override fun showLoading() = show(LOADING)

    override fun showContent() = show(CONTENT)

    override fun showError() = show(ERROR)

    override fun showEmpty() = show(EMPTY)

    private fun show(state: Int) {
        when (state) {
            CONTENT -> {
                pbLoading.visibility = View.INVISIBLE
                clNoChannels.visibility = View.INVISIBLE
                rvChannels.visibility = View.VISIBLE
                tvError.visibility = View.INVISIBLE
            }
            LOADING -> {
                pbLoading.visibility = View.VISIBLE
                clNoChannels.visibility = View.INVISIBLE
                rvChannels.visibility = View.INVISIBLE
                tvError.visibility = View.INVISIBLE
            }
            EMPTY -> {
                pbLoading.visibility = View.INVISIBLE
                clNoChannels.visibility = View.VISIBLE
                rvChannels.visibility = View.INVISIBLE
                tvError.visibility = View.INVISIBLE
            }
            ERROR -> {
                pbLoading.visibility = View.INVISIBLE
                clNoChannels.visibility = View.INVISIBLE
                rvChannels.visibility = View.INVISIBLE
                tvError.visibility = View.VISIBLE
            }
        }
    }

    companion object {
        const val CONTENT = 0
        const val LOADING = 1
        const val ERROR = 2
        const val EMPTY = 3
    }
}