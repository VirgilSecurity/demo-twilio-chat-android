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

package com.android.virgilsecurity.twiliodemo.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.android.virgilsecurity.twiliodemo.R
import com.android.virgilsecurity.twiliodemo.data.remote.virgil.VirgilRx
import com.android.virgilsecurity.twiliodemo.ui.base.BaseActivity
import com.android.virgilsecurity.twiliodemo.util.OnFinishTimer
import com.android.virgilsecurity.twiliodemo.util.UiUtils
import kotlinx.android.synthetic.main.activity_login.*
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

class LoginActivity : BaseActivity() {

    private var secondPress: Boolean = false

    companion object {
        fun startWithFinish(from: Activity) {
            from.startActivity(Intent(from, LoginActivity::class.java))
            from.finish()
        }

        fun startClearTop(from: Activity) {
            from.startActivity(Intent(from, LoginActivity::class.java)
                                       .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                                         Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    override fun provideLayoutId() = R.layout.activity_login

    override fun preInitUi() {
        // TODO Implement body or it will be empty ):
    }

    override fun initUi() {
        UiUtils.replaceFragmentNoTag(supportFragmentManager,
                                     flBaseContainer.id,
                                     LoginFragment.newInstance())

    }

    override fun initViewCallbacks() {
        // TODO Implement body or it will be empty ):
    }

    override fun initData() {
        // TODO Implement body or it will be empty ):
    }

    override fun onBackPressed() {
        hideKeyboard()

        if (secondPress)
            super.onBackPressed()
        else
            UiUtils.toast(this, getString(R.string.press_exit_once_more))

        secondPress = true

        object : OnFinishTimer(2000, 100) {
            override fun onFinish() {
                secondPress = false
            }
        }.start()
    }
}