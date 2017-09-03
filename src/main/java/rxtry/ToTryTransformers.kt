/**
 * Mainly for Java usage.
 */
@file:JvmName("ToTryTransformers")

package rxtry

import rx.Observable
import rx.Single

/**
 * Wraps any error from the upstream which matches [predicate] into [Failure] while
 * events emitted via [rx.Observer.onNext] will be wrapped into [Success].
 */
fun <T> toTryObservable(predicate: (Throwable) -> Boolean): Observable.Transformer<T, Try<T>> =
    ObservableTransformerToTry(predicate)

/**
 * Wraps any error from the upstream into [Failure] while
 * events emitted via [rx.Observer.onNext] will be wrapped into [Success].
 */
fun <T> toTryObservable(): Observable.Transformer<T, Try<T>> =
    ObservableTransformerToTry({ true })

/**
 * Wraps any error from the upstream which matches [predicate] into [Failure] while
 * events emitted via [rx.Observer.onNext] will be wrapped into [Success].
 */
fun <T> toTrySingle(predicate: (Throwable) -> Boolean): Single.Transformer<T, Try<T>> =
    SingleTransformerToTry(predicate)

/**
 * Wraps any error from the upstream into [Failure] while
 * events emitted via [rx.Observer.onNext] will be wrapped into [Success].
 */
fun <T> toTrySingle(): Single.Transformer<T, Try<T>> =
    SingleTransformerToTry({ true })
