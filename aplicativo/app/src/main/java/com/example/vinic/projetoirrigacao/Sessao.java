package com.example.vinic.projetoirrigacao;

import java.util.ArrayList;

/**
 * Created by vinic on 21/08/2017.
 */

public class Sessao {

    private static Sessao instance= new Sessao();
    private Valvula valvula;
    private static ArrayList<Valvula> arrayValvulas= new ArrayList<Valvula>();

    private Sessao(){

    }

    public static Sessao getInstance(){
        return  instance;
    }


    public Valvula getValvula() {
        return valvula;
    }

    public void setValvula(Valvula valvula) {
        this.valvula = valvula;
    }

    public static ArrayList<Valvula> getArrayValvulas() {
        return arrayValvulas;
    }

    public static void setArrayValvulas(ArrayList<Valvula> arrayValvulas) {
        Sessao.arrayValvulas = arrayValvulas;
    }

    public static void iniciarConfValvulas(){
        for(int i=0; i<7; i++){
            Valvula v= new Valvula();
            v.setVal(i+1);
            v.setTempo(0);
            v.setPulsos(0);
            arrayValvulas.add(v);
        }
    }

}
