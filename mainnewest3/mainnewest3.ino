#include "DualVNH5019MotorShield.h"
#include "PinChangeInt.h"

#include <Filters.h>
/*//////////////////
  changelog
  Accumulated error, in go Function
  sensor interrupt drive
*/
long _start;
bool calib1 = false;
DualVNH5019MotorShield md;
//const byte ledPin = 13;
const byte interruptPin = 5;
const byte interruptPin2 = 3;
//volatile byte state = LOW;
volatile int _left = 0, _right = 0;

int lSpeed, rSpeed;
bool isGoing = false, isRotating = false;
float sensorValue[6];
unsigned int tickRequire;

bool l_Dir = true, r_Dir = true; //true : Forward False:Reverse

float filterFrequency = 1.0 ;     //propotional to delay in loop
// create a one pole (RC) lowpass filter
FilterOnePole LPF0( LOWPASS, filterFrequency );
FilterOnePole LPF1( LOWPASS, filterFrequency );
FilterOnePole LPF2( LOWPASS, filterFrequency );
FilterOnePole LPF3( LOWPASS, filterFrequency );
FilterOnePole LPF4( LOWPASS, filterFrequency );
FilterOnePole LPF5( LOWPASS, filterFrequency );


boolean fastestPath = false;

int roundDistance(float dist)      //Rounding distance to 10cm resolution,take float return int*,true if round success
{
  int temp = round(dist / 10) * 10;
  return temp;
  //if(temp>=30 && abs(temp - dist) < 3)
  // return max(0,temp);
  // else   return max(0,temp-10);
}
void left() {
  _left++;

}
void Right()
{
  _right++;


}
volatile int rawData = 0;
volatile boolean clearData = true;
ISR (ADC_vect)
{
  rawData = ADC;

  clearData = false;

}  // end of ADC_vect


float sensorValue2[3];
void pid()
{
  int off1, off2;

  float Kp = 20.1; //4.55 7.5 6.7 8.1 9 8.1     8.6

  long count = 0;

  count = 0;

  while (1) {

    // isBlocking = false;
    //isGoing = true;*/
    off1 = l_Dir ?max(0, ( _right - _left) * Kp): max(0,
           (_left - _right) * Kp); //M1 :Left M2 :Right
    off2 = r_Dir ? max(0, ( _left - _right) * Kp) :  max(0, ( _right - _left) * Kp);

    //off1 = max(0, ( _right - _left) * Kp);
    //off2 = max(0, ( _left - _right) * Kp);
    md.setM1Speed(lSpeed + off1);
    md.setM2Speed(rSpeed + off2);
    
    if (_right >= tickRequire) {
      md.setM2Brake(400);
    }
    if (_left >= tickRequire) {

      md.setM1Brake(400);
    }
          if (_right >= tickRequire && _left >= tickRequire){ md.setM2Brake(400); md.setM1Brake(400);break;}

  }
//Serial.println(String(_left)+ "   "+String(_right));
}
float getDistance()
{
  return _left / 59.5 / 2;
}

void go(int speed, bool dir)     // true: back false :front
{
  tickRequire = tickRequire == 0 ? 65535 : tickRequire;
  isGoing = true;
  if (!dir)
  {
    lSpeed = rSpeed = 1;
    l_Dir = true;
    r_Dir = true;
  }
  else
  {
    lSpeed = rSpeed = -1;
    l_Dir = false;
    r_Dir = false;
  }
  _right = _left = 0;;
  // if(_left>=_right){_left-=_right;_right=0;}
  //if(_left<_right){_right-=_left;_left=0;}

  lSpeed *= speed;
  rSpeed *= speed;

  //pidThread.enabled = true;
  md.setM1Speed(lSpeed);
  md.setM2Speed(rSpeed);
  pid();
  //ADCSRA = 0;

}
float pre_sensor[6] = { -1, -1, -1, -1, -1, -1};

void goDistance(int speed, float cm, bool dir)
{

  //  cm/ 19.25*1124.5  60.5
  /* if (!fastestPath)
    {
     float diff = sensorValue[4] - 10.69;
     if (abs(diff) < 3 && abs(diff) > 0.3)turn(70, -0.8 * diff);
     else
     { diff = sensorValue[4] - 20.6;
       if (abs(diff) < 3 && abs(diff) > 0.3)turn(70, -0.8 * diff);
     }
     //if (abs(sensorValue[2] - 10.69) < 0.5 && abs(sensorValue[3] - 10.69) < 0.5)
     // calibrate();
    }*/
  delay(10);
  tickRequire = 59.5 * 2 * cm;
  go(speed, dir);

}

void stops(int delay)
{
  isGoing = false;
  isRotating = false;
  //  pidThread.enabled = false;
  lSpeed = rSpeed = 0;
  md.setM1Brake(delay);
  md.setM2Brake(delay);
  tickRequire = 0;

}


inline double analogtocm(int sensorValue)
{ double a1 = -0.0000016190246302912996, a2 = 0.0016975737052890034, a3 = -0.63710109740759124, a4 = 92.981861986315536;
  return (((a1 * sensorValue) + a2) * sensorValue + a3) * sensorValue + a4;
}


double analogtocm2(int sensorValue)
{ double a12 = 0.00014939473920335357, a22 = -0.22442528247679530, a32 = 85.53814183123405;// a42 = 63.705099991601685;
  return ((a12 * sensorValue) + a22) * sensorValue + a32;
}

float offset[6] = { -6.1, -7.1, -5.5, -4., -6.6, -3.3};



void getSensor(bool calibrate = false)
{ 
    float ave_sensor[6] = { 0, 0, 0, 0, 0, 0};
    for(int y=0;y<10;y++)
    {
      
      sensorValue[0]=analogtocm(LPF0.input(analogRead(A0)));
      ave_sensor[0]+=sensorValue[0];
    }
    for(int y=0;y<10;y++)
    {
      sensorValue[1]=analogtocm(LPF1.input(analogRead(A1)));
      ave_sensor[1]+=sensorValue[1];
    }
    for(int y=0;y<10;y++)
    {
       sensorValue[2]=analogtocm(LPF2.input(analogRead(A2)));
       ave_sensor[2]+=sensorValue[2];
    }
    for(int y=0;y<10;y++)
    {
       sensorValue[3]=analogtocm(LPF3.input(analogRead(A3)));
       ave_sensor[3]+=sensorValue[3];
    }
    for(int y=0;y<10;y++)
    {
      sensorValue[4]=analogtocm(LPF4.input(analogRead(A4))); 
      ave_sensor[4]+=sensorValue[4];
    }
    for(int y=0;y<10;y++)
    {
       sensorValue[5]=analogtocm2(LPF5.input(analogRead(A5)));
       ave_sensor[5]+=sensorValue[5];
    }
     for(int y=0;y<6;y++) sensorValue[y]=max(ave_sensor[y]/10+offset[y],0);
    

}


void turn(int speed, float angle)
{ isRotating = true;
  if (angle > 0) {
    l_Dir = true;
    lSpeed = 1;
    r_Dir = false;
    rSpeed = -1;
  }
  else if (angle < 0) {
    r_Dir = true;
    rSpeed = 1;
    l_Dir = false;
    lSpeed = -1;
  }
  _left = _right = 0;
  isGoing = true;
  lSpeed *= speed; rSpeed *= speed;
  tickRequire = 8.70 * 2 * abs(angle); //2pi*19*560.25 /19.5/360   8.65
  //pidThread.enabled = true;

  md.setM1Speed(lSpeed);
  md.setM2Speed(rSpeed);
  pid();
  _left = _right = 0;
  // pre_sensor[0] = -1;

}
String output;

void temp()
{ output = "";
  int temp_[6];
  getSensor();
  //  int temp_[5];

  for (int k = 0; k < 6; k++) {
    temp_[k] = roundDistance(sensorValue[k]) / 10 ;
    //  if (temp_[k] == -1)
    max( temp_[k], 0);
  } output += "p";
  output += temp_[0];
  output += "@";
  output += temp_[1];
  output += "@";
  output += temp_[3];
  output += "@";
  output += temp_[2];
  output += "@";
  output += temp_[5];
  output += "@";
  output += temp_[4];
  delay(1);
  Serial.println(output);

}


float k2 = +0.3, k3, k4;

void reposition(float accuracy)
{
  // set distance
  int timeout = 0;
  while (1) {
    timeout++;
    if (timeout > 8)break;
    getSensor(true);
    // set degree

    //if (sensorValue[2] == 0 || sensorValue[0] == 0)return;
    float diff = (sensorValue[3] - k2 - sensorValue[0]) / 2;


    // Serial.println(diff);
    // Serial.print(k2);
    if (abs(diff) > 10)break;
   // Serial.println(diff);
    if (abs(diff) > accuracy)
    {
      turn(90, diff);
       //Serial.println("b");
    }
    else
      break;
  }

}


//====================== jerome added code ================//
//   changed: float _diff = (sensorValue[0] + sensorValue[2]) / 2 - 9.4;
//   to     : float _diff = (sensorValue[0] + sensorValue[2]) / 2 - 9.4;

bool realign()
{ getSensor(true);
  float _diff = (sensorValue[3] + sensorValue[0]) / 2 -0.3;
//Serial.println((String)sensorValue[3]+ "  "+(String)sensorValue[0]);
  float pdist = abs(_diff);
  if (pdist > 0.3)
  {

    if (_diff < 0)
    {
      goDistance(60, pdist, true);

    }

    else
    {
      goDistance(60, pdist, false);


    }

  }
}
float k5= +0.15;
void reposition_left(float accuracy)
{
  // set distance
  int timeout = 0;
  while (1) {
    timeout++;
    if (timeout > 8)break;
    getSensor(true);
    // set degree

    if (sensorValue[4] > 12|| sensorValue[2] >12) return;
    float diff = (sensorValue[4] - k5 - sensorValue[2]) / 2;


    // Serial.println(diff);
    // Serial.print(k2);
    if (abs(diff) > 10)break;
    //Serial.println(diff);
    if (abs(diff) > accuracy)
    {
      turn(90, diff);
       //Serial.println("b");
    }
    else
      break;
  }

}


//====================== jerome added code ================//
//   changed: float _diff = (sensorValue[0] + sensorValue[2]) / 2 - 9.4;
//   to     : float _diff = (sensorValue[0] + sensorValue[2]) / 2 - 9.4;
/*
bool realign_left()
{ getSensor(true);
  float _diff = (sensorValue[4] + sensorValue[2]) / 2 -0.5;
//Serial.println((String)sensorValue[3]+ "  "+(String)sensorValue[0]);
  float pdist = abs(_diff);
  if (pdist > 0.3)
  {

    if (_diff < 0)
    {
       

    }

    else
    {
      


    }

  }
}
*/
void setup() {
  //pinMode(ledPin, OUTPUT);
  // analogReference(EXTERNAL);
  pinMode(interruptPin, INPUT_PULLUP);
  pinMode(interruptPin2, INPUT_PULLUP);
  Serial.begin(115200);
  md.init();
  //  pidThread.onRun(pid);
  //pidThread.setInterval(0);
  //sensorThread.onRun(getSensor);
  //sensorThread.setInterval(1);
  //mazeThread.onRun(updateMaze);
  //mazeThread.setInterval(1);
  //controller.add(&pidThread);
  // controller.add(&sensorThread);
  //controller.add(&mazeThread);
  PCintPort::attachInterrupt(3, &left, CHANGE);
  PCintPort::attachInterrupt(5, &left, CHANGE);
  PCintPort::attachInterrupt(13, &Right, CHANGE);
  PCintPort::attachInterrupt(11, &Right, CHANGE);
  /* #if FASTADC
    // set prescale to 32
    sbi(ADCSRA,ADPS2) ;
    cbi(ADCSRA,ADPS1) ;
    sbi(ADCSRA,ADPS0) ;
    #endif
    ADCSRB &= B11111000;*/
  //mazeThread.enabled = true;
  // pidThread.enabled = true;
  //sensorThread.enabled = true;
  //controller.run();
  //curr_position.x = 5;
  //curr_position.y = 1;
  //go(200);
}

void calibrate()
{ pre_sensor[0] = -1;
  reposition(0.2);

  realign();
  reposition(0.1);
  realign();

}


/////// turn(speed,int) -> turn(speed,float)///////////////

void loop() {

  static bool reply = false;
  char c[2];
  c[1] = '\n';
  if (Serial.available())
 {
    char k = Serial.read();
    
    switch (k)
    {
      case 'M':  Serial.println("pOK"); reposition_left(0.2);goDistance(300, 9.15, false); delay(100);break;
      //==================================== jerome added code =========================//

      //case 'M': goDistance(150, 9.300, false); reply = true; break;
      //==================================== jerome added code =========================//

      case 'L': Serial.println("pOK"); turn(250, -89.5);  delay(100); break;
      case 'R': Serial.println("pOK"); turn(250, 87.3);   delay(100);break;
      case 'B': Serial.println("pOK"); turn(250, -180.5); delay(20); break;
      case 'S':  delay(200); Serial.flush(); temp(); break;
      case 'C': //goDistance(100, 1.8, true);
      
        Serial.println("pOK"); calibrate(); delay(40); 
        break;
      
     
      
      case 'T':Serial.println("pOK"); reposition_left(0.2); delay(40); break;
      // case 'D': reply = true; calib1 = true; func = 1;  break;
      //case 'E': reply = true; calib1 = true; func = 2;  break;
      case 'P': _left = _right = 0; pre_sensor[0] = -1; Serial.println("pOK"); delay(60); fastestPath = true; while (!Serial.available()); c[0] = Serial.read(); int grid = atoi(c); goDistance(300, grid * 9.9 - 1.5, false);  fastestPath = false; delay(70); break;

    }


  }
 

}
