package oldVersions;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
public class BrickFx extends Rectangle{
    final static Color[] colours = new Color[]{Color.BLUE,Color.BLUE.darker(),Color.GREEN,Color.GREEN.darker(),Color.RED,Color.YELLOW,Color.CYAN,Color.PINK,Color.ORANGE};
    final static int WIDTH = 5;
    final static int HEIGHT = 2;
    //int x,y;
    //Color color;
    boolean visible;
    BrickFx(double x, double y, double width, double height){
        super(x,y,width,height);
        //color = Color.BLACK;
        visible = false;
    }

    public static BrickFx getRandomBrick(int x,int y,int scale){
        BrickFx ob = new BrickFx(x*scale,y*scale,WIDTH*scale,HEIGHT*scale);
        //ob.setLocation(x,y);
        //ob.setRect();
        ob.setVisible(true);
        //ob.setColor(colours[(int)(Math.random()*(colours.length))]);
        ob.setFill(Color.rgb((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255)));
        return ob;
    }

    //void setColor(Color color){
    //    this.color = color;
    //}

    void changeColor(){
        setFill(Color.rgb((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255)));
    }

    Color getColor(){
        return this.getColor();
    }

    // void setVisible(boolean visible){
        // this.visible = visible;
    // }

    // boolean isVisible(){
        // return this.visible;
    // }
    
    // void setLocation(int x,int y){
    // this.x = x;
    // this.y = y;
    // }
}