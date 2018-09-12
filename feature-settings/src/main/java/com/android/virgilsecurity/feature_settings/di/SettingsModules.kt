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

package com.android.virgilsecurity.feature_settings.di

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import com.android.virgilsecurity.feature_settings.di.Const.CONTEXT_SETTINGS
import com.android.virgilsecurity.feature_settings.di.Const.LIVE_DATA_SETTINGS_FOOTER
import com.android.virgilsecurity.feature_settings.di.Const.LIVE_DATA_SETTINGS_HEADER
import com.android.virgilsecurity.feature_settings.di.Const.LIVE_DATA_SETTINGS_MENU
import com.android.virgilsecurity.feature_settings.di.Const.LIVE_DATA_SETTINGS_TOOLBAR
import com.android.virgilsecurity.feature_settings.di.Const.MEDIATOR_DATA_SETTINGS_FOOTER
import com.android.virgilsecurity.feature_settings.domain.DeleteAccountDo
import com.android.virgilsecurity.feature_settings.domain.DeleteAccountDoDefault
import com.android.virgilsecurity.feature_settings.domain.LogoutDo
import com.android.virgilsecurity.feature_settings.domain.LogoutDoDefault
import com.android.virgilsecurity.feature_settings.viewmodel.SettingsVM
import com.android.virgilsecurity.feature_settings.viewmodel.SettingsVMDefault
import com.android.virgilsecurity.feature_settings.viewslice.footer.FooterSlice
import com.android.virgilsecurity.feature_settings.viewslice.footer.FooterSliceSettings
import com.android.virgilsecurity.feature_settings.viewslice.header.HeaderSlice
import com.android.virgilsecurity.feature_settings.viewslice.header.HeaderSliceSettings
import com.android.virgilsecurity.feature_settings.viewslice.menu.MenuSlice
import com.android.virgilsecurity.feature_settings.viewslice.menu.MenuSliceSettings
import com.android.virgilsecurity.feature_settings.viewslice.state.StateSlice
import com.android.virgilsecurity.feature_settings.viewslice.state.StateSliceSettings
import com.android.virgilsecurity.feature_settings.viewslice.toolbar.ToolbarSlice
import com.android.virgilsecurity.feature_settings.viewslice.toolbar.ToolbarSliceSettings
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

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

val settingsModule: Module = applicationContext {
    bean { StateSliceSettings() as StateSlice }

    context(CONTEXT_SETTINGS) {
        bean(LIVE_DATA_SETTINGS_TOOLBAR) { MutableLiveData<ToolbarSlice.Action>() }
        bean { ToolbarSliceSettings(get(LIVE_DATA_SETTINGS_TOOLBAR)) as ToolbarSlice }

        bean(LIVE_DATA_SETTINGS_MENU) { MutableLiveData<MenuSlice.Action>() }
        bean { MenuSliceSettings(get(LIVE_DATA_SETTINGS_MENU)) as MenuSlice }

        bean(LIVE_DATA_SETTINGS_HEADER) { MutableLiveData<HeaderSlice.Action>() }
        bean { HeaderSliceSettings(get(LIVE_DATA_SETTINGS_HEADER), get()) as HeaderSlice }

        bean(LIVE_DATA_SETTINGS_FOOTER) { MutableLiveData<FooterSlice.Action>() }
        bean { FooterSliceSettings(get(LIVE_DATA_SETTINGS_FOOTER)) as FooterSlice }

        bean { LogoutDoDefault(get(), get()) as LogoutDo }
        bean(MEDIATOR_DATA_SETTINGS_FOOTER) { MediatorLiveData<SettingsVM.State>() }
        bean { DeleteAccountDoDefault(get(), get(), get(), get()) as DeleteAccountDo }
        bean { SettingsVMDefault(get(MEDIATOR_DATA_SETTINGS_FOOTER), get(), get()) as SettingsVM }
    }
}

object Const {
    const val LIVE_DATA_SETTINGS_TOOLBAR = "LIVE_DATA_SETTINGS_TOOLBAR"
    const val LIVE_DATA_SETTINGS_MENU = "LIVE_DATA_SETTINGS_MENU"
    const val LIVE_DATA_SETTINGS_HEADER = "LIVE_DATA_SETTINGS_HEADER"
    const val LIVE_DATA_SETTINGS_FOOTER = "LIVE_DATA_SETTINGS_FOOTER"

    const val MEDIATOR_DATA_SETTINGS_FOOTER = "MEDIATOR_DATA_SETTINGS_FOOTER"

    const val CONTEXT_SETTINGS = "CONTEXT_SETTINGS"
}