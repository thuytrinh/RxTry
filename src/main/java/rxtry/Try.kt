/**
 * Was forked from https://github.com/MarioAriasC/funKTionale.
 */
package rxtry

sealed class Try<T>

data class Success<T>(private val value: T) : Try<T>() {
  operator fun invoke() = value

  override fun toString(): String = "Success($value)"
}

data class Failure<T>(private val error: Throwable) : Try<T>() {
  operator fun invoke() = error

  override fun toString(): String = "Failure($error)"
}
