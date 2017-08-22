package com.example.vinic.projetoirrigacao;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

/**
 * Created by vinic on 18/08/2017.
 */

public class PrincipalActivity extends AppCompatActivity {

    Sessao sessao= Sessao.getInstance();
    ConexaoBluetooth conexaoBluetooth= ConexaoBluetooth.getInstance();

    private static final int SOLICITA_ATIVACAO =1;
    private static final int SOLICITA_CONEXAO =2;
    private static final int MESSAGE_READ=3;


    BluetoothAdapter mBluetoothAdapter = null;
    BluetoothDevice mDevice=null;
    BluetoothSocket mSocket=null;

    boolean conexao= false;

    private static String MAC=null;

    UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        this.carregarArquivo();

        if(sessao.getArrayValvulas().size()==0){
            sessao.iniciarConfValvulas();
        }
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        final Button btnConectar = (Button) findViewById(R.id.btn_conectar);
        Button btnOperacao = (Button) findViewById(R.id.btn_operacao);
        Button btnClimatologia = (Button) findViewById(R.id.btn_climatologia);



        /*
        if(mBluetoothAdapter==null){
            Toast.makeText(getApplicationContext(),"Seu aparelho não suporta Bluetooth", Toast.LENGTH_LONG).show();
            finish();
        }else if(!mBluetoothAdapter.isEnabled()){
            Intent intent= new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, SOLICITA_ATIVACAO);
            //mBluetoothAdapter.enable();
            Toast.makeText(getApplicationContext(),"Bluetooth Ativado", Toast.LENGTH_LONG).show();
        }*/

        if(conexaoBluetooth.ativarBluetooth()){
            Intent intent= new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, SOLICITA_ATIVACAO);
        }else{
            if(!conexaoBluetooth.mBluetoothAdapter.isEnabled()){
                Toast.makeText(getApplicationContext(),"Seu Bluetooth não esta disponivel", Toast.LENGTH_LONG).show();
                finish();
            }

        }

    }


    public void onButtonClick(View v){

        if(v.getId()== R.id.btn_operacao) {

            Intent intent = new Intent(getApplicationContext(), OperacaoActivity.class );
            startActivity(intent);

        }else if(v.getId()==R.id.btn_climatologia){

            Intent intent = new Intent(getApplicationContext(),ClimatologiaActivity.class);
            startActivity(intent);

        } else if(v.getId() == R.id.btn_conectar){

            final Button btnConectar = (Button) findViewById(R.id.btn_conectar);

            if(conexao){

                conexao=false;
                btnConectar.setText("Conectar");

            }else{
                Intent intent = new Intent(PrincipalActivity.this, ListaDispositivos.class );
                startActivityForResult(intent, SOLICITA_CONEXAO);


            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){

            case SOLICITA_ATIVACAO:
                if(resultCode== Activity.RESULT_OK){
                    Toast.makeText(getApplicationContext(),"Bluetooth Ativo", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Bluetooth não pode ser ativado!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;

            case SOLICITA_CONEXAO:
                //Toast.makeText(getApplicationContext(),"Chegou aqui",Toast.LENGTH_LONG).show();
                if(resultCode == Activity.RESULT_OK){

                    MAC= data.getExtras().getString(ListaDispositivos.ENDERECO_MAC);

                    mDevice = mBluetoothAdapter.getRemoteDevice(MAC);

                    try{

                        mSocket = mDevice.createRfcommSocketToServiceRecord(mUUID);
                        mSocket.connect();
                        Toast.makeText(getApplicationContext(),"Conectado ao Aparelho: " + MAC,Toast.LENGTH_LONG).show();
                        final Button btnConectar = (Button)findViewById(R.id.btn_conectar);
                        btnConectar.setText("Desconectar");

                    }catch (IOException e) {
                        Toast.makeText(getApplicationContext(),"Erro ao tentar conectar!",Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                }
                break;
        }

    }

    public void carregarArquivo(){

        String ret = "";

        try {
            InputStream inputStream = getApplicationContext().openFileInput("valvulas");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    String array[]= new String[3];
                    array= receiveString.split(",");

                    Valvula valvulaTemp= new Valvula();

                    int valvulaInt= Integer.parseInt(array[0].substring(1));
                    int pulsosInt= Integer.parseInt(array[1]);
                    double tempoDouble = Double.parseDouble(array[2]);

                    valvulaTemp.setVal(valvulaInt);
                    valvulaTemp.setPulsos(pulsosInt);
                    valvulaTemp.setTempo(tempoDouble);

                    sessao.getArrayValvulas().add(valvulaTemp);
                }

                inputStream.close();

            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

    }
}
