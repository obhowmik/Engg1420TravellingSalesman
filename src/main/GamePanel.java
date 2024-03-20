package main;

import PlayerEntity.Player;
import PlayerEntity.Player2;
import object.SuperObject;
import tile.TileManager;
import PlayerEntity.BattleSystem;


import java.awt.*;
import javax.swing.*;
import PlayerEntity.PlayerEntity;

public class GamePanel extends JPanel implements Runnable {

    // screen settings

    final int OriginalTileSize = 32; // 32*32 pixels Tile size
    final int scale = 2;

    public final int tileSize = OriginalTileSize * scale;
    public final int maxScreenCol = 14;
    public final int maxScreenRow = 14;

    public final int screenWidth = tileSize * maxScreenCol; // 1056 px
    public final int screenHeight = tileSize * maxScreenRow; // 1056 px
    public main.TurnManager turnManager;

    // World settings
    public final int maxWorldCol =14;
    public final int maxWorldRow = 14;
    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;

    int FPS =60;



    TileManager tileM = new TileManager(this);

    KeyHandler keyH = new KeyHandler();
    Thread gameThread;
    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter aSetter = new AssetSetter(this);
    Player player = new Player(this, keyH, 100, 500, 50); // Player 1 with health=100, money=500, power=10
    Player2 player2 = new Player2(this, keyH, 100, 300, 12); // Player 2 with different attributes

    public SuperObject obj [] = new SuperObject[50];
    BattleSystem battleSystem = new BattleSystem();





    //set player default position
    int playerX = 100;
    int playerY = 100;
    int playerSpeed = 5;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        startGameThread();
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        PlayerEntity[] playerEntities = {player, player2};
        battleSystem = new BattleSystem();


        turnManager = new main.TurnManager(playerEntities);

    }

    public void setupGame() {
        aSetter.setObject();
    }


    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        // Game loop for updating and rendering the game at a consistent rate
        double drawInterval = (double) 1000000000 / FPS; // Time between draws in nanoseconds

        double delta = 0;

        long lastTime = System.nanoTime(); // Stores the last time the loop was run

        long currentTime; // Current time for each loop iteration

        long timer = 0; // Timer for tracking FPS

        int drawCount = 0; // Counter for the number of draws (frames) in a second


        while (gameThread != null) { // The game loop checks if the gameThread object is not null to continue running the loop
            // Capture the current system time in nanoseconds for this loop iteration
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;

            timer += (currentTime - lastTime);

            lastTime = currentTime;

            if (delta >= 1) {
                // Update the game state (e.g., player position, game logic)
                update();

                repaint();

                delta--;

                drawCount++;
            }

            if (timer >= 1000000000) {
                //System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }
        }
    }

    public  void update() {
        player.update();
        player2.update();

        // Battle logic
        if (player.getWorldX() == player2.getWorldX() && player.getWorldY() == player2.getWorldY()) {
            // Engage in battle
            battleSystem.engageBattle(player, player2);
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // to add the grid to the map
        Graphics2D g2 = (Graphics2D)g;

        tileM.draw(g2);

        // object
        for(int i =0; i<obj.length; i++) {
            if (obj[i]!= null){
                obj[i].draw(g2, this);
            }

        }

        player.draw(g2);
        player2.draw(g2);
        drawGrid(g);
        g2.dispose();

    }

    private void drawGrid(Graphics g) {
        g.setColor(Color.black);
        // Draw the vertical lines
        for (int x = 0; x <= screenWidth; x += tileSize) {
            g.drawLine(x, 0, x, screenHeight);
        }
        // Draw the horizontal lines
        for (int y = 0; y <= screenHeight; y += tileSize) {
            g.drawLine(0, y, screenWidth, y);
        }
    }


}
