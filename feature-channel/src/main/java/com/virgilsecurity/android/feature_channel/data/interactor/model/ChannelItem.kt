package com.virgilsecurity.android.feature_channel.data.interactor.model

import android.os.Parcelable
import com.virgilsecurity.android.base.data.model.MessageMeta

sealed class ChannelItem : Comparable<ChannelItem> {
    class Message(val message: MessageMeta): ChannelItem()
    class Date(val value: Long): ChannelItem()

    override fun compareTo(other: ChannelItem): Int {
        if (this is Message && other is Message) {
            return this.message.sid.compareTo(other.message.sid)
        }

        if (this is Date && other is Date) {
            return this.value.compareTo(other.value)
        }

        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        if (this is Message && other is Message) {
            return this.message == other.message
        }

        if (this is Date && other is Date) {
            return this.value == other.value
        }

        return true
    }

    override fun hashCode(): Int {
        if (this is Message) {
            return this.message.hashCode()
        }

        if (this is Date) {
            return this.value.hashCode()
        }

        return 0
    }
}