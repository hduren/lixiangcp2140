/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argox.sdk.barcodeprinter.demo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import grandroid.view.LayoutMaker;
import grandroid.view.ViewDesigner;

/**
 *
 * @author Rovers
 */
public class FrameMain extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutMaker maker = new LayoutMaker(this);
        maker.getLastLayout().setBackgroundColor(Color.WHITE);
        maker.setDrawableDesignWidth(this, 640);
        ViewDesigner vd = new ViewDesigner() {

            @Override
            public Button stylise(Button btn) {
                btn.setBackgroundResource(R.drawable.b1);
                btn.setTextColor(Color.BLACK);
                return btn;
            }

        };
        maker.setDesigner(vd);
        maker.addColLayout(false, maker.layFF());
        {
            maker.setScalablePadding(maker.getLastLayout(), 20, 25, 20, 25);
            Button btnHelloWorld = maker.addButton("Hello World");
            btnHelloWorld.setBackgroundResource(R.drawable.b1);
            btnHelloWorld.setLayoutParams(maker.layAbsolute(0, 0, LinearLayout.LayoutParams.MATCH_PARENT, 100));
            btnHelloWorld.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    startActivity(FrameHelloWorld.class);
                }
            });

            Button btnShowCases = maker.addButton("Show Cases");
            btnShowCases.setBackgroundResource(R.drawable.b1);
            btnShowCases.setLayoutParams(maker.layAbsolute(0, 25, LinearLayout.LayoutParams.MATCH_PARENT, 100));
            btnShowCases.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    startActivity(FrameDemoCommunications.class);
                }
            });

//            Button btnTestMode = maker.addButton("Test Mode");
//            btnTestMode.setBackgroundResource(R.drawable.b1);
//            btnTestMode.setLayoutParams(maker.layAbsolute(0, 25, LinearLayout.LayoutParams.MATCH_PARENT, 100));
//            btnTestMode.setOnClickListener(new View.OnClickListener() {
//
//                public void onClick(View v) {
//                    startActivity(FrameBarcodeTest.class);
//                }
//            });
            Button btnUSBMode = maker.addButton("USB Device Check");
            btnUSBMode.setBackgroundResource(R.drawable.b1);
            btnUSBMode.setLayoutParams(maker.layAbsolute(0, 25, LinearLayout.LayoutParams.MATCH_PARENT, 100));
            btnUSBMode.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    startActivity(FrameUSBConnection.class);
                }
            });
            maker.escape();
        }

    }

    private void startActivity(Class activity) {
        Intent i = new Intent();
        i.setClass(this, activity);
        startActivity(i);
    }
}
