/*--------------------------------------
The user interface implementation class
for the CDIO 2020 spring solitaire
solver. Its purpose is to display the
current move to be made.

author:       Phillip Eg Bomholtz
created:      08-06-2020
Last updated: 11-06-2020

version: 0.4

----------------------------------------*/

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;


public class UserInterface{


    /*
    *   small private class to describe how display cards should be percived.
    *   The card for this implementation are 153x230 px.
    *   Since this is a simple implementaion that are the only dimensions
    *   accounted for,
    */
    private class UICard {
        public int posX;
        public int posY;
        public int displayDepth;  //the layer this card should be drawn on
        public String Rank;       //i.e. the number or J,Q and so on
        public String Suit;       //i.e. heart, spade and so on
        public boolean flipped;
        private JLabel myLabel;   //the drawable component
    
        public UICard(int x, int y,int depth, String assetPath)
        {
            //init position
            this.posX = x;
            this.posY = y;

            this.displayDepth = depth; //set depth

            this.flipped = false;

            //init identity
            this.Rank = "UNDEFINED";
            this.Suit = "UNDEFINED";
    
            //init label (the drawable component)
            this.myLabel = new JLabel(new ImageIcon(assetPath+"\\card_back.png"));
            this.myLabel.setBounds(posX,posY,posX+153,posY+230);
    
        }
    
        /*
        *   flip the card to what is on its face.
        *   s:         The suit of the card
        *   r:         The rank of the card
        *   assetPath: Path to the asset folder
        */
        public void doAFlip(String s, String r, String assetPath)
        {
            Rank = r;
            Suit = s;
            myLabel.setIcon(new ImageIcon(assetPath+"\\"+Suit+"_"+Rank+".png"));  //update label
        }
    
        /*
        *   simple function for moving a card
        *   x: destination x axis value
        *   y: destination y axis value
        */
        public void move(int x, int y)
        {
            posX = x;
            posY = y;
            this.myLabel.setBounds(posX,posY,posX+153,posY+230);  //reset bounds and move
        }
    
        /*
        *   return the drawable component
        */
        public JLabel getLabel()
        {
            return this.myLabel;
        }
    }

    public String ImgPath;                       //path to image assets
    JFrame UIFrame;                              //frame for the UI to be placed in
    JLayeredPane UIPanel;                        //the layerd panel of the UI that allows cards to be placed over one another

    /* 
    *  an important data structure to understand!
    *  This list hold severel list of cards that that are used
    *  on the board. 0 is used for the card deck normally on the 
    *  top right. 1-7 are used for the card stacks on the play 
    *  area with the locked cards (initially that is). 8-11
    *  are used for the top left build stacks.
    *  12 is deck card currently pulled.
    */
    private ArrayList<ArrayList<UICard>> stackList;

    //standerd spacing parameters for generel use.
    //are private because i'm scared of Jan.
    private int std_stack_card_offset = 10;
    private int std_stack_delta = 167;

    boolean fancyOrNah = false; //exclude or include some animations

    boolean readErr = false;  //if the init read has produced an error


    public UserInterface(boolean fancyOrNah) {
        System.out.println("****Setting up user interface****");
        this.ImgPath = new File("assets").getAbsolutePath();        //set a path to the assets folder
        this.UIFrame = new JFrame("Ya like jazz?");                 //setup the widown frame
        this.UIPanel = new JLayeredPane();                          //setup the layerd panel for the frame
        this.stackList = new ArrayList<ArrayList<UICard>>();        //set 2d struckture of cards of differens spots on the board
        this.fancyOrNah = fancyOrNah;                               

        UIFrame.setSize(1823, 811); // dimensions equal to absolute background size
        UIFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // frame should exit on closing the window

        // setting background
        JLabel j = new JLabel(new ImageIcon(ImgPath + "\\background.png"));
        j.setBounds(0, 0, 1823, 811);
        UIPanel.add(j, new Integer(0)); //set background as first layer

        //adding panel to frame and setting visible
        UIFrame.add(UIPanel);
        UIFrame.setVisible(true);
        
        System.out.println("setting cards...");
        setCards();     //generate the cards
        System.out.println("displaying cards...");
        displayCards(initListRead()); //display initial set cards

        System.out.println("****Done****");
    }

    private void setCards() {
        ArrayList<UICard> deckTemp = new ArrayList<UICard>();
        for(int i = 0; i < 24; i++)
        {
            deckTemp.add(new UICard(1050, 20,i+1 , ImgPath));
        }
        stackList.add(deckTemp);

        for(int i = 1; i <= 7; i++)
        {
            ArrayList<UICard> stackTemp = new ArrayList<UICard>();
            for(int u = 0; u < i; u++)
            {
                stackTemp.add(new UICard(std_stack_delta*(i-1)+50, 200+(std_stack_card_offset*u),u+1 , ImgPath));
            }
            stackList.add(stackTemp);
        }

        for(int i = 0; i < 5; i++)
        {
            ArrayList<UICard> solveTemp = new ArrayList<UICard>();
            stackList.add(solveTemp);
        }


    }


    private void displayCards(ArrayList<ArrayList<String>> a)
    {
        for(int i = 0; i < 24; i++)
        {
            UIPanel.add(stackList.get(0).get(i).getLabel(),new Integer(stackList.get(0).get(i).displayDepth*100));
        }

        int u;
        for(int i = 1; i <= 7; i++)
        {
            for(u = 0; u < i; u++)
            {
                UIPanel.add(stackList.get(i).get(u).getLabel(), new Integer(stackList.get(i).get(u).displayDepth*100));
                if(fancyOrNah)sleep(50);
            }
            if(fancyOrNah)sleep(100);
            stackList.get(i).get(u-1).doAFlip(a.get(i-1).get(0),a.get(i-1).get(1),ImgPath);

        }
    }

    public void moveSug(int startIndex,int startReach,int destIndex,int steps)
    {
        //start x and y from input parameter
        int startX = stackList.get(startIndex).get(startReach).posX;
        int startY = stackList.get(startIndex).get(startReach).posY;

        //end x and y from longest reach on dest
        int endX = 0;
        int endY = 0;
        
        if(destIndex < 7 && destIndex > 0)
        {
            endX =stackList.get(destIndex).get(stackList.get(destIndex).size()-1).posX;
            endY =stackList.get(destIndex).get(stackList.get(destIndex).size()-1).posY + std_stack_card_offset;    
        }
        else if (destIndex == 8)
        {
            endX = 47;
            endY = 20;
        }
        else if (destIndex == 9)
        {
            endX = 47 + 116;
            endY = 20;
        }
        else if (destIndex == 10)
        {
            endX = 47 + 116 * 2;
            endY = 20;
        }
        else if (destIndex == 11)
        {
            endX = 47 + 116 * 3;
            endY = 20;
        }
        else if (destIndex == 12)
        {
            endX = 900;
            endY = 20;
        }
        else 
        {
            System.out.println("yo dawg what you doin? you can't do that!");
        }


        //make vector from start to end
        int vectorX = endX-startX;
        int vectorY = endY-startY;

        //devide into steps
        int stepX = vectorX / steps;
        int stepY = vectorY / steps;

        int destDisplayDepth = 1;
        if(stackList.get(destIndex).size() > 0)destDisplayDepth = stackList.get(destIndex).get(stackList.get(destIndex).size()-1).displayDepth + 1;
        stackList.get(startIndex).get(startReach).displayDepth = destDisplayDepth;

        for(int i = 1; i <= steps; i++)
        {
            int reach = startReach;
            int startGap = 0;
            while(reach != stackList.get(startIndex).size())
            {
                if(reach == startReach)startGap = 0;else startGap = 30;
                stackList.get(startIndex).get(reach).move(startX+(stepX*i),startY+(stepY*i)+std_stack_card_offset * (reach - startReach)+startGap);
                UIPanel.setLayer(stackList.get(startIndex).get(reach).getLabel(), new Integer(3000+reach));
                reach++;
            }
            sleep(60);
        }

        
        int reach = startReach;
        while(reach != stackList.get(startIndex).size())
        {
            stackList.get(startIndex).get(reach).move(startX,startY+std_stack_card_offset * (reach - startReach));
            stackList.get(startIndex).get(reach).displayDepth = reach + 1;
            UIPanel.setLayer(stackList.get(startIndex).get(reach).getLabel(), new Integer(stackList.get(startIndex).get(reach).displayDepth) * 100);
            reach++;
        }
        
       
    }

    public void failCheck()
    {
        for(int i = 1; i < stackList.size(); i++)
        {
            System.out.println("Stack nr.: "+i);
            for(int u = 0; u < stackList.get(i).size(); u++)
            {
               System.out.println("card: "+ stackList.get(i).get(u).Suit +" "+ stackList.get(i).get(u).Rank + " ("+ stackList.get(i).get(u).posX + "," + stackList.get(i).get(u).posY + ") at displaydepth: "+stackList.get(i).get(u).displayDepth);
            }

        }
    }

    public void stackEndFlip(int index, char s, char r)
    {
        String suit = suitInterpreter(s);
        String rank = String.valueOf(r);
        stackList.get(index).get(stackList.get(index).size()-1).doAFlip(suit, rank, ImgPath);
    }

    public void makeMove(int startIndex,int startReach,int destIndex)
    {
        
         //end x and y from longest reach on dest
        int endX = 0;
        int endY = 0;
        
        if(destIndex < 7 && destIndex > 0)
        {
            endX =stackList.get(destIndex).get(stackList.get(destIndex).size()-1).posX;
            endY =stackList.get(destIndex).get(stackList.get(destIndex).size()-1).posY + std_stack_card_offset;    
        }
        else if (destIndex == 8)
        {
            endX = 47;
            endY = 20;
        }
        else if (destIndex == 9)
        {
            endX = 47 + 116;
            endY = 20;
        }
        else if (destIndex == 10)
        {
            endX = 47 + 116 * 2;
            endY = 20;
        }
        else if (destIndex == 11)
        {
            endX = 47 + 116 * 3;
            endY = 20;
        }
        else if (destIndex == 12)
        {
            endX = 900;
            endY = 20;
        }
        else 
        {
            System.out.println("yo dawg what you doin? you can't do that!");
        }

         int reach = startReach;  //set start point for moving
         while(reach != stackList.get(startIndex).size())
         {
            stackList.get(startIndex).get(reach).move(endX, endY + std_stack_card_offset * (reach - startReach));   //move x,y component to display card at new place

            //make sure it is displayed in the correct layer. gets the displaydepth of last card on destination and adds one to that if it exists. will set one if not
            if(stackList.get(destIndex).size() > 0)
            stackList.get(startIndex).get(reach).displayDepth = stackList.get(destIndex).get(stackList.get(destIndex).size()-1).displayDepth + 1;
            else
            stackList.get(startIndex).get(reach).displayDepth = 1;

            UIPanel.setLayer(stackList.get(startIndex).get(reach).getLabel(), new Integer(stackList.get(startIndex).get(reach).displayDepth * 100)); //update display

            //add to the now stack
            stackList.get(destIndex).add(stackList.get(startIndex).get(reach));
            reach++;
         }

         //set reach to the last card in the start stack
         reach = stackList.get(startIndex).size()-1;

         //remove untill all cards that where moved are gone
         while(reach >= startReach)
         {
            stackList.get(startIndex).remove(reach);
            reach--;
         }
    }

    public int getStackSizeAtIndex(int i){ return stackList.get(i).size();}

    public void resetDeck()
    {
        ArrayList<UICard> temp = new ArrayList<UICard>();
        for(int i = stackList.get(12).size() - 1; i >= 0 ; i--)
        {
            stackList.get(12).get(i).getLabel().setIcon(new ImageIcon(ImgPath+"\\card_back.png"));
            stackList.get(12).get(i).move(1050, 20);
            temp.add(stackList.get(12).get(i));
        }

        stackList.set(0,temp);
    }

    private ArrayList<ArrayList<String>> initListRead()
    {
        System.out.println("UI device attempting fileread of tableFile");

        ArrayList<ArrayList<String>> cards = new ArrayList<ArrayList<String>>();
        String Path = new File("tableFile.txt").getAbsolutePath();

        String input ="";
        try
        {
            BufferedReader r = new BufferedReader(new FileReader(Path));
            input = r.readLine();
            r.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.out.println("UI read fail! prociding with caution");
        }

        //if everything whent fine
        if(!input.equals(""))
        {
            StringTokenizer st = new StringTokenizer(input,",");
            for(int i = 0; i < 5; i++)st.nextToken();
            for(int i = 0; i < 7; i++)
            {
                if(!st.hasMoreTokens()) System.out.println("err: to few tokens to fill all table positions");
                String holder = st.nextToken();
                ArrayList<String> builder = new ArrayList<String>();
                builder.add(suitInterpreter(holder.charAt(1)));
                builder.add(String.valueOf(holder.charAt(0)));
                cards.add(builder);
            }
            if(st.hasMoreTokens()) System.out.println("err: tokens not exausted. too many tokens");
            System.out.println("UI reading succes!");
        }
        else
        {
            readErr = true;
            System.out.println("UI reading failure!");
        }
        return cards;
    }


    private String suitInterpreter(char s)
    {
        switch(s)
        {
            case 'H':
                return "Heart";
            case 'D':
                return "Diamond";
            case 'C':
                return "Clover"; 
            case 'S':
                return "Spade";
            default:
                return "err";
        }
    }


    private void sleep(int s)
    {
        try
        {
            Thread.sleep(s);
        }
        catch(InterruptedException ex)
        {
            System.out.println("error while sleeping: "+ex);
        }
    }

    
}