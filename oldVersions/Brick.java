package oldVersions;

import java.awt.geom.Rectangle2D;
import java.awt.Color;
class Brick extends Rectangle2D.Double{
    final static Color[] colours = new Color[]{Color.BLUE,Color.BLUE.darker(),Color.GREEN,Color.GREEN.darker(),Color.RED,Color.YELLOW,Color.CYAN,Color.PINK,Color.ORANGE};
    final static int WIDTH = 5;
    final static int HEIGHT = 2;
    //int x,y;
    Color color;
    boolean visible;
    Brick(){
        color = Color.BLACK;
        visible = false;
    }

    public static Brick getRandomBrick(int x,int y,int scale){
        Brick ob = new Brick();
        //ob.setLocation(x,y);
        ob.setRect(x*scale,y*scale,WIDTH*scale,HEIGHT*scale);
        ob.setVisible(true);
        //ob.setColor(colours[(int)(Math.random()*(colours.length))]);
        ob.setColor(new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255)));
        return ob;
    }

    void setColor(Color color){
        this.color = color;
    }

    void changeColor(){
        setColor(new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255)));
    }

    Color getColor(){
        return this.color;
    }

    void setVisible(boolean visible){
        this.visible = visible;
    }

    boolean isVisible(){
        return this.visible;
    }
    // void setLocation(int x,int y){
    // this.x = x;
    // this.y = y;
    // }
}