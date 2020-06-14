############## Python-OpenCV Playing Card Detector ###############
#
# Author: Evan Juras
# Date: 9/5/17
# Description: Python script to detect and identify playing cards
# from a PiCamera video feed.
#

# Import necessary packages
import cv2
import numpy as np
import time
import os
import Cards
import csv


class bcolors:
    HEADER = '\033[95m'
    OKBLUE = '\033[94m'
    OKGREEN = '\033[92m'
    WARNING = '\033[93m'
    FAIL = '\033[91m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'
    UNDERLINE = '\033[4m'
    DEFAULT ="\033[0;00m" 

useRef = False

###################################

def compareDecks(camera, ref):

    outputPiles = ['MM', 'MM', 'MM', 'MM', 'MM', 'MM', 'MM'] 
    j = 0
    k = 0
    for i in range(1,8):
        if camera[j] == ref[i] and camera[j] != 'UU' and ref[i] != 'UU':
            print(bcolors.OKGREEN + "MATCH FOUND at " + str(i) + ". " + ref[i])
            outputPiles[k] = ref[i]


        if (camera[j][0] == 'U' or camera[j][1] == 'U') and ref[i] == 'UU':

            if camera[j] == 'UU':
                print(bcolors.FAIL + "UNKNOWN CARD FOUND at " + str(i) + ". Pleas reseat card and rescan")

            elif camera[j][0] == 'U':
                print(bcolors.FAIL + "UNKNOWN RANK at pile " + str(i) + ". Pleas reseat card and rescan")

            elif camera[j][1] == 'U':
                print(bcolors.FAIL + "UNKNOWN SUIT at pile " + str(i) + ". Pleas reseat card and rescan")
 
            outputPiles[k] = camera[j]
        
        if (camera[j][0] != 'U' and camera[j][1] != 'U') and ref[i] == 'UU':
            print(bcolors.OKBLUE + "UNKNOWN CARD at " + str(i) + ". Grabbing card from camera: " + camera[j])
            outputPiles[k] = camera[j]

        if camera[j] != ref[i] and ref[i] != 'UU' and ref[i] != 'XX':
            print(bcolors.WARNING + "MISMATCH FOUND at " + str(i) + ". Grabbing card from last known card. " + bcolors.FAIL + camera[j] + " " + bcolors.OKBLUE + ref[i])
            outputPiles[k] = ref[i]
        
        if ref[i] == 'XX':
            
            print(bcolors.OKBLUE + "EMPTY SPACE FOUND at " + str(i) + ", moving to next pile")
            outputPiles[k] = 'XX'
            j = j - 1
        
        j = j + 1
        k = k + 1

        
    print(bcolors.DEFAULT)
    return outputPiles

def checkDrawPile(drawPile, refCard):

    if (drawPile == refCard and refCard != 'UU' and refCard != 'XX'):
        print(bcolors.OKGREEN + "MATCH FOUND at Drawpile. " + drawPile)
        return drawPile
    
    if (drawPile != refCard and refCard != 'UU' and refCard != 'XX'):
        print(bcolors.WARNING + "MISMATCH FOUND at Drawpile. Grabbing card form last know card. " + bcolors.FAIL + drawPile + " " + bcolors.OKBLUE + refCard)
        return refCard

    if (refCard == 'UU' or refCard == 'XX'):
        
        if drawPile[0] == 'U' and drawPile[1] == 'U':
             print(bcolors.FAIL + "UNKNOWN CARD AT DRAWPILE. Pleas reseat card and rescan")

        elif drawPile[0] == 'U' and drawPile[1] != 'U':
            print(bcolors.FAIL + "UNKNOWN SUIT AT DRAWPILE. Pleas reseat card and rescan")

        elif drawPile[0] != 'U' and drawPile[1] == 'U':
            print(bcolors.FAIL + "UNKNOWN RANK AT DRAWPILE. Pleas reseat card and rescan")

        elif drawPile[0] != 'U' and drawPile[1] != 'U':
            print(bcolors.OKGREEN + "Drawpile identified, grabbing card from camera: " + drawPile[0] + drawPile[1])
            return drawPile

    print(bcolors.DEFAULT)
    return  drawPile[0] + drawPile[1]
    
        


def fileReader():
    
    tokens = ['MM', 'MM', 'MM', 'MM', 'MM', 'MM', 'MM', 'MM'] 
    #f = open("Solitaire\\src\\fileToOpenCV.txt", "r")
    f = open("fileToOpenCV.txt", "r")
    csv_reader = csv.reader(f, delimiter=',')

    for row in csv_reader:
        for i in range(0,8):
            tokens[i]=row[i]

    f.close
    return tokens

    

def fileWriter(string):
    #f = open("Solitaire\src\pythonOutput.txt", "w")
    f = open("pythonOutput.txt", "w")
    f.write(string)
    f.close
    

def translateRankToString(e):

    ranks = ['Ace','Two','Three','Four','Five','Six','Seven','Eight','Nine','Ten','Jack','Queen','King']
    letter = ['A', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'J', 'Q', 'K']
    index = 0

    for rank in ranks:
        if e.best_rank_match == rank:
            return letter[index]
        index += 1
    return 'U'


def translateSuitToString(e):

    suits = ['Spades','Diamonds','Clubs','Hearts']
    letter = ['S','D','C','H']
    index = 0

    for suit in suits:
        if e.best_suit_match == suit:
            return letter[index]
        index += 1
    return 'U'

def getPos(e):
  return e.center[0]


def GrabImage():

    ret, image = cap.read() #Used to clear buffer to grab fresh image
    ret, image = cap.read()

    #cv2.GaussianBlur(image,(5,5),0)
    #cv2.Blur(image, (5,5))

    #Create the identity filter, but with the 1 shifted to the right!
    kernel = np.zeros( (9,9), np.float32)
    kernel[4,4] = 2.0   #Identity, times two! 

    #Create a box filter:
    boxFilter = np.ones( (9,9), np.float32) / 81.0

    #Subtract the two:
    kernel = kernel - boxFilter


    #Note that we are subject to overflow and underflow here...but I believe that
    # filter2D clips top and bottom ranges on the output, plus you'd need a
    # very bright or very dark pixel surrounded by the opposite type.

    image = cv2.filter2D(image, -1, kernel)



    # Pre-process camera image (gray, blur, and threshold it)
    pre_proc = Cards.preprocess_image(image)

    # Find and sort the contours of all cards in the image (query cards)
    cnts_sort, cnt_is_card = Cards.find_cards(pre_proc)
    # If there are no contours, do nothing
    if len(cnts_sort) != 0:
        # Initialize a new "cards" list to assign the card objects.
        # k indexes the newly made array of cards.
        cards = []
        BuildPiles = []
        piles = []

        k = 0
        m = 0
        noDrawPile = True
        
        # For each contour detected:
        for i in range(len(cnts_sort)):
            if (cnt_is_card[i] == 1):
                # Create a card object from the contour and append it to the list of cards.
                # preprocess_card function takes the card contour and contour and
                # determines the cards properties (corner points, etc). It generates a
                # flattened 200x300 image of the card, and isolates the card's
                # suit and rank from the image.
                cards.append(Cards.preprocess_card(cnts_sort[i],image))
                # Find the best rank and suit match for the card.
                cards[k].best_rank_match,cards[k].best_suit_match,cards[k].rank_diff,cards[k].suit_diff = Cards.match_card(cards[k],train_ranks,train_suits)
                #print("Card:" + cards[k].best_rank_match + " " + cards[k].best_suit_match)
                # Draw center point and match result on the image.
                if (cards[k].center[0] < 550 and cards[k].center[1] < 550):
                    noDrawPile = False
                    drawPile = cards[k]
                    #print("Draw: " + cards[k].best_rank_match + " " + cards[k].best_suit_match)
                    
                if (cards[k].center[1] > 800):
                    piles.append(cards[k])
                    #print("Card:" + piles[m].best_rank_match + " " + piles[m].best_suit_match)
                    m = m + 1

                image = Cards.draw_results(image, cards[k])
                k = k + 1
        
        print(bcolors.HEADER + '#########################################' + bcolors.DEFAULT)

        cameraArray = ['MM', 'MM', 'MM', 'MM', 'MM', 'MM', 'MM'] 
        stringBuilder = ['MM', 'MM', 'MM', 'MM', 'MM', 'MM', 'MM']

        
        #stringBuilder[0] = (translateRankToString(drawPile) + translateSuitToString(drawPile))

        pilesNum = len(piles)
        piles.sort(key=getPos)

        for h in range(pilesNum):
            #print("Pos: " + str(h) + " " + piles[h].best_rank_match + " " + piles[h].best_suit_match)
            #stringBuilder = stringBuilder + (',' + translateRankToString(piles[h]) + translateSuitToString(piles[h]))
            #print(translateRankToString(piles[h]) + translateSuitToString(piles[h]))

            cameraArray[h] = translateRankToString(piles[h]) + translateSuitToString(piles[h])

        if useRef == True:

            refArray = fileReader()

            if noDrawPile == False:
                outputString = checkDrawPile(translateRankToString(drawPile) + translateSuitToString(drawPile),refArray[0])
                outputString = outputString + ",UU,UU,UU,UU"

            else:
                print(bcolors.FAIL + "UNKNOWN TO IDENTIFY DRAWPILE CARD. PLEASE RESEAT OF FLIP OVER A NEW CARD.")
                print(bcolors.DEFAULT)
                outputString = "UU"     ###############################
                outputString = outputString + ",UU,UU,UU,UU"

            tempArray = compareDecks(cameraArray, refArray)

            stringBuilder = tempArray

            for h in range(0,7):
                outputString = outputString + (',' + stringBuilder[h])

        else:

            if noDrawPile == False:
                outputString = translateRankToString(drawPile) + translateSuitToString(drawPile)
                outputString = outputString + ",UU,UU,UU,UU"

            else:
                print(bcolors.FAIL + "UNKNOWN TO IDENTIFY DRAWPILE CARD. PLEASE RESEAT OF FLIP OVER A NEW CARD.")
                print(bcolors.DEFAULT)
                outputString = "UU"     ###############################
                outputString = outputString + ",UU,UU,UU,UU"

            for h in range(0,7):
                stringBuilder[h] = cameraArray[h]

            for h in range(0,7):
                outputString = outputString + (',' + stringBuilder[h])
        if noDrawPile == False:
            print("Drawpile: " + translateRankToString(drawPile) + translateSuitToString(drawPile))
        else:
            print("Drawpile: UU" )
        
        print("Pillars: " + str(stringBuilder))
        fileWriter(outputString)

        print(bcolors.HEADER + '#########################################')
        print(bcolors.DEFAULT)

        # Draw card contours on image (have to do contours all at once or
        # they do not show up properly for some reason)
        if (len(cards) != 0):
            temp_cnts = []
            for i in range(len(cards)):
                temp_cnts.append(cards[i].contour)
            cv2.drawContours(image,temp_cnts, -1, (255,0,0), 2)


    dim = (1280, 720)
    image2 = cv2.resize(image, dim, interpolation = cv2.INTER_AREA)
    cv2.imshow("Solitaire Camera",image2)


## Camera settings
IM_WIDTH = 1920
IM_HEIGHT = 1080 
FRAME_RATE = 10


## Define font to use
font = cv2.FONT_HERSHEY_SIMPLEX

path = os.path.dirname(os.path.abspath(__file__))
train_ranks = Cards.load_ranks( path + '/Card_Imgs/')
train_suits = Cards.load_suits( path + '/Card_Imgs/')



cap = cv2.VideoCapture(cv2.CAP_DSHOW + 2)
cap.set(cv2.CAP_PROP_BUFFERSIZE, 0)
cap.set(cv2.CAP_PROP_FRAME_WIDTH,1920)
cap.set(cv2.CAP_PROP_FRAME_HEIGHT,1080)


GrabImage()


while True:

    key = cv2.waitKey(1) & 0xFF
    if key == ord(" "):
        print("Grabbing new frame")
        GrabImage()

    if key == ord("q"):
        cam_quit = 1 
        cv2.destroyAllWindows()
        quit()

    if key == ord("r"):
        
        if useRef == False:
            print("Ref enabled")
            useRef = True
        
        else:
            useRef = False
            print("Ref disabled")
    

