
####################################################
#          Test for tennisball collectror          #
#                                                  #
# version 0.1                                      #
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
    ret, frame = cap.read() #read from camera and store. not currently used but can substitute img in code

    #read from test image file
    file_path = os.path.abspath("Test_files\\test_card.jpg") 
    img = cv2.imread(file_path,1)

    #if on absolute path aka errorcheck
    if not img is None or img.size == 0:
    
        hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)   #converte rgb to hvs
        lower = np.array([0,100,100])              #set lover accept boundury
        upper = np.array([255,255,255])            #set higher accept boundury
        mask = cv2.inRange(hsv, lower, upper)    #make mask of boundurys
        res = cv2.bitwise_and(img,img, mask= mask) #get only desired color

        rgb = cv2.cvtColor(res, cv2.COLOR_HSV2BGR)
        grayed = cv2.cvtColor(rgb, cv2.COLOR_BGR2GRAY)

        contours,_ = cv2.findContours(grayed, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_NONE)

        #reletevistic characteristics for coordinate estimation. Subject to change!
        x_rel = 0 
        y_rel = 0
        for contour in contours:
            (x,y,w,h) = cv2.boundingRect(contour)
            start_point_x = x+(w*x_rel)
            start_point_y = y+(h*y_rel)
            end_point_x = start_point_x + (w-(w*x_rel)*2)
            end_point_y = start_point_y + (h-(h*y_rel)*2)
            cv2.rectangle(img, (int(start_point_x),int(start_point_y)), (int(end_point_x),int(end_point_y)), (0,255,0), 2) #draw field box

            cv2.circle(img, (int(start_point_x), int(start_point_y)), 1, (255, 0, 0), 2)  #draw top left
            cv2.circle(img, (int(start_point_x + (w-(w*x_rel)*2)), int(start_point_y)), 1, (255, 0, 0), 2) #draw top right
            cv2.circle(img, (int(start_point_x), int(start_point_y + (h-(h*y_rel)*2))), 1, (255, 0, 0), 2) #draw bottom left
            cv2.circle(img, (int(start_point_x + (w-(w*x_rel)*2)), int(start_point_y+(h-(h*y_rel)*2))), 1, (255, 0, 0), 2) #draw bottom right

        cv2.imshow('card',img)
        cv2.imshow('card mask',res)
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break
        continue
    else:
        print("critical error: no image found.\n since python is kind of shit, I would recomend checking the path to the image file.")
        exit()

cap.release()
cv2.destroyAllWindows()