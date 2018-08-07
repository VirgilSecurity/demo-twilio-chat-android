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

import com.android.virgilsecurity.base.data.model.ChannelInfo
import com.twilio.chat.Channel
import com.twilio.chat.ChatClient
import com.twilio.chat.ErrorInfo
import com.twilio.chat.User
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
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
interface ChannelsApi {

    fun getUserChannelById(id: String): Single<Channel>

    fun getUserChannels(): Observable<List<ChannelInfo>>

    fun createChannel(sender: String, interlocutor: String): Completable

    fun observeChannelsChanges(): Flowable<ChannelsChanges>

    fun joinChannel(channel: Channel): Single<ChannelInfo>

    sealed class ChannelsChanges {
        data class ChannelDeleted(val channel: Channel?) : ChannelsChanges()
        data class InvitedToChannelNotification(val notification: String?) : ChannelsChanges()
        data class ClientSynchronization(val status: ChatClient.SynchronizationStatus?) : ChannelsChanges()
        object NotificationSubscribed : ChannelsChanges()
        data class UserSubscribed(val user: User?) : ChannelsChanges()
        data class ChannelUpdated(val channel: Channel?, val reason: Channel.UpdateReason?) : ChannelsChanges()
        data class RemovedFromChannelNotification(val notification: String?) : ChannelsChanges()
        data class NotificationFailed(val error: ErrorInfo?) : ChannelsChanges()
        data class ChannelJoined(val channel: Channel?) : ChannelsChanges()
        data class ChannelAdded(val channel: Channel?) : ChannelsChanges()
        data class ChannelSynchronizationChange(val channel: Channel?) : ChannelsChanges()
        data class UserUnsubscribed(val user: User?) : ChannelsChanges()
        data class AddedToChannelNotification(val notification: String?) : ChannelsChanges()
        data class ChannelInvited(val channel: Channel?) : ChannelsChanges()
        data class NewMessageNotification(val p0: String?, val p1: String?, val p2: Long) : ChannelsChanges()
        data class ConnectionStateChange(val state: ChatClient.ConnectionState?) : ChannelsChanges()
        data class Error(val error: ErrorInfo?) : ChannelsChanges()
        data class UserUpdated(val user: User?, val reason: User.UpdateReason?) : ChannelsChanges()

        data class Exception(val error: Throwable) : ChannelsChanges()
    }
}