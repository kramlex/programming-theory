package ru.kramex.programmingTheory.ram

import ru.kramex.programmingTheory.ram.operands.direct
import ru.kramex.programmingTheory.ram.operands.literal
import ru.kramex.programmingTheory.ram.result.handle

fun ramMachineMain() {
    // abs machine = | input(0) |
    val ramMachine = RamMachine.create {
        read(1.literal)
        load(1.direct)
        jgtz(1.literal)
        load(0.literal)
        sub(1.direct)
        write(0.direct)
        halt()
    }

    val input = listOf(-5,4,3,2,1)

    val work = ramMachine.work(input)
    val result = work.start()
    result.handle()
}