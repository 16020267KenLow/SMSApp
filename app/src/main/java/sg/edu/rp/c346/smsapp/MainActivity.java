package sg.edu.rp.c346.smsapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button btn;
    Button btnMsg;
    EditText etReceiver;
    EditText etMessage;
    BroadcastReceiver br = new SMSReceiver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        this.registerReceiver(br, filter);

        btn = findViewById(R.id.button);
        btnMsg = findViewById(R.id.buttonMessages);
        etMessage = findViewById(R.id.editTextMessage);
        etReceiver = findViewById(R.id.editTextReceiver);

        checkPermission();
        btnMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) // At least KitKat
                {
                    //String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(this); // Need to change the build to API 19

                    Uri sms_uri = Uri.parse("smsto:" + etReceiver.getText().toString());
                    Intent sendIntent = new Intent(Intent.ACTION_SENDTO, sms_uri);
                    sendIntent.putExtra("sms_body", etMessage.getText().toString());

                    startActivity(sendIntent);

                }
                else {
                    String phoneNo = etReceiver.getText().toString();
                    String message = etMessage.getText().toString();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setType("vnd.android-dir/mms-sms");
                    intent.putExtra("address", phoneNo);
                    intent.putExtra("sms_body", message);
                    startActivity(intent);
                }
            }


        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = etReceiver.getText().toString();
                String[] addresses = address.split(",");
                String message = etMessage.getText().toString();
                SmsManager smsManager = SmsManager.getDefault();
                for(int i = 0; i < addresses.length; i++) {
                    smsManager.sendTextMessage(addresses[i], null, message, null, null);
                }
                Toast.makeText(MainActivity.this, "Message Sent", Toast.LENGTH_LONG).show();

                etMessage.setText("");
            }
        });
    }
    private void checkPermission() {
        int permissionSendSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);
        int permissionRecvSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS);
        if (permissionSendSMS != PackageManager.PERMISSION_GRANTED &&
                permissionRecvSMS != PackageManager.PERMISSION_GRANTED) {
            String[] permissionNeeded = new String[]{Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS};
            ActivityCompat.requestPermissions(this, permissionNeeded, 1);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(br);
    }
}
