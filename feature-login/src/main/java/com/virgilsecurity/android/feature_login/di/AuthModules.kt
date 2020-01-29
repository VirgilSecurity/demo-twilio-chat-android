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

import LoginDiConst.KEY_SPAN_COUNT
import LoginDiConst.LIVE_DATA_LOGIN
import LoginDiConst.LIVE_DATA_REGISTRATION
import LoginDiConst.SPAN_COUNT
import LoginDiConst.STATE_SLICE_LOGIN
import LoginDiConst.VM_LOGIN
import LoginDiConst.VM_REGISTRATION
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
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
import com.virgilsecurity.android.feature_login.view.AuthActivity
import com.virgilsecurity.android.feature_login.view.RegistrationController
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
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

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
val authActivityModule: Module = module {
    single(named(KEY_SPAN_COUNT)) { SPAN_COUNT }
    single { DoubleBack() }

    factory { LoadUsersDoDefault(get()) as LoadUsersDo }

    module {
        factory(named(VM_LOGIN)) { MediatorLiveData<LoginVM.State>() }
        factory(named(VM_LOGIN)) { LoginVMDefault(get(), get()) as LoginVM }
    }
}

val loginControllerModule: Module = module {
    single(named(LIVE_DATA_LOGIN)) { MutableLiveData<ViewPagerSlice.Action>() }
    factory { UsersPagerAdapterDefault(get(), get(named(LIVE_DATA_LOGIN))) as UserPagerAdapter }
    factory { ViewPagerSliceDefault(get(), get(named(LIVE_DATA_LOGIN))) as ViewPagerSlice }
    factory(named(STATE_SLICE_LOGIN)) { StateSliceLogin() as StateSlice }
}

val registrationControllerModule: Module = module {
    single { AuthRemote(get()) as AuthApi }
    single { AuthInteractorDefault(get(), get(), get(), get()) as AuthInteractor }

    factory { SignUpDoDefault(get(), get()) as SignUpDo }
    factory { StateSliceRegistrationDefault(get()) as StateSliceRegistration }
    factory(named(LIVE_DATA_REGISTRATION)) { MutableLiveData<ToolbarSlice.Action>() }
    factory { ToolbarSliceRegistration(get(named(LIVE_DATA_REGISTRATION))) as ToolbarSlice }

    module {
        factory { MediatorLiveData<RegistrationVM.State>() }
        factory(named(VM_REGISTRATION)) { RegistrationVMDefault(get(), get()) as RegistrationVM }
    }
}

object LoginDiConst {
    const val KEY_SPAN_COUNT = "KEY_SPAN_COUNT"
    const val LIVE_DATA_LOGIN = "LIVE_DATA_LOGIN"
    const val LIVE_DATA_REGISTRATION = "LIVE_DATA_REGISTRATION"
    const val STATE_SLICE_LOGIN = "STATE_SLICE_LOGIN"
    const val VM_LOGIN = "VM_LOGIN"
    const val VM_REGISTRATION = "VM_REGISTRATION"

    const val SPAN_COUNT = 2
}
