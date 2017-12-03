package com.example.leon.myapplication;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.google.zxing.client.result.TextParsedResult;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;
    private String lastText;
    private String m_url;
    private String m_key;
    private String m_club;
    Firebase firebase;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() == null || result.getText().equals(lastText)) {
                // Prevent duplicate scans
                return;
            }
            lastText = result.getText();
            barcodeView.setStatusText(result.getText());
            Thread t1 = new Thread(new Runnable() {
                public void run() {
                    firebase.addMeetingDay(lastText, m_club);
//                    boolean bool=firebase.getPaid(lastText, m_club);
//                    paid.setText(bool?"Yes":"No");



                }
            });
            t1.start();
            ((TextView) findViewById(R.id.studNum)).setText(lastText);
//            beepManager.playBeepSoundAndVibrate();

            //Added preview of scanned barcode
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.continuous_scan);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Club");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_club = input.getText().toString();
                ((TextView) findViewById(R.id.ClubName)).setText(m_club);
            }
        });
        builder.show();

        firebase = new Firebase("ItxR5i7mQ5TQjRGNzWlq8KC4dqWtVdT3OQ3weuYI", "https://clubattendancesjam.firebaseio.com/");
        barcodeView = (DecoratedBarcodeView) findViewById(R.id.barcode_scanner);
        barcodeView.decodeContinuous(callback);
    }


    @Override
    protected void onResume() {
        super.onResume();

        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        barcodeView.pause();
    }

    public void pause(View view) {
        barcodeView.pause();
    }

    public void resume(View view) {
        barcodeView.resume();
    }

    public void triggerScan(View view) {
        barcodeView.decodeSingle(callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
}
