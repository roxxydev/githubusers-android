package com.githubusers.android.domain.util

object ModelUtil {

}

inline fun <reified T : Enum<T>> valueOf(type: String?): T? {
    if (type == null) {
        return null
    }

    return try {
        java.lang.Enum.valueOf(T::class.java, type)
    } catch (e: IllegalArgumentException) {
        null
    }
}