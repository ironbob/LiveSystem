package com.wtb.livesystem.config.app.rule

object RuleParser {

    private val supportedMetrics = setOf("在线人数", "直播时长")
    private val supportedOperators = listOf(">=", "<=", ">", "<", "==", "!=")

    fun parse(ruleText: String): List<CompoundRule> {
        if (ruleText.isBlank()) return emptyList()

        val lines = ruleText.lines().map { it.trim() }.filter { it.isNotEmpty() }
        return lines.map { line ->
            parseLine(line)
        }
    }

    private fun parseLine(line: String): CompoundRule {
        val parts = when {
            line.contains("且") -> line.split("且").map { it.trim() }
            line.contains("或") -> line.split("或").map { it.trim() }
            else -> listOf(line)
        }

        val relation = when {
            line.contains("且") -> "AND"
            line.contains("或") -> "OR"
            else -> "SINGLE"
        }

        val ruleModels = parts.map { parseSingleRule(it) }
        return CompoundRule(ruleModels, relation)
    }

    private fun parseSingleRule(expression: String): RuleModel {
        for (op in supportedOperators) {
            if (expression.contains(op)) {
                val (left, right) = expression.split(op).map { it.trim() }
                if (!supportedMetrics.contains(left)) {
                    throw IllegalArgumentException("不支持的指标: $left")
                }
                val value = right.toIntOrNull()
                    ?: throw IllegalArgumentException("无效的数值: $right")
                return RuleModel(metric = left, operator = op, value = value)
            }
        }
        throw IllegalArgumentException("无法解析规则表达式: $expression")
    }

    fun validate(ruleText: String): String? {
        return try {
            parse(ruleText)
            null
        } catch (e: Exception) {
            e.message ?: "未知错误"
        }
    }
}


data class RuleModel(
    val metric: String,          // 指标：在线人数、直播时长、互动数等
    val operator: String,        // 操作符：>, >=, <, <=, ==, !=
    val value: Int               // 值
)

data class CompoundRule(
    val rules: List<RuleModel>,
    val relation: String         // 关系：且 或
)