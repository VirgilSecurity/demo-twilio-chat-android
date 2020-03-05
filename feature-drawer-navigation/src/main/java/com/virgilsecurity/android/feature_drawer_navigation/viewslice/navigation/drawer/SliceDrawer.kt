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

package com.virgilsecurity.android.feature_drawer_navigation.viewslice.navigation.drawer

import android.view.View
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.navigation.NavigationView
import com.virgilsecurity.android.base.viewslice.BaseViewSlice
import com.virgilsecurity.android.bcommon.util.ImageStorage
import com.virgilsecurity.android.bcommon.util.UiUtils
import com.virgilsecurity.android.bcommon.util.UserUtils
import com.virgilsecurity.android.feature_drawer_navigation.R
import de.hdodenhof.circleimageview.CircleImageView

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    7/12/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * DrawerSliceDefault
 */
class SliceDrawer(
        private val actionLiveData: MutableLiveData<Action>,
        private val imageStorage: ImageStorage
) : BaseViewSlice() {

    private lateinit var nvNavigation: NavigationView
    private lateinit var ivUserPicDrawer: CircleImageView
    private lateinit var tvInitialsDrawer: TextView
    private lateinit var tvUsernameDrawer: TextView

    override fun setupViews() {
        with(window) {
            this@SliceDrawer.nvNavigation = findViewById(R.id.nvNavigation)
            this@SliceDrawer.ivUserPicDrawer =
                    nvNavigation.getHeaderView(0).findViewById(R.id.ivUserPicDrawer)
            this@SliceDrawer.tvInitialsDrawer =
                    nvNavigation.getHeaderView(0).findViewById(R.id.tvInitialsDrawer)
            this@SliceDrawer.tvUsernameDrawer =
                    nvNavigation.getHeaderView(0).findViewById(R.id.tvUsernameDrawer)

        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        setupDrawer()
    }

    private fun setupDrawer() {
        nvNavigation.menu.getItem(1).isChecked = true

        nvNavigation.setNavigationItemSelectedListener {
            if (!it.isChecked) {
                when (it.itemId) {
                    R.id.itemContacts -> {
                        actionLiveData.value = Action.ContactsClicked
                        actionLiveData.value = Action.Idle
                    }
                    R.id.itemChats -> {
                        actionLiveData.value = Action.ChannelsListClicked
                        actionLiveData.value = Action.Idle
                    }
                    R.id.itemSettings -> {
                        actionLiveData.value = Action.SettingsClicked
                        actionLiveData.value = Action.Idle
                    }
                }

                it.isChecked = true
            } else {
                actionLiveData.value = Action.SameItemClicked
                actionLiveData.value = Action.Idle
            }

            return@setNavigationItemSelectedListener true
        }
    }

    fun getAction(): LiveData<Action> = actionLiveData

    fun setHeader(identity: String, picturePath: String?) {
        if (picturePath != null) {
            Glide.with(context)
                    .load(imageStorage.load(picturePath))
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivUserPicDrawer)
        } else {
            tvInitialsDrawer.text = UserUtils.firstInitials(identity)
            tvInitialsDrawer.visibility = View.VISIBLE
            Glide.with(context)
                    .load(UiUtils.letterBasedDrawable(context, R.array.loginBackgrounds,
                                                      tvInitialsDrawer.text[0]
                                                              .toLowerCase()
                                                              .toString()))
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivUserPicDrawer)
        }

        tvUsernameDrawer.text = identity
    }

    fun setItemSelected(position: Int) {
        nvNavigation.menu.getItem(position).isChecked = true
    }

    sealed class Action {
        object ContactsClicked : Action()
        object ChannelsListClicked : Action()
        object SettingsClicked : Action()
        object SameItemClicked : Action()
        object Idle : Action()
    }
}
