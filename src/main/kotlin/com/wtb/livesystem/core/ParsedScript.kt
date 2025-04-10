package com.wtb.livesystem.core

class ParsedScript(val script: Script) {
    val explanations = arrayListOf<String>()
    var warmUps = arrayListOf<String>()

    init {

        val originExplanation = script.explanation
        val originWarmUps = script.warmUpContent
        explanations.addAll(originExplanation.lineSequence().map { it.trim() }.filter { it.isNotEmpty() })
        warmUps.addAll(originWarmUps.lineSequence().map { it.trim() }.filter { it.isNotEmpty() })

    }
}