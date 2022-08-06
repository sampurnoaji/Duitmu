package id.petersam.catatankeuangan.util

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.Date

inline fun <reified T> List<T>.toJson(): String {
    val moshi = Moshi.Builder()
        .add(Date::class.java, DateJsonAdapter())
        .add(KotlinJsonAdapterFactory())
        .build()
    val type = Types.newParameterizedType(
        List::class.java,
        T::class.java
    )
    val adapter: JsonAdapter<List<T>> = moshi.adapter(type)
    return adapter.toJson(this)
}

inline fun <reified T> String.fromJson(): List<T>? {
    val moshi = Moshi.Builder()
        .add(Date::class.java, DateJsonAdapter())
        .add(KotlinJsonAdapterFactory())
        .build()
    val type = Types.newParameterizedType(
        List::class.java,
        T::class.java
    )
    val adapter: JsonAdapter<List<T>> = moshi.adapter(type)
    return adapter.fromJson(this)
}

class DateJsonAdapter : JsonAdapter<Date>() {
    @FromJson
    override fun fromJson(reader: JsonReader): Date {
        val value = reader.nextString()
        return Date().apply {
            time = value.toLong()
        }
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: Date?) {
        writer.value(value?.time)
    }

}