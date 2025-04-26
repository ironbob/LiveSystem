package com.wtb.livesystem.core
class ReadScriptState(val script: ParsedScript) {
    val alreadyReadSentence = arrayListOf<String>()
    var lastIndex: Int = -1  // 初始为 -1，表示尚未开始阅读

    fun getNextSentence(): String? {
        if (script.explanations.isEmpty()) {
            return null
        }
        lastIndex = (lastIndex + 1) % script.explanations.size
        val next = script.explanations[lastIndex]
        alreadyReadSentence.add(next)
        return next
    }
}
