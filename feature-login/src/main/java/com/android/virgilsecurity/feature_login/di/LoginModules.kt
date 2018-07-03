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

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.widget.GridLayout
import android.widget.LinearLayout
import com.android.virgilsecurity.feature_login.view.adapter.IndentItemDecoration
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    5/31/185/31/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * LoginModules
 */
val loginModule : Module = applicationContext {
    bean { IndentItemDecoration(LoginDiConst.INDENT_LEFT,
                                  LoginDiConst.INDENT_TOP,
                                  LoginDiConst.INDENT_RIGHT,
                                  LoginDiConst.INDENT_BOTTOM) }
    bean(name = LoginDiConst.KEY_SPAN_COUNT) { LoginDiConst.SPAN_COUNT }
    bean { GridLayoutManager(get(), get(LoginDiConst.KEY_SPAN_COUNT),
                             GridLayout.HORIZONTAL, false) }
}

object LoginDiConst {
    const val KEY_SPAN_COUNT = "KEY_SPAN_COUNT"

    const val INDENT_LEFT = 57
    const val INDENT_TOP = 40
    const val INDENT_RIGHT = 0
    const val INDENT_BOTTOM = 0
    const val SPAN_COUNT = 2
}

//android:numColumns="auto_fit"
//android:stretchMode="columnWidth"
//android:gravity="center"
//android:columnWidth="80dp"
//android:horizontalSpacing="57dp"
//android:verticalSpacing="40dp"