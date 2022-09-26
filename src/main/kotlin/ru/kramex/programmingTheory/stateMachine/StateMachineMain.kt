package ru.kramex.programmingTheory.stateMachine

enum class MainStates {
    State1, State2;
}

fun stateMachineMain()  {
    val stateMachine = StateMachine.create {
        initialState(MainStates.State1)
        finalStates = listOf(MainStates.State1)

        state(MainStates.State1) {
            on('0') { transition(MainStates.State2) }
            on('1') { transition(MainStates.State1) }
        }

        state(MainStates.State2) {
            onThis('1')
            on('0') { transition(MainStates.State1) }
        }
    }

    val word = "1001000111"

    if (stateMachine.checkString(word)) {
        println("the string fits the FSM")
    } else {
        println("the string does not fit the FSM")
    }
}
