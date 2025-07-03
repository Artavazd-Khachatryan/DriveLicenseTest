package com.drive.license.test.database.adapters

import app.cash.sqldelight.ColumnAdapter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class JsonListAdapter : ColumnAdapter<List<String>, String> {
    private val json = Json { ignoreUnknownKeys = true }
    
    override fun decode(databaseValue: String): List<String> {
        if (databaseValue.isEmpty()) {
            return emptyList()
        }
        return json.decodeFromString<List<String>>(databaseValue)
    }
    
    override fun encode(value: List<String>): String {
        return json.encodeToString(value)
    }
}