package com.example.loadinfo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InfoPageActivity extends AppCompatActivity {
    private static final String TAG = InfoPageActivity.class.getSimpleName();

    private static final String JSON_HOLDER_URL = "https://jsonplaceholder.typicode.com/photos";
    private static final int MSG_LOAD_INFO_FINISHED = 1;
    private static final int MSG_CALL_BACK_FAIL = 2;
    private static final int RECYCLER_VIEW_COL = 4;
    private List<InfoData> mList = null;
    private ArrayList<InfoData> mAdapterList;
    private LoadInfoHandler mLoadInfoHandler;
    private AlbumRecyclerViewAdapter mAlbumAdapter;
    private ProgressBar mProgressBar;
    private TextView mErrorMsgTextView;
    private Button mRetryButton;
    private interface LoadInfoListener {
        public void onLoadFinished(int msg);
    }

    private static class LoadInfoHandler extends Handler {
        LoadInfoListener mListener = null;

        public LoadInfoHandler(LoadInfoListener listener) {
            mListener = listener;
        }

        @Override
        public void handleMessage(Message msg) {
            mListener.onLoadFinished(msg.what);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.infopage_activity_layout);

        mProgressBar = findViewById(R.id.progress_bar);
        RecyclerView recyclerView = findViewById(R.id.list);
        mAdapterList = new ArrayList<>();
        mAlbumAdapter = new AlbumRecyclerViewAdapter(this, mAdapterList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, RECYCLER_VIEW_COL));
        recyclerView.setAdapter(mAlbumAdapter);

        mErrorMsgTextView = findViewById(R.id.error_msg);
        mRetryButton = findViewById(R.id.retry_button);
        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mErrorMsgTextView.setVisibility(View.INVISIBLE);
                mRetryButton.setVisibility(View.INVISIBLE);
                loadInfo(JSON_HOLDER_URL);
            }
        });

        mLoadInfoHandler = new LoadInfoHandler(new LoadInfoListener() {
            @Override
            public void onLoadFinished(int msg) {
                mProgressBar.setVisibility(View.INVISIBLE);
                switch (msg) {
                    case MSG_LOAD_INFO_FINISHED:
                        initAlbumRecyclerView();
                        break;
                    case MSG_CALL_BACK_FAIL:
                        showErrorMsgView();
                        break;
                }

            }
        });
        loadInfo(JSON_HOLDER_URL);
    }

    private void initAlbumRecyclerView() {
        mAdapterList.addAll(mList);
        mAlbumAdapter.notifyDataSetChanged();
    }

    private void showErrorMsgView() {
        mErrorMsgTextView.setVisibility(View.VISIBLE);
        mRetryButton.setVisibility(View.VISIBLE);
    }

    private void loadInfo(final String url) {
        mProgressBar.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient().newBuilder().build();
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "connect server fail");
                        mLoadInfoHandler.sendEmptyMessage(MSG_CALL_BACK_FAIL);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d(TAG, "connect server");
                        String result = response.body().string();
                        Gson gson = new Gson();
                        mList = gson.fromJson(result, new TypeToken<List<InfoData>>(){}.getType());
                        mLoadInfoHandler.sendEmptyMessage(MSG_LOAD_INFO_FINISHED);
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAlbumAdapter.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAlbumAdapter.onResume();
    }
}
