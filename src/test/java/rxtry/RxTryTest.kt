package rxtry

import org.junit.Test
import rx.Observable
import rx.Single
import java.io.IOException

class RxTryTest {
  @Test
  fun `should transform to Failure for Observable`() {
    val error = IOException()
    Observable.error<Int>(error)
        .toTry { it is IOException }
        .test()
        .assertNoErrors()
        .assertValue(Failure(error))
  }

  @Test
  fun `should transform to Failure for Single`() {
    val error = IOException()
    Single.error<Int>(error)
        .toTry { it is IOException }
        .test()
        .assertNoErrors()
        .assertValue(Failure(error))
  }

  @Test
  fun `should transform to Success for Observable`() {
    Observable.just(1)
        .toTry()
        .test()
        .assertNoErrors()
        .assertValue(Success(1))
  }

  @Test
  fun `should transform to Success for Single`() {
    Single.just(1)
        .toTry()
        .test()
        .assertNoErrors()
        .assertValue(Success(1))
  }
}
