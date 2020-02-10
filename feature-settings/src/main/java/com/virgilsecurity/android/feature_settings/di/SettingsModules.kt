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

package com.virgilsecurity.android.feature_settings.di

import androidx.lifecycle.MediatorLiveData
import com.virgilsecurity.android.base.extension.moduleWithScope
import com.virgilsecurity.android.feature_settings.domain.DeleteAccountDo
import com.virgilsecurity.android.feature_settings.domain.DeleteAccountDoDefault
import com.virgilsecurity.android.feature_settings.domain.LogoutDo
import com.virgilsecurity.android.feature_settings.domain.LogoutDoDefault
import com.virgilsecurity.android.feature_settings.view.SettingsController
import com.virgilsecurity.android.feature_settings.view.SettingsEditController
import com.virgilsecurity.android.feature_settings.viewmodel.edit.SettingsEditVM
import com.virgilsecurity.android.feature_settings.viewmodel.edit.SettingsEditVMDefault
import com.virgilsecurity.android.feature_settings.viewmodel.settings.SettingsVM
import com.virgilsecurity.android.feature_settings.viewmodel.settings.SettingsVMDefault
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.core.qualifier.named

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    7/18/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * SettingsModules
 */

val settingsModule: Module = moduleWithScope(named<SettingsController>()) {
    scoped { LogoutDoDefault(get(), get()) as LogoutDo }
    scoped { MediatorLiveData<SettingsVM.State>() }
    viewModel { SettingsVMDefault(get(), get()) as SettingsVM }
}

val settingsEditModule: Module = moduleWithScope(named<SettingsEditController>()) {
    scoped { DeleteAccountDoDefault(get(), get(), get(), get()) as DeleteAccountDo }
    scoped { MediatorLiveData<SettingsEditVM.State>() }
    viewModel { SettingsEditVMDefault(get(), get()) as SettingsEditVM }
}
