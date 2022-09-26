package ru.kramex.programmingTheory.ram

import ru.kramex.programmingTheory.ram.comands.Add
import ru.kramex.programmingTheory.ram.comands.Div
import ru.kramex.programmingTheory.ram.comands.Halt
import ru.kramex.programmingTheory.ram.comands.Jgtz
import ru.kramex.programmingTheory.ram.comands.Jump
import ru.kramex.programmingTheory.ram.comands.Jzero
import ru.kramex.programmingTheory.ram.comands.Load
import ru.kramex.programmingTheory.ram.comands.Mult
import ru.kramex.programmingTheory.ram.comands.RamCommand
import ru.kramex.programmingTheory.ram.comands.Read
import ru.kramex.programmingTheory.ram.comands.Store
import ru.kramex.programmingTheory.ram.comands.Sub
import ru.kramex.programmingTheory.ram.comands.Write
import ru.kramex.programmingTheory.ram.comands.isNotManagementCommand
import ru.kramex.programmingTheory.ram.operands.LiteralOperand
import ru.kramex.programmingTheory.ram.operands.LiteralOrDirectAddressingOperand
import ru.kramex.programmingTheory.ram.operands.RamOperand
import ru.kramex.programmingTheory.structures.RamDynamicIntArray
import ru.kramex.programmingTheory.utils.isZero

class RamMachine private constructor(val commands: List<RamCommand>) {

    fun work(input: List<Int>): Work = Work(input)

    inner class Work constructor(var input: List<Int>) {
        private val registers: RamDynamicIntArray = RamDynamicIntArray()
        private var output: List<Int> = emptyList()
        private var currentCommand: Int = 1
        private var state: RamMachineState = RamMachineState.Run

        private val commandIndex: Int
            get() = currentCommand - 1

        fun start(): Result {

            val history: MutableList<IntermediateResult> = mutableListOf()
            while (state is RamMachineState.Run) {
                try {
                    val command = commands[commandIndex]
                    command.handleCommand()

                    IntermediateResult(
                        command = command,
                        registers = registers
                    ).also { history.add(it) }

                    if (command.isNotManagementCommand) {
                        currentCommand++
                    }
                } catch (error: Throwable) {
                    state = RamMachineState.Error(error)
                    break
                }
            }

            return when (state) {
                is RamMachineState.Error -> Result.Error(
                    error = (state as RamMachineState.Error).error,
                    steps = history
                )
                RamMachineState.Final -> Result.Success(steps = history, output = output)
                else -> {
                    throw IllegalStateException("impossible condition")
                }
            }
        }

        private fun RamCommand.handleCommand() {
            when (this) {
                is Add -> {
                    registers += operand.finalValue
                }
                is Div -> {
                    registers /= operand.finalValue
                }
                is Sub -> {
                    registers -= operand.finalValue
                }
                is Mult -> {
                    registers *= operand.finalValue
                }
                is Load -> {
                    registers(operand.finalValue)
                }
                is Store -> {
                    registers[operand.finalValue] = registers.accumulator
                }

                is Jump -> {
                    currentCommand = operand.value
                }
                is Jzero -> {
                    if (registers.accumulator.isZero) {
                        currentCommand = operand.value - 1
                    } else {
                        currentCommand++
                    }
                }
                is Jgtz -> {
                    if (registers.accumulator > 0) {
                        currentCommand = operand.value - 1
                    } else {
                        currentCommand++
                    }
                }

                is Read -> {
                    val symbol: Int = input.firstOrNull()
                        ?: throw IllegalStateException("the entrance is exhausted")
                    registers[operand.finalValue] = symbol
                    input = input.drop(1)
                }
                is Write -> {
                    output = output + operand.finalValue
                }
                Halt -> {
                    state = RamMachineState.Final
                }
            }
        }

        private val RamOperand.finalValue: Int
            get() = this.finalValue(registers)

        private val Int.isValidCp: Boolean
            get() = this in 1..commands.size

    }

    class Builder {
        private val commands: MutableList<RamCommand> = mutableListOf()

        fun load(operand: RamOperand) {
            commands.add(Load(operand))
        }

        fun store(operand: LiteralOrDirectAddressingOperand) {
            commands.add(Store(operand))
        }

        fun add(operand: RamOperand) {
            commands.add(Add(operand))
        }

        fun sub(operand: RamOperand) {
            commands.add(Sub(operand))
        }

        fun mult(operand: RamOperand) {
            commands.add(Mult(operand))
        }

        fun div(operand: RamOperand) {
            commands.add(Div(operand))
        }

        fun read(operand: LiteralOrDirectAddressingOperand) {
            commands.add(Read(operand))
        }

        fun write(operand: RamOperand) {
            commands.add(Write(operand))
        }

        fun jump(operand: LiteralOperand) {
            commands.add(Jump(operand))
        }

        fun jzero(operand: LiteralOperand) {
            commands.add(Jzero(operand))
        }

        fun jgtz(operand: LiteralOperand) {
            commands.add(Jgtz(operand))
        }

        fun halt() {
            commands.add(Halt)
        }

        fun build(): List<RamCommand> = commands.toList()
    }

    private sealed interface RamMachineState {
        object Run : RamMachineState
        object Final : RamMachineState
        data class Error(val error: Throwable) : RamMachineState
    }

    data class IntermediateResult(
        val command: RamCommand,
        val registers: RamDynamicIntArray
    )

    sealed interface Result {
        val steps: List<IntermediateResult>

        data class Success(val output: List<Int>, override val steps: List<IntermediateResult>) : Result
        data class Error(val error: Throwable, override val steps: List<IntermediateResult>) : Result
    }

    companion object {
        fun create(init: Builder.() -> Unit): RamMachine =
            RamMachine(Builder().apply(init).build())
    }
}
