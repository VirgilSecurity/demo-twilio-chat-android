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

import LoginDiConst.VM_AUTH
import androidx.lifecycle.MediatorLiveData
import com.virgilsecurity.android.base.data.api.AuthApi
import com.virgilsecurity.android.base.extension.moduleWithScope
import com.virgilsecurity.android.common.data.remote.auth.AuthRemote
import com.virgilsecurity.android.common.util.DoubleBack
import com.virgilsecurity.android.feature_login.data.interactor.AuthInteractor
import com.virgilsecurity.android.feature_login.data.interactor.AuthInteractorDefault
import com.virgilsecurity.android.feature_login.domain.login.LoadUsersDo
import com.virgilsecurity.android.feature_login.domain.login.LoadUsersDoDefault
import com.virgilsecurity.android.feature_login.domain.registration.SignUpDo
import com.virgilsecurity.android.feature_login.domain.registration.SignUpDoDefault
import com.virgilsecurity.android.feature_login.view.RegistrationController
import com.virgilsecurity.android.feature_login.viewmodel.login.AuthVM
import com.virgilsecurity.android.feature_login.viewmodel.login.AuthVMDefault
import com.virgilsecurity.android.feature_login.viewmodel.registration.RegistrationVM
import com.virgilsecurity.android.feature_login.viewmodel.registration.RegistrationVMDefault
import org.koin.androidx.viewmodel.dsl.viewModel
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
    single { DoubleBack() }

    scope(named(VM_AUTH)) {
        scoped { LoadUsersDoDefault(get()) as LoadUsersDo }
        scoped { MediatorLiveData<AuthVM.State>() }
        viewModel { AuthVMDefault(get(), get()) as AuthVM }
    }
}

val registrationControllerModule: Module = moduleWithScope(named<RegistrationController>()) {
    scoped { AuthInteractorDefault(get(), get(), get(), get()) as AuthInteractor }
    scoped { SignUpDoDefault(get(), get()) as SignUpDo }
    scoped { MediatorLiveData<RegistrationVM.State>() }
    viewModel { RegistrationVMDefault(get(), get()) as RegistrationVM }
}

object LoginDiConst {
    const val VM_AUTH = "VM_AUTH"
    const val VM_AUTH_SCOPE_ID = "VM_AUTH_SCOPE_ID"
}
