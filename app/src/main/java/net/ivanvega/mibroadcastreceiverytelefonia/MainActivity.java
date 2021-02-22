package net.ivanvega.mibroadcastreceiverytelefonia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.ivanvega.mibroadcastreceiverytelefonia.receivers.MiReceiverTelefonia;
import net.ivanvega.mibroadcastreceiverytelefonia.receivers.MyBroadcastReceiver;

public class MainActivity extends AppCompatActivity {
    private static final String TAG="Que onda";
    MyBroadcastReceiver myBroadcastReceiver=
            new MyBroadcastReceiver();
    MiReceiverTelefonia miReceiverTelefonia = new MiReceiverTelefonia();
    Button btnS,btnConfigurar;
    TextView lbl;
    EditText txtTel, txtMessage;
    static String tel="",msg="";
    TextView lslMensaje;
    TextView lslNumero;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lslMensaje=findViewById(R.id.lblMensaje);
        msg="Este es el mensaje";
        tel="";
        btnS = findViewById(R.id.btnSend);
        lbl = findViewById(R.id.lbl);
        txtTel = findViewById(R.id.txtPhone);
        txtMessage = findViewById(R.id.txtTexto);
        lslNumero=findViewById(R.id.lblNum);
        btnConfigurar=findViewById(R.id.btnConfigurar);
        String leido=FileManager.readFromFile(getApplicationContext());
        if(leido.contains("%!%")){
            String resultado[]=
                    leido.replace("\n","").replace("\r","").split("%!%");
            tel=resultado[0];
            msg=resultado[1];
        }
        lslNumero.setText(tel);
        lslMensaje.setText(msg);
        btnS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent broadcast = new Intent();
                broadcast.setAction(getString(R.string.action_broadcast));
                broadcast.putExtra("key1", "parametro de la difusion");
                sendBroadcast(broadcast);
            }
        });
        btnConfigurar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String cadAgregar = txtTel.getText().toString() + "%!%" + txtMessage.getText().toString();
                FileManager.writeToFile(cadAgregar,getApplicationContext());
                lslNumero.setText(txtTel.getText());
                lslMensaje.setText(txtMessage.getText());
            }
        });
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        filter.addAction(getString(R.string.action_broadcast));
        this.registerReceiver(myBroadcastReceiver, filter);
        IntentFilter intentFilterTel = new IntentFilter(Telephony.Sms .Intents.SMS_RECEIVED_ACTION);
        getApplicationContext().registerReceiver(miReceiverTelefonia,
                intentFilterTel
        );
    }
    private void enviarSMS(String tel, String msj) {
         SmsManager smsManager =  SmsManager.getDefault();
         smsManager.sendTextMessage(tel,null, msj,
         null, null);
/*        Toast.makeText(
                this, "Mensaje enviado",
                Toast.LENGTH_LONG
        ).show();*/
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(myBroadcastReceiver);
    }

    public void btnSMS_onclick(View v){
        enviarSMS(txtTel.getText().toString(), txtMessage.getText().toString());
    }

}