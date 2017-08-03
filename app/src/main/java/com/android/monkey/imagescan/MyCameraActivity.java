package com.android.monkey.imagescan;

import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

/**
 * Description:模拟系统的拍照界面
 * Author: Monkey
 * Time: 2017/2/7 14:24
 */

public class MyCameraActivity extends AppCompatActivity implements View.OnClickListener,SurfaceHolder.Callback{
    public final String mTag = "IMAGE_TEST";
    private Button mBtnCamera;
    private SurfaceView mSurfaceView;
    private ImageView mIvImgs;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private Camera.Parameters mCameraParameters;
    private Button mBtnSwitch;
    //标记前后相机的CameraId
    private int mCameraBack = -1;
    private int mCameraFront = -1;
    private boolean isBackCamera = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(mTag, "MyCameraActivity onCreate()");
        setContentView(R.layout.activity_mycamera);
        initView();
        initCamera();
        initListner();
    }

    private void initView() {
        mSurfaceView = (SurfaceView)findViewById(R.id.surface_view);
        mBtnCamera = (Button)findViewById(R.id.btn_camera);
        mBtnSwitch = (Button)findViewById(R.id.btn_switch);
        mIvImgs = (ImageView)findViewById(R.id.iv_imgs);
    }

    private void initCamera() {
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        if(isHasCamera()){
            //优先打开后置摄像头相机
            if(mCameraBack!=-1){
                isBackCamera = true;
                mCamera = Camera.open(mCameraBack);
                setCameraDisplayOrientation(mCameraBack);
            }else if(mCameraFront!=-1){
                isBackCamera = false;
                mCamera = Camera.open(mCameraFront);
                setCameraDisplayOrientation(mCameraFront);
            }
            mCameraParameters = mCamera.getParameters();
            if(mCameraParameters.getFocusMode().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)){
                mCameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
            mCamera.setParameters(mCameraParameters);
        }else{
            showToast("相机功能不可用！！！");
        }
    }
    /**判断该设备是否支持相机功能*/
    public boolean isHasCamera(){
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            //得到相机个数
            int cameraCount = Camera.getNumberOfCameras();
            Camera.CameraInfo cameraInfo = null;
            for(int i=0;i<cameraCount;i++){
                cameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(i,cameraInfo);
                if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK){
                    mCameraBack = i;
                }else if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
                    mCameraFront = i;
                }
            }
            if(mCameraBack!=-1&&mCameraFront!=-1){//前后相机都存在的情况下才显示翻转相机的图标
                mBtnSwitch.setVisibility(View.VISIBLE);
            }else{
                mBtnSwitch.setVisibility(View.GONE);
            }
           return true;
        }else{
            return false;
        }
    }

    private void initListner() {
        mBtnSwitch.setOnClickListener(this);
        mBtnCamera.setOnClickListener(this);
    }
    public void showToast(String text){
        Toast.makeText(this,text,Toast.LENGTH_LONG).show();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(mCamera!=null){
            mCamera.startPreview();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(mCamera!=null){
            try {
                mCamera.stopPreview();//先停止再重新预览
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(mCamera!=null){
            mCamera.stopPreview();
            mCamera.release();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_camera:
                takePhoto();//拍照
                break;
            case R.id.btn_switch:
                switchCamera();//翻转相机
                break;
            default:
                break;
        }
    }

    /**翻转相机的前后摄像头*/
    private void switchCamera() {
        try {
            if(mCamera!=null){
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
            if(isBackCamera){
                isBackCamera = false;
                mCamera = Camera.open(mCameraFront);
                setCameraDisplayOrientation(mCameraFront);
            }else{
                isBackCamera = true;
                mCamera = Camera.open(mCameraBack);
                setCameraDisplayOrientation(mCameraBack);
            }
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**开始拍照*/
    private void takePhoto() {
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
                if(bitmap!=null){
                    mIvImgs.setImageBitmap(bitmap);
                }else{
                    showToast("解码图片失败！！！");
                }
                //重新开启照片预览
                mCamera.startPreview();
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(isBackCamera){
            setCameraDisplayOrientation(mCameraBack);
        }else{
            setCameraDisplayOrientation(mCameraFront);
        }
    }

    /**调整预览图片的方向*/
    public void setCameraDisplayOrientation(int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        Log.e(mTag, "手机Orientation: "+rotation);
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;//竖直
            case Surface.ROTATION_90: degrees = 90; break;//逆时针
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        mCamera.setDisplayOrientation(result);
    }
}
