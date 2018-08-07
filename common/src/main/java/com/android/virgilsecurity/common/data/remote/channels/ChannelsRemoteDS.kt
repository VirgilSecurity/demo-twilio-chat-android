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

package com.android.virgilsecurity.common.data.remote.channels

import com.android.virgilsecurity.base.data.api.ChannelsApi
import com.android.virgilsecurity.base.data.model.ChannelInfo
import com.android.virgilsecurity.common.data.helper.twilio.TwilioHelper
import com.twilio.chat.Channel
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single

class ChannelsRemoteDS(
        private val twilioHelper: TwilioHelper,
        private val channelIdGenerator: ChannelIdGenerator,
        private val mapper: MapperToChannelInfo
) : ChannelsApi {

    override fun getUserChannelById(id: String): Single<Channel> = twilioHelper.getChannelBySid(id)

    override fun getUserChannels(): Observable<List<ChannelInfo>> =
            Observable.concatArray(twilioHelper.getUserChannels().toObservable(),
                                   twilioHelper.getPublicChannels().toObservable())
                    .map(mapper::mapDescriptors)

    override fun createChannel(sender: String, interlocutor: String): Completable =
            twilioHelper.createChannel(interlocutor,
                                       channelIdGenerator.generatedChannelId(sender,
                                                                             interlocutor))

    override fun observeChannelsChanges(): Flowable<ChannelsApi.ChannelsChanges> =
            twilioHelper.observeChannelsChanges()

    override fun joinChannel(channel: Channel): Single<ChannelInfo> =
            twilioHelper.joinChannel(channel).map(mapper::mapChannel)
}