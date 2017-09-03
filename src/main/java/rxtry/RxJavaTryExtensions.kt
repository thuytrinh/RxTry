package rxtry

import rx.Observable
import rx.Single

/**
 * Wraps any error from the upstream which matches [predicate] into [Failure] while
 * events emitted via [rx.Observer.onNext] will be wrapped into [Success].
 */
fun <T> Observable<T>.toTry(
    predicate: (Throwable) -> Boolean = { true }
): Observable<Try<T>> =
    compose(ObservableTransformerToTry(predicate))

/**
 * Wraps any error from the upstream which matches [predicate] into [Failure] while
 * events emitted via [rx.Observer.onNext] will be wrapped into [Success].
 */
fun <T> Single<T>.toTry(
    predicate: (Throwable) -> Boolean = { true }
): Single<Try<T>> =
    compose(SingleTransformerToTry(predicate))
