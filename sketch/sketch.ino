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

int tempoRegaDia = 5; //tempo em minutos

String inicioFuncionamento = "18:01"; // HH:MM



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

  valvulas[posicao].setNumero(posicao+1);
  valvulas[posicao].setPino(pinosValvulas[posicao]);
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


String obterHoraAtual(){
  
  myRTC.updateTime();
  
  int hora = myRTC.hours;
  String horaString = String(hora);
  if (hora < 10){
    horaString = "0" + horaString;
    }
    
  int minuto = myRTC.minutes;
  String minutoString = String(minuto);
  if (minuto < 10){
    minutoString = "0" + minutoString;
    }

  String horaAtual = horaString + ":" + minutoString;
  return horaAtual;
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



void setup() {
  
  for (int i=0; i < 7; i++){
    int pino = pinosValvulas[i];
    pinMode(pino, OUTPUT);
    digitalWrite(pino, HIGH);
  }
  
  Serial.begin(9600);

  // Serial1.begin(9600);
    
  Serial.print("Initializing SD card... ");

  // see if the card is present and can be initialized:
  if (!SD.begin(chipSelect)) {
    Serial.println("Card failed, or not present");
    // don't do anything more:
    return;
  }
  Serial.println("card initialized.");
 
  atualizaConfigTodasValvulas();

  for (int i=0; i < 7; i++) {
   Valvula valvula = valvulas[i];
   Serial.print("Valvula ");
   Serial.println(valvula.getNumero());
   Serial.print("Pulsos Total: ");
   Serial.println(valvula.getQtPulsosTotal());
   Serial.print("Intervalo entre pulsos: ");
   Serial.println(valvula.getIntervaloPulsos());
   Serial.print("Controla o pino: ");
   Serial.println(valvula.getPino()); 
   Serial.println("------------------");  
  }
  
  Serial.println("");
  Serial.println("");
}

void loop() {
  
  String horaAtual = obterHoraAtual();

  atualizaEstadosValvulas(horaAtual);
   
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
