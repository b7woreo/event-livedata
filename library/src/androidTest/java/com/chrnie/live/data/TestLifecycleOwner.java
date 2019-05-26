package com.chrnie.live.data;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.LifecycleOwner;

public class TestLifecycleOwner implements LifecycleOwner {

  public final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

  @NonNull
  @Override
  public Lifecycle getLifecycle() {
    return lifecycleRegistry;
  }
}
