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

package com.virgilsecurity.android.common.di

import android.content.Context
import android.text.InputFilter
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.virgilsecurity.android.base.data.api.AuthApi
import com.virgilsecurity.android.base.data.api.MessagesApi
import com.virgilsecurity.android.base.data.api.VirgilApi
import com.virgilsecurity.android.base.data.dao.ChannelsDao
import com.virgilsecurity.android.base.data.dao.MessagesDao
import com.virgilsecurity.android.base.data.dao.UsersDao
import com.virgilsecurity.android.base.data.model.ChannelMeta
import com.virgilsecurity.android.base.data.model.MessageMeta
import com.virgilsecurity.android.base.data.properties.UserProperties
import com.virgilsecurity.android.base.view.adapter.DiffCallback
import com.virgilsecurity.android.common.R
import com.virgilsecurity.android.common.data.helper.fuel.FuelHelper
import com.virgilsecurity.android.common.data.helper.room.RoomDB
import com.virgilsecurity.android.common.data.helper.smack.SmackHelper
import com.virgilsecurity.android.common.data.helper.smack.SmackRx
import com.virgilsecurity.android.common.data.helper.virgil.RenewTokenCallbackImpl
import com.virgilsecurity.android.common.data.helper.virgil.VirgilHelper
import com.virgilsecurity.android.common.data.helper.virgil.VirgilRx
import com.virgilsecurity.android.common.data.local.channels.ChannelsLocalDS
import com.virgilsecurity.android.common.data.local.messages.MessagesLocalDS
import com.virgilsecurity.android.common.data.local.users.UserPropertiesDefault
import com.virgilsecurity.android.common.data.local.users.UsersLocalDS
import com.virgilsecurity.android.common.data.remote.auth.AuthRemote
import com.virgilsecurity.android.common.data.remote.channels.ChannelIdGenerator
import com.virgilsecurity.android.common.data.remote.channels.ChannelIdGeneratorDefault
import com.virgilsecurity.android.common.data.remote.messages.MessagesRemoteDS
import com.virgilsecurity.android.common.data.remote.virgil.VirgilRemoteDS
import com.virgilsecurity.android.common.data.repository.*
import com.virgilsecurity.android.common.di.CommonDiConst.DIVIDER_DRAWABLE
import com.virgilsecurity.android.common.di.CommonDiConst.KEY_DIFF_CALLBACK_CHANNEL_META
import com.virgilsecurity.android.common.di.CommonDiConst.KEY_DIFF_CALLBACK_MESSAGE_META
import com.virgilsecurity.android.common.di.CommonDiConst.KEY_ROOM_DB_NAME
import com.virgilsecurity.android.common.di.CommonDiConst.ROOM_DB_NAME
import com.virgilsecurity.android.common.util.AuthUtils
import com.virgilsecurity.android.common.util.DefaultSymbolsInputFilter
import com.virgilsecurity.android.common.util.ImageStorage
import com.virgilsecurity.android.common.util.ImageStorageLocal
import com.virgilsecurity.android.common.view.adapter.ItemDecoratorBottomDivider
import com.virgilsecurity.sdk.cards.CardManager
import com.virgilsecurity.sdk.cards.validation.CardVerifier
import com.virgilsecurity.sdk.cards.validation.VirgilCardVerifier
import com.virgilsecurity.sdk.crypto.VirgilCardCrypto
import com.virgilsecurity.sdk.crypto.VirgilCrypto
import com.virgilsecurity.sdk.crypto.VirgilPrivateKeyExporter
import com.virgilsecurity.sdk.jwt.accessProviders.CachingJwtProvider
import com.virgilsecurity.sdk.jwt.contract.AccessTokenProvider
import com.virgilsecurity.sdk.storage.JsonFileKeyStorage
import com.virgilsecurity.sdk.storage.KeyStorage
import com.virgilsecurity.sdk.storage.PrivateKeyStorage
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

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
 * CommonModules
 */

val commonModules: Module = module {
    single(named(KEY_ROOM_DB_NAME)) { ROOM_DB_NAME }
    single {
        Room.databaseBuilder(get(), RoomDB::class.java, get(named(KEY_ROOM_DB_NAME)))
                .fallbackToDestructiveMigration()
                .build()
    }
    single { UsersLocalDS(get()) as UsersDao }
    single { ImageStorageLocal(get()) as ImageStorage }
    factory { DefaultSymbolsInputFilter() as InputFilter }
    single { UsersRepositoryDefault(get(), get()) as UsersRepository }
    factory { AuthRemote(get()) as AuthApi }
}

val utilsModule : Module = module {
    single { UserPropertiesDefault(get()) as UserProperties }
    single { AuthUtils(get(), get(), get()) }
    single { ChannelIdGeneratorDefault(get()) as ChannelIdGenerator }
}

val networkModule : Module = module {
    single { FuelHelper() }
}

val virgilModule : Module = module {
    single { VirgilCardCrypto() }
    single { VirgilCrypto() }
    single { VirgilCardVerifier(get()) as CardVerifier }
    single { RenewTokenCallbackImpl(get(), get(), get()) as CachingJwtProvider.RenewJwtCallback }
    single { CachingJwtProvider(get()) as AccessTokenProvider }
    single { VirgilPrivateKeyExporter() }
    single { JsonFileKeyStorage(get(named(CommonDiConst.STORAGE_PATH))) as KeyStorage }
    single { PrivateKeyStorage(get(), get()) }
    single { VirgilHelper(get(), get(), get(), get()) }
    single { CardManager(get(), get(), get()) }
    single { VirgilRx(get()) }
    single { VirgilRemoteDS(get()) as VirgilApi }
}

val smackModule : Module = module {
    single { SmackRx() }
    single { SmackHelper(get(), get(), get()) }
}

val paramsModule : Module = module {
    single(named(CommonDiConst.STORAGE_PATH)) { ((get() as Context).filesDir.absolutePath) as String }
}

// This module in common because for now we using it for contacts screen also
val channelsModule: Module = module {
    single { (get() as RoomDB).channelsQao() }
    single { ChannelsLocalDS(get(), get()) as ChannelsDao }
    single { ChannelsRepositoryDefault(get()) as ChannelsRepository }

    single(named(KEY_DIFF_CALLBACK_CHANNEL_META)) { DiffCallback<ChannelMeta>() }
    single(named(DIVIDER_DRAWABLE)) { (get() as Context).getDrawable(R.drawable.divider_bottom_gray) }
    single { ItemDecoratorBottomDivider(get(named(DIVIDER_DRAWABLE))) as RecyclerView.ItemDecoration }
}

val messagesModule: Module = module {
    single { MessagesRemoteDS(get(), get()) as MessagesApi }
    single { (get() as RoomDB).messagesQao() }
    single { MessagesLocalDS(get()) as MessagesDao }
    single { MessagesRepositoryDefault(get(), get(), get()) as MessagesRepository }
    single(named(KEY_DIFF_CALLBACK_MESSAGE_META)) { DiffCallback<MessageMeta>() }
}

object CommonDiConst {
    const val KEY_ROOM_DB_NAME = "ROOM_DB_NAME"
    const val DIVIDER_DRAWABLE = "DIVIDER_DRAWABLE"
    const val KEY_DIFF_CALLBACK_CHANNEL_META = "KEY_DIFF_CALLBACK_CHANNEL_META"
    const val KEY_DIFF_CALLBACK_MESSAGE_META = "KEY_DIFF_CALLBACK_MESSAGE_META"

    const val STORAGE_PATH = "storagePath"
    const val ROOM_DB_NAME = "virgil_messenger_database"
}
