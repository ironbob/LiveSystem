package com.wtb.livesystem.core.rule

object RuleEvaluator {

    fun evaluate(rule: RuleModel, context: Map<String, Int>): Boolean {
        return when (rule) {
            is ComparisonRule -> {
                val actual = context[rule.field] ?: return false
                compare(actual, rule.operator, rule.value)
            }
            is LogicRule -> {
                when (rule.operator) {
                    LogicOperator.AND -> rule.subRules.all { evaluate(it, context) }
                    LogicOperator.OR -> rule.subRules.any { evaluate(it, context) }
                }
            }
        }
    }

    private fun compare(actual: Int, operator: Operator, expected: Int): Boolean {
        return when (operator) {
            Operator.GT -> actual > expected
            Operator.LT -> actual < expected
            Operator.EQ -> actual == expected
            Operator.GTE -> actual >= expected
            Operator.LTE -> actual <= expected
        }
    }
}