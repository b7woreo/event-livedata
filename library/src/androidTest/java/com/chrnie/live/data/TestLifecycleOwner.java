package com.chrnie.live.data;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.support.annotation.NonNull;

public class TestLifecycleOwner implements LifecycleOwner {

  public final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

  @NonNull
  @Override
  public Lifecycle getLifecycle() {
    return lifecycleRegistry;
  }
}
