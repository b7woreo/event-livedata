package com.chrnie.live.data;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import android.app.Instrumentation;
import androidx.lifecycle.Lifecycle.State;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EventLiveDataTest {

  private final Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();

  @Test
  public void testNormalEventLiveData() {
    EventLiveData<Object> eventLiveData = new EventLiveData<>();
    TestLifecycleOwner owner = new TestLifecycleOwner();
    setLifecycleOwnerState(owner, State.RESUMED);

    final AtomicInteger lifecycleCalledCount = new AtomicInteger(0);

    sendEventSync(eventLiveData);
    observeEventSync(eventLiveData, owner, lifecycleCalledCount);
    assertEquals(0, lifecycleCalledCount.get());

    sendEventSync(eventLiveData);
    assertEquals(1, lifecycleCalledCount.get());

    setLifecycleOwnerState(owner, State.CREATED);
    sendEventSync(eventLiveData);
    assertEquals(1, lifecycleCalledCount.get());

    setLifecycleOwnerState(owner, State.RESUMED);
    assertEquals(1, lifecycleCalledCount.get());

    final AtomicInteger foreverCalledCount = new AtomicInteger(0);
    observeEventSync(eventLiveData, null, foreverCalledCount);
    assertEquals(0, foreverCalledCount.get());

    sendEventSync(eventLiveData);
    assertEquals(2, lifecycleCalledCount.get());
    assertEquals(1, foreverCalledCount.get());
  }

  @Test
  public void testStickyEventLiveData() {
    EventLiveData<Object> eventLiveData = new EventLiveData<>(true);
    TestLifecycleOwner owner = new TestLifecycleOwner();
    setLifecycleOwnerState(owner, State.RESUMED);

    final AtomicInteger lifecycleCalledCount = new AtomicInteger(0);

    sendEventSync(eventLiveData);
    observeEventSync(eventLiveData, owner, lifecycleCalledCount);
    assertEquals(1, lifecycleCalledCount.get());

    sendEventSync(eventLiveData);
    assertEquals(2, lifecycleCalledCount.get());

    setLifecycleOwnerState(owner, State.CREATED);
    sendEventSync(eventLiveData);
    assertEquals(2, lifecycleCalledCount.get());

    setLifecycleOwnerState(owner, State.RESUMED);
    assertEquals(3, lifecycleCalledCount.get());

    final AtomicInteger foreverCalledCount = new AtomicInteger(0);
    observeEventSync(eventLiveData, null, foreverCalledCount);
    assertEquals(0, foreverCalledCount.get());

    sendEventSync(eventLiveData);
    assertEquals(4, lifecycleCalledCount.get());
    assertEquals(1, foreverCalledCount.get());

    setLifecycleOwnerState(owner, State.CREATED);
    sendEventSync(eventLiveData);
    assertEquals(4, lifecycleCalledCount.get());
    assertEquals(2, foreverCalledCount.get());

    setLifecycleOwnerState(owner, State.RESUMED);
    assertEquals(4, lifecycleCalledCount.get());
    assertEquals(2, foreverCalledCount.get());
  }

  @Test
  public void testRemoveObserve() {
    EventLiveData<Object> eventLiveData = new EventLiveData<>();
    assertFalse(eventLiveData.hasObservers());

    Observer<Object> foreverObserver = observeEventSync(eventLiveData, null, null);
    assertTrue(eventLiveData.hasObservers());
    removeObserve(eventLiveData, foreverObserver);
    assertFalse(eventLiveData.hasActiveObservers());

    assertFalse(eventLiveData.hasActiveObservers());
    TestLifecycleOwner owner = new TestLifecycleOwner();
    setLifecycleOwnerState(owner, State.RESUMED);
    observeEventSync(eventLiveData, owner, null);
    assertTrue(eventLiveData.hasObservers());
    assertTrue(eventLiveData.hasActiveObservers());

    setLifecycleOwnerState(owner, State.CREATED);
    assertTrue(eventLiveData.hasObservers());
    assertFalse(eventLiveData.hasActiveObservers());

    removeObserve(eventLiveData, owner);
    assertFalse(eventLiveData.hasObservers());
    assertFalse(eventLiveData.hasActiveObservers());

    setLifecycleOwnerState(owner, State.RESUMED);
    observeEventSync(eventLiveData, owner, null);
    assertTrue(eventLiveData.hasObservers());
    assertTrue(eventLiveData.hasActiveObservers());

    setLifecycleOwnerState(owner, State.DESTROYED);
    assertFalse(eventLiveData.hasObservers());
    assertFalse(eventLiveData.hasActiveObservers());
  }

  @Test
  public void testPostValue() {
    EventLiveData<Object> eventLiveData = new EventLiveData<>();
    final AtomicInteger calledCount = new AtomicInteger(0);
    observeEventSync(eventLiveData, null, calledCount);
    assertEquals(0, calledCount.get());

    eventLiveData.postValue(new Object());
    instrumentation.waitForIdleSync();

    assertEquals(1, calledCount.get());
  }

  @Test(expected = RuntimeException.class)
  public void testGetValue() {
    EventLiveData<Object> eventLiveData = new EventLiveData<>();
    eventLiveData.getValue();
  }

  private void sendEventSync(EventLiveData<Object> eventLiveData) {
    instrumentation.runOnMainSync(() -> eventLiveData.setValue(new Object()));
  }

  private Observer<Object> observeEventSync(
      EventLiveData<Object> eventLiveData,
      LifecycleOwner owner,
      AtomicInteger calledCount
  ) {
    final Observer<Object> observer = e -> {
      if (calledCount != null) {
        calledCount.getAndIncrement();
      }
    };

    instrumentation.runOnMainSync(() -> {
      if (owner == null) {
        eventLiveData.observeForever(observer);
      } else {
        eventLiveData.observe(owner, observer);
      }
    });

    return observer;
  }

  private void removeObserve(EventLiveData<Object> eventLiveData, Observer<Object> observer) {
    instrumentation.runOnMainSync(() -> eventLiveData.removeObserver(observer));
  }

  private void removeObserve(EventLiveData<Object> eventLiveData, LifecycleOwner owner) {
    instrumentation.runOnMainSync(() -> eventLiveData.removeObservers(owner));
  }

  private void setLifecycleOwnerState(TestLifecycleOwner owner, State state) {
    instrumentation.runOnMainSync(() -> owner.lifecycleRegistry.markState(state));
  }
}
