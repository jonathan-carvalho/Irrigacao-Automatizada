package com.example.vinic.projetoirrigacao;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by vinic on 18/08/2017.
 */

public class ClimatologiaActivity extends AppCompatActivity {

    private static final int MESSAGE_READ=3;

    Handler mHandler;
    StringBuilder dadosBluetooth = new StringBuilder();

    ConnectedThread connectedThread;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_climatologia);
        Toast.makeText(getApplicationContext(),"Iniciando coleta dos dados...", Toast.LENGTH_LONG).show();

        mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {

                if(msg.what == MESSAGE_READ){
                    //Toast.makeText(getApplicationContext(),msg.toString(),Toast.LENGTH_LONG).show();
                    String recebidos = (String) msg.obj;

                    dadosBluetooth.append(recebidos);

                    int fimInformacao = dadosBluetooth.indexOf("}");

                    if(fimInformacao>0){

                        String dadosCompletos = dadosBluetooth.substring(0, fimInformacao);

                        int tamanhoInformacao = dadosCompletos.length();

                        if(dadosBluetooth.charAt(0)== '{'){

                            String dadosFinais= dadosBluetooth.substring(1, tamanhoInformacao);
                            Log.d("TAG", dadosFinais);

                            try {
                                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("climatologia.csv", Context.MODE_PRIVATE));
                                outputStreamWriter.write(dadosFinais);
                                outputStreamWriter.close();
                            } catch (IOException e) {
                                Log.e("Exception", "File write failed: " + e.toString());
                            }
                            Toast.makeText(getApplicationContext(),"Concluído!", Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(),"Dados recebidos: "+dadosFinais, Toast.LENGTH_LONG).show();

                        }
                        dadosBluetooth.delete(0,dadosBluetooth.length());
                        dadosCompletos=" ";
                    }
                }

            }
        };

    }


    public void onButtonClick(View v){

    }


    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[30720];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    String dadosBt= new String(buffer, 0, bytes);

                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, dadosBt)
                            .sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }


    }

}
