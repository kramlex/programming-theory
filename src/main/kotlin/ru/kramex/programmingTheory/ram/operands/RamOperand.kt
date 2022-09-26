package ru.kramex.programmingTheory.ram.operands

import ru.kramex.programmingTheory.structures.RamDynamicIntArray

sealed interface RamOperand {
    val value: Int
    fun finalValue(registers: RamDynamicIntArray): Int
}

sealed interface LiteralOrDirectAddressingOperand : RamOperand

data class LiteralOperand(override val value: Int) : RamOperand, LiteralOrDirectAddressingOperand {
    override fun finalValue(registers: RamDynamicIntArray): Int {
        return value
    }

    override fun toString(): String = "=$value"
}

data class DirectAddressingOperand(override val value: Int) : RamOperand, LiteralOrDirectAddressingOperand {
    override fun finalValue(registers: RamDynamicIntArray): Int {
        return registers[value]
    }

    override fun toString(): String = "R($value)"
}

data class IndirectAddressingOperand(override val value: Int) : RamOperand {
    override fun finalValue(registers: RamDynamicIntArray): Int {
        return registers[registers[value]]
    }

    override fun toString(): String = "R(R($value))"
}

val Int.direct get() = DirectAddressingOperand(this)
val Int.indirect get() = IndirectAddressingOperand(this)
val Int.literal get() = LiteralOperand(this)
