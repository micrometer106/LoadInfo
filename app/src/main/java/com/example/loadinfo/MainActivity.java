package com.example.loadinfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.loadinfo.databinding.ActivityMainBinding;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setMainModel(this);
    }

    public void onGoToNextClick() {
        boolean isNetworkAvailable = NetworkUtils.isNetworkAvailable(this);
        if (isNetworkAvailable) {
            Intent intent = new Intent();
            intent.setClass(this, InfoPageActivity.class);
            startActivity(intent);
        }
        TextView failTitle = findViewById(R.id.fail_title);
        TextView failMsg = findViewById(R.id.fail_msg);
        failTitle.setVisibility(isNetworkAvailable ? GONE : VISIBLE);
        failMsg.setVisibility(isNetworkAvailable ? GONE : VISIBLE);
    }
}
