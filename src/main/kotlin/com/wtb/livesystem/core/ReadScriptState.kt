package com.wtb.livesystem.core

class ReadScriptState(val script: ParsedScript) {
    val alreadyReadSentence = arrayListOf<String>()
    var lastReadSentence: String? = null

    fun getNextSentence(): String? {
        if (script.explanations.isEmpty()) {
            return null
        }
        lastReadSentence?.let {
            var index = script.explanations.indexOf(lastReadSentence)
            index++
            index %= script.explanations.size
            return script.explanations[index]
        }
        return script.explanations[0]
    }
}