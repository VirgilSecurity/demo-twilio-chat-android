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

package com.android.virgilsecurity.feature_drawer_navigation.viewslice.drawer

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.OnLifecycleEvent
import android.net.Uri
import com.android.virgilsecurity.base.viewslice.BaseViewSlice
import com.android.virgilsecurity.common.util.ImageStorage
import com.android.virgilsecurity.common.util.UserUtils
import com.android.virgilsecurity.feature_drawer_navigation.R
import kotlinx.android.synthetic.main.activity_drawer_navigation.*
import kotlinx.android.synthetic.main.layout_drawer_header.*
import kotlinx.android.synthetic.main.layout_drawer_header.view.*

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
class DrawerSliceDefault(
        private val actionLiveData: MutableLiveData<DrawerSlice.Action>,
        private val imageStorage: ImageStorage
) : BaseViewSlice(), DrawerSlice {

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        setupDrawer()
    }

    private fun setupDrawer() {
        nvNavigation.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.itemContacts -> actionLiveData.value = DrawerSlice.Action.ContactsClicked
                R.id.itemChats -> actionLiveData.value = DrawerSlice.Action.ChannelsListClicked
                R.id.itemSettings -> actionLiveData.value = DrawerSlice.Action.SettingsClicked
            }

            return@setNavigationItemSelectedListener true
        }
    }

    override fun getAction(): LiveData<DrawerSlice.Action> = actionLiveData

    override fun setHeader(identity: String, picturePath: String?) {
        if (picturePath != null) {
            nvNavigation.getHeaderView(0).ivUserPicDrawer.setImageBitmap(imageStorage.get(Uri.parse(picturePath)))
        } else {
            nvNavigation.getHeaderView(0).tvInitialsDrawer.text = UserUtils.firstInitials(identity)
            nvNavigation.getHeaderView(0).ivUserPicDrawer.background = context.getDrawable(R.drawable.dark_red_red_gradient_oval)
        }

        nvNavigation.getHeaderView(0).tvUsernameDrawer.text = identity
    }
}