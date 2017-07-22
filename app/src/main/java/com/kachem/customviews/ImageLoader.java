package com.kachem.customviews;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 图片异步加载类
 * Created by kachem on 2017/7/22 0022.
 */

public class ImageLoader {
    private RecyclerView recyclerView;
    private Set<LoadImageTask> tasks;
    private LruCache<String, Bitmap> cache;
    private List<String> urlList;

    public ImageLoader(RecyclerView recyclerView, List<String> urlList) {
        this.recyclerView = recyclerView;
        this.urlList = urlList;
        tasks = new HashSet<>();
        //获取最大的缓存空间
        int max = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = max / 4;
        //赋予缓存区最大缓存四分之一的空间
        cache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Bitmap bitmap = (Bitmap) msg.obj;
            if (bitmap != null) {

            }
        }
    };

    //将图片存入缓存
    private void addBitmapCache(String url, Bitmap bitmap) {
        if (getBitmapFromCache(url) == null)
            cache.put(url, bitmap);
    }


    private Bitmap getBitmapFromCache(String url) {
        return cache.get(url);
    }

    public void loadImageByThread(final ImageView imageView, final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = getBitmapByUrl(url);
                if (bitmap != null) {
                    Message msg = new Message();
                    msg.obj = bitmap;
                    handler.sendMessage(msg);
                    if (imageView.getTag().equals(url)) {
                        imageView.setImageBitmap(bitmap);
                    }
                }
            }
        }).start();
    }

    /**
     * 采用异步的方式加载
     */
    public void loadImageByAsync(ImageView imageView, String url) {
        Bitmap bitmap = getBitmapFromCache(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.mipmap.ic_launcher);
        }
    }

    /**
     * 在RecyclerView停止滚动时加载图片
     * @param startIndex 开始位置
     * @param endIndex 结束位置
     */
    public void loadImages(int startIndex, int endIndex) {
        String url;
        LoadImageTask task;
        for (int i = startIndex; i <= endIndex; i++){
            url = urlList.get(i);
            Bitmap bitmap = cache.get(url);
            if(bitmap == null){
                task = new LoadImageTask();
                task.execute(url);
                tasks.add(task);
            }else{
                ImageView imageView = recyclerView.findViewWithTag(url);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * 在屏幕进行滚动时，取消所有待加载的task
     */
    public void cancelAllTask(){
        if(tasks!=null){
            for (LoadImageTask task:tasks){
                task.cancel(false);
            }
        }
    }

    /**
     * 根据url获取图片的Bitmap
     */
    private Bitmap getBitmapByUrl(String picUrl) {
        Bitmap bitmap = null;
        InputStream inputStream = null;

        try {
            URL url = new URL(picUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            inputStream = httpURLConnection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    /**
     * 加載圖片的異步任務
     */
    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        private String url;

        @Override
        protected Bitmap doInBackground(String... strings) {
            String url = strings[0];
            this.url = url;
            Bitmap bitmap = getBitmapByUrl(url);
            if (bitmap != null) {
                addBitmapCache(url, bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                ImageView imageView = recyclerView.findViewWithTag(url);
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
            tasks.remove(this);
        }
    }
}
