

def legal_actions(list):
    #for i in range(len(list)-1):
    #    if list[i].colour != list[i+1].colour and list[i].value == list[i+1].value-1:
    #         print ("It's a match!", list[i].colour, list[i].value, "can be placed on", list[i+1].colour, list[i+1].value)
    #    if list[i].value == 13 and list[i+1].value == 0: 
    #        print (list[i].colour, "king can be placed at empty space")

        #actions = []

        #Find visible cards, put into array 
        #field = []

        #Array of hand 
        #hand = []
        #print("Function")
    
    print("Finding legal moves")
    
    for j in range(len(list)):
        for i in range(len(list)-1):
            if list[j].colour != list[i].colour and list[j].value == list[i].value-1:
                print ("It's a match!", list[j].colour, list[j].value, "can be placed on", list[i].colour, list[i].value)
            if list[j].value == 13 and list[i].value == 0: 
                print (list[j].colour, "king can be placed at empty space")

        #actions = []

        #Find visible cards, put into array 
        #field = []

        #Array of hand 
        #hand = []
        #print("Function")
    return



class Card():
    def __init__(self, colour, value):
        self.colour = colour
        self.value = value
        
        #self.colour = ('black', 'red')
        #self.ranks = ('two', 'three', 'four', 'five', 'six', 'seven', 'eight',
        #        'nine', 'ten', 'jack', 'queen', 'king', 'ace')
        #self.value = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,}
        #values = {'two':2, 'three':3, 'four':4, 'five':5, 'six':6, 'seven':7,
        #        'eight':8,'nine':9, 'ten':10, 'jack':11,
        #        'queen':12, 'king':13, 'ace':1,}
       


def main():
    print("Running main function\n")
    # creating list        
list = []  
  
# appending instances to list  
list.append( Card('black', 2) ) 
list.append( Card('red', 3) ) 
list.append( Card('black', 4) ) 
list.append( Card('red', 7) ) 
list.append( Card('red', 13) )
list.append( Card('red', 0) )
list.append( Card('black', 6) )

#Print list   
for obj in range(len(list)): 
    print( list[obj].colour, list[obj].value, sep =' ' )

#Searching for legal actions within the list
legal_actions(list)

    
    
   

main()

