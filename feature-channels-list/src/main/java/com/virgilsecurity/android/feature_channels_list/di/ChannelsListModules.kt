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

package com.virgilsecurity.android.feature_channels_list.di

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.virgilsecurity.android.base.data.model.ChannelInfo
import com.virgilsecurity.android.base.view.adapter.DelegateAdapter
import com.virgilsecurity.android.base.view.adapter.DelegateAdapterItem
import com.virgilsecurity.android.base.view.adapter.DelegateAdapterItemDefault
import com.virgilsecurity.android.common.di.CommonDiConst.KEY_DIFF_CALLBACK_CHANNEL_INFO
import com.virgilsecurity.android.common.viewslice.StateSliceEmptyable
import com.virgilsecurity.android.feature_channels_list.di.Const.ADAPTER_CHANNELS_LIST
import com.virgilsecurity.android.feature_channels_list.di.Const.ITEM_ADAPTER_CHANNEL
import com.virgilsecurity.android.feature_channels_list.di.Const.LD_LIST_CHANNELS
import com.virgilsecurity.android.feature_channels_list.di.Const.LD_TOOLBAR_CHANNELS_LIST
import com.virgilsecurity.android.feature_channels_list.di.Const.STATE_CHANNELS
import com.virgilsecurity.android.feature_channels_list.di.Const.VM_CHANNELS_LIST
import com.virgilsecurity.android.feature_channels_list.domain.list.GetChannelsDo
import com.virgilsecurity.android.feature_channels_list.domain.list.GetChannelsDoDefault
import com.virgilsecurity.android.feature_channels_list.domain.list.ObserveChannelsListChangeDo
import com.virgilsecurity.android.feature_channels_list.domain.list.ObserveChannelsListChangeDoDefault
import com.virgilsecurity.android.feature_channels_list.view.ChannelsListController
import com.virgilsecurity.android.feature_channels_list.viewmodel.list.ChannelsVM
import com.virgilsecurity.android.feature_channels_list.viewmodel.list.ChannelsVMDefault
import com.virgilsecurity.android.feature_channels_list.viewslice.list.ChannelsSlice
import com.virgilsecurity.android.feature_channels_list.viewslice.list.ChannelsSliceDefault
import com.virgilsecurity.android.feature_channels_list.viewslice.list.adapter.ChannelItem
import com.virgilsecurity.android.feature_channels_list.viewslice.state.StateSliceChannels
import com.virgilsecurity.android.feature_channels_list.viewslice.toolbar.ToolbarSlice
import com.virgilsecurity.android.feature_channels_list.viewslice.toolbar.ToolbarSliceChannelsList
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    6/1/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * ThreadsListModules
 */
val channelsListModule: Module = module {
    single(named(STATE_CHANNELS)) { StateSliceChannels() as StateSliceEmptyable }

    factory(named(LD_TOOLBAR_CHANNELS_LIST)) { MutableLiveData<ToolbarSlice.Action>() }
    factory { ToolbarSliceChannelsList(get(named(LD_TOOLBAR_CHANNELS_LIST))) as ToolbarSlice }

    factory(named(LD_LIST_CHANNELS)) { MutableLiveData<ChannelsSlice.Action>() }
    factory(named(ITEM_ADAPTER_CHANNEL)) {
        ChannelItem(get(named(LD_LIST_CHANNELS)),
                    get()) as DelegateAdapterItem<DelegateAdapterItemDefault.KViewHolder<ChannelInfo>, ChannelInfo>
    }

    factory { GetChannelsDoDefault(get()) as GetChannelsDo }
    factory { ObserveChannelsListChangeDoDefault(get()) as ObserveChannelsListChangeDo }

    factory(named(ADAPTER_CHANNELS_LIST)) {
        DelegateAdapter.Builder<ChannelInfo>()
                .add(get(named(ITEM_ADAPTER_CHANNEL)))
                .diffCallback(get(named(KEY_DIFF_CALLBACK_CHANNEL_INFO)))
                .build()
    }
    factory {
        ChannelsSliceDefault(get(named(LD_LIST_CHANNELS)),
                             get(named(ADAPTER_CHANNELS_LIST)),
                             get(),
                             get()) as ChannelsSlice
    }

    module {
        factory { MediatorLiveData<ChannelsVM.State>() }
        factory(named(VM_CHANNELS_LIST)) { ChannelsVMDefault(get(), get(), get()) as ChannelsVM }
    }
}

object Const {
    const val STATE_CHANNELS = "STATE_CHANNELS"
    const val LD_TOOLBAR_CHANNELS_LIST = "LD_TOOLBAR_CHANNELS_LIST"
    const val LD_LIST_CHANNELS = "LD_LIST_CHANNELS"
    const val ITEM_ADAPTER_CHANNEL = "ITEM_ADAPTER_CHANNEL"
    const val ADAPTER_CHANNELS_LIST = "ADAPTER_CHANNELS_LIST"
    const val VM_CHANNELS_LIST = "VM_CHANNELS_LIST"
}
