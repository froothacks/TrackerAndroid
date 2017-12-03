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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
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
                    firebase.addMeetingDay(lastText, "SCU");
                }
            });
            t1.start();
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
        barcodeView = (DecoratedBarcodeView) findViewById(R.id.barcode_scanner);
        barcodeView.decodeContinuous(callback);


        SharedPreferences prefs = getSharedPreferences("clubdata1", MODE_ENABLE_WRITE_AHEAD_LOGGING);
        if (prefs.getString("club_name", "null").equals(prefs.getString("database_url", "null")) == prefs.getString("club_name", "null").equals(prefs.getString("database_key", "null"))) {
            final Dialog d = new Dialog(this);
            d.setTitle("NumberPicker");
            d.setContentView(R.layout.custom);
            Button ok = (Button) d.findViewById(R.id.ok_button);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    m_club = ((EditText)d.findViewById(R.id.club)).getText().toString();
                    m_url = ((EditText)d.findViewById(R.id.database_url)).getText().toString();
                    m_key = ((EditText)d.findViewById(R.id.database_key)).getText().toString();

                    if (!m_club.equals("")&&!m_url.equals("")&&!m_key.equals("")){
                        Toast.makeText(MainActivity.this, m_club, Toast.LENGTH_SHORT).show();
                        d.dismiss();
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Fill in all fields", Toast.LENGTH_SHORT).show();
                    }
                }

            });
            d.show();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("club_name", m_club);
            editor.putString("database_url", m_url);
            editor.putString("database_key", m_key);
            editor.commit();

        } else {
            m_club = prefs.getString("club_name", "No name defined");//"No name defined" is the default value.
            m_url = prefs.getString("database_url", "No name defined");//"No name defined" is the default value.
            m_key = prefs.getString("database_key", "No name defined");//"No name defined" is the default value.
        }
        new Firebase(m_key, m_url);
        beepManager = new BeepManager(this);

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
