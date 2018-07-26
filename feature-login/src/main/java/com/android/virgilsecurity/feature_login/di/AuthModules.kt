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

import LoginDiConst.KEY_MEDIATOR_LOGIN
import LoginDiConst.KEY_MEDIATOR_REGISTRATION
import LoginDiConst.LIVE_DATA_LOGIN
import LoginDiConst.LIVE_DATA_REGISTRATION
import LoginDiConst.STATE_SLICE_LOGIN
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import com.android.virgilsecurity.base.data.api.AuthApi
import com.android.virgilsecurity.common.data.remote.AuthRemote
import com.android.virgilsecurity.common.util.DoubleBack
import com.android.virgilsecurity.common.viewslice.StateSlice
import com.android.virgilsecurity.feature_login.data.repository.AuthInteractor
import com.android.virgilsecurity.feature_login.data.repository.AuthInteractorDefault
import com.android.virgilsecurity.feature_login.data.repository.UsersRepository
import com.android.virgilsecurity.feature_login.domain.login.LoadUsersDo
import com.android.virgilsecurity.feature_login.domain.login.LoadUsersDoDefault
import com.android.virgilsecurity.feature_login.domain.registration.SignInDo
import com.android.virgilsecurity.feature_login.domain.registration.SignInDoDefault
import com.android.virgilsecurity.feature_login.domain.registration.SignUpDo
import com.android.virgilsecurity.feature_login.domain.registration.SignUpDoDefault
import com.android.virgilsecurity.feature_login.viewmodel.login.LoginVM
import com.android.virgilsecurity.feature_login.viewmodel.login.LoginVMDefault
import com.android.virgilsecurity.feature_login.viewmodel.registration.RegistrationVM
import com.android.virgilsecurity.feature_login.viewmodel.registration.RegistrationVMDefault
import com.android.virgilsecurity.feature_login.viewslice.login.list.ViewPagerSlice
import com.android.virgilsecurity.feature_login.viewslice.login.list.ViewPagerSlice.Action
import com.android.virgilsecurity.feature_login.viewslice.login.list.ViewPagerSliceDefault
import com.android.virgilsecurity.feature_login.viewslice.login.list.adapter.UserPagerAdapter
import com.android.virgilsecurity.feature_login.viewslice.login.list.adapter.UsersPagerAdapterDefault
import com.android.virgilsecurity.feature_login.viewslice.login.state.StateSliceLogin
import com.android.virgilsecurity.feature_login.viewslice.registration.state.StateSliceRegistration
import com.android.virgilsecurity.feature_login.viewslice.registration.state.StateSliceRegistrationDefault
import com.android.virgilsecurity.feature_login.viewslice.registration.toolbar.ToolbarSlice
import com.android.virgilsecurity.feature_login.viewslice.registration.toolbar.ToolbarSliceRegistration
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
    bean { UsersRepositoryDefault(get(), get()) as UsersRepository }
    bean { LoadUsersDoDefault(get()) as LoadUsersDo }
    bean { SignInDoDefault(get(), get()) as SignInDo }
    bean(KEY_MEDIATOR_LOGIN) { MediatorLiveData<Action>() }
    bean { LoginVMDefault(get(KEY_MEDIATOR_LOGIN), get(), get()) as LoginVM }
}

val authActivityModule: Module = applicationContext {
    bean(name = LoginDiConst.KEY_SPAN_COUNT) { LoginDiConst.SPAN_COUNT }
    bean { DoubleBack() }
}

val loginControllerModule: Module = applicationContext {
    bean(LIVE_DATA_LOGIN) { MutableLiveData<Action>() }
    bean { UsersPagerAdapterDefault(get(), get(LIVE_DATA_LOGIN)) as UserPagerAdapter }
    bean { ViewPagerSliceDefault(get(), get(LIVE_DATA_LOGIN)) as ViewPagerSlice }
    bean(STATE_SLICE_LOGIN) { StateSliceLogin() as StateSlice }
}

val registrationControllerModule: Module = applicationContext {
    bean(KEY_MEDIATOR_REGISTRATION) { MediatorLiveData<RegistrationVM.State>() }
    bean { AuthRemote(get()) as AuthApi }
    bean { AuthInteractorDefault(get(), get(), get(), get(), get()) as AuthInteractor }
    bean { SignUpDoDefault(get(), get()) as SignUpDo }
    bean { RegistrationVMDefault(get(KEY_MEDIATOR_REGISTRATION), get()) as RegistrationVM }
    bean { StateSliceRegistrationDefault() as StateSliceRegistration }
    bean(LIVE_DATA_REGISTRATION) { MutableLiveData<ToolbarSlice.Action>() }
    bean { ToolbarSliceRegistration(get(LIVE_DATA_REGISTRATION)) as ToolbarSlice }
}

object LoginDiConst {
    const val KEY_SPAN_COUNT = "KEY_SPAN_COUNT"
    const val KEY_MEDIATOR_REGISTRATION = "KEY_MEDIATOR_REGISTRATION"
    const val KEY_MEDIATOR_LOGIN = "KEY_MEDIATOR_LOGIN"
    const val LIVE_DATA_LOGIN = "LIVE_DATA_LOGIN"
    const val LIVE_DATA_REGISTRATION = "LIVE_DATA_REGISTRATION"
    const val STATE_SLICE_LOGIN = "STATE_SLICE_LOGIN"

    const val SPAN_COUNT = 2
}
