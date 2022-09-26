package ru.kramex.programmingTheory.stateMachine

fun StateMachine<*, Char>.checkString(word: String): Boolean {
    val stateMachine = this.clone()
    word.forEach { char ->
        stateMachine.transition(char)
    }
    return stateMachine.isFinalState
}
