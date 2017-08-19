#include <virtuabotixRTC.h>

// #include "DHT.h"
#include <SPI.h>
#include <SD.h>

// myRTC(clock, data, rst)
virtuabotixRTC myRTC(31, 29, 27);

// #define DHTPIN 2     // what digital pin we're connected to
// #define DHTTYPE DHT22   // DHT 22  (AM2302), AM2321
// DHT dht(DHTPIN, DHTTYPE);


const int chipSelect = 53;
File clima; // Dia,Mes,Ano,Hora,Minuto,Segundo,Temperatura,Umidade
File configValvulas; // Valvula,Qt_pulsos,Intervalo

int tempoRega = 120; //tempo em segundos

// ------------------------- Inicio da classe Valvula -------------------------
class Valvula{
  private:
    int numero;
    boolean estado;
    int qtPulsosTotal;
    int qtPulsosDados;
    int intervaloPulsos;
    String alarme;
    
  public:
    Valvula(){}
    
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
};

// ------------------------- Fim da classe Valvula -------------------------

Valvula valvula1, valvula2, valvula3, valvula4, valvula5, valvula6, valvula7;

Valvula valvulas[] = {valvula1, valvula2, valvula3, valvula4, valvula5, valvula6, valvula7};


String readLinha(File file){
  String configuracao;
  while (file.available()) {
      configuracao = file.readStringUntil('\n');
      break;
    }
  return configuracao;
}

void configuraInicialValvula(String configuracaoInicial, int posicao){
  
  String configSeparada[3];
  int inicio = 0;
  int fim ;
  String fatia;
  
  for (int i=0; i < 3; i++){ 

    fim = configuracaoInicial.indexOf(",",inicio);
    
    if (fim == -1){
      fatia = configuracaoInicial.substring(inicio);
    }else{
      fatia = configuracaoInicial.substring(inicio,fim);
      }
    configSeparada[i] = fatia;
    
    inicio = fim + 1; 
  }  

  int qtPulsos = configSeparada[1].toInt();
  valvulas[posicao].setQtPulsosTotal(qtPulsos);  

  int intervalo = configSeparada[2].toInt();
  valvulas[posicao].setIntervaloPulsos(intervalo);
  
} 


void atualizaConfigTodasValvulas(){

 configValvulas = SD.open("valvulas.csv");

  String configuracao;
    
  for (int i=0; i < 7; i++) {
    
    configuracao = readLinha(configValvulas);
    configuraInicialValvula(configuracao,i);
 }
 
 configValvulas.close();
}

void setup() {
  // Serial1.begin(9600);
  Serial.begin(9600);

  //Serial.println(teste.getNumero());
  //Serial1.println("DHTxx test!");

  
  Serial.print("Initializing SD card...");

  // see if the card is present and can be initialized:
  if (!SD.begin(chipSelect)) {
    Serial.println("Card failed, or not present");
    // don't do anything more:
    return;
  }
  Serial.println("card initialized.");


  //Informacoes iniciais de data e hora
  //Apos setar as informacoes, comente a linha abaixo
  //(segundos, minutos, hora, dia da semana, dia do mes, mes, ano)
  //myRTC.setDS1302Time(00, 28, 21, 5, 17, 8, 2017);
  
  atualizaConfigTodasValvulas();

  for (int i=0; i < 7; i++) {
   Serial.print("Valvula ");
   Serial.println(i+1);
   Valvula valvula = valvulas[i];
   Serial.print("Pulsos Total: ");
   Serial.println(valvula.getQtPulsosTotal());
   Serial.print("Intervalo entre pulsos: ");
   Serial.println(valvula.getIntervaloPulsos()); 
   Serial.println("------------------");
   
  }
   
}

void loop() {
  
  // Reading temperature or humidity takes about 250 milliseconds!
  // Sensor readings may also be up to 2 seconds 'old' (its a very slow sensor)
  /*float h = dht.readHumidity();
  // Read temperature as Celsius (the default)
  float t = dht.readTemperature();

  // Le as informacoes do CI
  myRTC.updateTime(); 

  
   if (isnan(h) || isnan(t)) {
    Serial1.println("Failed to read from DHT sensor!");
    return;
  }
   String dataString = "";
   String hora = "";
   String minutos = "";
   String segundos = "";
   
    if (myRTC.hours < 10)
  {
    hora = "0" + (String)myRTC.hours;
  }else{hora =(String)myRTC.hours;}

  if (myRTC.minutes < 10){
    minutos = 0 + (String)myRTC.minutes;
    }else{minutos =(String)myRTC.minutes;}

  if (myRTC.seconds < 10){
    segundos = "0"+ (String)myRTC.seconds;
    }else{segundos = (String)myRTC.seconds;}
   
  //Informacoes Humidade e temperatura 
  dataString = "H: "+(String)h+ "% "+ "T: "+(String)t+"*C "+(String)myRTC.dayofmonth + "/"+ (String)myRTC.month + "/"+ (String)myRTC.year +"  "+"Hora: "+ hora + ":" + minutos+":"+ segundos;
 //Serial1.println(dataString);
    
   // open the file. note that only one file can be open at a time,
  // so you have to close this one before opening another.
  File dataFile = SD.open("datalog.txt", FILE_WRITE);

  // if the file is available, write to it:
  if (dataFile) {
    dataFile.println(dataString);
    dataFile.close();
    // print to the serial port too:
    Serial.println(dataString);
  }
  // if the file isn't open, pop up an error:
  else {
    Serial.println("error opening datalog.txt");
  }
  delay(5000);
*/
}
