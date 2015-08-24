package com.example.shkwsk.myapp01;

import android.content.Context;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;

import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.LinearLayout;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.BitmapFactory;
import android.widget.Toast;
import android.widget.RelativeLayout;

import java.io.*;
import java.net.URL;

public class DrawActivity extends AppCompatActivity {
    private DrawingView drawingView;
    private String url, post_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_draw);
        System.out.println("start DrawActivity.");

        try {
            Class.forName("android.os.AsyncTask");
        } catch (ClassNotFoundException e) {
            System.out.println(e);
        }

        System.out.println("start DrawActivity.");
        url = getIntent().getExtras().getString("url");
        post_url = getIntent().getExtras().getString("query");

        //ラジオボタンの振る舞い設定
        RadioGroup color_radiogroup = (RadioGroup)findViewById(R.id.color_radiogroup);
        color_radiogroup.setOnCheckedChangeListener(changeColor);

        // 描画画面の振る舞い設定
        this.drawingView = (DrawingView)findViewById(R.id.drawing_view);

        // 描画画面の背景画像設定
        try {
            System.out.println("DrawActivity: " + url);
            // サブスレッドで実行するタスクを作成
            AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>() {
                BitmapDrawable ob;
                @Override
                protected Boolean doInBackground(String... params) {
                    try {
                        URL imgurl = new URL(params[0]);
                        InputStream is = imgurl.openStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        ob = new BitmapDrawable(getResources(), bitmap);
                        return true;
                    } catch (Exception e) {
                        // error
                        return false;
                    }
                }
                @Override
                protected void onPostExecute(Boolean result) {
                    drawingView.setBackground(ob);
                }
            };
            task.execute(url);
        } catch (Exception e) {
            System.out.println(e); // IOerror, URLerror
        }

        // 下部ボタンの振る舞い設定
        findViewById(R.id.commit_button).setOnClickListener(commitDrawing);
        //findViewById(R.id.delete_button).setOnClickListener(deleteDrawing);
    }

    RadioGroup.OnCheckedChangeListener changeColor = new RadioGroup.OnCheckedChangeListener() {
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            RadioButton radioButton = (RadioButton) findViewById(checkedId);
//            Toast.makeText(DrawActivity.this,
//                    "onCheckedChanged():" + radioButton.getTextColors().getDefaultColor(),
//                    Toast.LENGTH_SHORT).show();
            drawingView.setColor(radioButton.getTextColors().getDefaultColor());
        }
    };
    View.OnClickListener commitDrawing = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            drawingView.commit(getCacheDir(), post_url);
        }
    };
//    View.OnClickListener deleteDrawing = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            drawingView.delete();
//        }
//    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_draw, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
