/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.samples.vision.barcodereader;

import android.Manifest;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.samples.vision.barcodereader.ui.camera.AESenc;
import com.google.android.gms.samples.vision.barcodereader.ui.camera.HttpGetRequest;
import com.google.android.gms.samples.vision.barcodereader.ui.camera.HttpPostRequest;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Main activity demonstrating how to pass extra parameters to an activity that
 * reads barcodes.
 */
public class MainActivity extends Activity implements View.OnClickListener {

    // use a compound button so either checkbox or switch widgets work.
    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private TextView statusMessage;
    private TextView barcodeValue;

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";
    String val="";
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusMessage = (TextView)findViewById(R.id.status_message);
        barcodeValue = (TextView)findViewById(R.id.barcode_value);

        autoFocus = (CompoundButton) findViewById(R.id.auto_focus);
        useFlash = (CompoundButton) findViewById(R.id.use_flash);
        try {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

        }catch (Exception e){
            System.out.println(e);
        }


        /*String myUrl = "https://joshstamp.azurewebsites.net/data/"+val;
        String result= "";

        try{
            HttpGetRequest getRequest = new HttpGetRequest();
            result = getRequest.execute(myUrl).get();
            if(result == "[]") {
                System.out.print(result + "16253432155555555555555555555555555555555555551111111111");
            }
            else
            {
                System.out.print("afdfasfdfashgdhghgdhahsdhdshdahadshdshghadjjdahdahjdahjahjagdaj");
            }
        }catch(Exception e){
            System.out.println(e);
        }*/
        findViewById(R.id.read_barcode).setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_barcode) {
            // launch barcode activity.
            Intent intent = new Intent(this, BarcodeCaptureActivity.class);
            intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus.isChecked());
            intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());

            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        }

    }

    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.  The <var>resultCode</var> will be
     * {@link #RESULT_CANCELED} if the activity explicitly returned that,
     * didn't return any result, or crashed during its operation.
     * <p/>
     * <p>You will receive this call immediately before onResume() when your
     * activity is re-starting.
     * <p/>
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     * @see #startActivityForResult
     * @see #createPendingResult
     * @see #setResult(int)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    statusMessage.setText(R.string.barcode_success);

                    //my changes
                    //String val="";
                    try {
                        AESenc aes = new AESenc();
                        val = aes.decrypt(barcode.displayValue);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    Pattern pattern = Pattern.compile("(\\d){6}(_)(\\d){1}");
                    Matcher matcher = pattern.matcher(val);
                    if (!matcher.find()) {
                        barcodeValue.setText("Declined-Invalid Barcode");
                    }

                    else
                    {
                    String myUrl = "https://joshstamp.azurewebsites.net/data/" + val;
                    String result = "";

                    try {
                        HttpGetRequest getRequest = new HttpGetRequest();
                        result = getRequest.execute(myUrl).get();
                    } catch (Exception e) {
                        System.out.println(e);
                    }

                    if (!result.equals("[]")) {
                        barcodeValue.setText("Declined- Already Scanned " + " " + val);
                    } else {
                        String myUrlPost = "https://joshstamp.azurewebsites.net/data/" + val + "&true";

                        HttpPostRequest postRequest = new HttpPostRequest();
                        try {
                            String resultTemp = postRequest.execute(myUrlPost).get();
                        } catch (Exception e) {
                            System.out.println(e);
                        }

                        if (postRequest.responseCode >= 200 && postRequest.responseCode < 300) {
                            barcodeValue.setText("Accepted");
                        } else {
                            barcodeValue.setText("Bad Request");
                        }
                    }
                }
                } else {
                    statusMessage.setText(R.string.barcode_failure);
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                statusMessage.setText(String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        /*else {
            super.onActivityResult(requestCode, resultCode, data);
        }*/
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


}
