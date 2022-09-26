package ru.kramex.programmingTheory.structures

class RamDynamicIntArray(
    private var _capacity: Int = DEFAULT_CAPACITY,
    private val defaultValue: Int = DEFAULT_VALUE
) {
    private var data: Array<Int> = Array(_capacity) { defaultValue }
    private var maxIndex: Int = 0

    fun contains(element: Int) = data.contains(element)

    val capacity: Int
        get() = _capacity

    val accumulator: Int
        get() = data[0]

    operator fun plusAssign(rhs: Int) {
        data[0] += rhs
    }

    operator fun minusAssign(rhs: Int) {
        data[0] -= rhs
    }

    operator fun timesAssign(rhs: Int) {
        data[0] *= rhs
    }

    operator fun divAssign(rhs: Int) {
        data[0] /= rhs
    }

    operator fun invoke(value: Int) {
        data[0] = value
    }

    operator fun set(index: Int, value: Int) {
        if (index.isNotBound) { increaseSize() }
        if (index > maxIndex) { maxIndex = index }
        data[index] = value
    }

    operator fun get(index: Int): Int =
        if (index.isBound) {
            data[index]
        } else {
            increaseSize()
            data[index]
        }

    override fun toString(): String =
        data.joinToString(", ")

    fun toList(): List<Int> {
        return (0..maxIndex).map { index -> data[index] }.toList()
    }

    private val Int.isBound: Boolean
        get() = this in 0 until _capacity

    private val Int.isNotBound: Boolean
        get() = !isBound

    private fun increaseSize() {
        _capacity *= DEFAULT_MULTIPLIER
        val newArray = Array(_capacity) { defaultValue }
        data.forEachIndexed { index, element ->
            newArray[index] = element
        }
        data = newArray

    }

    private companion object {
        const val DEFAULT_VALUE: Int = 0
        const val DEFAULT_CAPACITY: Int = 16
        const val DEFAULT_MULTIPLIER: Int = 2
    }
}


