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
import com.virgilsecurity.android.base.data.model.User.Companion.KEY_USERS_TABLE_NAME
import com.virgilsecurity.sdk.cards.Card
import com.virgilsecurity.sdk.cards.model.RawSignedModel
import com.virgilsecurity.sdk.crypto.VirgilCardCrypto
import kotlinx.android.parcel.Parcelize

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
 * User class stores properties of User.
 */
@Entity(tableName = KEY_USERS_TABLE_NAME)
@Parcelize
class User(
        @PrimaryKey @ColumnInfo(name = KEY_IDENTITY)
        val identity: String,
        @ColumnInfo(name = KEY_RAW_SIGNED_MODEL)
        val rawSignedModelString: String,
        @ColumnInfo(name = KEY_USER_PIC_PATH) val picturePath: String? = null
) : Comparable<User>, Parcelable {

    override fun compareTo(other: User): Int = this.identity.compareTo(other.identity)

    fun card(): Card = Card.parse(VirgilCardCrypto(), rawSignedModel())

    fun rawSignedModel(): RawSignedModel = RawSignedModel.fromString(rawSignedModelString)

    companion object {
        const val EXTRA_USER = "EXTRA_USER"
        const val KEY_IDENTITY = "identity"
        const val KEY_RAW_SIGNED_MODEL = "raw_signed_model"
        const val KEY_USER_PIC_PATH = "user_pic_path"
        const val KEY_USERS_TABLE_NAME = "users"
    }
}
