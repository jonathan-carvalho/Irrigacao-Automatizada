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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by vinic on 18/08/2017.
 */

public class ClimatologiaActivity extends AppCompatActivity {

    private static final int MESSAGE_READ=3;

    Handler mHandler;
    StringBuilder dadosBluetooth = new StringBuilder();
    ConexaoBluetooth conexaoBluetooth= ConexaoBluetooth.getInstance();
    ConnectedThread connectedThread;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_climatologia);

        Toast.makeText(getApplicationContext(),"Iniciando coleta dos dados...", Toast.LENGTH_LONG).show();

        /*
        conexaoBluetooth.enviarDados("clima");
        connectedThread= new ConnectedThread(conexaoBluetooth.mSocket);
        connectedThread.start();
*/
        String test= "15,08,2017,20,00,00,26.2,48.7\n15,08,2017,21,00,00,28.0,40.0\n";


        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("climatologia", Context.MODE_PRIVATE));
            outputStreamWriter.write(test);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }




        /*mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {

                if(msg.what == MESSAGE_READ){
                    //Toast.makeText(getApplicationContext(),msg.toString(),Toast.LENGTH_LONG).show();
                    String recebidos = (String) msg.obj;

                    dadosBluetooth.append(recebidos);

                    try {
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("climatologia", Context.MODE_APPEND));
                        outputStreamWriter.write(dadosBluetooth.toString());
                        outputStreamWriter.close();
                    } catch (IOException e) {
                        Log.e("Exception", "File write failed: " + e.toString());
                    }
                    Toast.makeText(getApplicationContext(),"Concluído!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(),"Dados recebidos: "+dadosBluetooth.toString(), Toast.LENGTH_LONG).show();

                }

            }
        };*/

        ArrayList<Leitura> arrayList = new ArrayList<Leitura>();



        try {
            InputStream inputStream = getApplicationContext().openFileInput("climatologia");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    String informacao[]= new String[8];
                    informacao= receiveString.split(",");

                    Leitura leitura= new Leitura();

                    leitura.setDia(informacao[0]);
                    leitura.setMes(informacao[1]);
                    leitura.setAno(informacao[2]);
                    leitura.setHora(informacao[3]);
                    leitura.setMinuto(informacao[4]);
                    leitura.setSegundo(informacao[5]);
                    leitura.setTemperatura(informacao[6]);
                    leitura.setUmidade(informacao[7]);


                    arrayList.add(leitura);

                }

                inputStream.close();

            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        final LeituraAdapter leituraAdapter= new LeituraAdapter(this,arrayList);
        final ListView listClimatologia = (ListView) findViewById(R.id.id_listview_climatologia);
        listClimatologia.setAdapter(leituraAdapter);


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
