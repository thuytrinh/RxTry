# RxTry

`Try<T>` computation for RxJava

## What

`Try<T>` is a simple [sum type](https://en.wikipedia.org/wiki/Tagged_union) representing 2 cases: success and failure.

```kotlin
val success = Success(1)
success() // This will return 1.

val error = IllegalStateException()
val failure = Failure(error)
failure() // This will return the `error`.
```

```kotlin
val result = getResult() // Returns `Try<String>`
when (result) {
  is Success -> print(result())
  is Failure -> logError(result())
}
```

```kotlin
fun getTop10NearbyRideShares(val position: Coordinates): Single<List<Ride>> {
  return api.getRideShares(position = position, count = 10)
    .toTry()
}
```

Corresponding Java usage via `compose()`:

```java
Single<List<Ride>> getTop10NearbyRideShares(final Coordinates position) {
  return api.getRideShares(position, 10)
    .compose(toTrySingle())
}
```

## Why

### To produce a non-interrupted stream for RxJava

If you use Kotlin, this library provides 2 extension functions `toTry()` so that you can compose with `Single` or `Observable`.

#### Example

> Whenever the user moves to a new location, you will ask `PokémonService` to retrieve top 10 nearby pokémons. Under the hood, `PokémonService` will shoot a network request to a RESTful backend service.

An initial impl can be like:

```kotlin
fun getTop10Pokemons(): Observable<List<Pokemon>> {
  // Assume that you have `getUserLocationStream(): Observable<Location>`.
  return getUserLocationStream()
    .switchMap(userLocation -> pokemonService.retrieveNearbyPokemons(count = 10))
}
```

But there's problem. If somehow `retrieveNearbyPokemons()` runs into network errors like, due to no Internet connection, or temporarily unreachable backend service, the stream returned by `getTop10Pokemons()` will be terminated. For example,

* When the user moves to (1, 2), we got pokemons A, and B from `pokemonService`.
* When the user moves to (2, 3), `pokemonService` fails due to `IOException`. The stream `Observable<List<Pokemon>>` triggers its `onError()`, thus terminates.
* Next, when the user moves to (4, 5), we receive no event from `getTop10Pokemons()` anymore because it already terminated before.

So, using `toTry()` can solve the problem here:

```kotlin
fun getTop10Pokemons(): Observable<Try<List<Pokemon>>> {
  // Assume that you have `getUserLocationStream(): Observable<Location>`.
  return getUserLocationStream()
    .switchMap(userLocation -> {
      pokemonService.retrieveNearbyPokemons(count = 10)
        // Assume that `isKindOfNetworkError()` is an extension function
        // in form of `(Throwable) -> Boolean`.
        .toTry { it.isKindOfNetworkError() }
    })
}
```

How can it be used from the outside?

```kotlin
getTop10Pokemons()
  .subscribe {
    when (it) {
      is Success -> showPokemons(it())
      is Failure -> showError(it())
    }
  }
```

But how can `toTry()` actually be helpful? Let's go through the emission again:

* When the user moves to (1, 2), we got pokemons A, and B from `pokemonService`, and they are wrapped into a `Success`.
* When the user moves to (2, 3), `pokemonService` fails due to `IOException`. `toTry()` will wrap the error into a `Failure`. But actually `getTop10Pokemons()` won't terminate at all. The `Failure` will be emitted via `onNext()`.
* Next, when the user moves to (4, 5), we got pokemons C, and D from `pokemonService`. `getTop10Pokemons()` will continue to emit them via a `Success` again.

### To separate anticipated errors from unexpected errors

Anticipated errors will be emitted via `onNext()` with `Failure` while `onError()` is the place dedicated for unexpected errors. Some example of the 2 kinds of error:

* **Anticipated errors**: network errors (e.g. no Internet connection, temporarily unreachable backend service), validation errors (e.g. wrong email format).
* **Unexpected errors**: Mostly due to programmer mistake for example.

```kotlin
// With `Try<T>`
getTop10Pokemons()
  .subscribe({
    when (it) {
      is Success -> showPokemons(it())
      // showError() will handle anticipated errors.
      is Failure -> showError(it())
    }
  }, {
    // Unexpected errors will be reported to Crashlytics
    // for further investigation.
    logErrorViaCrashlytics(it)
  })
```

vs.

```kotlin
// Without `Try<T>`. Everything goes into `onError()`.
getTop10Pokemons()
  .subscribe({
    showPokemons(it)
  }, { error ->
    when (error) {
      is NoInternetConnectionError -> showNoInternetConnectionError(error)
      is UnreachableServerError -> showUnreachableServerError(error)
      else -> logErrorViaCrashlytics(error)
    }
  })
```
