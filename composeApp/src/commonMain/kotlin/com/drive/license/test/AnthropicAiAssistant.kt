package com.drive.license.test

import com.drive.license.test.domain.repository.AiAssistant
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class AnthropicAiAssistant(private val apiKey: String) : AiAssistant {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    override suspend fun explainAnswer(
        questionText: String,
        userAnswer: String,
        correctAnswer: String,
        isCorrect: Boolean
    ): String {
        if (apiKey.isBlank()) return "API key not configured."

        val prompt = if (isCorrect) {
            "Question: $questionText\nCorrect answer: $correctAnswer\n\nBriefly explain why this is the correct answer for a driving license exam. Be concise (2-3 sentences)."
        } else {
            "Question: $questionText\nUser answered: $userAnswer\nCorrect answer: $correctAnswer\n\nExplain why \"$correctAnswer\" is correct and \"$userAnswer\" is wrong for a driving license exam. Be concise (2-3 sentences)."
        }

        return try {
            val response = client.post("https://api.anthropic.com/v1/messages") {
                header("x-api-key", apiKey)
                header("anthropic-version", "2023-06-01")
                contentType(ContentType.Application.Json)
                setBody(
                    AnthropicRequest(
                        model = "claude-haiku-4-5-20251001",
                        maxTokens = 250,
                        messages = listOf(AnthropicMessage(role = "user", content = prompt))
                    )
                )
            }
            val body = response.body<AnthropicResponse>()
            body.content.firstOrNull()?.text ?: "No explanation available."
        } catch (e: Exception) {
            "Could not load explanation. Please try again."
        }
    }
}

@Serializable
private data class AnthropicRequest(
    val model: String,
    @SerialName("max_tokens") val maxTokens: Int,
    val messages: List<AnthropicMessage>
)

@Serializable
private data class AnthropicMessage(val role: String, val content: String)

@Serializable
private data class AnthropicResponse(val content: List<AnthropicContent>)

@Serializable
private data class AnthropicContent(val type: String, val text: String)
