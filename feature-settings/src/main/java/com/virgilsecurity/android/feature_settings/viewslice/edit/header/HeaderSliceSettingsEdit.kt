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

package com.virgilsecurity.android.feature_settings.viewslice.edit.header

import android.net.Uri
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.virgilsecurity.android.base.viewslice.BaseViewSlice
import com.virgilsecurity.android.bcommon.util.ImageStorage
import com.virgilsecurity.android.bcommon.util.ImageStorageLocal
import com.virgilsecurity.android.bcommon.util.UiUtils
import com.virgilsecurity.android.bcommon.util.UserUtils
import com.virgilsecurity.android.feature_settings.R
import de.hdodenhof.circleimageview.CircleImageView

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
class HeaderSliceSettingsEdit(
        private val mutableLiveData: MutableLiveData<Action>,
        private val imageStorage: ImageStorage
) : BaseViewSlice() {

    private lateinit var ivUserPicEdit: CircleImageView
    private lateinit var tvUsernameEditSettingsHeader: TextView
    private lateinit var etUsername: EditText

    override fun setupViews() {
        with(window) {
            this@HeaderSliceSettingsEdit.ivUserPicEdit = findViewById(R.id.ivUserPicEdit)
            this@HeaderSliceSettingsEdit.tvUsernameEditSettingsHeader =
                    findViewById(R.id.tvUsernameEditSettingsHeader)
            this@HeaderSliceSettingsEdit.etUsername = findViewById(R.id.etUsername)

            ivUserPicEdit.setOnClickListener {
                mutableLiveData.value = Action.ChangePicClicked
                mutableLiveData.value = Action.Idle
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        setupViews()
    }

    fun setName(name: String) {
        tvUsernameEditSettingsHeader.text = name
        etUsername.setText(name)
    }

    /**
     * If picture path is null - then user's name initials will be shown, taken from the identity.
     */
    fun setUserPic(identity: String, picturePath: String?) {
        if (picturePath != null) {
            Glide.with(context)
                    .load(imageStorage.load(picturePath))
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivUserPicEdit)
        } else {
            Glide.with(context)
                    .load(UiUtils.letterBasedDrawable(context, R.array.loginBackgrounds,
                                                      UserUtils.firstInitials(identity)[0]
                                                              .toLowerCase()
                                                              .toString()))
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivUserPicEdit)
        }
    }

    fun getAction(): LiveData<Action> = mutableLiveData

    sealed class Action {
        object ChangePicClicked : Action()
        object Idle : Action()
    }
}
