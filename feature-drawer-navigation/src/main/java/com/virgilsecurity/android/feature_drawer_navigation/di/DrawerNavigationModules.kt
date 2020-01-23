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

package com.virgilsecurity.android.feature_drawer_navigation.di

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import com.virgilsecurity.android.common.viewslice.StateSlice
import com.virgilsecurity.android.feature_drawer_navigation.data.interactor.InitTwilioInteractor
import com.virgilsecurity.android.feature_drawer_navigation.data.interactor.InitTwilioInteractorDefault
import com.virgilsecurity.android.feature_drawer_navigation.di.Const.LIVE_DATA_DRAWER
import com.virgilsecurity.android.feature_drawer_navigation.di.Const.LIVE_DATA_TWILIO_INIT
import com.virgilsecurity.android.feature_drawer_navigation.di.Const.MEDIATOR_LIVE_DATA_INIT_TWILIO
import com.virgilsecurity.android.feature_drawer_navigation.di.Const.STATE_SLICE_TWILIO_INIT
import com.virgilsecurity.android.feature_drawer_navigation.domain.InitTwilioDo
import com.virgilsecurity.android.feature_drawer_navigation.domain.InitTwilioDoDefault
import com.virgilsecurity.android.feature_drawer_navigation.view.TwilioInitController
import com.virgilsecurity.android.feature_drawer_navigation.viewmodel.InitTwilioVM
import com.virgilsecurity.android.feature_drawer_navigation.viewmodel.InitTwilioVMDefault
import com.virgilsecurity.android.feature_drawer_navigation.viewslice.navigation.drawer.DrawerSlice
import com.virgilsecurity.android.feature_drawer_navigation.viewslice.navigation.drawer.DrawerSliceDefault
import com.virgilsecurity.android.feature_drawer_navigation.viewslice.navigation.state.DrawerStateSlice
import com.virgilsecurity.android.feature_drawer_navigation.viewslice.navigation.state.DrawerStateSliceDefault
import com.virgilsecurity.android.feature_drawer_navigation.viewslice.twilioInit.interaction.TwilioInitSlice
import com.virgilsecurity.android.feature_drawer_navigation.viewslice.twilioInit.interaction.TwilioInitSliceDefault
import com.virgilsecurity.android.feature_drawer_navigation.viewslice.twilioInit.state.StateSliceTwilioInit
import org.koin.dsl.module.Module
import org.koin.dsl.module.module

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    7/16/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * DrawerNavigationModules
 */

val drawerNavigationModule: Module = module {
    factory(LIVE_DATA_DRAWER) { MutableLiveData<DrawerSlice.Action>() }
    factory { DrawerSliceDefault(get(LIVE_DATA_DRAWER), get()) as DrawerSlice }
    factory { DrawerStateSliceDefault() as DrawerStateSlice }
}

val twilioInitModule: Module = module {
    single { InitTwilioInteractorDefault(get(), get(), get()) as InitTwilioInteractor }

    factory { InitTwilioDoDefault(get()) as InitTwilioDo }

    factory(LIVE_DATA_TWILIO_INIT) { MutableLiveData<TwilioInitSlice.Action>() }
    factory { TwilioInitSliceDefault(get(LIVE_DATA_TWILIO_INIT)) as TwilioInitSlice }

    factory(STATE_SLICE_TWILIO_INIT) { StateSliceTwilioInit() as StateSlice }

    module(TwilioInitController::class.java.simpleName) {
        factory(MEDIATOR_LIVE_DATA_INIT_TWILIO) { MediatorLiveData<InitTwilioVM.State>() }
        factory { InitTwilioVMDefault(get(MEDIATOR_LIVE_DATA_INIT_TWILIO), get()) as InitTwilioVM }
    }
}

object Const {
    const val STATE_SLICE_TWILIO_INIT = "STATE_SLICE_TWILIO_INIT"
    const val LIVE_DATA_DRAWER = "LIVE_DATA_DRAWER"
    const val LIVE_DATA_TWILIO_INIT = "LIVE_DATA_TWILIO_INIT"
    const val MEDIATOR_LIVE_DATA_INIT_TWILIO = "MEDIATOR_LIVE_DATA_INIT_TWILIO"
}