############## Python-OpenCV Solitaire Camera ###############
#
# Author: Gruppe 10
# Date: 24/6/20
# Description: Python program to import an physical solitaire to our Java Program
# Based on the work of Edje Electronics and his work on https://github.com/EdjeElectronics/OpenCV-Playing-Card-Detector

# Import necessary packages
import cv2
import numpy as np
import time
import os
import Cards
import csv
import tkinter as tk
from tkinter import simpledialog

#Class to allow for colors in the debug terminal.
#Based on: https://stackoverflow.com/questions/287871/how-to-print-colored-text-in-terminal-in-python
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
unknowns = False

###################################

#Check for unknowns when ref is disabled
def checkUnknownsWithoutRef(piles):

    for h in range(0, len(piles)):

        if piles[h][0] == 'U' or piles[h][1] == 'U' or piles[h] == 'UU' :
            piles[h] = userCardInput("pile " + str(h))

    
    return piles

#Prompt the user to type in the unidentified card
#Based on: https://djangocentral.com/creating-user-input-dialog/
def userCardInput(location):
    ROOT = tk.Tk()
    ROOT.withdraw()
    USER_INP = simpledialog.askstring(title=('Unable to detect card at pillar ' + location), prompt=('Type value of card at ' + location + ':'))
    return USER_INP

#Compare the camera deck with the reference deck
def compareDecks(camera, ref):

    j = 1
    k = 1
    outputPiles = ['MM', 'MM', 'MM', 'MM', 'MM', 'MM', 'MM', 'MM'] 

    outputPiles[0] = checkDrawPile(camera[0], ref[0])

    for i in range(1,8):
        if camera[j] == ref[i] and camera[j] != 'UU' and ref[i] != 'UU':
            print(bcolors.OKGREEN + "MATCH FOUND at " + str(i) + ". " + ref[i])
            outputPiles[k] = ref[i]


        if (camera[j][0] == 'U' or camera[j][1] == 'U') and ref[i] == 'UU':

            if unknowns == False:
                outputPiles[k] = userCardInput("pile " + str(i))
                print(bcolors.WARNING + "UNKNOWN CARD AT PILE " + str(i) + ". Prompting user for input: " + bcolors.OKBLUE + outputPiles[k])
            
            else:
                if camera[j] == 'UU':
                    print(bcolors.FAIL + "UNKNOWN CARD FOUND at " + str(i) + ". Please reseat card and rescan")

                elif camera[j][0] == 'U':
                    print(bcolors.FAIL + "UNKNOWN RANK at pile " + str(i) + ". Please reseat card and rescan")

                elif camera[j][1] == 'U':
                    print(bcolors.FAIL + "UNKNOWN SUIT at pile " + str(i) + ". Please reseat card and rescan")
 
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

#Checks the drawpile against the reference drawpile
def checkDrawPile(drawPile, refCard):

    if (drawPile == refCard and refCard != 'UU' and refCard != 'XX'):
        print(bcolors.OKGREEN + "MATCH FOUND at Drawpile. " + drawPile)
        return drawPile
    
    if (drawPile != refCard and refCard != 'UU' and refCard != 'XX'):
        print(bcolors.WARNING + "MISMATCH FOUND at Drawpile. Grabbing card form last know card. " + bcolors.FAIL + drawPile + " " + bcolors.OKBLUE + refCard)
        return refCard

    if (refCard == 'UU' or refCard == 'XX'):

        if drawPile[0] == 'U' or drawPile[1] == 'U' and unknowns == False:
            userInput = userCardInput('drawpile')
            print(bcolors.WARNING + "UNKNOWN CARD AT DRAWPILE. Prompting user for input: " + bcolors.OKBLUE + userInput)
            return userInput
        else:
        
            if drawPile[0] == 'U' and drawPile[1] == 'U':
                 print(bcolors.FAIL + "UNKNOWN CARD AT DRAWPILE. Pleas reseat card and rescan")

            elif drawPile[0] == 'U' and drawPile[1] != 'U':
                print(bcolors.FAIL + "UNKNOWN SUIT AT DRAWPILE. Pleas reseat card and rescan")

            elif drawPile[0] != 'U' and drawPile[1] == 'U':
                print(bcolors.FAIL + "UNKNOWN RANK AT DRAWPILE. Pleas reseat card and rescan")

            elif drawPile[0] != 'U' and drawPile[1] != 'U':
                print(bcolors.OKGREEN + "Drawpile identified, grabbing card from camera: " + drawPile[0] + drawPile[1])
                return drawPile

    #print(bcolors.DEFAULT)
    return  drawPile[0] + drawPile[1]

#Displays the image on the screen        
def displayImage(image, x, y):
    dim = (x, y)
    image = cv2.resize(image, dim, interpolation = cv2.INTER_AREA)
    cv2.imshow("Solitaire Camera",image)


#Reads the .txt file with comma seperation
#Based on: https://realpython.com/python-csv/
def fileReader():
    
    tokens = ['MM', 'MM', 'MM', 'MM', 'MM', 'MM', 'MM', 'MM'] 
    f = open("fileToOpenCV.txt", "r")
    csv_reader = csv.reader(f, delimiter=',')

    for row in csv_reader:
        for i in range(0,8):
            tokens[i]=row[i]

    f.close
    return tokens

#Writes the file to the Java program
def fileWriter(string):
    f = open("pythonOutput.txt", "w")
    f.write(string)
    f.close

#Translate the rank to a single chracter
def translateRankToString(e):

    ranks = ['Ace','Two','Three','Four','Five','Six','Seven','Eight','Nine','Ten','Jack','Queen','King']
    letter = ['A', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'J', 'Q', 'K']
    index = 0

    for rank in ranks:
        if e.best_rank_match == rank:
            return letter[index]
        index += 1
    return 'U'

#Translates the suit to a single chracter
def translateSuitToString(e):

    suits = ['Spades','Diamonds','Clubs','Hearts']
    letter = ['S','D','C','H']
    index = 0

    for suit in suits:
        if e.best_suit_match == suit:
            return letter[index]
        index += 1
    return 'U'

#Grabs the posistion of the card
def getPos(e):
  return e.center[0]

#Grabs a new image
def GrabImage():

    ret, image = cap.read() #Used to clear buffer to grab fresh image
    ret, image = cap.read()

    return image

#Translates the array to an string ready for beeing written to the file
def arrayToOutputString(array):

    outputString = array[0] + ",UU,UU,UU,UU"

    for h in range(1, len(array)):
        outputString = outputString + (',' + array[h])
    
    return outputString


#The function that performes the card recognition
# Based on CardDetector.py from https://github.com/EdjeElectronics/OpenCV-Playing-Card-Detector
def processImage(image):
    # Pre-process camera image (gray, blur, and threshold it)
    pre_proc = Cards.preprocess_image(image)

    # Find and sort the contours of all cards in the image (query cards)
    cnts_sort, cnt_is_card = Cards.find_cards(pre_proc)
    # If there are no contours, do nothing
    if len(cnts_sort) != 0:
        # Initialize a new "cards" list to assign the card objects.
        # k indexes the newly made array of cards.
        cards = []
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
                if (cards[k].center[0] < 550 and cards[k].center[1] < 350):
                    noDrawPile = False
                    drawPile = cards[k]
                    #print("Draw: " + cards[k].best_rank_match + " " + cards[k].best_suit_match)
                    
                if (cards[k].center[1] > 800):
                    piles.append(cards[k])
                    #print("Card:" + piles[m].best_rank_match + " " + piles[m].best_suit_match)
                    m = m + 1

                image = Cards.draw_results(image, cards[k])
                k = k + 1

        # Draw card contours on image (have to do contours all at once or
        # they do not show up properly for some reason)
        if (len(cards) != 0):
            temp_cnts = []
            for i in range(len(cards)):
                temp_cnts.append(cards[i].contour)
            cv2.drawContours(image,temp_cnts, -1, (255,0,0), 2)

        displayImage(image,1280,720)

        print(bcolors.HEADER + '#########################################' + bcolors.DEFAULT)

        cameraArray = ['MM', 'MM', 'MM', 'MM', 'MM', 'MM', 'MM'] 
        stringBuilder = ['MM', 'MM', 'MM', 'MM', 'MM', 'MM', 'MM']


        pilesNum = len(piles)
        piles.sort(key=getPos)

        for h in range(pilesNum):

            cameraArray[h] = translateRankToString(piles[h]) + translateSuitToString(piles[h])

        if noDrawPile == False:
            cameraArray.insert(0, translateRankToString(drawPile) + translateSuitToString(drawPile))
        else:
            cameraArray.insert(0, 'UU')
        
        
        if useRef == True:  #### Ref enabled
            print("ref")

            refArray = fileReader()

            stringBuilder = compareDecks(cameraArray,refArray)



        else:
            print("no ref")

            if unknowns == True:
                stringBuilder = cameraArray
            else:
                stringBuilder = checkUnknownsWithoutRef(cameraArray)


        
        outputString = arrayToOutputString(stringBuilder)
        
        print("File to Java: " + str(outputString))
        fileWriter(outputString)

        print(bcolors.HEADER + '#########################################')
        print(bcolors.DEFAULT)


font = cv2.FONT_HERSHEY_SIMPLEX

path = os.path.dirname(os.path.abspath(__file__))
train_ranks = Cards.load_ranks( path + '/Card_Imgs/')
train_suits = Cards.load_suits( path + '/Card_Imgs/')



cap = cv2.VideoCapture(cv2.CAP_DSHOW + 2)
cap.set(cv2.CAP_PROP_BUFFERSIZE, 0)
cap.set(cv2.CAP_PROP_FRAME_WIDTH,1920)
cap.set(cv2.CAP_PROP_FRAME_HEIGHT,1080)


processImage(GrabImage())


# Main loop
#Key listener based on https://www.programcreek.com/python/example/72137/cv2.waitKey
while True:

    key = cv2.waitKey(1) & 0xFF
    if key == ord(" "):
        print("Grabbing new frame")
        processImage(GrabImage())

    if key == ord(chr(27).encode()):
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
    
    if key == ord("u"):
        
        if unknowns == False:
            print("Unknowns enabled")
            unknowns = True
        
        else:
            unknowns = False
            print("Unknowns disabled")
    

