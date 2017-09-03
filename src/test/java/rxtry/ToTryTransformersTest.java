package rxtry;

import org.junit.Test;
import rx.Observable;
import rx.Single;

import java.io.IOException;

import static rxtry.ToTryTransformers.toTryObservable;
import static rxtry.ToTryTransformers.toTrySingle;

public class ToTryTransformersTest {
  @Test
  public void shouldTransformToFailureForObservable() {
    final IOException error = new IOException();
    Observable.<Integer>error(error)
        .compose(toTryObservable())
        .test()
        .assertNoErrors()
        .assertValue(new Failure<>(error));
  }

  @Test
  public void shouldTransformToFailureForSingle() {
    final IOException error = new IOException();
    Single.<Integer>error(error)
        .compose(toTrySingle())
        .test()
        .assertNoErrors()
        .assertValue(new Failure<>(error));
  }
}
