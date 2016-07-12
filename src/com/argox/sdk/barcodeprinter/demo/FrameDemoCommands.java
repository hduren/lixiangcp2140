/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argox.sdk.barcodeprinter.demo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.argox.sdk.barcodeprinter.BarcodePrinter;
import com.argox.sdk.barcodeprinter.connection.ConnectionState;
import com.argox.sdk.barcodeprinter.connection.IConnectionStateListener;
import com.argox.sdk.barcodeprinter.connection.PrinterConnection;
import com.argox.sdk.barcodeprinter.connection.PrinterDataListener;
import com.argox.sdk.barcodeprinter.connection.bluetooth.BluetoothConnection;
import com.argox.sdk.barcodeprinter.connection.file.FileConnection;
import com.argox.sdk.barcodeprinter.connection.tcp.TCPConnection;
import com.argox.sdk.barcodeprinter.connection.usb.USBConnection;
import com.argox.sdk.barcodeprinter.emulation.pplz.PPLZ;
import com.argox.sdk.barcodeprinter.emulation.pplz.PPLZBarCodeType;
import com.argox.sdk.barcodeprinter.emulation.pplz.PPLZFont;
import com.argox.sdk.barcodeprinter.emulation.pplz.PPLZOrient;
import com.argox.sdk.barcodeprinter.emulation.pplz.PPLZQRCodeErrCorrect;
import com.argox.sdk.barcodeprinter.emulation.pplz.PPLZQRCodeModel;
import com.argox.sdk.barcodeprinter.emulation.pplz.PPLZStorage;
import com.argox.sdk.barcodeprinter.util.Encoding;
import grandroid.view.LayoutMaker;
import grandroid.view.ViewDesigner;
import java.io.File;
import java.util.Random;

/**
 *
 * @author user
 */
public class FrameDemoCommands extends Activity {

    protected BarcodePrinter<PrinterConnection, PPLZ> printer;
    private TextView tvState, tvQueue;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        LayoutMaker maker = new LayoutMaker(this);
        ViewDesigner vd = new ViewDesigner() {

            @Override
            public Button stylise(Button btn) {
                btn.setBackgroundResource(R.drawable.b1);
                btn.setTextColor(Color.BLACK);
                return btn;
            }

        };
        maker.setDesigner(vd);
        maker.getLastLayout().setBackgroundColor(Color.WHITE);
        maker.setDrawableDesignWidth(this, 640);
        Bundle b = getIntent().getExtras();
        ConnectType type = ConnectType.values()[b.getInt("type")];
        tvState = maker.add(maker.createStyledText("Connection State : ").size(12).color(Color.BLACK).get(), maker.layFW());
        tvQueue = maker.add(maker.createStyledText("Print Queue : ").size(12).color(Color.BLACK).get(), maker.layFW());
        maker.addColLayout(true, maker.layFF());
        {
            maker.setScalablePadding(maker.getLastLayout(), 20, 25, 20, 25);

            Button btnBarcode = maker.add(maker.createButton("Print Barcode"), maker.layAbsolute(0, 25, LinearLayout.LayoutParams.MATCH_PARENT, 100));
            btnBarcode.setOnClickListener(new View.OnClickListener() {

                public void onClick(View arg0) {

                    try {
                        Encoding encode = Encoding.UTF_8;
                        byte[] buf, buf2;
                        buf = encode.getBytes("23456");
                        printer.getEmulation().getBarcodeUtil().printOneDBarcode(50, 50, PPLZOrient.Clockwise_0_Degrees, 5, 13, 50,
                                PPLZBarCodeType.Code_39, 1, buf, 'N', 'Y', 'N', 'N', 'N');// Code 39
                        buf2 = encode.getBytes("01234567890123456789");
                        printer.getEmulation().getBarcodeUtil().printOneDBarcode(50, 200, PPLZOrient.Clockwise_0_Degrees, 5, 13, 50,
                                PPLZBarCodeType.Code_128, 1, buf, 'Y', 'N', 'N', 'N', 'N');// Code 128
                        buf2 = encode.getBytes(">9123$%&");
                        printer.getEmulation().getBarcodeUtil().printOneDBarcode(50, 350, PPLZOrient.Clockwise_0_Degrees, 5, 13, 50,
                                PPLZBarCodeType.Code_128, 1, buf2, 'Y', 'N', 'N', 'N', 'N');// Code 128, Subsets A(Numeric Pairs give Alpha/Numerics)
                        printer.getEmulation().getSetUtil().setLabelLength(500);
                        printer.getEmulation().getSetUtil().setPrintOut(1, 0, 1, false);
                        apendQueue("Barcode");
                    } catch (Exception ex) {
                        Log.e("argox_demo", null, ex);
                    }
                }
            });

            Button btnQRcode = maker.add(maker.createButton("Print QRcode"), maker.layAbsolute(0, 25, LinearLayout.LayoutParams.MATCH_PARENT, 100));
            btnQRcode.setOnClickListener(new View.OnClickListener() {

                public void onClick(View arg0) {
                    try {
                        Encoding encode = Encoding.UTF_8;
                        byte[] buf;
                        buf = encode.getBytes("[)_1E01_1D960001Z004951_1DUPSN_1D6X61_1D305_1D_1DN_1D_1DSEA_1DWA_1E_04");
                        printer.getEmulation().getBarcodeUtil().printQRCode(50, 50, PPLZQRCodeModel.Model_2, 3, PPLZQRCodeErrCorrect.Standard, buf, 1);// QR Code
                        printer.getEmulation().getSetUtil().setLabelLength(200);
                        printer.getEmulation().getSetUtil().setPrintOut(1, 0, 1, false);
                        apendQueue("QRcode");
                    } catch (Exception ex) {
                        Log.e("argox_demo", null, ex);
                    }
                }
            });
            Button btnText = maker.add(maker.createButton("Print Text"), maker.layAbsolute(0, 25, LinearLayout.LayoutParams.MATCH_PARENT, 100));
            btnText.setOnClickListener(new View.OnClickListener() {

                public void onClick(View arg0) {
                    try {
                        Encoding encode = Encoding.UTF_8;
                        byte[] buf;
                        buf = encode.getBytes("Label: print internal font");
                        printer.getEmulation().getTextUtil().printText(30, 0, PPLZOrient.Clockwise_0_Degrees, PPLZFont.Font_Zero, 30, 30, buf, 0);
                        buf = encode.getBytes("ARGOX Printer SDK Demo");
                        printer.getEmulation().getTextUtil().printText(30, 50, PPLZOrient.Clockwise_0_Degrees, PPLZFont.Font_Zero, 30, 30, buf, 0);
                        printer.getEmulation().getSetUtil().setLabelLength(100);
                        printer.getEmulation().getSetUtil().setPrintOut(1, 0, 1, false);
                        apendQueue("Text");
                    } catch (Exception ex) {
                        Log.e("argox_demo", null, ex);
                    }
                }
            });
            Button btnGraphicText = maker.add(maker.createButton("Print Graphic Text"), maker.layAbsolute(0, 25, LinearLayout.LayoutParams.MATCH_PARENT, 100));
            btnGraphicText.setOnClickListener(new View.OnClickListener() {

                public void onClick(View arg0) {
                    try {
                        Encoding encode = Encoding.UTF_8;
                        byte[] buf;
                        //Demo graphic text
                        String imageName = "txt" + String.format("%05d", new Random().nextInt(100000));
                        printer.getEmulation().getTextUtil().storeTextGraphic(Typeface.DEFAULT, 30, false, false, false, false, PPLZStorage.Dram, imageName, "Graphic Text Demo");
                        printer.getEmulation().getGraphicsUtil().printStoreGraphic(50, 50, PPLZStorage.Dram, imageName, 1, 1);
                        printer.getEmulation().getSetUtil().setLabelLength(100);
                        printer.getEmulation().getSetUtil().setPrintOut(1, 0, 1, false);
                        apendQueue("GraphicText");
                    } catch (Exception ex) {
                        Log.e("argox_demo", null, ex);
                    }
                }
            });
            Button btnGraphics = maker.add(maker.createButton("Print Graphics"), maker.layAbsolute(0, 25, LinearLayout.LayoutParams.MATCH_PARENT, 100));
            btnGraphics.setOnClickListener(new View.OnClickListener() {

                public void onClick(View arg0) {
                    try {

                        Bitmap bmp = BitmapFactory.decodeResource(FrameDemoCommands.this.getResources(), R.drawable.android);

                        printer.getEmulation().getSetUtil().setLabelLength(200);
                        printer.getEmulation().getGraphicsUtil().storeGraphic(bmp, PPLZStorage.Dram, "graphic");
                        printer.getEmulation().getGraphicsUtil().printStoreGraphic(
                                30, 30, PPLZStorage.Dram, "graphic", 1, 1);
                        printer.getEmulation().getSetUtil().setPrintOut(1, 0, 1, false);
                        if (bmp != null) {
                            bmp.recycle();
                        }
                        apendQueue("Graphics");
                    } catch (Exception ex) {
                        Log.e("argox_demo", null, ex);
                    }
                }
            });
            Button btnSend = maker.add(maker.createButton("Send"), maker.layAbsolute(0, 25, LinearLayout.LayoutParams.MATCH_PARENT, 100));
            btnSend.setOnClickListener(new View.OnClickListener() {

                public void onClick(View arg0) {
                    try {
                        //print data and cut
                        printer.getEmulation().getIOUtil().printOut();
                        tvQueue.setText("Print Queue : ");
                    } catch (Exception ex) {
                        Log.e("argox_demo", null, ex);
                    }
                }
            });
            Button btnClose = maker.add(maker.createButton("Colse Connection"), maker.layAbsolute(0, 25, LinearLayout.LayoutParams.MATCH_PARENT, 100));
            btnClose.setOnClickListener(new View.OnClickListener() {

                public void onClick(View arg0) {
                    try {

                        //The output data.
                        printer.getConnection().close();
                        tvQueue.setText("Print Queue : ");

                    } catch (Exception ex) {
                        Log.e("argox_demo", null, ex);
                    }
                }
            });

            maker.escape();
        }

        //
        printer = new BarcodePrinter<PrinterConnection, PPLZ>();
        //
        printer.setEmulation(new PPLZ());
        //
        IConnectionStateListener stateListener = new IConnectionStateListener() {

            public void onStateChanged(final ConnectionState state) {
                FrameDemoCommands.this.runOnUiThread(new Runnable() {

                    public void run() {
                        if (state == ConnectionState.Connected) {
                            Toast.makeText(FrameDemoCommands.this, "Printer connection is " + state.name(), Toast.LENGTH_SHORT).show();
                            tvState.setText("Connected: " + printer.getConnection().toString());
                        } else {
                            tvState.setText(state.name());
                        }
                    }
                });
                Log.e("argox_demo", "Printer connection is " + state.name());
            }
        };

        //
        switch (type) {
            case NETWORK:
                String ip = b.getString("IP");
                int port = b.getInt("Port");
                connectViaNetwork(printer, ip, port, stateListener);
                break;
            case BLUETOOTH:
                connectViaBluetooth(printer, stateListener);
                break;
            case USB:
                connectViaUSB(printer, stateListener);
                break;
            case FILE:
                connectViaFile(printer, stateListener);
                break;
        }
        //set data listener for receiving data
        printer.getConnection().setDataListener(new PrinterDataListener() {

            public void onReceive(byte[] bytes) {
                try {
                    Log.d("argox_demo", "received bytes length=" + bytes.length);
                } catch (Exception ex) {
                    Log.e("argox_demo", null, ex);
                }
            }
        });
        try {
            //open connection
            printer.getConnection().open();
        } catch (Exception ex) {
            Log.e("argox_demo", null, ex);
            Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void connectViaNetwork(BarcodePrinter<PrinterConnection, PPLZ> printer, String ip, int port, IConnectionStateListener stateListener) {
        printer.setConnection(new TCPConnection(ip, port));
        printer.getConnection().setStateListener(stateListener);
    }

    public void connectViaBluetooth(BarcodePrinter<PrinterConnection, PPLZ> printer, IConnectionStateListener stateListener) {
        printer.setConnection(new BluetoothConnection(FrameDemoCommands.this));
        printer.getConnection().setStateListener(stateListener);
    }

    public void connectViaUSB(BarcodePrinter<PrinterConnection, PPLZ> printer, IConnectionStateListener stateListener) {
        printer.setConnection(new USBConnection(FrameDemoCommands.this));
        printer.getConnection().setStateListener(stateListener);
    }

    public void connectViaFile(BarcodePrinter<PrinterConnection, PPLZ> printer, IConnectionStateListener stateListener) {
        printer.setConnection(new FileConnection(new File(Environment.getExternalStorageDirectory(), "argox_printer.txt").getPath()));
        printer.getConnection().setStateListener(stateListener);
    }

    public TextView apendQueue(String s) {
        tvQueue.setText(tvQueue.getText() + s + " ");
        return tvQueue;
    }

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    printer.getConnection().onActivityResult(requestCode, resultCode, data);
}
}
