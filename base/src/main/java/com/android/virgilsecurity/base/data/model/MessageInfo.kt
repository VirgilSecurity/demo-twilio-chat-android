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

package com.android.virgilsecurity.base.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import com.android.virgilsecurity.base.data.model.ChannelInfo.Companion.KEY_ATTRIBUTES
import com.android.virgilsecurity.base.util.GeneralConstants
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    5/31/185/31/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * User Virgil Twilio. Summarizes Virgil and Twilio properties of User.
 */
@Entity
@Parcelize
class MessageInfo(
        @PrimaryKey @ColumnInfo(name = KEY_SID)
        val sid: String,
        @ColumnInfo(name = KEY_CHANNEL_ID)
        val channelSid: String,
        @ColumnInfo(name = KEY_BODY)
        val body: String,
        @ColumnInfo(name = KEY_ATTRIBUTES)
        val attributesString: String,
        @ColumnInfo(name = GeneralConstants.KEY_SENDER)
        val sender: String,
        @ColumnInfo(name = GeneralConstants.KEY_INTERLOCUTOR)
        val interlocutor: String,
        @ColumnInfo(name = KEY_HAS_MEDIA)
        val hasMedia: Boolean
) : Comparable<MessageInfo>, Parcelable {

    override fun compareTo(other: MessageInfo): Int = this.sid.compareTo(other.sid)

    fun attributes() = JSONObject(attributesString)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MessageInfo

        if (sid != other.sid) return false

        return true
    }

    override fun hashCode(): Int {
        return sid.hashCode()
    }

    companion object {
        const val EXTRA_MESSAGE = "EXTRA_MESSAGE"
        const val KEY_SID = "sid"
        const val KEY_BODY = "body"
        const val KEY_ATTRIBUTES = "attributes"
        const val KEY_CHANNEL_ID = "channel_id"
        const val KEY_HAS_MEDIA = "has_media"
    }
}
