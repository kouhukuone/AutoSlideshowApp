package jp.techacademy.kousei.asayama.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Button mNextButton;
    Button mPrevButton;
    Button mRunStopButton;

    Timer mTimer;
    int mTimerSec = 0;
    Handler mHandler = new Handler();

    Cursor cursor ;
    int fieldIndex;
    Long id;
    Uri imageUri;
    ImageView imageView;
    int page;
    boolean onOff = false;
    private static final int PERMISSIONS_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Android6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //許可されている
                Log.d("ANDROID", "許可されている");
                getContentsInfo();
            } else {
                Log.d("ANDROID", "許可されていない");
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
        } else {
            getContentsInfo();
        }

        mNextButton = (Button) findViewById(R.id.next_button);
        mPrevButton = (Button) findViewById(R.id.prev_button);
        mRunStopButton = (Button) findViewById(R.id.run_stop_button);

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    switch (page) {
                        case 0:
                            zeroToOne();
                            break;
                        case 1:
                            oneToTwo();
                            break;
                        case 2:
                            getContentsInfo();
                            break;
                    }
                }
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    switch (page) {
                        case 2:
                            zeroToOne();
                            break;
                        case 1:
                            getContentsInfo();
                            break;
                        case 0:
                            oneToTwo();
                            break;
                    }
                }
            }
        });

        mRunStopButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    if (onOff == false){
                        mNextButton.setEnabled(false);
                        mPrevButton.setEnabled(false);
                        onOff = true;
                        mTimer = new Timer();
                        mTimer.schedule(new TimerTask(){
                            @Override
                            public void run(){

                                mHandler.post(new Runnable(){
                                    @Override
                                    public void run(){
                                        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                            switch (page) {
                                                case 0:
                                                    zeroToOne();
                                                    break;
                                                case 1:
                                                    oneToTwo();
                                                    break;
                                                case 2:
                                                    getContentsInfo();
                                                    break;
                                            }
                                        }
                                    }
                                });
                            }
                        },2000, 2000);
                    } else {
                        mNextButton.setEnabled(true);
                        mPrevButton.setEnabled(true);
                        onOff = false;
                        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                            if(mTimer != null){
                                mTimer.cancel();
                                mTimer = null;
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                }
                break;
            default:
                break;
        }
    }

    //0枚目の写真を表示
    private void getContentsInfo() {
        page = 0;
        ContentResolver resolver = getContentResolver();
        cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null
        );
        if (cursor.moveToFirst()) {
              imageCall();
        }
        cursor.close();
    }

    //1枚目の写真を表示
    private void zeroToOne(){
        page = 1;
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            ContentResolver resolver = getContentResolver();
            cursor = resolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );
            if (cursor.moveToFirst()) {
                if (cursor.moveToNext()) {
                imageCall();
                }
            }
            cursor.close();
        }
    }

    //2枚目の写真を表示
    private void oneToTwo(){
        page = 2;
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            ContentResolver resolver = getContentResolver();
            cursor = resolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );
            if (cursor.moveToFirst()) {
                if (cursor.moveToLast()) {
             imageCall();
                }
            }
            cursor.close();
        }
    }

    //画像のURIを取得
    private void imageCall(){
        fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
        id = cursor.getLong(fieldIndex);
        imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageURI(imageUri);
    }
}