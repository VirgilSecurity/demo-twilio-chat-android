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

package com.android.virgilsecurity.twiliodemo.view

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import com.android.virgilsecurity.base.view.Screen
import com.android.virgilsecurity.base.view.ScreenRouter
import com.android.virgilsecurity.common.view.ScreenChat
import com.android.virgilsecurity.feature_login.view.AuthActivity

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    6/21/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * ScreenRouterDefault
 */
class ScreenRouterDefault : ScreenRouter {

    override fun getScreenIntent(context: Context,
                                 screen: Screen): Intent? {
        val screenClass = getScreenClass(screen)
        return if (screenClass == null) null else Intent(context, screenClass)
    }

    override fun getScreenIntent(context: Context,
                                 screen: Screen,
                                 key: String,
                                 value: Parcelable): Intent? {
        val screenClass = getScreenClass(screen)

        return if (screenClass == null) null else Intent(context, screenClass).apply {
            putExtra(key, value)
        }
    }

    private fun getScreenClass(screen: Screen) = when (screen) {
        ScreenChat.Login -> AuthActivity::class.java
//        ScreenChat.ChannelsList -> ChannelsListActivity::class.java
        ScreenChat.Channel -> null // TODO
        else -> null
    }
}