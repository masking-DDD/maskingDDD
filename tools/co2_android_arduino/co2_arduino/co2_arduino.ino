
//이걸로 성공
#include <MHZ19.h>
#include <MHZ19PWM.h>

#include <Arduino.h>
                                      
#include <SoftwareSerial.h>                                
#define BT_RXD 8
#define BT_TXD 7

#define RX_PIN 2                                          
#define TX_PIN 3                                          
#define BAUDRATE 9600                                      
                                          

SoftwareSerial bluetooth(BT_RXD, BT_TXD);
SoftwareSerial mySerial(RX_PIN, TX_PIN);                   
                            
MHZ19 myMHZ19(&mySerial);  

unsigned long getDataTimer = 0;

void setup()
{
    Serial.begin(9600);           
    Serial.println(F("Starting..."));
    bluetooth.begin(9600);
    mySerial.begin(BAUDRATE);                              
    myMHZ19.setAutoCalibration(true);                              
}

void loop()
{
    if (millis() - getDataTimer >= 2000)
    {
        int CO2; 
        MHZ19_RESULT response = myMHZ19.retrieveData();
        
        if (response == MHZ19_RESULT_OK){
        CO2 = myMHZ19.getCO2();                            
          if (Serial.available()) {
              bluetooth.write(Serial.read());
          } 
        bluetooth.println(CO2);                 
        Serial.println(CO2);                                
                                 
        }
        else{
          Serial.println(response);
        }
         
        getDataTimer = millis();
        
    }
}
