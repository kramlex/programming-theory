package ru.kramex.programmingTheory.ram.comands

import ru.kramex.programmingTheory.ram.operands.LiteralOperand
import ru.kramex.programmingTheory.ram.operands.LiteralOrDirectAddressingOperand
import ru.kramex.programmingTheory.ram.operands.RamOperand

sealed interface RamCommand {
    val ramOperand: RamOperand?
}

sealed interface RamArithmeticCommand : RamCommand

data class Load(val operand: RamOperand) : RamArithmeticCommand {
    override fun toString(): String = "Load (operand $operand)"

    override val ramOperand: RamOperand?
        get() = operand
}

data class Store(val operand: LiteralOrDirectAddressingOperand) : RamArithmeticCommand {
    override fun toString(): String = "Store (operand $operand)"

    override val ramOperand: RamOperand?
        get() = operand
}

data class Add(val operand: RamOperand) : RamArithmeticCommand {
    override fun toString(): String = "Add (operand $operand)"

    override val ramOperand: RamOperand?
        get() = operand
}

data class Sub(val operand: RamOperand) : RamArithmeticCommand {
    override fun toString(): String = "Sub (operand $operand)"

    override val ramOperand: RamOperand?
        get() = operand
}

data class Mult(val operand: RamOperand) : RamArithmeticCommand {
    override fun toString(): String = "Mult (operand $operand)"

    override val ramOperand: RamOperand?
        get() = operand
}

data class Div(val operand: RamOperand) : RamArithmeticCommand {
    override fun toString(): String = "Div (operand $operand)"

    override val ramOperand: RamOperand?
        get() = operand
}

sealed interface RamManagementCommand : RamCommand

data class Jump(val operand: LiteralOperand) : RamManagementCommand {
    override fun toString(): String = "Jump (operand $operand)"

    override val ramOperand: RamOperand?
        get() = operand
}

data class Jzero(val operand: LiteralOperand) : RamManagementCommand {
    override fun toString(): String = "Jzero (operand $operand)"

    override val ramOperand: RamOperand?
        get() = operand
}

data class Jgtz(val operand: LiteralOperand) : RamManagementCommand {
    override fun toString(): String = "Jgtz (operand $operand)"

    override val ramOperand: RamOperand?
        get() = operand
}

object Halt : RamManagementCommand {
    override fun toString(): String = "Halt (stop)"

    override val ramOperand: RamOperand?
        get() = null
}

sealed interface RamReadWriteCommand : RamCommand

data class Read(val operand: LiteralOrDirectAddressingOperand) : RamReadWriteCommand {
    override fun toString(): String = "Read (operand $operand)"

    override val ramOperand: RamOperand?
        get() = operand
}

data class Write(val operand: RamOperand) : RamReadWriteCommand {
    override fun toString(): String = "Write (operand $operand)"

    override val ramOperand: RamOperand?
        get() = operand
}

val RamCommand.isManagementCommand: Boolean get() = this is RamManagementCommand
val RamCommand.isNotManagementCommand: Boolean get() = this !is RamManagementCommand
