package com.virgilsecurity.android.feature_channel.data.interactor.model

import com.virgilsecurity.android.base.data.model.MessageMeta

sealed class ChannelItem : Comparable<ChannelItem> {
    class Message(val value: MessageMeta): ChannelItem()
    class Date(val value: String): ChannelItem()

    override fun compareTo(other: ChannelItem): Int {
        if (this is Message && other is Message) {
            return this.value.sid.compareTo(other.value.sid)
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
            return this.value == other.value
        }

        if (this is Date && other is Date) {
            return this.value == other.value
        }

        return true
    }

    override fun hashCode(): Int {
        if (this is Message) {
            return this.value.hashCode()
        }

        if (this is Date) {
            return this.value.hashCode()
        }

        return 0
    }
}