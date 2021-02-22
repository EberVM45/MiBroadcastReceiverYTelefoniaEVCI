package net.ivanvega.mibroadcastreceiverytelefonia.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import net.ivanvega.mibroadcastreceiverytelefonia.FileManager;

public class MyReceiverCalls extends BroadcastReceiver {
    private static final String TAG="Tel";
    String num;
    String message;
    String entrada;
    static boolean incomingCall = false;
    static String numEntrante;
    @Override
    public void onReceive(Context context, Intent intent) {
        num="";
        message="";
        TelephonyManager tm=(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber){
                super.onCallStateChanged(state, incomingNumber);
                if(state==TelephonyManager.CALL_STATE_RINGING){
                    numEntrante = incomingNumber.substring(3);
                    incomingCall = true;
                }
                if(state==TelephonyManager.CALL_STATE_IDLE){
                    if(incomingCall){
                        incomingCall = false;
                        entrada = FileManager.readFromFile(context).replace("\n","").replace("\r","");
                        if(entrada.contains("%!%")){
                            num = entrada.split("%!%")[0];
                            message = entrada.split("%!%")[1];
                        }
                        Log.d("Hola", "onCallStateChanged: "+num);
                        Log.d("Hola", "onCallStateChanged: "+incomingNumber);
                        num = num.replace(" ","");
                        Toast.makeText(context, "Registrado: "+num +"\nEntrante: "+incomingNumber, Toast.LENGTH_SHORT).show();
                        if(num.equals(incomingNumber)){
                            enviarSMS(incomingNumber, message,context);
                        }
                    }
                }
            }
        },PhoneStateListener.LISTEN_CALL_STATE);
    }
    private void enviarSMS(String tel, String msj,Context context) {
        SmsManager smsManager =  SmsManager.getDefault();
        smsManager.sendTextMessage(tel,null, msj, null, null);
        Toast.makeText(context, "Mensaje enviado", Toast.LENGTH_SHORT).show();
    }
}
