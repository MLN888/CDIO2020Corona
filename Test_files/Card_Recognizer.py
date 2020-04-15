
####################################################
#          Test for tennisball collectror          #
#                                                  #
# version 0.3                                      #
# description: A test file for finding cards in    #
#              openCV 2                            #
#												   #
# auther:       Phillip Bomholtz                   #
# created:      18-03-2020						   #
# last updated: 15-04-2020						   #
#												   #
####################################################


import cv2
import numpy as np 
import os
import time

######################################
#                                    #
# simple card data structure         #
#                                    #
######################################
class Card:

    def __init__(self,x1,y1,x2,y2,type,num):
        self.start_x = x1
        self.start_y = y1
        self.end_x = x2
        self.end_y = y2
        self.type = type
        self.num = num



######################################
#                                    #
# this is a class that is meant for  #
# looking for and keeping cards seen #
# on an image given to the class     #
#                                    #
######################################
class solitaire:
    card_list = []
    suit_list = []
    rank_list = []
    filepath = ''

########## Constructor ################################
    def __init__(self):
        print("initialising the game instace of cards...")
        self.filepath = os.path.abspath("Test_files\\Card_Imgs")
        self.read_suits()
        self.read_ranks()
        print("initialising done!")


########## Function for reading suits ##################
    def read_suits(self):
        print("reading suits...",end ='')
        i = 0
        for suit in ['Clubs','Diamonds','Hearts','Spades']:
            self.suit_list.append(cv2.imread(self.filepath+'\\'+suit+'.jpg',1))
            if  np.all(self.suit_list[i] is None) or np.all(self.suit_list[i] == 0):
                print("critical error: no suit found.\nsince python is kind of shit, I would recomend checking the path to the image file.\n\nProgram will now kill itself XP")
                exit()
            i += 1
        print("done!")

######### Function for reading ranks ###################
    def read_ranks(self):
        print("reading ranks...",end ='')
        i = 0
        for rank in ['Ace','Two','Three','Four','Five','Six','Seven','Eight','Nine','Ten','Jack','Queen','King']:
            self.rank_list.append(cv2.imread(self.filepath+'\\'+rank+'.jpg',1))
            if  np.all(self.rank_list[i] is None) or np.all(self.rank_list[i] == 0):
                print("critical error: no rank found.\nsince python is kind of shit, I would recomend checking the path to the image file.\n\nProgram will now kill itself XP")
                exit()
            i += 1

        print("done!")

####### Add a card to the list #########################
    def add_card(self,card):
        self.card_list.append(card)


###### Set a cards variables (WIP) #####################
    def set_cards(self,img,contours,cardlist):
       for contour in contours:
            (x,y,w,h) = cv2.boundingRect(contour)
            if w*h > 25000:
               cv2.rectangle(img, (x,y), (x+w,y+h), (200,255,0), 2) #draw field box

               cv2.circle(img, (x, y), 1, (255, 0, 0), 2)  #draw top left
               cv2.circle(img, (x+w, y), 1, (255, 0, 0), 2) #draw top right
               cv2.circle(img, (x, y+h), 1, (255, 0, 0), 2) #draw bottom left
               cv2.circle(img, (x+w, y+h), 1, (255, 0, 0), 2) #draw bottom right


###### Find cards in an image
    def find_cards(self,img):
        gray = cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)  #greyscale
        blur = cv2.GaussianBlur(gray,(3,3),0)        

        img_w, img_h = np.shape(img)[:2]
        bkg_level = gray[int(img_h/100)][int(img_w/2)]
        thresh_level = bkg_level + 64

        retval, thresh = cv2.threshold(blur,thresh_level,255,cv2.THRESH_BINARY)

        contours,_ = cv2.findContours(thresh, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)

     
        self.set_cards(img,contours,self.card_list)

    




def draw_contour(img,contours):
     #reletevistic characteristics for coordinate estimation. Subject to change!
     for contour in contours:
         (x,y,w,h) = cv2.boundingRect(contour)
         if w > 10 and h > 10:
            cv2.rectangle(img, (x,y), (x+w,y+h), (200,255,0), 2) #draw field box

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

def find_black(img,cardlist):

    for a in range(len(cardlist)):
        i = cardlist[a].start_y  
        while i < cardlist[a].end_y:
            u = cardlist[a].start_x
            while u < cardlist[a].end_x:
               if  img[i,u,0] < 70  and img[i,u,1] < 70  and img[i,u,2] < 70:
                    #set to green
                    img.itemset((i,u,0),20)
                    img.itemset((i,u,1),255)
                    img.itemset((i,u,2),52)
               u +=1
            i +=1
    find_contour(img,0,233,254,80,235,255,"black")





            





cap = cv2.VideoCapture(1);  #setup video capture. might need to change to 0 depenting on own setup

#set resolution to 1920x1080
cap.set(cv2.CAP_PROP_FRAME_WIDTH,1920);
cap.set(cv2.CAP_PROP_FRAME_HEIGHT,1080);



while(True):
    ret, frame = cap.read() #read from camera and store. not currently used but can substitute img in code

    #read from test image file
    file_path = os.path.abspath("Test_files\\test_img_Mads.png") 
    img = cv2.imread(file_path,1)
    gameinstance = solitaire()
    

    #if on absolute path aka errorcheck
    if not img is None or img.size != 0:
    
        gameinstance.find_cards(img) #find white
       # find_black(img,card_list)
       # find_contour(img,0,110,150,20,255,255,'red') #find and draw red

        cv2.imshow('card',img)
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break
        continue
    else:
        print("critical error: no image found.\nsince python is kind of shit, I would recomend checking the path to the image file.\n\nProgram will now kill itself XP")
        exit()

cap.release()
cv2.destroyAllWindows()

