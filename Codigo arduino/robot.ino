const int pinMotor1A = 7;
const int pinMotor1B = 6;
const int pinMotor2A = 5;
const int pinMotor2B = 4;
int disparadorI = 8;   // triger
int entradaI = 9;      // echo
int disparadorD = 10;   // triger
int entradaD = 11;      // echo
long tiempo=0;
long tiempoI;
float distanciaI;
long tiempoD;
float distanciaD;
String valor="";
String separador=";";
String D="D";
String I="I";
String inicio="-";
String igual="=";
String respuesta="";
void setup() {
  
  Serial.begin(9600);
  
  pinMode(pinMotor1A, OUTPUT);
  pinMode(pinMotor1B, OUTPUT);
  pinMode(pinMotor2A, OUTPUT);
  pinMode(pinMotor2B, OUTPUT);
  pinMode(disparadorI, OUTPUT);
  pinMode(entradaI, INPUT);
  pinMode(disparadorD, OUTPUT);
  pinMode(entradaD, INPUT);

  digitalWrite(pinMotor1A, LOW); 
  digitalWrite(pinMotor1B, LOW); 
  digitalWrite(pinMotor2A, LOW); 
  digitalWrite(pinMotor2B, LOW); 
}

void loop() {
long valorD=0;
long valorI=0;
long duration, distance;
digitalWrite(disparadorD, LOW); 
//delayMicroseconds(15); 
digitalWrite(disparadorD, HIGH);
//delayMicroseconds(15); 
digitalWrite(disparadorD, LOW);

duration = pulseIn(entradaD, HIGH);
distance = (duration/2) / 29.1;

  if (distance >= 2000 || distance <= 1){
   // Serial.println("Fuera de Rango para Derecha");
   
  }
  else {
    valorD=distance;
    if(valorD > 60){
       valorD=60;
      }
  }

long duracion, distancia;
digitalWrite(disparadorI, LOW); 
//delayMicroseconds(15);
digitalWrite(disparadorI, HIGH);
//delayMicroseconds(15); 
digitalWrite(disparadorI, LOW);

duracion = pulseIn(entradaI,HIGH);
distancia =(duracion/2)/29.1;
 
  if (distancia >=2000|| distancia <= 1){
    //Serial.println("Fuera de rango para Izquierda");
  }
  else {
    valorI=distancia;
    if(valorI > 60){
          valorI = 60;
      }
  }
       if(valorD == 0){
       valorD=1;
      }
             if(valorI == 0){
       valorI=1;
      }
    respuesta = inicio + D + igual + valorD + separador + I + igual + + valorI;
    Serial.println(respuesta);
 delay(1000);

  if (Serial.available() > 0) {
   // Serial.println("obtubo dato por serial");
    delay(20);
    String bufferString = "";
    while (Serial.available() > 0) {
      bufferString += (char)Serial.read();
     // Serial.println(bufferString);
    }
    int num = bufferString.toInt();
    if (num > 0 && num <= 79){

     // Serial.println("Dato para el motor 1 Derecha");
     if(num > 0 && num <=10){
        tiempo = 120;
      }
      if(num > 10 && num <=20){
        tiempo = 110;
      }
      if(num > 20 && num <=30){
        tiempo = 100;
      }
         if(num > 30 && num <=40){
        tiempo = 100;
      }
       if(num > 40 && num <=50){
        tiempo = 100;
      }
       if(num > 50 && num <=60){
        tiempo = 100;
      }
            if(num > 60 && num <=70){
        tiempo = 90;
      }
       if(num > 70 && num <=79){
        tiempo = 80;
      }
      digitalWrite(pinMotor1A, HIGH); 
      digitalWrite(pinMotor1B, LOW); 
      digitalWrite(pinMotor2A, HIGH); 
      digitalWrite(pinMotor2B, LOW);
      delay(tiempo);
      digitalWrite(pinMotor1A, LOW); 
      digitalWrite(pinMotor1B, LOW); 
      digitalWrite(pinMotor2A, LOW); 
      digitalWrite(pinMotor2B, LOW);
      num=0;
      tiempo=0;
      
    }
    if (num > 100 && num <= 180){
       
       if(num > 100 && num <=110){
        tiempo = 90;
      }
       if(num > 110 && num <=120){
        tiempo = 90;
      }
      if(num > 120 && num <=130){
        tiempo = 100;
      }
       if(num > 130 && num <=140){
        tiempo = 100;
      }
      if(num > 140 && num <=150){
        tiempo = 100;
      }
     if(num > 150 && num <=160){
        tiempo = 100;
      }
      if(num > 160 && num <=170){
        tiempo = 110;
      }
      if(num > 170 && num <=180){
        tiempo = 120;
      }
      digitalWrite(pinMotor1A, LOW); 
      digitalWrite(pinMotor1B, HIGH); 
      digitalWrite(pinMotor2A, LOW); 
      digitalWrite(pinMotor2B, HIGH); 
      delay(tiempo);
      digitalWrite(pinMotor1A, LOW); 
      digitalWrite(pinMotor1B, LOW); 
      digitalWrite(pinMotor2A, LOW); 
      digitalWrite(pinMotor2B, LOW);
      num=0;
      tiempo=0;
      
    }
    if(num >= 80 && num <= 100){
        if(num >= 80 && num <= 85){
        tiempo = 100;
      }
      if(num > 85 && num <= 90){
        tiempo = 200;
      }
      if(num > 90 && num <=95){
        tiempo = 300;
      }
      if(num > 95 && num <=100){
        tiempo = 400;
      }
      digitalWrite(pinMotor1A, LOW); 
      digitalWrite(pinMotor1B, HIGH); 
      digitalWrite(pinMotor2A, HIGH); 
      digitalWrite(pinMotor2B, LOW); 
      delay(tiempo);
      digitalWrite(pinMotor1A, LOW); 
      digitalWrite(pinMotor1B, LOW); 
      digitalWrite(pinMotor2A, LOW); 
      digitalWrite(pinMotor2B, LOW);
      }
  }
}