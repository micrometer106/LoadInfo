package com.example.loadinfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AlbumRecyclerViewAdapter extends RecyclerView.Adapter<AlbumRecyclerViewAdapter.AlbumViewHolder> {
    private static final String TAG = AlbumRecyclerViewAdapter.class.getSimpleName();

    private static final int NUMBER_OF_THREAD = 15;
    private Context mContext;
    private List<InfoData> mList = null;
    private SparseArray<Bitmap> mCache;
    private boolean mIsNetWorkAvailable = true;
    private NetworkChangedReceiver mNetworkChangedReceiver;

    class AlbumViewHolder extends RecyclerView.ViewHolder {
        public ImageView mAlbumThumb;
        public TextView mId;
        public TextView mTitle;

        public AlbumViewHolder(View itemView) {
            super(itemView);
        }
    }

    public AlbumRecyclerViewAdapter(Context context, List<InfoData> list) {
        mContext = context;
        mList = list;
        mCache = new SparseArray<>();
        mNetworkChangedReceiver = new NetworkChangedReceiver();
    }

    @Override
    public void onBindViewHolder(@NonNull final AlbumViewHolder holder, int position) {
        if (mList != null) {
            InfoData data = mList.get(position);
            holder.mId.setText(String.valueOf(data.getId()));
            holder.mTitle.setText(String.valueOf(data.getTitle()));
            if (mCache.get(position) != null) {
                Log.d(TAG, "get Cache = "+position);
                holder.mAlbumThumb.setTag(position);
                holder.mAlbumThumb.setImageBitmap(mCache.get(position));
            } else {
                Log.d(TAG, "load bitmap = "+position);
                holder.mAlbumThumb.setImageBitmap(null);
                holder.mAlbumThumb.setTag(position);
                if (mIsNetWorkAvailable) {
                    LoadThumbNailTask loadThumbNailTask = new LoadThumbNailTask(holder.mAlbumThumb, position);
                    loadThumbNailTask.executeOnExecutor(Executors.newFixedThreadPool(NUMBER_OF_THREAD), data.getThumbnailUrl());
                }
            }
        }
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.album_item, parent, false);
        AlbumViewHolder albumViewHolder = new AlbumViewHolder(view);
        albumViewHolder.mAlbumThumb = view.findViewById(R.id.album_thumb);
        albumViewHolder.mId = view.findViewById(R.id.id);
        albumViewHolder.mTitle = view.findViewById(R.id.album_title);
        return albumViewHolder;
    }

    @Override
    public int getItemCount() {
        if (mList != null) {
            return mList.size();
        }
        return 0;
    }

    class LoadThumbNailTask extends AsyncTask<String, Void, Bitmap> {
        WeakReference<ImageView> mImageViewRef = null;
        private int mPosition;

        public LoadThumbNailTask(ImageView imageView, int position) {
            mImageViewRef = new WeakReference<>(imageView);
            mPosition = position;
        }

        @Override
        protected Bitmap doInBackground(String... param) {
            try {
                URL url = new URL(param[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                return bitmap;
            } catch (Exception ex){
                ex.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mCache.put(mPosition, bitmap);
            ImageView imageView = mImageViewRef.get();

            if (imageView != null && (int)imageView.getTag() == mPosition) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    public class NetworkChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mIsNetWorkAvailable = NetworkUtils.isNetworkAvailable(context);
        }
    }

    public void onResume() {
        mIsNetWorkAvailable = NetworkUtils.isNetworkAvailable(mContext);
        mContext.registerReceiver(mNetworkChangedReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    public void onPause() {
        mContext.unregisterReceiver(mNetworkChangedReceiver);
    }
}
