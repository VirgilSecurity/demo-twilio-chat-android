package com.virgilsecurity.android.bcommon.util

import com.google.gson.reflect.TypeToken
import com.virgilsecurity.sdk.utils.ConvertionUtils

class JsonUtils {
    companion object {
        fun stringToMap(json: String): Map<String, Any?> = ConvertionUtils.getGson().fromJson(
                json,
                object : TypeToken<Map<String, Any?>?>() {}.type
        )

        fun mapToString(map: Map<String, Any?>): String = ConvertionUtils.getGson().toJson(map)
    }
}