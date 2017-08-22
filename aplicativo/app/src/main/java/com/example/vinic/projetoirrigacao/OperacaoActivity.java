package com.example.vinic.projetoirrigacao;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by vinic on 18/08/2017.
 */

public class OperacaoActivity extends AppCompatActivity {
    private Sessao sessao= Sessao.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operacao);

        /*ArrayList<Valvula> arrayList= new ArrayList<Valvula>();

        Valvula v1= new  Valvula();
        v1.setVal(1);
        v1.setPulsos(2);
        v1.setTempo(2);

        arrayList.add(v1);*/

        final ValvulaAdapter valvulaAdapter= new ValvulaAdapter(this,sessao.getArrayValvulas());

        final ListView listView = (ListView) findViewById(R.id.list_adapter);
        listView.setAdapter(valvulaAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Valvula val= (Valvula) parent.getAdapter().getItem(position);
                sessao.setValvula(val);
                Intent intent= new Intent(getApplication(),EditarValvula.class);
                startActivity(intent);

                //Toast.makeText(getApplicationContext(),Integer.toString(arrayList.get(0).getPulsos()),Toast.LENGTH_SHORT).show();
                listView.invalidateViews();
            }
        });



    }


    public void onButtonClick(View v){

        try {

            FileOutputStream outputStreamWriter = openFileOutput("valvulas", Context.MODE_PRIVATE);
            StringBuilder stringBuilder=new StringBuilder();
            for(int i = 0; i<sessao.getArrayValvulas().size(); i++){

                Valvula valvulaTemp=sessao.getArrayValvulas().get(i);

                String posicao= "V" + Integer.toString(valvulaTemp.getVal());
                String pulsos= Integer.toString(valvulaTemp.getPulsos());
                String tempo= Double.toString(valvulaTemp.getTempo());

                stringBuilder.append(posicao);
                stringBuilder.append(",");
                stringBuilder.append(pulsos);
                stringBuilder.append(",");
                stringBuilder.append(tempo);
                stringBuilder.append("\n");

            }

            String config = stringBuilder.toString();
            outputStreamWriter.write(config.getBytes());
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
        sessao.getArrayValvulas().clear();
        Intent intent= new Intent(getApplicationContext(),PrincipalActivity.class);
        startActivity(intent);

    }


    public void carregarArquivo(){

        String ret = "";

        try {
            InputStream inputStream = getApplicationContext().openFileInput("config.txt");

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

            }else{
                sessao.iniciarConfValvulas();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

    }

}
