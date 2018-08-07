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

package com.android.virgilsecurity.common.data.helper.twilio

import android.content.Context
import com.android.virgilsecurity.base.data.api.ChannelsApi
import com.twilio.chat.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    5/30/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * TwilioHelper
 */

class TwilioHelper(private val context: Context,
                   private val twilioRx: TwilioRx) {

    private var chatClient: ChatClient? = null

    fun startChatClient(identity: String,
                        authHeader: () -> String): Completable {

        if (this.chatClient == null) {
            return twilioRx.getToken(identity, authHeader)
                    .flatMap { token ->
                        twilioRx.createClient(context, token = token).flatMap { chatClient ->
                            Single.create<Pair<ChatClient, String>> {
                                it.onSuccess(chatClient to token)
                            }
                        }
                    }
                    .map { pair ->
                        twilioRx.createAccessManager(pair.second, identity, authHeader, pair.first)
                                .andThen(Single.create<ChatClient> {
                                    it.onSuccess(pair.first)
                                })

                        this.chatClient = pair.first
                        pair.first
                    }.toCompletable()
        } else {
            return Completable.complete()
        }
    }

    fun shutdownChatClient() {
        if (chatClient != null) {
            chatClient?.shutdown()
            chatClient = null
        }
    }

    fun observeChannelsChanges(): Flowable<ChannelsApi.ChannelsChanges> =
        twilioRx.observeChannelsChanges(chatClient)

    fun createChannel(interlocutor: String,
                      channelName: String) =
            twilioRx.createChannel(interlocutor, channelName, chatClient)

    fun getPublicChannelsFirstPage() =
            twilioRx.getPublicChannelsFirstPaginator(chatClient)

    fun getUserChannels() =
            twilioRx.getUserChannels(chatClient)

    fun getPublicChannels() =
            twilioRx.getPublicChannels(chatClient)

    fun getChannelsNextPage(paginator: Paginator<ChannelDescriptor>) =
            twilioRx.getChannelsNextPaginator(paginator)

    fun getChannelFromChannelDescriptor(channelDescriptor: ChannelDescriptor): Single<Channel> =
            twilioRx.getChannelFromChannelDescriptor(channelDescriptor)

    fun joinChannel(channel: Channel) = twilioRx.joinChannel(channel)

    fun getMessages(channel: Channel) = twilioRx.getMessages(channel)

    fun sendMessage(channel: Channel, body: String, interlocutor: String) =
            twilioRx.sendMessage(channel, body, interlocutor)

    fun getChannelBySid(channelSid: String) =
            twilioRx.getChannelBySid(chatClient, channelSid)
}