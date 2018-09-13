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

import LoginDiConst.CONTEXT_AUTH_ACTIVITY
import LoginDiConst.CONTEXT_LOGIN_CONTROLLER
import LoginDiConst.CONTEXT_REGISTRATION_CONTROLLER
import LoginDiConst.KEY_MEDIATOR_LOGIN
import LoginDiConst.KEY_MEDIATOR_REGISTRATION
import LoginDiConst.LIVE_DATA_LOGIN
import LoginDiConst.LIVE_DATA_REGISTRATION
import LoginDiConst.STATE_SLICE_LOGIN
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import com.virgilsecurity.android.base.data.api.AuthApi
import com.virgilsecurity.android.common.data.remote.auth.AuthRemote
import com.virgilsecurity.android.common.util.DoubleBack
import com.virgilsecurity.android.common.viewslice.StateSlice
import com.virgilsecurity.android.feature_login.data.interactor.AuthInteractor
import com.virgilsecurity.android.feature_login.data.interactor.AuthInteractorDefault
import com.virgilsecurity.android.feature_login.domain.login.LoadUsersDo
import com.virgilsecurity.android.feature_login.domain.login.LoadUsersDoDefault
import com.virgilsecurity.android.feature_login.domain.registration.SignUpDo
import com.virgilsecurity.android.feature_login.domain.registration.SignUpDoDefault
import com.virgilsecurity.android.feature_login.viewmodel.login.LoginVM
import com.virgilsecurity.android.feature_login.viewmodel.login.LoginVMDefault
import com.virgilsecurity.android.feature_login.viewmodel.registration.RegistrationVM
import com.virgilsecurity.android.feature_login.viewmodel.registration.RegistrationVMDefault
import com.virgilsecurity.android.feature_login.viewslice.login.list.ViewPagerSlice
import com.virgilsecurity.android.feature_login.viewslice.login.list.ViewPagerSliceDefault
import com.virgilsecurity.android.feature_login.viewslice.login.list.adapter.UserPagerAdapter
import com.virgilsecurity.android.feature_login.viewslice.login.list.adapter.UsersPagerAdapterDefault
import com.virgilsecurity.android.feature_login.viewslice.login.state.StateSliceLogin
import com.virgilsecurity.android.feature_login.viewslice.registration.state.StateSliceRegistration
import com.virgilsecurity.android.feature_login.viewslice.registration.state.StateSliceRegistrationDefault
import com.virgilsecurity.android.feature_login.viewslice.registration.toolbar.ToolbarSlice
import com.virgilsecurity.android.feature_login.viewslice.registration.toolbar.ToolbarSliceRegistration
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

/**
 * LoginModules
 */
val authActivityModule: Module = applicationContext {
    bean(name = LoginDiConst.KEY_SPAN_COUNT) { LoginDiConst.SPAN_COUNT }
    bean { DoubleBack() }

    context(CONTEXT_AUTH_ACTIVITY) {
        bean { LoadUsersDoDefault(get()) as LoadUsersDo }
        bean(KEY_MEDIATOR_LOGIN) { MediatorLiveData<LoginVM.State>() }
        bean { LoginVMDefault(get(KEY_MEDIATOR_LOGIN), get()) as LoginVM }
    }
}

val loginControllerModule: Module = applicationContext {
    context(CONTEXT_LOGIN_CONTROLLER) {
        bean(LIVE_DATA_LOGIN) { MutableLiveData<ViewPagerSlice.Action>() }
        bean { UsersPagerAdapterDefault(get(), get(LIVE_DATA_LOGIN)) as UserPagerAdapter }
        bean { ViewPagerSliceDefault(get(), get(LIVE_DATA_LOGIN)) as ViewPagerSlice }
        bean(STATE_SLICE_LOGIN) { StateSliceLogin() as StateSlice }
    }
}

val registrationControllerModule: Module = applicationContext {
    bean { AuthRemote(get()) as AuthApi }
    bean { AuthInteractorDefault(get(), get(), get(), get()) as AuthInteractor }

    context(CONTEXT_REGISTRATION_CONTROLLER) {
        bean(KEY_MEDIATOR_REGISTRATION) { MediatorLiveData<RegistrationVM.State>() }
        bean { SignUpDoDefault(get(), get()) as SignUpDo }
        bean { RegistrationVMDefault(get(KEY_MEDIATOR_REGISTRATION), get()) as RegistrationVM }
        bean { StateSliceRegistrationDefault(get()) as StateSliceRegistration }
        bean(LIVE_DATA_REGISTRATION) { MutableLiveData<ToolbarSlice.Action>() }
        bean { ToolbarSliceRegistration(get(LIVE_DATA_REGISTRATION)) as ToolbarSlice }
    }
}

object LoginDiConst {
    const val KEY_SPAN_COUNT = "KEY_SPAN_COUNT"
    const val KEY_MEDIATOR_REGISTRATION = "KEY_MEDIATOR_REGISTRATION"
    const val KEY_MEDIATOR_LOGIN = "KEY_MEDIATOR_LOGIN"
    const val LIVE_DATA_LOGIN = "LIVE_DATA_LOGIN"
    const val LIVE_DATA_REGISTRATION = "LIVE_DATA_REGISTRATION"
    const val STATE_SLICE_LOGIN = "STATE_SLICE_LOGIN"

    const val CONTEXT_AUTH_ACTIVITY = "CONTEXT_AUTH_ACTIVITY"
    const val CONTEXT_LOGIN_CONTROLLER = "CONTEXT_LOGIN_CONTROLLER"
    const val CONTEXT_REGISTRATION_CONTROLLER = "CONTEXT_REGISTRATION_CONTROLLER"

    const val SPAN_COUNT = 2
}
