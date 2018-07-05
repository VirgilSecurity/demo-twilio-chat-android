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

import LoginDiConst.KEY_AUTH_ACTIVITY
import LoginDiConst.KEY_VM_PROVIDER_FACTORY
import android.app.Activity
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.arch.persistence.room.Room
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.GridLayout
import com.android.virgilsecurity.common.data.api.UsersApi
import com.android.virgilsecurity.common.data.local.RoomDS
import com.android.virgilsecurity.common.data.local.UsersLocalDS
import com.android.virgilsecurity.common.data.repository.UsersRepository
import com.android.virgilsecurity.common.util.DoubleBack
import com.android.virgilsecurity.feature_login.domain.LoadUsersDo
import com.android.virgilsecurity.feature_login.domain.LoadUsersDoDefault
import com.android.virgilsecurity.feature_login.view.AuthActivity
import com.android.virgilsecurity.feature_login.viewmodel.LoginVMFactory
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
val usersPageModule: Module = applicationContext {
    bean(name = LoginDiConst.KEY_SPAN_COUNT) { LoginDiConst.SPAN_COUNT }
    bean {
        GridLayoutManager(get(), get(LoginDiConst.KEY_SPAN_COUNT),
                          GridLayout.HORIZONTAL, false) as RecyclerView.LayoutManager
    }
    bean { DoubleBack() }
    bean(KEY_AUTH_ACTIVITY) { AuthActivity() }
    bean { UsersRepositoryDefault(get()) as UsersRepository }
    bean { LoadUsersDoDefault(get()) as LoadUsersDo }
    bean { LoginVMFactory(get()) }
    bean {
        ViewModelProviders.of(get(KEY_AUTH_ACTIVITY) as Activity,
                              get() as ViewModelProvider.Factory)
    }
}

object LoginDiConst {
    const val KEY_SPAN_COUNT = "KEY_SPAN_COUNT"
    const val KEY_AUTH_ACTIVITY = "KEY_AUTH_ACTIVITY"

    const val SPAN_COUNT = 2
}

//android:numColumns="auto_fit"
//android:stretchMode="columnWidth"
//android:gravity="center"
//android:columnWidth="80dp"
//android:horizontalSpacing="57dp"
//android:verticalSpacing="40dp"