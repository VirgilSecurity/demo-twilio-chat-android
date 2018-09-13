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

package com.virgilsecurity.android.common.data.local.users

import android.content.Context
import com.google.gson.Gson
import com.virgilsecurity.android.base.data.model.User
import com.virgilsecurity.android.base.data.properties.UserProperties
import com.virgilsecurity.android.common.data.helper.preferences.PreferenceHelper
import com.virgilsecurity.android.common.data.helper.preferences.PreferenceHelper.get
import com.virgilsecurity.android.common.data.helper.preferences.PreferenceHelper.set
import com.virgilsecurity.android.common.data.helper.preferences.PreferenceHelper.edit

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    5/29/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */
class UserPropertiesDefault(context: Context) : UserProperties {

    companion object {
        private const val CURRENT_USER = "CURRENT_USER"
    }

    private val preferences = PreferenceHelper.defaultPrefs(
        context)

    override var currentUser: User?
        get() {
            val serialized: String = preferences[CURRENT_USER] ?: return null
            return Gson().fromJson(serialized, User::class.java)
        }
        set(user) {
            preferences[CURRENT_USER] = Gson().toJson(user)
        }

    override fun clearCurrentUser() {
        preferences.edit { it.remove(CURRENT_USER) }
    }
}