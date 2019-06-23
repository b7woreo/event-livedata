package com.chrnie.live.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle.State;
import androidx.lifecycle.Observer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class EventLiveDataTest {

  private static final Object EVENT = new Object();

  @Rule
  public final InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

  @Test
  public void testNormalEventLiveData() {
    EventLiveData<Object> eventLiveData = new EventLiveData<>();

    TestLifecycleOwner owner = new TestLifecycleOwner();
    owner.markState(State.RESUMED);

    eventLiveData.setValue(EVENT);
    Observer<Object> lifecycleObserver = mock(Observer.class);
    eventLiveData.observe(owner, lifecycleObserver);
    verify(lifecycleObserver, times(0)).onChanged(EVENT);

    eventLiveData.setValue(EVENT);
    verify(lifecycleObserver, times(1)).onChanged(EVENT);

    owner.markState(State.CREATED);
    eventLiveData.setValue(EVENT);
    verify(lifecycleObserver, times(1)).onChanged(EVENT);

    owner.markState(State.RESUMED);
    verify(lifecycleObserver, times(1)).onChanged(EVENT);

    Observer<Object> foreverObserver = mock(Observer.class);
    eventLiveData.observeForever(foreverObserver);
    verify(foreverObserver, times(0)).onChanged(EVENT);

    eventLiveData.setValue(EVENT);
    verify(lifecycleObserver, times(2)).onChanged(EVENT);
    verify(foreverObserver, times(1)).onChanged(EVENT);
  }

  @Test
  public void testStickyEventLiveData() {
    EventLiveData<Object> eventLiveData = new EventLiveData<>(true);
    TestLifecycleOwner owner = new TestLifecycleOwner();
    owner.markState(State.RESUMED);

    eventLiveData.setValue(EVENT);
    Observer<Object> lifecycleObserver = mock(Observer.class);
    eventLiveData.observe(owner, lifecycleObserver);
    verify(lifecycleObserver, times(1)).onChanged(EVENT);

    eventLiveData.setValue(EVENT);
    verify(lifecycleObserver, times(2)).onChanged(EVENT);

    owner.markState(State.CREATED);
    eventLiveData.setValue(EVENT);
    verify(lifecycleObserver, times(2)).onChanged(EVENT);

    owner.markState(State.RESUMED);
    verify(lifecycleObserver, times(3)).onChanged(EVENT);

    Observer<Object> foreverObserver = mock(Observer.class);
    eventLiveData.observeForever(foreverObserver);
    verify(foreverObserver, times(0)).onChanged(EVENT);

    eventLiveData.setValue(EVENT);
    verify(lifecycleObserver, times(4)).onChanged(EVENT);
    verify(foreverObserver, times(1)).onChanged(EVENT);

    owner.markState(State.CREATED);
    eventLiveData.setValue(EVENT);
    verify(lifecycleObserver, times(4)).onChanged(EVENT);
    verify(foreverObserver, times(2)).onChanged(EVENT);

    owner.markState(State.RESUMED);
    verify(lifecycleObserver, times(4)).onChanged(EVENT);
    verify(foreverObserver, times(2)).onChanged(EVENT);
  }

  @Test
  public void testRemoveObserve() {
    EventLiveData<Object> eventLiveData = new EventLiveData<>();
    assertFalse(eventLiveData.hasObservers());

    Observer<Object> foreverObserver = mock(Observer.class);
    eventLiveData.observeForever(foreverObserver);
    assertTrue(eventLiveData.hasObservers());
    eventLiveData.removeObserver(foreverObserver);
    assertFalse(eventLiveData.hasObservers());

    assertFalse(eventLiveData.hasActiveObservers());
    TestLifecycleOwner owner = new TestLifecycleOwner();
    owner.markState(State.RESUMED);
    Observer<Object> lifecycleObserver = mock(Observer.class);
    eventLiveData.observe(owner, lifecycleObserver);
    assertTrue(eventLiveData.hasObservers());
    assertTrue(eventLiveData.hasActiveObservers());

    owner.markState(State.CREATED);
    assertTrue(eventLiveData.hasObservers());
    assertFalse(eventLiveData.hasActiveObservers());

    owner.markState(State.RESUMED);
    assertTrue(eventLiveData.hasObservers());
    assertTrue(eventLiveData.hasActiveObservers());

    owner.markState(State.DESTROYED);
    assertFalse(eventLiveData.hasObservers());
    assertFalse(eventLiveData.hasActiveObservers());
  }

  @Test
  public void testPostValue() {
    EventLiveData<Object> eventLiveData = new EventLiveData<>();
    Observer<Object> observer = mock(Observer.class);

    eventLiveData.observeForever(observer);
    verify(observer, times(0)).onChanged(EVENT);

    eventLiveData.postValue(EVENT);
    verify(observer, times(1)).onChanged(EVENT);
  }

  @Test(expected = RuntimeException.class)
  public void testGetValue() {
    EventLiveData<Object> eventLiveData = new EventLiveData<>();
    eventLiveData.getValue();
  }

}
