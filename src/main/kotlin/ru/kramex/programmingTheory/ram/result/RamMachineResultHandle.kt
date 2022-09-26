package ru.kramex.programmingTheory.ram.result

import ru.kramex.programmingTheory.ram.RamMachine
import ru.kramex.programmingTheory.ram.operands.DirectAddressingOperand
import ru.kramex.programmingTheory.ram.operands.IndirectAddressingOperand
import ru.kramex.programmingTheory.ram.operands.LiteralOperand
import ru.kramex.programmingTheory.ram.operands.RamOperand
import ru.kramex.programmingTheory.structures.RamDynamicIntArray
import kotlin.math.floor
import kotlin.math.log2

private enum class EstimationType {
    Logarithmic, Uniform
}

private val Int.l: Int get() = floor(log2(this.toDouble())).toInt() + 1
private fun RamOperand?.w(
    registers: RamDynamicIntArray,
    estimationType: EstimationType
): Int {
    this ?: return 1
    return when (estimationType) {
        EstimationType.Logarithmic -> when (this) {
            is DirectAddressingOperand -> value.l + registers[value].l
            is IndirectAddressingOperand -> value.l + registers[value].l + registers[registers[value]].l
            is LiteralOperand -> value.l
        }
        EstimationType.Uniform -> 1
    }
}


fun RamMachine.Result.handle() {
    val result = this
    var logEstimation = 0
    var uniformEstimation = 0

    result.steps.forEachIndexed { index, step ->
        println("\nStep #${index + 1}")
        println(step.command)
        println(step.registers)

        logEstimation += step.command.ramOperand.w(
            registers = step.registers,
            estimationType = EstimationType.Logarithmic
        )
        uniformEstimation += step.command.ramOperand.w(
            registers = step.registers,
            estimationType = EstimationType.Uniform
        )
    }

    println()
    println("uniform estimation = $uniformEstimation")
    println("log estimation = $logEstimation")
    println()

    when (result) {
        is RamMachine.Result.Error ->
            println("RAM machine terminated with an error = ${result.error}")
        is RamMachine.Result.Success -> {
            print("RAM machine has successfully completed execution, output = ${result.output}")
        }
    }
}
