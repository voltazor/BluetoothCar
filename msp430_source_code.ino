#include "msp430g2553.h"
 
void setup() {
   Serial.begin(9600);
   pinMode(GREEN_LED, OUTPUT); 
   digitalWrite(GREEN_LED, LOW);
   pinMode(RED_LED, OUTPUT); 
   digitalWrite(RED_LED, LOW);
   
   pinMode(P1_7, OUTPUT);
   pinMode(P2_5, OUTPUT);
   pinMode(P2_4, OUTPUT);
   pinMode(P2_3, OUTPUT);
   digitalWrite(P1_7, HIGH);
   digitalWrite(P2_5, HIGH);
   digitalWrite(P2_4, HIGH);
   digitalWrite(P2_3, HIGH);
}

void loop() {
  readState();
  
  delay(15);
}

void readState() {
  if (Serial.available() > 0) {
    byte b = (byte) Serial.read(); 
    switch (b) {
      case 0b00000000:
        pause();
        break;
      case 0b00000011:
        forward();
        break; 
      case 0b00001100:
        left();
        break; 
      case 0b00110000:
        right();
        break; 
      case 0b11000000:
        backward();
        break;   
    }
  }  
}

void pause() {
  Serial.println("Pause"); 
  lightRed(false);
  lightGreen(false);
  leftWheelStop();
  rightWheelStop();  
}

void forward() {
  Serial.println("Forward"); 
  lightRed(false);
  lightGreen(true);
  leftWheelForward();
  rightWheelForward();
}

void backward() {
  Serial.println("Backward");  
  lightRed(true);
  lightGreen(false);
  leftWheelBack();
  rightWheelBack();
}

void left() {
  Serial.println("Left");  
  leftWheelBack();
  rightWheelForward();
  blinkGreen();
}

void right() {
  Serial.println("Right");  
  leftWheelForward();
  rightWheelBack();
  blinkRed();
}

//Left wheel control
void leftWheelForward() {
  digitalWrite(P1_7, HIGH);
  digitalWrite(P2_5, LOW);
}

void leftWheelBack() { 
  digitalWrite(P2_5, HIGH);
  digitalWrite(P1_7, LOW);
}

void leftWheelStop() {
  digitalWrite(P2_5, HIGH);
  digitalWrite(P1_7, HIGH);
}

//Right wheel control
void rightWheelForward() {
  digitalWrite(P2_4, HIGH);
  digitalWrite(P2_3, LOW);
}

void rightWheelBack() {
  digitalWrite(P2_3, HIGH);
  digitalWrite(P2_4, LOW);  
}

void rightWheelStop() {
  digitalWrite(P2_3, HIGH);
  digitalWrite(P2_4, HIGH);  
}

void blinkGreen() {
  digitalWrite(GREEN_LED, HIGH);  
  delay(100);
  digitalWrite(GREEN_LED, LOW); 
  delay(100);
}

void lightGreen(boolean light) {
  if (light) {
    lightRed(false);
    digitalWrite(GREEN_LED, HIGH);
  } else {
    digitalWrite(GREEN_LED, LOW);  
  }  
}

void lightRed(boolean light) {
  if (light) {
    lightGreen(false);
    digitalWrite(RED_LED, HIGH);
  } else {
    digitalWrite(RED_LED, LOW);  
  }  
}

void blinkRed() {
  digitalWrite(RED_LED, HIGH);  
  delay(100);
  digitalWrite(RED_LED, LOW); 
  delay(100);
}

