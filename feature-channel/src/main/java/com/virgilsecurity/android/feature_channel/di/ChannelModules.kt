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

package com.virgilsecurity.android.feature_channel.di

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.virgilsecurity.android.base.data.model.MessageInfo
import com.virgilsecurity.android.base.view.adapter.DelegateAdapter
import com.virgilsecurity.android.base.view.adapter.DelegateAdapterItem
import com.virgilsecurity.android.base.view.adapter.DelegateAdapterItemDefault
import com.virgilsecurity.android.common.di.CommonDiConst.KEY_DIFF_CALLBACK_MESSAGE_INFO
import com.virgilsecurity.android.common.viewslice.StateSliceEmptyable
import com.virgilsecurity.android.feature_channel.data.interactor.CardsInteractor
import com.virgilsecurity.android.feature_channel.data.interactor.CardsInteractorDefault
import com.virgilsecurity.android.feature_channel.data.repository.MessagesRepository
import com.virgilsecurity.android.feature_channel.data.repository.MessagesRepositoryDefault
import com.virgilsecurity.android.feature_channel.di.Const.ADAPTER_CHANNEL
import com.virgilsecurity.android.feature_channel.di.Const.ITEM_ADAPTER_MESSAGE_IN_DEVELOPMENT
import com.virgilsecurity.android.feature_channel.di.Const.ITEM_ADAPTER_MESSAGE_ME
import com.virgilsecurity.android.feature_channel.di.Const.ITEM_ADAPTER_MESSAGE_YOU
import com.virgilsecurity.android.feature_channel.di.Const.LD_CHANNEL
import com.virgilsecurity.android.feature_channel.di.Const.LD_TOOLBAR_CHANNEL
import com.virgilsecurity.android.feature_channel.di.Const.STATE_CHANNEL
import com.virgilsecurity.android.feature_channel.di.Const.TOOLBAR_CHANNEL
import com.virgilsecurity.android.feature_channel.di.Const.VM_CHANNEL
import com.virgilsecurity.android.feature_channel.domain.*
import com.virgilsecurity.android.feature_channel.view.ChannelController
import com.virgilsecurity.android.feature_channel.viewmodel.ChannelVM
import com.virgilsecurity.android.feature_channel.viewmodel.ChannelVMDefault
import com.virgilsecurity.android.feature_channel.viewslice.list.ChannelSlice
import com.virgilsecurity.android.feature_channel.viewslice.list.ChannelSliceDefault
import com.virgilsecurity.android.feature_channel.viewslice.list.adapter.MessageItemInDevelopment
import com.virgilsecurity.android.feature_channel.viewslice.list.adapter.MessageItemMe
import com.virgilsecurity.android.feature_channel.viewslice.list.adapter.MessageItemYou
import com.virgilsecurity.android.feature_channel.viewslice.state.StateSliceChannel
import com.virgilsecurity.android.feature_channel.viewslice.toolbar.ToolbarSlice
import com.virgilsecurity.android.feature_channel.viewslice.toolbar.ToolbarSliceChannel
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

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
val channelModule: Module = module {
    single { MessagesRepositoryDefault(get(), get(), get()) as MessagesRepository }
    single(named(STATE_CHANNEL)) { StateSliceChannel() as StateSliceEmptyable }

    factory(named(LD_TOOLBAR_CHANNEL)) { MutableLiveData<ToolbarSlice.Action>() }
    factory(named(TOOLBAR_CHANNEL)) { ToolbarSliceChannel(get(named(LD_TOOLBAR_CHANNEL))) as ToolbarSlice }

    factory(named(LD_CHANNEL)) { MutableLiveData<ChannelSlice.Action>() }
    factory(named(ITEM_ADAPTER_MESSAGE_ME)) {
        MessageItemMe(get(named(LD_CHANNEL)),
                      get(),
                      get()) as DelegateAdapterItem<DelegateAdapterItemDefault.KViewHolder<MessageInfo>, MessageInfo>
    }
    factory(named(ITEM_ADAPTER_MESSAGE_YOU)) {
        MessageItemYou(get(named(LD_CHANNEL)),
                       get(),
                       get()) as DelegateAdapterItem<DelegateAdapterItemDefault.KViewHolder<MessageInfo>, MessageInfo>
    }
    factory(named(ITEM_ADAPTER_MESSAGE_IN_DEVELOPMENT)) {
        MessageItemInDevelopment() as DelegateAdapterItem<DelegateAdapterItemDefault.KViewHolder<MessageInfo>, MessageInfo>
    }

    factory(named(ADAPTER_CHANNEL)) {
        DelegateAdapter.Builder<MessageInfo>()
                .add(get(named(ITEM_ADAPTER_MESSAGE_ME)))
                .add(get(named(ITEM_ADAPTER_MESSAGE_YOU)))
                .add(get(named(ITEM_ADAPTER_MESSAGE_IN_DEVELOPMENT)))
                .diffCallback(get(named(KEY_DIFF_CALLBACK_MESSAGE_INFO)))
                .build()
    }
    factory { ChannelSliceDefault(get(named(LD_CHANNEL)), get(named(ADAPTER_CHANNEL)), get()) as ChannelSlice }

    factory { GetMessagesDoDefault(get()) as GetMessagesDo }
    factory { ObserveChannelChangesDoDefault(get()) as ObserveChannelChangesDo }
    factory { SendMessageDoDefault(get(), get()) as SendMessageDo }
    factory { CardsInteractorDefault(get()) as CardsInteractor }
    factory { GetCardDoDefault(get()) as GetCardDo }
    factory { GetChannelDoDefault(get()) as GetChannelDo }
    factory { ShowMessagePreviewDoDefault(get(), get()) as ShowMessagePreviewDo }
    factory { CopyMessageDoDefault(get()) as CopyMessageDo }

    module {
        factory { MediatorLiveData<ChannelVM.State>() }
        factory(named(VM_CHANNEL)) {
            ChannelVMDefault(get(),
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
    const val ADAPTER_CHANNEL = "ADAPTER_CHANNEL"
    const val VM_CHANNEL = "VM_CHANNEL"
}
