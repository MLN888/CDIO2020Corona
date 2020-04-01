
####################################################
#          Test for tennisball collectror          #
#                                                  #
# version 0.2                                      #
# description: A test file for finding cards in    #
#              openCV 2                            #
#												   #
# auther:       Phillip Bomholtz                   #
# created:      18-03-2020						   #
# last updated: 25-03-2020						   #
#												   #
####################################################


import cv2
import numpy as np 
import os

def draw_contour(img,contours):
     #reletevistic characteristics for coordinate estimation. Subject to change!
     for contour in contours:
         (x,y,w,h) = cv2.boundingRect(contour)
         if w > 10 and h > 10:
            cv2.rectangle(img, (x,y), (x+w,y+h), (0,255,0), 2) #draw field box

            cv2.circle(img, (x, y), 1, (255, 0, 0), 2)  #draw top left
            cv2.circle(img, (x+w, y), 1, (255, 0, 0), 2) #draw top right
            cv2.circle(img, (x, y+h), 1, (255, 0, 0), 2) #draw bottom left
            cv2.circle(img, (x+w, y+h), 1, (255, 0, 0), 2) #draw bottom right


#find counters with img input, HVS lower and HVS upper
def find_contour(img,H_lower,S_lower,V_lower,H_upper,S_upper,V_upper,str):
     hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)   #converte rgb to hvs
     lower = np.array([H_lower,S_lower,V_lower])              #set lover accept boundury
     upper = np.array([H_upper,S_upper,V_upper])            #set higher accept boundury
     mask = cv2.inRange(hsv, lower, upper)    #make mask of boundurys
     res = cv2.bitwise_and(img,img, mask= mask) #get only desired color

     rgb = cv2.cvtColor(res, cv2.COLOR_HSV2BGR)
     grayed = cv2.cvtColor(rgb, cv2.COLOR_BGR2GRAY)

     contours,_ = cv2.findContours(grayed, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_NONE)
     draw_contour(img,contours)
     cv2.imshow(str,res)

def find_black(img):
    for i in range(img.shape[0]):
        for u in range(img.shape[1]):
            if  img[i,u,0] < 30 and img[i,u,0] > 10 and img[i,u,1] < 30 and img[i,u,1] > 10 and img[i,u,2] < 30 and img[i,u,2] > 10 :
                print(i,u,img[i,u,0])
                #set to green
                img.itemset((i,u,0),20)
                img.itemset((i,u,1),255)
                img.itemset((i,u,2),52)

    


cap = cv2.VideoCapture(1);  #setup video capture. might need to change to 0 depenting on own setup

#set resolution to 1920x1080
cap.set(cv2.CAP_PROP_FRAME_WIDTH,1920);
cap.set(cv2.CAP_PROP_FRAME_HEIGHT,1080);


while(True):
    ret, frame = cap.read() #read from camera and store. not currently used but can substitute img in code

    #read from test image file
    file_path = os.path.abspath("Test_files\\test_img_Mads.png") 
    img = cv2.imread(file_path,1)

    find_black(img)

    #if on absolute path aka errorcheck
    if not img is None or img.size != 0:
    
       
        

        find_contour(img,0,0,160,255,80,231,'white') #find and draw red
        find_contour(img,0,110,150,20,255,255,'red') #find and draw red

        cv2.imshow('card',img)
        
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break
        continue
    else:
        print("critical error: no image found.\n since python is kind of shit, I would recomend checking the path to the image file.")
        exit()

cap.release()
cv2.destroyAllWindows()

