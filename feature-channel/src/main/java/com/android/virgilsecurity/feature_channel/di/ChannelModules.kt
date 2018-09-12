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

package com.android.virgilsecurity.feature_channel.di

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import com.android.virgilsecurity.base.data.model.MessageInfo
import com.android.virgilsecurity.base.view.adapter.DelegateAdapter
import com.android.virgilsecurity.base.view.adapter.DelegateAdapterItem
import com.android.virgilsecurity.base.view.adapter.DelegateAdapterItemDefault
import com.android.virgilsecurity.common.viewslice.StateSliceEmptyable
import com.android.virgilsecurity.feature_channel.data.interactor.CardsInteractor
import com.android.virgilsecurity.feature_channel.data.interactor.CardsInteractorDefault
import com.android.virgilsecurity.feature_channel.data.repository.MessagesRepository
import com.android.virgilsecurity.feature_channel.data.repository.MessagesRepositoryDefault
import com.android.virgilsecurity.feature_channel.di.Const.CONTEXT_CHANNEL
import com.android.virgilsecurity.feature_channel.di.Const.ITEM_ADAPTER_MESSAGE_IN_DEVELOPMENT
import com.android.virgilsecurity.feature_channel.di.Const.ITEM_ADAPTER_MESSAGE_ME
import com.android.virgilsecurity.feature_channel.di.Const.ITEM_ADAPTER_MESSAGE_YOU
import com.android.virgilsecurity.feature_channel.di.Const.LD_CHANNEL
import com.android.virgilsecurity.feature_channel.di.Const.LD_TOOLBAR_CHANNEL
import com.android.virgilsecurity.feature_channel.di.Const.MLD_CHANNEL
import com.android.virgilsecurity.feature_channel.di.Const.STATE_CHANNEL
import com.android.virgilsecurity.feature_channel.di.Const.TOOLBAR_CHANNEL
import com.android.virgilsecurity.feature_channel.domain.*
import com.android.virgilsecurity.feature_channel.viewmodel.ChannelVM
import com.android.virgilsecurity.feature_channel.viewmodel.ChannelVMDefault
import com.android.virgilsecurity.feature_channel.viewslice.list.ChannelSlice
import com.android.virgilsecurity.feature_channel.viewslice.list.ChannelSliceDefault
import com.android.virgilsecurity.feature_channel.viewslice.list.adapter.MessageItemInDevelopment
import com.android.virgilsecurity.feature_channel.viewslice.list.adapter.MessageItemMe
import com.android.virgilsecurity.feature_channel.viewslice.list.adapter.MessageItemYou
import com.android.virgilsecurity.feature_channel.viewslice.state.StateSliceChannel
import com.android.virgilsecurity.feature_channel.viewslice.toolbar.ToolbarSlice
import com.android.virgilsecurity.feature_channel.viewslice.toolbar.ToolbarSliceChannel
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    6/2/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * ChannelModules
 */
val channelModule: Module = applicationContext {
    bean { MessagesRepositoryDefault(get(), get(), get()) as MessagesRepository }
    bean(STATE_CHANNEL) { StateSliceChannel() as StateSliceEmptyable }

    context(CONTEXT_CHANNEL) {
        bean(LD_TOOLBAR_CHANNEL) { MutableLiveData<ToolbarSlice.Action>() }
        bean(TOOLBAR_CHANNEL) { ToolbarSliceChannel(get(LD_TOOLBAR_CHANNEL)) as ToolbarSlice }

        bean(LD_CHANNEL) { MutableLiveData<ChannelSlice.Action>() }
        bean(ITEM_ADAPTER_MESSAGE_ME) {
            MessageItemMe(get(LD_CHANNEL),
                          get(),
                          get()) as DelegateAdapterItem<DelegateAdapterItemDefault.KViewHolder<MessageInfo>, MessageInfo>
        }
        bean(ITEM_ADAPTER_MESSAGE_YOU) {
            MessageItemYou(get(LD_CHANNEL),
                           get(),
                           get()) as DelegateAdapterItem<DelegateAdapterItemDefault.KViewHolder<MessageInfo>, MessageInfo>
        }
        bean(ITEM_ADAPTER_MESSAGE_IN_DEVELOPMENT) {
            MessageItemInDevelopment() as DelegateAdapterItem<DelegateAdapterItemDefault.KViewHolder<MessageInfo>, MessageInfo>
        }
        bean {
            DelegateAdapter.Builder<MessageInfo>()
                    .add(get(ITEM_ADAPTER_MESSAGE_ME))
                    .add(get(ITEM_ADAPTER_MESSAGE_YOU))
                    .add(get(ITEM_ADAPTER_MESSAGE_IN_DEVELOPMENT))
                    .diffCallback(get())
                    .build()
        }

        bean { ChannelSliceDefault(get(LD_CHANNEL), get(), get()) as ChannelSlice }

        bean { GetMessagesDoDefault(get()) as GetMessagesDo }
        bean(MLD_CHANNEL) { MediatorLiveData<ChannelVM.State>() }
        bean { ObserveChannelChangesDoDefault(get()) as ObserveChannelChangesDo }
        bean { SendMessageDoDefault(get(), get()) as SendMessageDo }
        bean { CardsInteractorDefault(get()) as CardsInteractor }
        bean { GetCardDoDefault(get()) as GetCardDo }
        bean { GetChannelDoDefault(get()) as GetChannelDo }
        bean { ShowMessagePreviewDoDefault(get(), get()) as ShowMessagePreviewDo }
        bean { CopyMessageDoDefault(get()) as CopyMessageDo }
        bean {
            ChannelVMDefault(get(MLD_CHANNEL),
                             get(),
                             get(),
                             get(),
                             get(),
                             get(),
                             get(),
                             get(),
                             get()) as ChannelVM
        }
    }
}

object Const {
    const val STATE_CHANNEL = "STATE_CHANNEL"
    const val LD_TOOLBAR_CHANNEL = "LD_TOOLBAR_CHANNEL"
    const val TOOLBAR_CHANNEL = "TOOLBAR_CHANNEL"
    const val LD_CHANNEL = "LD_CHANNEL"
    const val ITEM_ADAPTER_MESSAGE_ME = "ITEM_ADAPTER_MESSAGE_ME"
    const val ITEM_ADAPTER_MESSAGE_YOU = "ITEM_ADAPTER_MESSAGE_YOU"
    const val ITEM_ADAPTER_MESSAGE_IN_DEVELOPMENT = "ITEM_ADAPTER_MESSAGE_IN_DEVELOPMENT"
    const val MLD_CHANNEL = "MLD_CHANNEL"

    const val CONTEXT_CHANNEL = "CONTEXT_CHANNEL"
}