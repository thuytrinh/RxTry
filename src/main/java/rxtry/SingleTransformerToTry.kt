package rxtry

import rx.Single

internal class SingleTransformerToTry<T>(
    private val predicate: (Throwable) -> Boolean
) : Single.Transformer<T, Try<T>> {
  override fun call(upstream: Single<T>): Single<Try<T>> {
    return upstream
        .map<Try<T>> { Success(it) }
        .onErrorResumeNext {
          when (predicate(it)) {
            true -> Single.just(Failure(it))
            false -> Single.error(it)
          }
        }
  }
}
