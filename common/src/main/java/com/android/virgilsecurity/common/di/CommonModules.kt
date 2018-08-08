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

package com.android.virgilsecurity.common.di

import android.arch.persistence.room.Room
import android.content.Context
import com.android.virgilsecurity.base.data.api.ChannelsApi
import com.android.virgilsecurity.base.data.dao.ChannelsDao
import com.android.virgilsecurity.base.data.dao.UsersDao
import com.android.virgilsecurity.base.data.properties.UserProperties
import com.android.virgilsecurity.common.data.helper.fuel.FuelHelper
import com.android.virgilsecurity.common.data.helper.room.RoomDB
import com.android.virgilsecurity.common.data.helper.twilio.TwilioHelper
import com.android.virgilsecurity.common.data.helper.twilio.TwilioRx
import com.android.virgilsecurity.common.data.helper.virgil.GetTokenCallbackImpl
import com.android.virgilsecurity.common.data.helper.virgil.VirgilHelper
import com.android.virgilsecurity.common.data.helper.virgil.VirgilRx
import com.android.virgilsecurity.common.data.local.channels.ChannelsLocalDS
import com.android.virgilsecurity.common.data.local.users.UserPropertiesDefault
import com.android.virgilsecurity.common.data.local.users.UsersLocalDS
import com.android.virgilsecurity.common.data.remote.channels.ChannelIdGenerator
import com.android.virgilsecurity.common.data.remote.channels.ChannelIdGeneratorDefault
import com.android.virgilsecurity.common.data.remote.channels.ChannelsRemoteDS
import com.android.virgilsecurity.common.data.remote.channels.MapperToChannelInfo
import com.android.virgilsecurity.common.di.CommonDiConst.KEY_ROOM_DB_NAME
import com.android.virgilsecurity.common.di.CommonDiConst.ROOM_DB_NAME
import com.android.virgilsecurity.common.util.AuthUtils
import com.android.virgilsecurity.common.util.ImageStorage
import com.virgilsecurity.sdk.cards.CardManager
import com.virgilsecurity.sdk.cards.validation.CardVerifier
import com.virgilsecurity.sdk.cards.validation.VirgilCardVerifier
import com.virgilsecurity.sdk.crypto.*
import com.virgilsecurity.sdk.jwt.accessProviders.CallbackJwtProvider
import com.virgilsecurity.sdk.jwt.contract.AccessTokenProvider
import com.virgilsecurity.sdk.storage.JsonFileKeyStorage
import com.virgilsecurity.sdk.storage.KeyStorage
import com.virgilsecurity.sdk.storage.PrivateKeyStorage
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    7/4/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * commonModules
 */
val commonModules: Module = applicationContext {
    bean(KEY_ROOM_DB_NAME) { ROOM_DB_NAME }
    bean {
        Room.databaseBuilder(get(), RoomDB::class.java, get(KEY_ROOM_DB_NAME))
                .fallbackToDestructiveMigration()
                .build()
    }
    bean { UsersLocalDS(get()) as UsersDao }
    bean { ImageStorage(get()) }
}

val utilsModule : Module = applicationContext {
    bean { UserPropertiesDefault(get()) as UserProperties }
    bean { AuthUtils(get(), get(), get()) }
    bean { ChannelIdGeneratorDefault(get()) as ChannelIdGenerator }
}

val networkModule : Module = applicationContext {
    bean { FuelHelper() }
}

val virgilModule : Module = applicationContext {
    bean { VirgilCardCrypto() as CardCrypto }
    bean { VirgilCrypto() }
    bean { VirgilCardVerifier(get()) as CardVerifier }
    bean { GetTokenCallbackImpl(get(), get(), get()) as CallbackJwtProvider.GetTokenCallback }
    bean { CallbackJwtProvider(get()) as AccessTokenProvider }
    bean { VirgilPrivateKeyExporter() as PrivateKeyExporter }
    bean { JsonFileKeyStorage(get(CommonDiConst.STORAGE_PATH)) as KeyStorage }
    bean { PrivateKeyStorage(get(), get()) }
    bean { VirgilHelper(get(), get(), get(), get()) }
    bean { CardManager(get(), get(), get()) }
    bean { VirgilRx(get()) }
}

val twilioModule : Module = applicationContext {
    bean { TwilioRx(get(), get()) }
    bean { TwilioHelper(get(), get()) }
}

val paramsModule : Module = applicationContext {
    bean(CommonDiConst.STORAGE_PATH) { ((get() as Context).filesDir.absolutePath) as String }
}

val channelsModule: Module = applicationContext {
    bean { MapperToChannelInfo(get()) }
    bean { ChannelsRemoteDS(get(), get(), get()) as ChannelsApi }
    bean { (get() as RoomDB).channelsDao() }
    bean { ChannelsLocalDS(get(), get()) as ChannelsDao }
}

object CommonDiConst {
    const val KEY_ROOM_DB_NAME = "ROOM_DB_NAME"

    const val STORAGE_PATH = "storagePath"
    const val ROOM_DB_NAME = "virgil_messenger_database"
}