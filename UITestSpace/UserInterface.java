/*--------------------------------------
The user interface implementation class
for the CDIO 2020 spring solitaire
solver. Its purpose is to display the
current move to be made.

author:       Phillip Eg Bomholtz
created:      08-06-2020
Last updated: 10-06-2020

version: 0.2



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
        private JLabel myLabel;   //the drawable component
    
        public UICard(int x, int y,int depth, String assetPath)
        {
            //init position
            this.posX = x;
            this.posY = y;

            this.displayDepth = depth; //set depth

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
    *  are used for the top left solution stacks.
    */
    private ArrayList<ArrayList<UICard>> stackList;

    //standerd spacing parameters for generel use.
    //are private because i'm scared of Jan.
    private int std_stack_card_offset = 10;
    private int std_stack_delta = 167;


    boolean readErr = false;  //if the init read has produced an error


    public UserInterface(boolean fancyOrNah) {
        System.out.println("****Setting up user interface****");
        this.ImgPath = new File("assets").getAbsolutePath();
        this.UIFrame = new JFrame();
        this.UIPanel = new JLayeredPane();
        this.stackList = new ArrayList<ArrayList<UICard>>();

        UIFrame.setSize(1823, 811); // dimensions equal to absolute background size
        UIFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // frame should exit on closing the window

        // setting background
        JLabel j = new JLabel(new ImageIcon(ImgPath + "\\background.png"));
        j.setBounds(0, 0, 1823, 811);
        UIPanel.add(j, new Integer(0)); 

        //adding panel to frame and setting visible
        UIFrame.add(UIPanel);
        UIFrame.setVisible(true);
        
        System.out.println("setting cards...");
        setCards();     //generate the cards
        System.out.println("displaying cards...");
        displayCards(fancyOrNah,initListRead()); //display initial set cards


        


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


    }


    private void displayCards(boolean fancyOrNah, ArrayList<ArrayList<String>> a)
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
        System.out.println(a.get(0).get(0) + "    "+ a.get(0).get(1));
    }

    public void moveSug(int startIndex,int startReach,int destIndex,int steps)
    {
        //start x and y from input parameter
        int startX = stackList.get(startIndex).get(startReach).posX;
        int startY = stackList.get(startIndex).get(startReach).posY;

        //end x and y from longest reach on dest
        int endX =stackList.get(destIndex).get(stackList.get(destIndex).size()-1).posX;
        int endY =stackList.get(destIndex).get(stackList.get(destIndex).size()-1).posY;

        //make vector from start to end
        int vectorX = endX-startX;
        int vectorY = endY-startY;

        //devide into steps
        int stepX = vectorX / steps;
        int stepY = vectorY / steps;


        int destDisplayDepth = stackList.get(destIndex).get(stackList.get(destIndex).size()-1).displayDepth + 1;
        int startDisplayDepth = stackList.get(startIndex).get(startReach).displayDepth;
        stackList.get(startIndex).get(startReach).displayDepth = destDisplayDepth;

        for(int i = 1; i <= steps; i++)
        {
            stackList.get(startIndex).get(startReach).move(startX+(stepX*i),startY+(stepY*i));
            sleep(60);
            if(steps - i == 3)UIPanel.setLayer(stackList.get(startIndex).get(startReach).getLabel(), new Integer(stackList.get(startIndex).get(startReach).displayDepth) * 100);
        }

        stackList.get(startIndex).get(startReach).move(startX,startY);
        stackList.get(startIndex).get(startReach).displayDepth = startDisplayDepth;
        UIPanel.setLayer(stackList.get(startIndex).get(startReach).getLabel(), new Integer(stackList.get(startIndex).get(startReach).displayDepth) * 100);
    }

    public void failCheck()
    {
        for(int i = 1; i <= 7; i++)
        {
            for(int u = 0; u < stackList.get(i).size(); u++)
            {
               System.out.println("(x,y): ("+ stackList.get(i).get(u).posX + "," + stackList.get(i).get(u).posY + ") at displaydepth: "+stackList.get(i).get(u).displayDepth);
            }

        }
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