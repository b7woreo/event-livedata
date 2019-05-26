package com.chrnie.live.data;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Lifecycle.State;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

public class TestLifecycleOwner implements LifecycleOwner {

  private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

  @NonNull
  @Override
  public Lifecycle getLifecycle() {
    return lifecycleRegistry;
  }

  public void markState(@NonNull State state) {
    lifecycleRegistry.markState(state);
  }
}
