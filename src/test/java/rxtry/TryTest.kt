package rxtry

import org.amshove.kluent.shouldBe
import org.junit.Test

class TryTest {
  @Test
  fun `invoke() should return value for Success`() {
    val x = Success(1)
    x().shouldBe(1)
  }

  @Test
  fun `invoke() should return error for Failure`() {
    val error = RuntimeException()
    val x = Failure<Int>(error)
    x().shouldBe(error)
  }
}
