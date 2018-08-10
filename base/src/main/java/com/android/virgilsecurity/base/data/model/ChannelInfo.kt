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
import com.android.virgilsecurity.base.data.model.MessageInfo.Companion.KEY_ATTRIBUTES
import com.android.virgilsecurity.base.data.properties.UserProperties
import com.android.virgilsecurity.base.util.GeneralConstants
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
class ChannelInfo(
        @PrimaryKey
        val sid: String,
        @ColumnInfo(name = KEY_ATTRIBUTES)
        val attributesString: String,
        @ColumnInfo(name = KEY_FRIENDLY_NAME)
        val friendlyName: String,
        @ColumnInfo(name = KEY_UNIQUE_NAME)
        val uniqueName: String,
        @ColumnInfo(name = GeneralConstants.KEY_SENDER)
        val sender: String,
        @ColumnInfo(name = GeneralConstants.KEY_INTERLOCUTOR)
        val interlocutor: String
): Comparable<ChannelInfo>, Parcelable {

    fun attributes() = JSONObject(attributesString)

    override fun compareTo(other: ChannelInfo): Int = this.sid.compareTo(other.sid)

    /**
     * When user creates channel - you will be as interlocutor for him, while when this channel
     * on your device you should be as sender, so this function encapsulates get of localized
     * interlocutor for current case.
     */
    fun localizedInterlocutor(userProperties: UserProperties) =
        (userProperties.currentUser!!.identity == sender).let {
            if (it) interlocutor else sender
        }

    companion object {
        const val EXTRA_CHANNEL_INFO = "EXTRA_CHANNEL_INFO"
        const val KEY_ATTRIBUTES = "attributes"
        const val KEY_FRIENDLY_NAME = "friendly_name"
        const val KEY_UNIQUE_NAME = "unique_name"
    }
}