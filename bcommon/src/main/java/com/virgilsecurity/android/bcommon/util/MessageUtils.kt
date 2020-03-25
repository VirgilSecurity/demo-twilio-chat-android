package com.virgilsecurity.android.bcommon.util

import android.content.Context
import android.text.format.DateUtils
import com.virgilsecurity.android.base.data.model.MessageMeta
import com.virgilsecurity.android.bcommon.data.helper.virgil.VirgilHelper
import com.virgilsecurity.sdk.utils.ConvertionUtils
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

class MessageUtils {
    companion object {
        fun getMessageText(message: MessageMeta, virgilHelper: VirgilHelper): String {
            val text = try {
                virgilHelper.decrypt(message.body!!)
            } catch (e: Exception) {
                "**Could not decrypt this message**"
            }

            if (text == "**Could not decrypt this message**") {
                return text
            }

            try {
                val map = JsonUtils.stringToMap(text)

                when (message.version) {
                    in "v1" -> return text
                    in "v2" -> {
                        when (val type = map["type"] as String) {
                            in "text" -> {
                                val payload = map["payload"] as? Map<*, *>
                                        ?: return "[Message does not include payload]"

                                return payload["body"] as? String
                                        ?: "[Message does not include payload.body]"
                            }
                            else -> {
                                return "[Message type '$type' is not yet supported]"
                            }
                        }
                    }
                    else -> {
                        return "[Update your app to see this message]"
                    }
                }
            } catch (e: Exception) {
                return "[Could not decode this message: $text. Error: $e]"
            }
        }

        fun mapToMessage(map: Map<String, Any?>, stanzaId: String, sender: String, channelId: String): MessageMeta =
                MessageMeta(stanzaId,
                        map["ciphertext"]!! as String,
                        sender,
                        channelId,
                        false,
                        (map["date"]!! as Double).toLong(),
                        map["version"] as? String ?: "v1"
                )

        fun getAMPMString(message: MessageMeta): String {
            return SimpleDateFormat("h:mm a").format(message.getDateMillisSince1970())
        }
    }
}