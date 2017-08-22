package com.example.vinic.projetoirrigacao;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by vinic on 21/08/2017.
 */

public class EditarValvula extends AppCompatActivity {

    private Sessao sessao = Sessao.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_valvula);




        final EditText tempo = (EditText) findViewById(R.id.editTempo);
        final EditText pulsos = (EditText) findViewById(R.id.editPulsos);

        tempo.setText(Double.toString(sessao.getValvula().getTempo()));
        pulsos.setText(Integer.toString(sessao.getValvula().getPulsos()));
        Button editar = (Button) findViewById(R.id.btn_edit_ok);


    }

    public void onClickButton(View view){

        EditText tempo = (EditText) findViewById(R.id.editTempo);
        EditText pulsos = (EditText) findViewById(R.id.editPulsos);

        String tempoString= tempo.getText().toString();
        String pulsosString= pulsos.getText().toString();

        Double tempoDouble = Double.parseDouble(tempoString);
        int pulsosInt= Integer.parseInt(pulsosString);

        int posicao= sessao.getValvula().getVal()-1;

        sessao.getArrayValvulas().get(posicao).setTempo(tempoDouble);
        sessao.getArrayValvulas().get(posicao).setPulsos(pulsosInt);

        sessao.setValvula(null);

        Intent intent= new Intent(getApplicationContext(), OperacaoActivity.class);
        startActivity(intent);
    }
}
