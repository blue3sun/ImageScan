package com.android.monkey.imagescan;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public final String mTag = "IMAGE_TEST";
    private final int REQ_PERMISSION_EXTERNAL_STOREAGE = 1;
    private GridView mGvImgs;
    private ContentResolver mContentResolver;
    private ArrayList<Integer> mImageIds = new ArrayList<>();
    private ArrayList<String> mThumbnailImagePaths = new ArrayList<>();
    private ArrayList<String> mImagePaths = new ArrayList<>();
    private ThumbnailImageAdapter mThumbnailImageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        checkPermission();
    }
    //初始化视图
    private void initView() {
        mGvImgs = (GridView)findViewById(R.id.gv_imgs);
    }

    //初始化数据
    private void initData() {
        //先从缩略图表中找出与原图表相对应的IMAGE_ID
        mContentResolver = getContentResolver();
        Cursor curcor = mContentResolver.query(
                MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Thumbnails.IMAGE_ID,MediaStore.Images.Thumbnails.DATA},
                null,
                null,
                MediaStore.Images.Thumbnails.DEFAULT_SORT_ORDER);
        if(curcor!=null&&curcor.moveToFirst()){
           do{
               mImageIds.add(curcor.getInt(0));
               mThumbnailImagePaths.add(curcor.getString(1));
            }while(curcor.moveToNext());
        }
        for(int imageId:mImageIds){
            curcor = mContentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.Media.DATA},
                    MediaStore.Images.Media._ID+"=?",
                    new String[]{String.valueOf(imageId)},
                    MediaStore.Images.Media.DEFAULT_SORT_ORDER);
            if(curcor!=null && curcor.moveToFirst()){
                mImagePaths.add(curcor.getString(0));
            }
        }

        if(curcor!=null){
            curcor.close();
        }
        Log.e(mTag, "mImageIds:");
        print(mImageIds);
        Log.e(mTag, "mThumbnailImagePaths:");
        print(mThumbnailImagePaths);
        Log.e(mTag, "mImagePaths:");
        print(mImagePaths);

        initAdapter();
    }

    public void checkPermission(){
        if(Build.VERSION.SDK_INT>=23){
            //检查权限
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQ_PERMISSION_EXTERNAL_STOREAGE);
            }else{
                initData();
            }
        }else{
            initData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQ_PERMISSION_EXTERNAL_STOREAGE:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    initData();
                }else{
                    Toast.makeText(
                            this,
                            "您拒绝了读取内存卡的权限，导致该项功能不可用",
                            Toast.LENGTH_LONG)
                            .show();
                }
                break;
            default:
                break;
        }
    }

    private void initAdapter() {
        mThumbnailImageAdapter = new ThumbnailImageAdapter(mTag, mThumbnailImagePaths,this);
        mGvImgs.setAdapter(mThumbnailImageAdapter);
        mGvImgs.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(MainActivity.this,ImageScanActivity.class);
                        intent.putExtra(ImageScanActivity.EXTRA_TAG,mTag);
                        intent.putExtra(ImageScanActivity.EXTRA_IMAGE_LIST,mImagePaths);
                        intent.putExtra(ImageScanActivity.EXTRA_CURRENT_SELECT_POSITION,position);
                        startActivity(intent);
                    }
                }
        );
    }

    public void print(ArrayList list){
        if(list!=null && list.size()>0){
            for(Object obj:list){
                Log.e(mTag, obj.toString()+" ");
            }
        }else{
            Log.e(mTag, "list为空");
        }
    }
}
