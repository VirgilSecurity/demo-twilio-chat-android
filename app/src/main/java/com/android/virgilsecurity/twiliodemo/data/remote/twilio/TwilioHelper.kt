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

package com.android.virgilsecurity.twiliodemo.data.remote.twilio

import android.content.Context
import com.android.virgilsecurity.twiliodemo.R.string.identity
import com.twilio.chat.*
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import org.json.JSONObject

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

    private val keySender = "sender"
    private val keyReceiver = "receiver"

    private var chatClient: ChatClient? = null

    fun createChatClient(identity: String,
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
                    .flatMap { pair ->
                        twilioRx.createAccessManager(pair.second, identity, authHeader, pair.first)
                                .andThen(Single.create<ChatClient> {
                                    it.onSuccess(pair.first)
                                })
                    }
                    .map {
                        this.chatClient = it
                        it
                    }
                    .toCompletable()
        } else {
            return Completable.complete()
        }
    }

    fun setChatListener(chatClientListener: ChatClientListener) {
        chatClient?.setListener(chatClientListener)
    }

//    fun createChannel(interlocutor: String) {
//        val attrs = JSONObject()
//        attrs.put(keySender, "testing channel creation with options ${value}")
//        attrs.put("topic", "testing channel creation with options ${value}")
//
//        val builder = chatClient?.channels?.channelBuilder()
//
//        builder?.withFriendlyName("${typ}_TestChannelF_${value}")
//                ?.withUniqueName("${typ}_TestChannelU_${value}")
//                ?.withType(type)
//                ?.withAttributes(attrs)
//                ?.build(object : CallbackListener<Channel>() {
//                    override fun onSuccess(newChannel: Channel) {
//                        debug { "Successfully created a channel with options." }
//                        channels.put(newChannel.sid, ChannelModel(newChannel))
//                        refreshChannelList()
//                    }
//
//                    override fun onError(errorInfo: ErrorInfo?) {
//                        error { "Error creating a channel" }
//                    }
//                })
//    }
}