/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argox.sdk.barcodeprinter.demo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.argox.sdk.barcodeprinter.BarcodePrinter;
import com.argox.sdk.barcodeprinter.connection.usb.USBConnection;
import com.argox.sdk.barcodeprinter.emulation.pplz.PPLZ;
import com.argox.sdk.barcodeprinter.emulation.pplz.PPLZFont;
import com.argox.sdk.barcodeprinter.emulation.pplz.PPLZOrient;
import grandroid.view.LayoutMaker;
import grandroid.view.ViewDesigner;
import java.util.HashMap;

/**
 * 
 * @author Rovers
 */
public class FrameUSBConnection extends Activity {

	protected UsbDevice device;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); // To change body of generated
											// methods, choose Tools |
											// Templates.
		device = (UsbDevice) getIntent().getParcelableExtra(
				UsbManager.EXTRA_DEVICE);

		if (device == null) {
			UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
			HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
			for (String key : deviceList.keySet()) {
				UsbDevice dev = deviceList.get(key);
				if (null != dev) {
					if (dev.getProductId() == 2078 && dev.getVendorId() == 5732) {
						device = dev;
					}
					Log.d("grandroid", dev.getDeviceName() + ", vendorID="
							+ dev.getVendorId());
				}
			}
		}

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
			if (device != null) {
				maker.addTextView("DeviceName: " + device.getDeviceName());
				maker.addTextView("DeviceId: " + device.getDeviceId());
				maker.addTextView("VendorId: " + device.getVendorId());
				maker.addTextView("ProductId: " + device.getProductId());
				maker.addTextView("DeviceProtocol: "
						+ device.getDeviceProtocol());
				maker.addTextView("DeviceClass: " + device.getDeviceClass());
				Button btnShowCases = maker.addButton("Print \"Hello World\"");
				btnShowCases.setBackgroundResource(R.drawable.b1);
				btnShowCases.setLayoutParams(maker.layAbsolute(0, 25,
						LinearLayout.LayoutParams.MATCH_PARENT, 100));
				btnShowCases.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						BarcodePrinter<USBConnection, PPLZ> printer = new BarcodePrinter<USBConnection, PPLZ>();
						// if you use BluetoothConnection instead of
						// TCPConnection , you must implements
						// onActivityResult(...) callback function
						// with calling
						// printer.getConnection().onActivityResult(...)
						printer.setConnection(new USBConnection(v.getContext(),
								device));
						printer.setEmulation(new PPLZ());

						try {
							printer.getConnection().open();
							String text = "Hello World!";
							printer.getEmulation()
									.getTextUtil()
									.printText(0, 0,
											PPLZOrient.Clockwise_0_Degrees,
											PPLZFont.Font_Zero, 20, 20,
											text.getBytes(), 0);
							
							printer.getEmulation().getIOUtil().printOut();
							Toast.makeText(FrameUSBConnection.this, "成功", Toast.LENGTH_SHORT).show();
						} catch (Exception ex) {
							Log.e("argox_demo", null, ex);
							Toast.makeText(FrameUSBConnection.this, "失败"+ex.toString(), Toast.LENGTH_SHORT).show();
						}
					}
				});
			} else {
				maker.addTextView("No usb printer exist.");
			}
			maker.escape();
		}
	}
}
