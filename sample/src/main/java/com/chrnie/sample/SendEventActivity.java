package com.chrnie.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;

public class SendEventActivity extends AppCompatActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_send_event);

    findViewById(R.id.btn).setOnClickListener(view -> {
      MainActivity.NORMAL_EVENT.setValue(new Object());
      MainActivity.STICKY_EVENT.setValue(new Object());
    });
  }
}
