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

package com.virgilsecurity.android.virgilmessenger

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.virgilsecurity.android.base.data.model.User
import com.virgilsecurity.android.base.data.properties.UserProperties
import com.virgilsecurity.android.base.view.ScreenRouter
import com.virgilsecurity.android.common.view.ScreenChat
import org.koin.android.ext.android.inject


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

class SplashActivity : AppCompatActivity() {

    private val userProperties: UserProperties by inject()
    private val screenRouter: ScreenRouter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bottom_sheet_attachments)

//        initBottomSheet()

        if (isAuthenticated())
            startChannelsActivity()
        else
            startLoginActivity()
    }

    private fun initBottomSheet() {
//        TestBottomSheet.instance().show(supportFragmentManager, "test_bottom_sheet")
    }

    private fun isAuthenticated(): Boolean {
        return userProperties.currentUser != null
    }

    override fun onBackPressed() {

    }

    private fun startChannelsActivity() {
        screenRouter.getScreenIntent(this, ScreenChat.DrawerNavigation,
                                     User.EXTRA_USER, userProperties.currentUser!!)
                .run {
                    startActivity(this)
                    finish()
                }
    }

    private fun startLoginActivity() {
        screenRouter.getScreenIntent(this, ScreenChat.Login)
                .apply {
                    this?.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                }
                .run {
                    startActivity(this)
                    finish()
                }
    }
}
