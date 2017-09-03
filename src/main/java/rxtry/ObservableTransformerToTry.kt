package rxtry

import rx.Observable

internal class ObservableTransformerToTry<T>(
    private val predicate: (Throwable) -> Boolean
) : Observable.Transformer<T, Try<T>> {
  override fun call(upstream: Observable<T>): Observable<Try<T>> {
    return upstream
        .map<Try<T>> { Success(it) }
        .onErrorResumeNext {
          when (predicate(it)) {
            true -> Observable.just(Failure(it))
            false -> Observable.error(it)
          }
        }
  }
}
