package com.marinshalamanov.kompoti;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    View jar;
    RelativeLayout layout;

    List<ImageView> fruit;

    final int SPEED_OF_FALLING = 10;
    int addFruitOnEachMs = 2000;

    long lastFruitAddedAt = -1;

    int[] fruitIds = {R.drawable.apple, R.drawable.apricot, R.drawable.banana, R.drawable.cherry,
    R.drawable.grape, R.drawable.lemon, R.drawable.orange, R.drawable.pear, R.drawable.strawberry};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jar = findViewById(R.id.jar);
        layout = (RelativeLayout) jar.getParent();
        fruit = new ArrayList<>();

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        loop();
                    }
                }).start(); //.run();
    }

    void loop() {

        if (lastFruitAddedAt + addFruitOnEachMs < System.currentTimeMillis()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addFruit();
                }
            });

        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateFruit();
            }
        });

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Log.e(getClass().toString(), "error on sleep", e);
        }
        loop();
    }

    void addFruit() {

        ImageView aFruit = new ImageView(this);

        Random random =  new Random();
        int randomFruitImageId = fruitIds[random.nextInt(fruitIds.length)];
        aFruit.setImageDrawable(getResources().getDrawable(randomFruitImageId));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(150, 150);
        params.topMargin = 10;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int displayWidth = size.x;

        params.leftMargin = random.nextInt((int) (displayWidth * 0.8)) + (int) (displayWidth * 0.1);

        layout.addView(aFruit, params);
        fruit.add(aFruit);
        jar.bringToFront();

        lastFruitAddedAt = System.currentTimeMillis();
    }

    void updateFruit() {
        RelativeLayout.LayoutParams jarParams = (RelativeLayout.LayoutParams) jar.getLayoutParams();

        for (int i = 0; i < fruit.size(); i++) {
            ImageView aFruit = fruit.get(i);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) aFruit.getLayoutParams();
            params.topMargin += SPEED_OF_FALLING;
            aFruit.setLayoutParams(params);

            int location[] = new int[2];
            jar.getLocationInWindow(location);


            if (params.topMargin > location[1]) {
                layout.removeView(aFruit);
                fruit.remove(i);
                i--;
                Log.e(getClass().toString(), "here");
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) jar.getLayoutParams(); //new RelativeLayout.LayoutParams(150, 150);
        params.leftMargin = (int) x - params.width / 2;
        jar.setLayoutParams(params);

        return super.onTouchEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
