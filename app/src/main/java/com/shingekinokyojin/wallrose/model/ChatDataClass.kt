package com.shingekinokyojin.wallrose.model

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonNames

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable(with = MessageSerializer::class)
sealed class Message

object MessageSerializer : JsonContentPolymorphicSerializer<Message>(Message::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<Message> {
        return when (val role = element.jsonObject["role"]?.jsonPrimitive?.content) {
            "system" -> SystemMessage.serializer()
            "user" -> UserMessage.serializer()
            "assistant" -> AssistantMessage.serializer()
            "tool" -> ToolMessage.serializer()
            else -> throw SerializationException("Unknown role: $role")
        }
    }
}

@Serializable
@SerialName("system")
data class SystemMessage(
    val role: String = "system",
    val content: String
) : Message()

@Serializable
@SerialName("user")
data class UserMessage(
    val role: String = "user",
    val content: String
) : Message()

@Serializable
@SerialName("assistant")
data class AssistantMessage(
    val role: String = "assistant",
    val content: String?,
    val tool_calls: List<ToolCall>?
) : Message()

@Serializable
@SerialName("tool")
data class ToolMessage(
    val role: String = "tool",
    val name: String,
    val tool_call_id: String,
    val content: String
) : Message()

@Serializable
data class Chat (
    val _id: String? = null,
    val owner: String,
    val name: String? = null,
    val messages: List<Message>
)

@Serializable
data class Function(
    val arguments: String,
    val name: String
)

@Serializable
data class ToolCall(
    val id: String,
    val function: Function,
    val type: String
)