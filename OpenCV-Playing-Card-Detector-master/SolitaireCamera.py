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


###################################

def getPos(e):
  return e.center[0]


def GrabImage():

    ret, image = cap.read() #Used to clear buffer to grab fresh image
    ret, image = cap.read()

    # Pre-process camera image (gray, blur, and threshold it)
    pre_proc = Cards.preprocess_image(image)

    # Find and sort the contours of all cards in the image (query cards)
    cnts_sort, cnt_is_card = Cards.find_cards(pre_proc)
    # If there are no contours, do nothing
    if len(cnts_sort) != 0:
        # Initialize a new "cards" list to assign the card objects.
        # k indexes the newly made array of cards.
        cards = []
        
        drawPile
        BuildPiles = []
        piles = []

        k = 0
        m = 0
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

                if (cards[k].center[1] > 450):
                    piles.append(cards[k])
                    #print("Card:" + piles[m].best_rank_match + " " + piles[m].best_suit_match)
                    m = m + 1

                image = Cards.draw_results(image, cards[k])
                k = k + 1
        
        print('#########################################')

        l = len(piles)

        piles.sort(key=getPos)

        for h in range(l):
            print("Pos: " + str(h) + " " + piles[h].best_rank_match + " " + piles[h].best_suit_match)

        # Draw card contours on image (have to do contours all at once or
        # they do not show up properly for some reason)
        if (len(cards) != 0):
            temp_cnts = []
            for i in range(len(cards)):
                temp_cnts.append(cards[i].contour)
            cv2.drawContours(image,temp_cnts, -1, (255,0,0), 2)


    dim = (1280, 720)
    image2 = cv2.resize(image, dim, interpolation = cv2.INTER_AREA)
    cv2.imshow("Card Detector",image2)


## Camera settings
IM_WIDTH = 1920
IM_HEIGHT = 1080 
FRAME_RATE = 10


## Define font to use
font = cv2.FONT_HERSHEY_SIMPLEX

path = os.path.dirname(os.path.abspath(__file__))
train_ranks = Cards.load_ranks( path + '/Card_Imgs/')
train_suits = Cards.load_suits( path + '/Card_Imgs/')


cap = cv2.VideoCapture(cv2.CAP_DSHOW + 0)
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

