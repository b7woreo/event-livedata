package com.chrnie.sample;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
