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

package com.virgilsecurity.android.base.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import com.virgilsecurity.android.base.data.properties.UserProperties
import com.virgilsecurity.android.base.util.GeneralConstants
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject

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
 * ChannelInfo
 */
@Entity
@Parcelize
class ChannelMeta(
        @PrimaryKey
        val sid: String,

        @ColumnInfo(name = GeneralConstants.KEY_SENDER)
        val sender: String,

        @ColumnInfo(name = GeneralConstants.KEY_INTERLOCUTOR)
        val interlocutor: String
): Comparable<ChannelMeta>, Parcelable {

    /**
     * When user creates channel - you will be as interlocutor for him, while when this channel
     * on your device you should be as sender, so this function encapsulates get of localized
     * interlocutor for current case.
     */
    fun localizedInterlocutor(userProperties: UserProperties) =
        with(userProperties.currentUser!!.identity == sender) {
            if (this) interlocutor else sender
        }

    override fun compareTo(other: ChannelMeta): Int = this.sid.compareTo(other.sid)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChannelMeta

        if (sid != other.sid) return false
        if (sender != other.sender) return false
        if (interlocutor != other.interlocutor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sid.hashCode()
        result = 31 * result + sender.hashCode()
        result = 31 * result + interlocutor.hashCode()
        return result
    }
}
