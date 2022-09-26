package ru.kramex.programmingTheory.stateMachine

import java.util.concurrent.atomic.AtomicReference

data class Transition<State: Any, AlphabetType: Any>(
    val toState: State,
    val value: AlphabetType,
    val listener: List<() -> Unit>
)

data class Graph<State: Any, AlphabetType: Any>(
    val initialState: State,
    val finalStates: List<State>,
    val transitions: Map<State, List<Transition<State, AlphabetType>>>
)

class GraphBuilder<State: Any, AlphabetType: Any>(
    graph: Graph<State, AlphabetType>? = null
) {
    private var initialState = graph?.initialState
    private val transitions = LinkedHashMap(graph?.transitions ?: emptyMap())
    var finalStates: List<State> = emptyList()

    fun initialState(initialState: State) {
        this.initialState = initialState
    }

    fun <S: State> state(
        state: S,
        init: StateTransitionsBuilder<S>.() -> Unit
    ) {
        transitions[state] = StateTransitionsBuilder<S>(state).apply(init).build()
    }

    fun build(): Graph<State, AlphabetType> =
        Graph(
            requireNotNull(initialState),
            finalStates = finalStates,
            transitions = transitions
        )

    inner class StateTransitionsBuilder<S: State>(
        private val state: State
    ) {

        private var statesTransitions: List<Transition<State, AlphabetType>> = mutableListOf()

        fun on(value: AlphabetType, builder: TransitionBuilder.() -> Unit) {
            val transition = TransitionBuilder(value).apply(builder).build() ?: return
            statesTransitions = statesTransitions.filter { it.value != transition.value } + transition
        }

        fun onThis(value: AlphabetType, builder: TransitionBuilderWithoutState.() -> Unit) {
            val transition = TransitionBuilderWithoutState(state, value).apply(builder).build()
            statesTransitions = statesTransitions.filter { it.value != transition.value } + transition
        }

        fun onThis(value: AlphabetType) {
            val transition = TransitionBuilderWithoutState(state, value).build()
            statesTransitions = statesTransitions.filter { it.value != transition.value } + transition
        }

        fun build(): List<Transition<State, AlphabetType>> = statesTransitions
    }

    inner class TransitionBuilder constructor(
        private val value: AlphabetType
    ){
        private var toState: State? = null
        private var listeners: MutableList<() -> Unit> = mutableListOf()

        fun transition(toState: State) {
            this.toState = toState
        }

        fun transition(toState: State, listener: () -> Unit) {
            this.toState = toState
            listeners.add(listener)
        }

        fun addListener(listener: () -> Unit) {
            listeners.add(listener)
        }

        fun build(): Transition<State, AlphabetType>? {
            val toState = toState ?: return null
            return Transition(
                toState = toState,
                value = value,
                listener = listeners
            )
        }
    }

    inner class TransitionBuilderWithoutState constructor(
        private var toState: State,
        private val value: AlphabetType
    ){
        private var listeners: MutableList<() -> Unit> = mutableListOf()

        fun addListener(listener: () -> Unit) {
            listeners.add(listener)
        }

        fun build(): Transition<State, AlphabetType> {
            return Transition(
                toState = toState,
                value = value,
                listener = listeners
            )
        }
    }
}

class StateMachine<State: Any, AlphabetType: Any> private constructor(
    private val graph: Graph<State, AlphabetType>
){

    private val stateRef = AtomicReference(graph.initialState)

    val state: State
        get() = stateRef.get()

    val isFinalState: Boolean
        get() = graph.finalStates.contains(stateRef.get())

    fun transition(value: AlphabetType) {
        val transition = synchronized(this) {
            val fromState = stateRef.get()

            val transition = fromState.getTransition(value)
                ?: error("Missing definition for state = ${fromState.simplyName()}!")

            Pair(fromState,transition.toState).transitionNotify()

            stateRef.set(transition.toState)
            transition
        }

        transition.notify()
    }

    fun clone(): StateMachine<State, AlphabetType> {
        return StateMachine(graph)
    }

    private fun State.getTransition(value: AlphabetType): Transition<State, AlphabetType>? {
        for (transition in getTransitions()) {
            if (transition.value == value) {
                return transition
            }
        }
        return null
    }

    private fun Pair<State, State>.transitionNotify(): Unit =
        println("ChangeState from ${first.simplyName()} to ${second.simplyName()}")


    private fun State.simplyName(): String =
        this.toString()

    private fun State.getTransitions() = graph.transitions
        .filter { it.key == this }
        .flatMap { it.value }

    private fun Transition<State, AlphabetType>.notify(): Unit =
        this.listener.forEach { it.invoke() }


    companion object {
        fun <State: Any, AlphabetType: Any> create(
            init: GraphBuilder<State, AlphabetType>.() -> Unit
        ): StateMachine<State, AlphabetType> = create(null, init)

        private fun <State: Any, AlphabetType: Any> create(
            graph: Graph<State, AlphabetType>?,
            init: GraphBuilder<State, AlphabetType>.() -> Unit
        ): StateMachine<State, AlphabetType> =
            StateMachine(GraphBuilder(graph).apply(init).build())
    }
}