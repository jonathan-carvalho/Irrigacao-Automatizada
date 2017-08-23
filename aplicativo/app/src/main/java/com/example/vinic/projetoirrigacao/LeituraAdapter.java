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
 * Created by vinic on 22/08/2017.
 */

public class LeituraAdapter extends ArrayAdapter {

    public LeituraAdapter(Context context, ArrayList<Leitura> leituras){
        super(context, 0, leituras);
    }

    @Override
    public View getView(int posicao, View convertView, ViewGroup parent){
        Leitura leitura= (Leitura) getItem(posicao);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_climatologia_adapteer, parent, false);
        }

        String date = leitura.getDia()+"/"+leitura.getMes()+"/"+leitura.getAno();
        String hora= leitura.getHora()+":"+leitura.getMinuto()+":"+leitura.getSegundo();

        TextView leitura_date= (TextView) convertView.findViewById(R.id.id_data);
        TextView leiura_hora= (TextView) convertView.findViewById(R.id.id_hora);
        TextView leitura_temperatura= (TextView) convertView.findViewById(R.id.id_temperatura);
        TextView leitura_umidade= (TextView) convertView.findViewById(R.id.id_umidade);

        leitura_date.setText(date);
        leiura_hora.setText(hora);
        leitura_temperatura.setText(leitura.getTemperatura());
        leitura_umidade.setText(leitura.getUmidade());

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
