package com.chrnie.live.data;

import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class EventLiveData<T> extends LiveData<T> {

  private final Map<Observer<? super T>, Observer<? super T>> observers = new WeakHashMap<>();

  private final boolean sticky;
  private final AtomicReference<T> paddingValue = new AtomicReference<>(null);

  public EventLiveData() {
    this(false);
  }

  public EventLiveData(boolean sticky) {
    this.sticky = sticky;
  }

  @Nullable
  @Override
  public T getValue() {
    throw new RuntimeException("event can only be observed");
  }

  @Override
  public void setValue(T value) {
    assertMainThread();

    if (value == null) {
      throw new NullPointerException("event can not be null");
    }

    if (interceptValue(value)) {
      return;
    }

    super.setValue(value);
    super.setValue(null);
  }

  @Override
  public void postValue(T value) {
    if (value == null) {
      throw new NullPointerException("event can not be null");
    }

    super.postValue(value);
  }

  private boolean interceptValue(T value) {
    if (sticky && !hasActiveObservers()) {
      paddingValue.set(value);
      return true;
    }

    return false;
  }

  @Override
  protected void onActive() {
    super.onActive();
    T value = paddingValue.getAndSet(null);
    if (value != null) {
      setValue(value);
    }
  }

  @Override
  public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
    assertMainThread();

    Observer<? super T> wrapper = observers.get(observer);
    if (wrapper == null) {
      wrapper = new WrapperObserver<>(observer);
      observers.put(observer, wrapper);
    }
    super.observe(owner, wrapper);
  }

  @Override
  public void observeForever(@NonNull Observer<? super T> observer) {
    assertMainThread();

    Observer<? super T> wrapper = observers.get(observer);
    if (wrapper == null) {
      wrapper = new WrapperObserver<>(observer);
      observers.put(observer, wrapper);
    }
    super.observeForever(wrapper);
  }

  @Override
  public void removeObserver(@NonNull Observer<? super T> observer) {
    assertMainThread();

    Observer<? super T> wrapper = observers.get(observer);
    Observer<? super T> removed = wrapper == null ? observer : wrapper;
    super.removeObserver(removed);
  }

  private static class WrapperObserver<T> implements Observer<T> {

    private final Observer<? super T> observer;

    WrapperObserver(Observer<? super T> observer) {
      this.observer = observer;
    }

    @Override
    public void onChanged(@Nullable T value) {
      if (value == null) {
        return;
      }

      observer.onChanged(value);
    }
  }

  private static void assertMainThread() {
    if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
      throw new RuntimeException("must call on main thread");
    }
  }
}
