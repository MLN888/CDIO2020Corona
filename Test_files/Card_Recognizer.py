
####################################################
#          Test for tennisball collectror          #
#                                                  #
# version 0.5                                      #
# description: A test file for finding cards in    #
#              openCV 2                            #
#												   #
# auther:       Phillip Bomholtz                   #
# created:      18-03-2020						   #
# last updated: 22-04-2020						   #
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
    light_sens = 56

########## Constructor ################################
    def __init__(self):
        print("initialising the game instace of cards...")
        self.filepath = os.path.abspath("Test_files\\Card_Imgs")  #set global path to local files
        self.read_suits()                                         #read all suit images
        self.read_ranks()                                         #read all rank images
        print("initialising done!")


########## Function for reading suits ##################
    def read_suits(self):
        print("reading suits...",end ='')

        i = 0 #set index

        #loop through all suits
        for suit in ['Clubs','Diamonds','Hearts','Spades']:
            self.suit_list.append(cv2.imread(self.filepath+'\\'+suit+'.jpg',1))     #read and add to list of suits

            #check if what added is actually valid
            if  np.all(self.suit_list[i] is None) or np.all(self.suit_list[i] == 0):
                print("critical error: no suit found.\nsince python is kind of shit, I would recomend checking the path to the image file.\n\nProgram will now kill itself XP")
                exit() #die

            i += 1 #move index
        print("done!")

######### Function for reading ranks ###################
    def read_ranks(self):
        print("reading ranks...",end ='')

        i = 0 #set index

        #loop through all ranks
        for rank in ['Ace','Two','Three','Four','Five','Six','Seven','Eight','Nine','Ten','Jack','Queen','King']:
            self.rank_list.append(cv2.imread(self.filepath+'\\'+rank+'.jpg',1))                                    #read and add to list

            #check if what added is actually valid
            if  np.all(self.rank_list[i] is None) or np.all(self.rank_list[i] == 0):
                print("critical error: no rank found.\nsince python is kind of shit, I would recomend checking the path to the image file.\n\nProgram will now kill itself XP")
                exit() #die

            i += 1 #move index

        print("done!")

####### Add a card to the list #########################
    def add_card(self,card):
        self.card_list.append(card)


###### Set a cards variables and draw (WIP) ############
    def set_cards(self,img,contours,cardlist):
       for contour in contours:
            (x,y,w,h) = cv2.boundingRect(contour)
            peri = cv2.arcLength(contour,True)
            approx = cv2.approxPolyDP(contour,0.01*peri,True)
            pts = np.float32(approx)
            
            #if of card size
            #this can vary with camera position but is needed to filter out non card contours
            #this part of the code should be changed so as to be more universal if it can
            #also can be made more tolorant depending on light reflection on bagground surface
            if w*h > 24000 and len(pts) == 4:
               print(len(pts))
               cv2.circle(img, (pts[0,0,0], pts[0,0,1]), 1, (255, 0, 0), 2) #draw first point
               cv2.circle(img, (pts[1,0,0], pts[1,0,1]), 1, (255, 0, 0), 2) #draw second point
               cv2.circle(img, (pts[2,0,0], pts[2,0,1]), 1, (255, 0, 0), 2) #draw third point
               cv2.circle(img, (pts[3,0,0], pts[3,0,1]), 1, (255, 0, 0), 2) #draw fourth point
               cv2.line(img,(pts[0,0,0], pts[0,0,1]),(pts[1,0,0], pts[1,0,1]),(0,200,100),thickness = 6,lineType=8) #first to second
               cv2.line(img,(pts[1,0,0], pts[1,0,1]),(pts[2,0,0], pts[2,0,1]),(0,200,100),thickness = 6,lineType=8) #second to third
               cv2.line(img,(pts[2,0,0], pts[2,0,1]),(pts[3,0,0], pts[3,0,1]),(0,200,100),thickness = 6,lineType=8) #third to fourth
               cv2.line(img,(pts[3,0,0], pts[3,0,1]),(pts[0,0,0], pts[0,0,1]),(0,200,100),thickness = 6,lineType=8) #fourth to first



               

###### Find cards in an image ##########################
    def find_cards(self,img):
        gray = cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)  #greyscale
        blur = cv2.GaussianBlur(gray,(5,5),0)        #blur with gauss algorythm

        img_w, img_h = np.shape(img)[:2]               #get hight and width of image
        bkg_level = gray[int(img_h/100)][int(img_w/2)] 
        thresh_level = bkg_level + self.light_sens

        retval, thresh = cv2.threshold(blur,thresh_level,255,cv2.THRESH_BINARY)  
        contours,_ = cv2.findContours(thresh, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)  #find contours in the blurred and grayscaled image
        cv2.imshow('lul',thresh)
        
        
        self.set_cards(img,contours,self.card_list) #process contours

    




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


gameinstance = solitaire()

while(True):
    ret, frame = cap.read() #read from camera and store. not currently used but can substitute img in code

    #read from test image file
    file_path = os.path.abspath("Test_files\\test_img_Mads.png") 
    img = cv2.imread(file_path,1)

    

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

