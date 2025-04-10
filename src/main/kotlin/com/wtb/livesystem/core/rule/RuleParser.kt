package com.wtb.livesystem.core.rule


object RuleParser {

    fun parseRules(input: String): List<RuleModel> {
        return input.split(",")
            .map { it.trim() }
            .map { parseLogicalRule(it) }
    }

    private fun parseLogicalRule(text: String): RuleModel {
        val logicSplits = Regex("且|或").findAll(text).map { it.range.first to it.value }.toList()

        return if (logicSplits.isEmpty()) {
            parseComparisonRule(text.trim())
        } else {
            val parts = mutableListOf<String>()
            var lastIndex = 0
            for ((index, _) in logicSplits) {
                parts.add(text.substring(lastIndex, index).trim())
                lastIndex = index + 1
            }
            parts.add(text.substring(lastIndex).trim())

            val logicOperator = LogicOperator.fromKeyword(logicSplits.first().second)
                ?: error("Unknown logic operator in: $text")

            LogicRule(
                operator = logicOperator,
                subRules = parts.map { parseComparisonRule(it) }
            )
        }
    }

    fun validate(ruleText: String): String? {
        return try {
            parseRules(ruleText)
            null
        } catch (e: Exception) {
            e.message ?: "未知错误"
        }
    }

    private fun parseComparisonRule(text: String): ComparisonRule {
        val regex = Regex("""(.+?)(>=|<=|>|<|==)(.+)""")
        val match = regex.find(text.trim()) ?: error("Invalid comparison: $text")
        val (field, opStr, valueStr) = match.destructured

        val operator = Operator.fromSymbol(opStr.trim())
            ?: error("Unknown operator: $opStr")

        return ComparisonRule(
            field = field.trim(),
            operator = operator,
            value = valueStr.trim().toInt()
        )
    }
}
