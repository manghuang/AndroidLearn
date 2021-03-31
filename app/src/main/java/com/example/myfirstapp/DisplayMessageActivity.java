package com.example.myfirstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.camera2.*;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

public class DisplayMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

    }

    // 控制相机
    private Camera camera;
    private Preview preview;

    private boolean safeCameraOpen(int id) {
        boolean qOpened = false;

        try {
            releaseCameraAndPreview();
            camera = Camera.open(id);
            qOpened = (camera != null);
        } catch (Exception e) {
            Log.e(getString(R.string.app_name), "failed to open Camera");
            e.printStackTrace();
        }

        return qOpened;
    }

    private void releaseCameraAndPreview() {
        preview.setCamera(null);
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    class Preview extends ViewGroup implements SurfaceHolder.Callback {

        SurfaceView surfaceView;
        SurfaceHolder holder;
        Camera mCamera;
        private List<Camera.Size> supportedPreviewSizes;

        Preview(Context context) {
            super(context);

            surfaceView = new SurfaceView(context);
            addView(surfaceView);

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            holder = surfaceView.getHolder();
            holder.addCallback(this);
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        @Override
        protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

        }

        @Override
        public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // Now that the size is known, set up the camera parameters and begin
            // the preview.
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(previewSize.width, previewSize.height);
            requestLayout();
            mCamera.setParameters(parameters);

            // Important: Call startPreview() to start updating the preview surface.
            // Preview must be started before you can take a picture.
            mCamera.startPreview();
        }


        // 重启预览
        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

        }

        public void setCamera(Camera camera) {
            if (mCamera == camera) { return; }

            stopPreviewAndFreeCamera();

            mCamera = camera;

            if (mCamera != null) {
                List<Camera.Size> localSizes = mCamera.getParameters().getSupportedPreviewSizes();
                supportedPreviewSizes = localSizes;
                requestLayout();

                try {
                    mCamera.setPreviewDisplay(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Important: Call startPreview() to start updating the preview
                // surface. Preview must be started before you can take a picture.
                mCamera.startPreview();
            }
        }

        private void stopPreviewAndFreeCamera() {
        }
    }




}