import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;


public class GamePanel extends JPanel implements ActionListener {
    //szerokość okna
    static final int SCREEN_WIDTH=600;
    //wysokość okna
    static final int SCREEN_HEIGHT=600;
    //jak duże chcemy obiekty w oknie
    static final int UNIT_SIZE=25;
    //jak dużo możemy zmieścić obiektów na oknie
    static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
    //czym większe tym gra wolniejsza
    static final int DELEY=75;
    //koordynaty naszego 'snake' x,y
    final int x[]=new int[GAME_UNITS];
    final int y[]=new int[GAME_UNITS];
    //początkowa liczba części
    int bodyParts=6;
    //liczba jabłek w oknie
    int applesEaten;
    //pozycja jabłka
    int appleX;
    int appleY;
    //kierunek w którym się udaje początkowo w prawo(R)/ Lewo(L)/ Góra(U)/ Dół(D)
    char direction='D';
    //sprawdzenie czy gra działa
    boolean running =false;
    Timer timer;
    Random random;

    GamePanel(){
        random= new Random();
        //wyznacza nam preferowany rozmiar okna. W Dimension używamy stałych wcześniej wyznaczonych
        this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        //metoda decyduje czy metoda ma mieć focus na jakimś miejscu
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();

    }
    //Rozpocznie grę
    public void startGame(){
        newApple();
        running=true;
        timer= new Timer(DELEY,this);
        timer.start();

    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){
        if(running) {
			/*
			for(int i=0;i<SCREEN_HEIGHT/UNIT_SIZE;i++) {
				g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
				g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
			}
			*/
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for(int i = 0; i< bodyParts;i++) {
                if(i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
                else {
                    g.setColor(new Color(45,180,0));
                    //g.setColor(new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255)));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(Color.red);
            g.setFont( new Font("Ink Free",Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: "+applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: "+applesEaten))/2, g.getFont().getSize());
        }
        else {
            gameOver(g);
        }
    }


    // Tworzy nowe jabłko
    public void newApple(){
        //tworzy jakbłko w koordynatach x,y (int)<-aby program nie wywalił błędu
        appleX=random.nextInt((int)SCREEN_WIDTH/UNIT_SIZE)*UNIT_SIZE;
        appleY=random.nextInt((int)SCREEN_HEIGHT/UNIT_SIZE)*UNIT_SIZE;
    }
    //poruszanie się wężem
    public void move(){
        for(int i = bodyParts;i>0;i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }

        switch(direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }


    }
    //sprawdza czy jabłko zostało zjedzone
    public  void checkApple(){
        if((x[0]== appleX)&& (y[0]==appleY)){
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions(){
        //sprawdza czy głowa zderzyła się z ciałem
        for(int i=bodyParts;i>0;i--){
            if((x[0]==x[i])&&(y[0]==y[i])){
                running=false;
            }
        }
        //sprawdza czy nie zderzył się ze ścianą
        if(x[0] < 0){
             running=false;
        }
        if(x[0]>SCREEN_WIDTH){
            running=false;

        }
        if(y[0]>SCREEN_HEIGHT) {
            running = false;
        }
        if(y[0] < 0){
            running=false;
        }
        if(!running){
            timer.stop();
        }
    }
    public void gameOver(Graphics g){
        //Score
        g.setColor(Color.red);
        g.setFont( new Font("Ink Free",Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: "+applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: "+applesEaten))/2, g.getFont().getSize());
        //Game Over text
        g.setColor(Color.red);
        g.setFont( new Font("Ink Free",Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);
    }
    @Override
    public void actionPerformed(ActionEvent e) {

        if(running){
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }
    public class MyKeyAdapter extends KeyAdapter{
// An abstract adapter class for receiving keyboard events. The methods in this class are empty. This class exists as convenience for creating listener objects.
// Extend this class to create a KeyEvent listener and override the methods for the events of interest.
// (If you implement the KeyListener interface, you have to define all of the methods in it. This abstract class defines null methods for them all, so you can only have to define methods for events you care about.)
// Create a listener object using the extended class and then register it with a component using the component's addKeyListener method. When a key is pressed, released, or typed, the relevant method in the listener object is invoked, and the KeyEvent is passed to it

        @Override
        public void keyPressed(KeyEvent e) {
            switch(e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if(direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if(direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if(direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if(direction != 'U') {
                        direction = 'D';
                    }
                    break;

            }
        }
    }
}
