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

package com.virgilsecurity.android.feature_settings.viewslice.header

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.OnLifecycleEvent
import android.net.Uri
import android.view.View
import com.virgilsecurity.android.base.viewslice.BaseViewSlice
import com.virgilsecurity.android.common.util.ImageStorage
import com.virgilsecurity.android.common.util.UserUtils
import com.virgilsecurity.android.feature_settings.R
import kotlinx.android.synthetic.main.controller_settings.*

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
 * HeaderSliceSettings
 */
class HeaderSliceSettings(
        private val mutableLiveData: MutableLiveData<HeaderSlice.Action>,
        private val imageStorage: ImageStorage
) : BaseViewSlice(), HeaderSlice {

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        setupViews()
    }

    private fun setupViews() {
        ivChangeUserPic.setOnClickListener {
            mutableLiveData.value = HeaderSlice.Action.ChangePicClicked
            mutableLiveData.value = HeaderSlice.Action.Idle
        }
    }

    override fun setName(name: String) {
        tvUsernameSettings.text = name
        tvUsernameSettingsInfo.text = name
    }

    override fun setUserPic(identity: String, picturePath: String?) {
        if (picturePath != null) {
            ivUserPicSettings.setImageBitmap(imageStorage.get(Uri.parse(picturePath)))
        } else {
            tvInitials.text = UserUtils.firstInitials(identity)
            tvInitials.visibility = View.VISIBLE
            ivUserPicSettings.background = context.getDrawable(R.drawable.dark_red_red_gradient_oval)
        }

    }

    override fun getAction(): LiveData<HeaderSlice.Action> = mutableLiveData
}