#include <virtuabotixRTC.h>
#include "DHT.h"
#include <SPI.h>
#include <SD.h>

// myRTC(clock, data, rst)
virtuabotixRTC myRTC(31, 29, 27);

#define DHTPIN 2     // what digital pin we're connected to
#define DHTTYPE DHT22   // DHT 22  (AM2302), AM2321
DHT dht(DHTPIN, DHTTYPE);

#define DIA 1
#define MES 2
#define ANO 3
#define HORA 4
#define MINUTO 5
#define SEGUNDO 6

const int chipSelect = 53;
File clima; // Dia,Mes,Ano,Hora,Minuto,Segundo,Temperatura,Umidade
File configValvulas; // Valvula,Qt_pulsos,Intervalo

int tempoRegaDia = 5; //tempo em minutos

String inicioFuncionamento = "13:42"; // HH:MM

String ultimaColeta;

String comunicacao = "";

// ------------------------- Inicio da classe Valvula -------------------------
class Valvula{
  private:
    int numero;
    boolean estado;
    int qtPulsosTotal;
    int qtPulsosDados;
    int intervaloPulsos;
    String alarme;
    int pino;
    
  public:
    Valvula(){
      estado = false;
      qtPulsosDados = 0;
      alarme = inicioFuncionamento;
      }
    
    int getNumero(){
      return numero;
      }
      
    void setNumero(int num){
      numero = num;
      }
    
    boolean getEstado(){
      return estado;
      }
      
    void setEstado(boolean novoEstado){
      estado = novoEstado;
      }

    int getQtPulsosTotal(){
      return qtPulsosTotal;
      }
      
    void setQtPulsosTotal(int quantidade){
      qtPulsosTotal = quantidade;
      }
    
    int getQtPulsosDados(){
      return qtPulsosDados;
      }
    
    void setQtPulsosDados(int quantidade){
      qtPulsosDados = quantidade;
      }
    
    int getIntervaloPulsos(){
      return intervaloPulsos;
      }
      
    void setIntervaloPulsos(int intervalo){
      intervaloPulsos = intervalo;
      }

    String getAlarme(){
      return alarme;
      }
    
    void setAlarme(String novoAlarme){
      alarme = novoAlarme;
      }

    int getPino(){
      return pino;
      }
      
    void setPino(int numeroPino){
      pino = numeroPino;
      }  
};

// ------------------------- Fim da classe Valvula -------------------------

Valvula valvula1, valvula2, valvula3, valvula4, valvula5, valvula6, valvula7;

Valvula valvulas[] = {valvula1, valvula2, valvula3, valvula4, valvula5, valvula6, valvula7};
int pinosValvulas[] = {22, 24, 26, 28, 30, 32, 34};


String readLinha(File file){
  String linha;
  while (file.available()) {
      linha = file.readStringUntil('\n');
      break;
    }
  return linha;
}

void configuraValvulas(String configuracao, int posicao){
  
  String configSeparada[3];
  int inicio = 0;
  int fim ;
  String fatia;
  
  for (int i=0; i < 3; i++){ 

    fim = configuracao.indexOf(",",inicio);
    
    if (fim == -1){
      fatia = configuracao.substring(inicio);
    }else{
      fatia = configuracao.substring(inicio,fim);
      }
    configSeparada[i] = fatia;
    
    inicio = fim + 1; 
  }  
    
  int qtPulsos = configSeparada[1].toInt();
  valvulas[posicao].setQtPulsosTotal(qtPulsos);  

  int intervalo = configSeparada[2].toInt();
  valvulas[posicao].setIntervaloPulsos(intervalo);

  valvulas[posicao].setNumero(posicao+1);
  valvulas[posicao].setPino(pinosValvulas[posicao]);
} 


void atualizaConfigTodasValvulas(){

  configValvulas = SD.open("valvulas.csv");

  String configuracao;
    
  for (int i=0; i < 7; i++) {
    
    configuracao = readLinha(configValvulas);
    configuraValvulas(configuracao,i);
 }
 
 configValvulas.close();
}

void atualizaEstadosValvulas(String horaAtual){
  
  for (int i=0; i < 7; i++) {
        
    String alarme = valvulas[i].getAlarme();
    
    if (horaAtual == alarme){
      Serial.println("");
      Serial.println("");
      Serial.println("------------------------------------");
      Serial.print("Vamos ligar ou desligar a válvula: ");
      Serial.println(valvulas[i].getNumero());
          
      boolean estado = valvulas[i].getEstado();
      
      if (estado == true){
        
        valvulas[i].setEstado(false);
        int pino = valvulas[i].getPino();
        digitalWrite(pino, HIGH);
        Serial.println("Válvula Desligada!");
        
        int qtPulsosDados = valvulas[i].getQtPulsosDados() + 1;
        valvulas[i].setQtPulsosDados(qtPulsosDados);
        Serial.print("Foram dados: ");
        Serial.print(qtPulsosDados);
        Serial.println(" pulsos");
        
        int qtPulsosTotal = valvulas[i].getQtPulsosTotal();
        Serial.print("O total de pulsos para essa válvula é: ");
        Serial.println(qtPulsosTotal);
        
        if (qtPulsosDados == qtPulsosTotal){
          Serial.print("Essa válvula terminou de irrigar por hoje e só será ligada novamente às: ");
          valvulas[i].setQtPulsosDados(0);
          valvulas[i].setAlarme(inicioFuncionamento);
          Serial.println(valvulas[i].getAlarme());
          
          }else{

            int intervalo = valvulas[i].getIntervaloPulsos();
            
            String novoAlarme = somaHora(alarme, intervalo);
            
            valvulas[i].setAlarme(novoAlarme);
            Serial.print("Ela será ligada novamente às: ");
            Serial.println(novoAlarme);
            Serial.print("E agora são: ");
            Serial.println(horaAtual);
            
            
            
            }
            
        }else{

          valvulas[i].setEstado(true);
          int pino = valvulas[i].getPino();
          digitalWrite(pino, LOW);
          Serial.println("Válvula Ligada!");
          
          int tempoPulsoMinutos = tempoRegaDia / valvulas[i].getQtPulsosTotal();
          String novoAlarme = somaHora(alarme, tempoPulsoMinutos);
          valvulas[i].setAlarme(novoAlarme);                 
          Serial.print("Vai ficar ligada até: ");
          Serial.println(novoAlarme);
          Serial.print("E agora são: ");
          Serial.println(horaAtual);
          }
          
    }
  
  } 
}

String campoData(int campo){
  
  int retorno;

  switch (campo) {
    case DIA:
      retorno = myRTC.dayofmonth;
      break;
    case MES:
      retorno = myRTC.month;
      break;
    case ANO:
      retorno = myRTC.year;
      break;
    case HORA:
      retorno = myRTC.hours;
      break;
    case MINUTO:
      retorno = myRTC.minutes;
      break;
    case SEGUNDO:
      retorno = myRTC.seconds;
      break;
  }
  
  String retornoString = String(retorno); 
  if (retorno < 10){
    retornoString = "0" + retornoString;
    }         
  return retornoString;
}

String obterHorarioAtual(){
  
  myRTC.updateTime();

  String hora = campoData(HORA);
  String minuto = campoData(MINUTO);
  
  String horarioAtual = hora + ":" + minuto;
  return horarioAtual;
}

String somaHora(String horarioAtual, int minutos){

  int horaFinal = horarioAtual.substring(0,2).toInt();
  int minutoAtual = horarioAtual.substring(3).toInt();

  int minutosFinal = minutoAtual + minutos;

  if (minutosFinal > 59){
    
    horaFinal += 1;
    minutosFinal -= 60;    
    }

  String horaString = String(horaFinal);
  if (horaFinal < 10){
    horaString = "0" + horaString;
    }
  
  String minutoString = String(minutosFinal);
  if (minutosFinal < 10){
    minutoString = "0" + minutoString;
    }   
  
  return horaString + ":" + minutoString;
}


String coletaTempUmid(){
  
  float temperatura = dht.readTemperature();
  float umidade = dht.readHumidity();

  String temperaturaString;
  String umidadeString;
  
  if (isnan(temperatura) || isnan(umidade)) {
    temperaturaString = "erro";
    umidadeString = "erro";
  }else{
    temperaturaString = String(temperatura);
    umidadeString = String(umidade);
  }

  String dia = campoData(DIA); 
  String mes = campoData(MES);
  String ano = campoData(ANO);
  
  String hora = campoData(HORA);
  String minuto = campoData(MINUTO);
  String segundo = campoData(SEGUNDO);

  String retorno = dia + "," + mes + "," + ano + "," + hora + "," + minuto + "," + segundo + "," + temperaturaString + "," + umidadeString;

  return retorno;
}


boolean alarmeColeta(String horarioAtual){

  String horarioNovaColeta = somaHora(ultimaColeta,1);
  
  if (horarioAtual == horarioNovaColeta){
    return true;
  }else{
    return false;
  }
}


void escreveArquivo(String nomeArquivo, String texto){
 
 File dataFile = SD.open(nomeArquivo, FILE_WRITE);

 if (dataFile) {
    dataFile.print(texto);
    dataFile.close();
    Serial.print(texto);
  }
  else {
    Serial.println("error opening file");
  }
}

void enviaArquivoBluetooth(String nomeArquivo){
  
  File dataFile = SD.open(nomeArquivo);
  if (dataFile){
      while(dataFile.available()) {
        Serial1.write(dataFile.read());
      }
      dataFile.close();
    }else{
        Serial1.println("error opening file");
      }
}

void imprimeConfigValvulas(){
  
  for (int i=0; i < 7; i++) {
   Valvula valvula = valvulas[i];
   Serial.print("Valvula ");
   Serial.println(valvula.getNumero());
   Serial.print("Pulsos Total: ");
   Serial.println(valvula.getQtPulsosTotal());
   Serial.print("Intervalo entre pulsos: ");
   Serial.println(valvula.getIntervaloPulsos());
   Serial.println("------------------");  
  }
  
  Serial.println("");
  Serial.println("");
}


void setup() {
  
  for (int i=0; i < 7; i++){
    int pino = pinosValvulas[i];
    pinMode(pino, OUTPUT);
    digitalWrite(pino, HIGH);
  }
  
  Serial.begin(9600);
  Serial1.begin(9600);  
     
  Serial.print("Initializing SD card... ");

  // see if the card is present and can be initialized:
  if (!SD.begin(chipSelect)) {
    Serial.println("Card failed, or not present");
    // don't do anything more:
    return;
  }
  Serial.println("card initialized.");
 
  atualizaConfigTodasValvulas();

  imprimeConfigValvulas();
  
  dht.begin();

  String horarioAtual = obterHorarioAtual();
  String dados = coletaTempUmid();
  ultimaColeta = horarioAtual;

  Serial1.println("Testando bluetooth");

}

void loop() {

  String horarioAtual = obterHorarioAtual();
  
  atualizaEstadosValvulas(horarioAtual);

  boolean novaColeta = alarmeColeta(horarioAtual);
  
  if (novaColeta){  
    String dadosColetados = coletaTempUmid();
    ultimaColeta = horarioAtual;
    escreveArquivo("clima.csv", dadosColetados + "\n");
  }

  if (Serial1.available()){
    while(Serial1.available()){
      int codAscii = Serial1.read();
      comunicacao += char(codAscii);
      } 
    String pedaco = comunicacao.substring(0,5);
    
    if (pedaco.equals("clima")){
      enviaArquivoBluetooth("clima.csv");      
      }
    else{
      boolean remocao = SD.remove("valvulas.csv");
      escreveArquivo("valvulas.csv", comunicacao);
      atualizaConfigTodasValvulas();
      imprimeConfigValvulas();
      }
    comunicacao = "";
  }
  delay(2000);
}
