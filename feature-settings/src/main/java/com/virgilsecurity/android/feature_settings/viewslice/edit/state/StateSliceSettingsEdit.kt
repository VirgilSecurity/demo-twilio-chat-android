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

package com.virgilsecurity.android.feature_settings.viewslice.edit.state

import android.animation.ValueAnimator
import android.view.View
import android.widget.FrameLayout
import com.sdsmdg.harjot.vectormaster.VectorMasterView
import com.virgilsecurity.android.base.viewslice.BaseViewSlice
import com.virgilsecurity.android.bcommon.util.UiUtils
import com.virgilsecurity.android.feature_settings.R

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    7/25/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * StateSliceSettingsEdit
 */
class StateSliceSettingsEdit : BaseViewSlice() {

    private lateinit var loadingFader: ValueAnimator

    private lateinit var flLoading: FrameLayout
    private lateinit var ivLoading: VectorMasterView

    override fun setupViews() {
        with(window) {
            this@StateSliceSettingsEdit.flLoading = findViewById(R.id.flLoading)
            this@StateSliceSettingsEdit.ivLoading = findViewById(R.id.ivLoading)
        }
    }

    fun showLoading() = showState(LOADING)

    fun showError() = showState(ERROR)

    private fun showState(state: Int) {
        when (state) {
           LOADING -> {
               flLoading.visibility = View.VISIBLE
               loadingFader = UiUtils.fadeVectorReverse(ivLoading)
           }
           ERROR -> {
               flLoading.visibility = View.INVISIBLE
               loadingFader.end()
               UiUtils.toast(context, resources.getString(R.string.logout_error))
           }
        }
    }

    companion object {
        const val LOADING = 0
        const val ERROR = 1
    }
}
