package com.wtb.livesystem.core

import com.wtb.livesystem.core.rule.RuleModel
import com.wtb.livesystem.core.rule.RuleParser

class ParsedScript(val script: Script) {
    val explanations = arrayListOf<String>()
    val parsedRules:List<RuleModel>

    init {

        val originExplanation = script.explanation
        explanations.addAll(originExplanation.lineSequence().map { it.trim() }.filter { it.isNotEmpty() })
        parsedRules = RuleParser.parseRules(script.ruleString)

    }
}