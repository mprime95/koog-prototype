package org.example

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet
import ai.koog.agents.core.tools.reflect.asTools
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import kotlinx.coroutines.runBlocking

class ClassPickerTools: ToolSet {

    @Tool
    fun rolePicker(): List<String> {
        println("-----------------")
        println("There are multiple different types roles you can fulfill in D&D, but your main options are Tank, Support, Damage, or a mixture")
        println("Please input 1 or 2 roles you would like to fill")
        println("-----------------")
        val roles = readln().split(" ")
        return roles
    }

    @Tool
    fun rangePicker(): String {
        println("-----------------")
        println("would you prefer to be close or long range?")
        println("-----------------")
        val range = readln()
        return range
    }

    @Tool
    fun classPicker(roles: List<String>, range: String): String {
        println("---------------")
        val characterClass = if (roles.size == 1) {
            when (roles[0].lowercase()) {
                "support" -> "Bard or Cleric"
                "tank" -> "Barbarian, Fighter, or Paladin"
                "damage" -> when (range.lowercase()) {
                    "close" -> "Fighter, Monk, or Rogue"
                    "long" -> "Ranger, Sorcerer, Warlock, or Wizard"
                    else -> "Any class"
                }

                else -> "Any class"
            }
        } else {
            when {
                roles[0].lowercase() == "support" && roles[1].lowercase() == "tank" -> "Cleric or Paladin"
                roles[0].lowercase() == "support" && roles[1].lowercase() == "damage" -> "Cleric or Druid"
                roles[0].lowercase() == "tank" && roles[1].lowercase() == "damage" -> "Barbarian or Fighter"
                else -> "Any class"
            }
        }
        return characterClass
    }
}


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() = runBlocking {
    // Get an API key from the GOOGLE_API_KEY environment variable
    val apiKey = System.getenv("GOOGLE_API_KEY")
        ?: error("The API key is not set.")

    val toolRegistry = ToolRegistry {
        tools(ClassPickerTools().asTools())
    }

    // Create an agent
    val agent = AIAgent(
        executor = simpleGoogleAIExecutor(apiKey),
        systemPrompt = "You are a D&D assistant. Using the available tools, suggest a class for the user",
        llmModel = GoogleModels.Gemini2_0Flash,
        temperature = 0.7,
        toolRegistry = toolRegistry,
    )

    // Run the agent
    val result = agent.run("What class should I play? please use the tools available to you")
    println(result)
}