# "Sharpen" an image by  multiplying every pixel by 2, and then subtracting
# the average value of the neighborhood from it.

#See slide number 22 from IrfanEssa-CP-02-5-Filtering.pdf

#
# Jay Summet 2015
#
#Python 2.7, OpenCV 2.4.x
#

import cv2
import numpy as np


#Linux window/threading setup code.
cv2.startWindowThread()
cv2.namedWindow("Original")
cv2.namedWindow("Sharpen")

#Load source / input image as grayscale, also works on color images...
imgIn = cv2.imread("test.jpg", cv2.IMREAD_GRAYSCALE)
cv2.imshow("Original", imgIn)


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

custom = cv2.filter2D(imgIn, -1, kernel)
cv2.imshow("Sharpen", custom)


cv2.waitKey(0)

