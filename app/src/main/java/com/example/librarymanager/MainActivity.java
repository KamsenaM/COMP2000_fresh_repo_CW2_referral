package com.example.librarymanager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final long FADE_IN_DURATION = 1500;
    private static final long DISPLAY_DURATION = 1500;
    private static final long FADE_OUT_DURATION = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create notification channel at app start
        NotificationHelper.createChannel(this);

        View splashImage = findViewById(R.id.splashImage);

        splashImage.animate()
                .alpha(1f)
                .setDuration(FADE_IN_DURATION)
                .withEndAction(() -> new Handler().postDelayed(() -> splashImage.animate()
                        .alpha(0f)
                        .setDuration(FADE_OUT_DURATION)
                        .withEndAction(() -> {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        }), DISPLAY_DURATION));
    }
}