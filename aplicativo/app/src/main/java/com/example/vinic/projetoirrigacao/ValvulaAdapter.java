package com.example.vinic.projetoirrigacao;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by vinic on 20/08/2017.
 */

public class ValvulaAdapter extends ArrayAdapter {

    public ValvulaAdapter(Context context, ArrayList<Valvula> valvulas){
        super(context, 0, valvulas);
    }

    @Override
    public View getView(int posicao, View convertView, ViewGroup parent){
        Valvula valvula= (Valvula) getItem(posicao);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_adapter, parent, false);
        }

        TextView val_nome= (TextView) convertView.findViewById(R.id.id_nome);
        TextView val_tempo= (TextView) convertView.findViewById(R.id.id_tempo);
        TextView val_pulsos= (TextView) convertView.findViewById(R.id.id_pulsos);

        val_nome.setText(Integer.toString(valvula.getVal()));
        val_tempo.setText(Double.toString(valvula.getTempo()));
        val_pulsos.setText(Integer.toString(valvula.getPulsos()));

        return convertView;
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }


}
