package com.virgilsecurity.android.bcommon.util

import com.virgilsecurity.android.base.data.model.MessageMeta
import com.virgilsecurity.android.bcommon.data.helper.virgil.VirgilHelper

class MessageUtils {
    companion object {
        fun getMessageText(message: MessageMeta, virgilHelper: VirgilHelper): String {
            val text = try {
                virgilHelper.decrypt(message.body!!)
            } catch (e: Exception) {
                "**Could not decrypt this message**"
            }

            val map = JsonUtils.stringToMap(text)

            when (message.codableVersion) {
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
        }
    }
}