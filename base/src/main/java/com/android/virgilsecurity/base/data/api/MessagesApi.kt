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

package com.android.virgilsecurity.base.data.api

import com.android.virgilsecurity.base.data.model.MessageInfo
import com.twilio.chat.Channel
import com.twilio.chat.Member
import com.twilio.chat.Message
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    7/27/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * TwilioApi
 */
interface MessagesApi {

    fun messagesCount(channel: Channel): Single<Long>

    fun messagesAfter(channel: Channel, startIndex: Long, count: Int): Single<List<MessageInfo>>

    fun lastMessages(channel: Channel, count: Int): Single<List<MessageInfo>>

    fun observeChannelChanges(channel: Channel): Flowable<ChannelChanges>

    fun sendMessage(channel: Channel, body: String, interlocutor: String): Single<MessageInfo>

    sealed class ChannelChanges {
        data class MemberDeleted(val member: Member?) : ChannelChanges()
        data class TypingEnded(val channel: Channel?, val member: Member?) : ChannelChanges()
        data class MessageAdded(val message: Message?) : ChannelChanges()
        data class MessageDeleted(val message: Message?) : ChannelChanges()
        data class MemberAdded(val member: Member?) : ChannelChanges()
        data class TypingStarted(val channel: Channel?, val member: Member?) : ChannelChanges()
        data class SynchronizationChanged(val channel: Channel?) : ChannelChanges()
        data class MessageUpdated(val message: Message?, val reason: Message.UpdateReason?) : ChannelChanges()
        data class MemberUpdated(val member: Member?, val reason: Member.UpdateReason?) : ChannelChanges()

        data class Exception(val error: Throwable) : ChannelChanges()
    }
}