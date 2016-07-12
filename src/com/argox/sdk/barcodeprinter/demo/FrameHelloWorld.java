/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argox.sdk.barcodeprinter.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.argox.sdk.barcodeprinter.BarcodePrinter;
import com.argox.sdk.barcodeprinter.connection.tcp.TCPConnection;
import com.argox.sdk.barcodeprinter.emulation.pplz.PPLZ;
import com.argox.sdk.barcodeprinter.emulation.pplz.PPLZFont;
import com.argox.sdk.barcodeprinter.emulation.pplz.PPLZOrient;

/**
 *
 * @author Rovers
 */
public class FrameHelloWorld extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String ip = "192.168.9.112";
        int port = 2000;
        Toast.makeText(this, "Trying to print text with network printer at [" + ip + ":" + port + "], or you can change the ip and port in source code.", Toast.LENGTH_LONG).show();
        BarcodePrinter<TCPConnection, PPLZ> printer = new BarcodePrinter<TCPConnection, PPLZ>();
        //if you use BluetoothConnection instead of TCPConnection , you must implements onActivityResult(...) callback function
        //with calling printer.getConnection().onActivityResult(...)
        printer.setConnection(new TCPConnection(ip, port));
        printer.setEmulation(new PPLZ());
        try {
            printer.getConnection().open();
            String text = "Hello World!";
            printer.getEmulation().getTextUtil().printText(0, 0, PPLZOrient.Clockwise_0_Degrees, PPLZFont.Font_Zero, 20, 20, text.getBytes(), 0);
            printer.getEmulation().printOut();
        } catch (Exception ex) {
            Log.e("argox_demo", null, ex);
        }
    }
}
