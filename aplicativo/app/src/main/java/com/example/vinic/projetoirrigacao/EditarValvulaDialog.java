package com.example.vinic.projetoirrigacao;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static android.app.Activity.RESULT_OK;


/**
 * Created by vinic on 20/08/2017.
 */

public class EditarValvulaDialog extends DialogFragment {
    private Sessao sessao= Sessao.getInstance();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(R.layout.activity_edit_val);

        View view= inflater.inflate(R.layout.activity_edit_val,null);


        EditText tempo= (EditText)view.findViewById(R.id.id_tempo_d);
        EditText pulsos= (EditText)view.findViewById(R.id.id_pulsos_d);



        builder.setMessage("Edição de Valvula")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Toast.makeText(getContext(),tempo.getText().toString(),Toast.LENGTH_LONG).show();
                        //sessao.getValvula().setTempo(Double.parseDouble(tempoString));
                        //sessao.getValvula().setPulsos(Integer.parseInt(pulsosString));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //EditarValvulaDialog.this.getDialog().cancel();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}

