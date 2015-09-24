package com.sking.photo;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class PhotoActivity extends Activity {

    private boolean isPreview;
    private Camera camera;
    private SurfaceView surfaceView;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.w("onConfigurationChanged", "1");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("onCreate", "1");
        super.onCreate(savedInstanceState);
        Log.e("onCreate", "2");
        Window window = getWindow();
        Log.e("onCreate", "3");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Log.e("onCreate", "4");
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Log.e("onCreate", "5");
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Log.e("onCreate", "6");
        setContentView(R.layout.activity_photo);
        Log.e("onCreate", "7");
        surfaceView = (SurfaceView)findViewById(R.id.surfaceview);
        Log.e("onCreate", "8");
        surfaceView.getHolder().setFixedSize(800, 480);
        Log.e("onCreate", "9");
        surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        Log.e("onCreate", "10");
        surfaceView.getHolder().addCallback(new SurfaceCallback());


    }


    private class SurfaceCallback implements SurfaceHolder.Callback{
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                Log.e("+++++++++++++", 1+"");
                camera = Camera.open();//打开硬件摄像头，这里导包得时候一定要注意是android.hardware.Camera
                Log.e("+++++++++++++", 2+"");
                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);//得到窗口管理器
                Log.e("+++++++++++++", 3+"");
                Display display  = wm.getDefaultDisplay();//得到当前屏幕
                Log.e("+++++++++++++", 4+"");
                Camera.Parameters parameters = camera.getParameters();//得到摄像头的参数


//                List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
//                List<Integer> frameList = parameters.getSupportedPreviewFrameRates();
//
//                Iterator<Camera.Size> itor = sizeList.iterator();
//                while (itor.hasNext()) {
//                    Camera.Size cur = itor.next();
//                    if (cur.width >= 0
//                            && cur.height >= 0) {
//
//                        Log.e("+++++++++++++", cur.width+ "  "+ cur.height) ;
//
//
//                    }
//                }
//                Iterator<Integer> itor1 = frameList.iterator();

//                while (itor1.hasNext())
//                {
//                    Log.e("Integer", itor1.next()+"") ;
//                }
                Log.e("+++++++++++++", 5 + "");
                parameters.setPreviewSize(1280, 720);//设置预览照片的大小
                Log.e("+++++++++++++", display.getWidth() + "  " + display.getHeight()) ;
                parameters.setPreviewFrameRate(30);//设置每秒3帧
                Log.e("+++++++++++++", 7 + "");
                parameters.setPictureFormat(PixelFormat.JPEG);//设置照片的格式

                Log.e("+++++++++++++", 8 + "");
                parameters.setJpegQuality(85);//设置照片的质量
                Log.e("+++++++++++++", 9 + "");
                parameters.setPictureSize(1280, 720);//设置照片的大小，默认是和屏幕一样大

                Log.e("+++++++++++++", 10 + "");
                camera.setParameters(parameters);
                Log.e("+++++++++++++", 11 + "");
                camera.setPreviewDisplay(surfaceView.getHolder());//通过SurfaceView显示取景画面

                if(PhotoActivity.this.getResources().getConfiguration().orientation==1)
                camera.setDisplayOrientation(90);

                Log.e("+++++++++++++", 12 + "");
                camera.startPreview();//开始预览
                Log.e("+++++++++++++", 13 + "");
                isPreview = true;//设置是否预览参数为真
            } catch (IOException e) {
                Log.e("+++++++++++++", e.toString());
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
// TODO Auto-generated method stub
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.e("+++++++++++++", "surfaceDestroyed");
            if(camera!=null){
                if(isPreview){//如果正在预览
                    camera.stopPreview();
                    camera.release();
                }
            }
        }

    }


//    public boolean onKeyDown(int keyCode, KeyEvent event) {//处理按键事件
//        if(camera!=null&&event.getRepeatCount()==0)//代表只按了一下
//        {
//            switch(keyCode){
//                case KeyEvent.KEYCODE_BACK://如果是搜索键
//                    camera.autoFocus(null);
//
//                    break;
//                case KeyEvent.KEYCODE_DPAD_CENTER://如果是中间键
//                    camera.takePicture(null, null, new TakePictureCallback());//将拍到的照片给第三个对象中，这里的TakePictureCallback()是自己定义的，在下面的代码中
//                    break;
//            }
//        }
//        return true;//阻止事件往下传递，否则按搜索键会变成系统默认的
//    }


    private final class TakePictureCallback implements Camera.PictureCallback {
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                File file = new File(Environment.getExternalStorageDirectory(),System.currentTimeMillis()+".jpg");
                FileOutputStream outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();
                camera.stopPreview();
                camera.startPreview();//处理完数据之后可以预览
            } catch (Exception e) {
                Log.e("TakePictureCallback", e.toString());
            }
        }
    }

}
