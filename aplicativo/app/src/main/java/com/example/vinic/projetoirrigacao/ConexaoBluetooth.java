package com.example.vinic.projetoirrigacao;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.util.UUID;

/**
 * Created by vinic on 19/08/2017.
 */

public class ConexaoBluetooth {

    private static ConexaoBluetooth instance = new ConexaoBluetooth();

    private static final int SOLICITA_ATIVACAO =1;
    private static final int SOLICITA_CONEXAO =2;
    private static final int MESSAGE_READ=3;


    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothDevice mDevice=null;
    BluetoothSocket mSocket=null;

    boolean conexao= false;

    private static String MAC=null;

    UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");


    private  ConexaoBluetooth(){}

    public static ConexaoBluetooth getInstance(){
        return instance;
    }

    public boolean ativarBluetooth(){
        if(mBluetoothAdapter==null){
            return false;
        }else if(!mBluetoothAdapter.isEnabled()){
            return true;
        }else{
            return false;
        }
    }

}
