package com.wtb.livesystem.core.rule
sealed class RuleModel

enum class RuleFieldName(val value: String){
    FieldCount("在线人数"),
    FieldLiveDuration("直播时长")
}

data class ComparisonRule(
    val field: String,
    val operator: Operator,
    val value: Int
) : RuleModel()

data class LogicRule(
    val operator: LogicOperator,
    val subRules: List<RuleModel>
) : RuleModel()

enum class Operator {
    GT, LT, EQ, GTE, LTE;

    companion object {
        fun fromSymbol(symbol: String): Operator? = when (symbol) {
            ">" -> GT
            "<" -> LT
            "==" -> EQ
            ">=" -> GTE
            "<=" -> LTE
            else -> null
        }
    }
}

enum class LogicOperator {
    AND, OR;

    companion object {
        fun fromKeyword(keyword: String): LogicOperator? = when (keyword.trim()) {
            "且" -> AND
            "或" -> OR
            else -> null
        }
    }
}
