package com.chrnie.sample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.chrnie.live.data.EventLiveData;

public class MainActivity extends AppCompatActivity {

  public static final EventLiveData<Object> NORMAL_EVENT = new EventLiveData<>(false);
  public static final EventLiveData<Object> STICKY_EVENT = new EventLiveData<>(true);

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    NORMAL_EVENT.observe(this,
        o -> Toast.makeText(MainActivity.this, "Receive event from normal source", Toast.LENGTH_SHORT).show());

    STICKY_EVENT.observe(this,
        o -> Toast.makeText(MainActivity.this, "Receive event from sticky source", Toast.LENGTH_SHORT).show());

    findViewById(R.id.btn).setOnClickListener(view -> {
      Intent intent = new Intent(MainActivity.this, SendEventActivity.class);
      startActivity(intent);
    });
  }
}
