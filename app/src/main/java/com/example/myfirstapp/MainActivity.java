package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    // 生命周期第一步
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    // 自定义相机
    public void selfCamera(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        startActivity(intent);
    }


    // 获得图片
    private String currentPhotoPath;
    private static final int REQUEST_TAKE_PHOTO = 1;


    public void getImage(View view) {
        System.out.println("获得图片");
        dispatchTakePictureIntent();
        galleryAddPic();
    }

    // 创建保存照片的文件
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        System.out.println(currentPhotoPath);
        return image;
    }


    // 保存完整尺寸的照片
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.myfirstapp.fileprovider", // 必须保持一致，而且需要是包名
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    //将照片添加到图库
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    // 展示
    private void setPic() {
        ImageView imageView = findViewById(R.id.imageView);

        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();
        System.out.println("W；" + targetW + "  H:" + targetH);
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        System.out.println(bitmap);
        imageView.setImageBitmap(bitmap);
    }


    // 获得视频
    static final int REQUEST_VIDEO_CAPTURE = 2;

    public void getVideo(View view) {
        System.out.println("获得视频");
        dispatchTakeVideoIntent();
    }


    // 拍摄
    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    // 回调展示
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            System.out.println("视频成功返回");
            Uri videoUri = intent.getData();
//            System.out.println(videoUri);
            VideoView videoView = findViewById(R.id.videoView);
//            System.out.println(videoView);
            videoView.setVideoURI(videoUri);
            videoView.start();
        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            System.out.println("拍照成功返回");
            setPic();
        }
    }


}