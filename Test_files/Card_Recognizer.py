
####################################################
#          Test for tennisball collectror          #
#                                                  #
# version 0.0                                      #
# description: A test file for finding cards in    #
#              openCV 2                            #
#												   #
# auther:       Phillip Bomholtz                   #
# created:      18-03-2020						   #
# last updated: 18-03-2020						   #
#												   #
####################################################


import cv2
import numpy as np 
import os

cap = cv2.VideoCapture(1);  #setup video capture. might need to change to 0 depenting on own setup

#set resolution to 1920x1080
cap.set(cv2.CAP_PROP_FRAME_WIDTH,1920);
cap.set(cv2.CAP_PROP_FRAME_HEIGHT,1080);


while(True):
    ret, frame = cap.read() #read from camera and store


    cv2.imshow('Webcam',frame)
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break
    continue

cap.release()
cv2.destroyAllWindows()