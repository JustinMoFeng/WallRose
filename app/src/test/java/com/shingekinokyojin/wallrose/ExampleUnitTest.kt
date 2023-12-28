package com.shingekinokyojin.wallrose

import android.util.Log
import com.shingekinokyojin.wallrose.model.Message
import com.shingekinokyojin.wallrose.model.MessageSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testMessageRole(){
        val json = Json { serializersModule = SerializersModule { contextual(Message::class, MessageSerializer) } }
        val jsonStr = "{\n" +
                "        \"role\": \"assistant\",\n" +
                "        \"content\": \"\",\n" +
                "        \"tool_calls\": [\n" +
                "          {\n" +
                "            \"id\": \"call_5ZSsXyMFqteWtzhpSCCql5W1\",\n" +
                "            \"function\": {\n" +
                "              \"arguments\": \"{\\\"date\\\":\\\"2023-12-28\\\",\\\"time\\\":\\\"23:00\\\"}\",\n" +
                "              \"name\": \"set_alarm\"\n" +
                "            },\n" +
                "            \"type\": \"function\"\n" +
                "          }\n" +
                "        ]\n" +
                "      }"
        val message = json.decodeFromString<Message>(jsonStr)
        println(message)
    }
}