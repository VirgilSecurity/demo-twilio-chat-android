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
import android.arch.lifecycle.*
import android.support.v4.app.FragmentActivity
import com.android.virgilsecurity.feature_login.data.repository.UsersRepository
import com.android.virgilsecurity.common.util.DoubleBack
import com.android.virgilsecurity.common.viewslice.StateSlice
import com.android.virgilsecurity.feature_login.domain.login.LoadUsersDo
import com.android.virgilsecurity.feature_login.domain.login.LoadUsersDoDefault
import com.android.virgilsecurity.feature_login.view.AuthActivity
import com.android.virgilsecurity.feature_login.viewmodel.login.LoginVM
import com.android.virgilsecurity.feature_login.viewmodel.login.LoginVMDefault
import com.android.virgilsecurity.feature_login.viewslice.list.ViewPagerSlice
import com.android.virgilsecurity.feature_login.viewslice.list.ViewPagerSlice.Action
import com.android.virgilsecurity.feature_login.viewslice.list.ViewPagerSliceDefault
import com.android.virgilsecurity.feature_login.viewslice.list.adapter.UserPagerAdapter
import com.android.virgilsecurity.feature_login.viewslice.list.adapter.UsersPagerAdapterDefault
import com.android.virgilsecurity.feature_login.viewslice.state.StateSliceDefault
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    5/31/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */
// TODO check dates in print above in all files
/**
 * LoginModules
 */
val authModule: Module = applicationContext {
    bean(KEY_AUTH_ACTIVITY) { AuthActivity() as FragmentActivity }
    bean { UsersRepositoryDefault(get(), get()) as UsersRepository }
    bean { LoadUsersDoDefault(get()) as LoadUsersDo }
    bean { MediatorLiveData<Action>() }
    bean {
        LoginVMDefault(get(), get()) as LoginVM
    }
}

val authActivityModule: Module = applicationContext {
    bean(name = LoginDiConst.KEY_SPAN_COUNT) { LoginDiConst.SPAN_COUNT }
    bean { DoubleBack() }
}

val loginFragmentModule: Module = applicationContext {
    bean { UsersPagerAdapterDefault(get(), get()) as UserPagerAdapter }
    bean { MutableLiveData<Action>() }
    bean { ViewPagerSliceDefault(get(), get()) as ViewPagerSlice }
    bean { StateSliceDefault() as StateSlice }
}

object LoginDiConst {
    const val KEY_SPAN_COUNT = "KEY_SPAN_COUNT"
    const val KEY_AUTH_ACTIVITY = "KEY_AUTH_ACTIVITY"

    const val SPAN_COUNT = 2
}
