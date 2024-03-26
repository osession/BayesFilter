
import java.awt.event.*;
import java.awt.Color;
import java.awt.Graphics;
import java.lang.*;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.io.*;
import java.net.*;
import java.util.Arrays;


// This class draws the probability map and value iteration map that you create to the window
// You need only call updateProbs() and updateValues() from your theRobot class to update these maps
class mySmartMap extends JComponent implements KeyListener {
    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int EAST = 2;
    public static final int WEST = 3;
    public static final int STAY = 4;


    int currentKey;

    int winWidth, winHeight;
    double sqrWdth, sqrHght;
    Color gris = new Color(170,170,170);
    Color myWhite = new Color(220, 220, 220);
    WorldServer mundo;
    
    int gameStatus;

    double[][] probs;
    double[][] vals;
    
    public mySmartMap(int w, int h, WorldServer wld) {
        mundo = wld;
        probs = new double[mundo.width][mundo.height];
        vals = new double[mundo.width][mundo.height];
        winWidth = w;
        winHeight = h;
        
        sqrWdth = (double)w / mundo.width;
        sqrHght = (double)h / mundo.height;
        currentKey = -1;
        
        addKeyListener(this);
        
        gameStatus = 0;
    }
    
    public void addNotify() {
        super.addNotify();
        requestFocus();
    }
    
    public void setWin() {
        gameStatus = 1;
        repaint();
    }
    
    public void setLoss() {
        gameStatus = 2;
        repaint();
    }
    
    public void updateProbs(double[][] _probs) {
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                probs[x][y] = _probs[x][y];
            }
        }
        
        repaint();
    }
    
    public void updateValues(double[][] _vals) {
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                vals[x][y] = _vals[x][y];
            }
        }
        
        repaint();
    }

    public void paint(Graphics g) {
        paintProbs(g);
        //paintValues(g);
    }

    public void paintProbs(Graphics g) {
        double maxProbs = 0.0;
        int mx = 0, my = 0;
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (probs[x][y] > maxProbs) {
                    maxProbs = probs[x][y];
                    mx = x;
                    my = y;
                }
                if (mundo.grid[x][y] == 1) {
                    g.setColor(Color.black);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 0) {
                    //g.setColor(myWhite);
                    
                    int col = (int)(255 * Math.sqrt(probs[x][y]));
                    if (col > 255)
                        col = 255;
                    g.setColor(new Color(255-col, 255-col, 255));
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 2) {
                    g.setColor(Color.red);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 3) {
                    g.setColor(Color.green);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
            
            }
            if (y != 0) {
                g.setColor(gris);
                g.drawLine(0, (int)(y * sqrHght), (int)winWidth, (int)(y * sqrHght));
            }
        }
        for (int x = 0; x < mundo.width; x++) {
                g.setColor(gris);
                g.drawLine((int)(x * sqrWdth), 0, (int)(x * sqrWdth), (int)winHeight);
        }
        
        //System.out.println("repaint maxProb: " + maxProbs + "; " + mx + ", " + my);
        
        g.setColor(Color.green);
        g.drawOval((int)(mx * sqrWdth)+1, (int)(my * sqrHght)+1, (int)(sqrWdth-1.4), (int)(sqrHght-1.4));
        
        if (gameStatus == 1) {
            g.setColor(Color.green);
            g.drawString("You Won!", 8, 25);
        }
        else if (gameStatus == 2) {
            g.setColor(Color.red);
            g.drawString("You're a Loser!", 8, 25);
        }
    }
    
    public void paintValues(Graphics g) {
        double maxVal = -99999, minVal = 99999;
        int mx = 0, my = 0;
        
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] != 0)
                    continue;
                
                if (vals[x][y] > maxVal)
                    maxVal = vals[x][y];
                if (vals[x][y] < minVal)
                    minVal = vals[x][y];
            }
        }
        if (minVal == maxVal) {
            maxVal = minVal+1;
        }

        int offset = winWidth+20;
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] == 1) {
                    g.setColor(Color.black);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 0) {
                    //g.setColor(myWhite);
                    
                    //int col = (int)(255 * Math.sqrt((vals[x][y]-minVal)/(maxVal-minVal)));
                    int col = (int)(255 * (vals[x][y]-minVal)/(maxVal-minVal));
                    if (col > 255)
                        col = 255;
                    g.setColor(new Color(255-col, 255-col, 255));
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 2) {
                    g.setColor(Color.red);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 3) {
                    g.setColor(Color.green);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
            
            }
            if (y != 0) {
                g.setColor(gris);
                g.drawLine(offset, (int)(y * sqrHght), (int)winWidth+offset, (int)(y * sqrHght));
            }
        }
        for (int x = 0; x < mundo.width; x++) {
                g.setColor(gris);
                g.drawLine((int)(x * sqrWdth)+offset, 0, (int)(x * sqrWdth)+offset, (int)winHeight);
        }
    }

    
    public void keyPressed(KeyEvent e) {
        //System.out.println("keyPressed");
    }
    public void keyReleased(KeyEvent e) {
        //System.out.println("keyReleased");
    }
    public void keyTyped(KeyEvent e) {
        char key = e.getKeyChar();
        //System.out.println(key);
        
        switch (key) {
            case 'i':
                currentKey = NORTH;
                break;
            case ',':
                currentKey = SOUTH;
                break;
            case 'j':
                currentKey = WEST;
                break;
            case 'l':
                currentKey = EAST;
                break;
            case 'k':
                currentKey = STAY;
                break;
        }
    }
}


// This is the main class that you will add to in order to complete the lab
public class theRobot extends JFrame {
    // Mapping of actions to integers
    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int EAST = 2;
    public static final int WEST = 3;
    public static final int STAY = 4;
    public static final int OPEN_SPACE = 0;
    public static final int WALL = 1;
    public static final int STAIRWELL = 2;
    public static final int GOAL = 3;

    Color bkgroundColor = new Color(230,230,230);
    
    static mySmartMap myMaps; // instance of the class that draw everything to the GUI
    String mundoName;
    
    WorldServer mundo; // mundo contains all the information about the world.  See World.java
    double moveProb, sensorAccuracy;  // stores probabilies that the robot moves in the intended direction
                                      // and the probability that a sonar reading is correct, respectively
    
    // variables to communicate with the Server via sockets
    public Socket s;
	public BufferedReader sin;
	public PrintWriter sout;
    
    // variables to store information entered through the command-line about the current scenario
    boolean isManual = false; // determines whether you (manual) or the AI (automatic) controls the robots movements
    boolean knownPosition = false;
    int startX = -1, startY = -1;
    int decisionDelay = 250;
    
    // store your probability map (for position of the robot in this array
    double[][] probs;
    
    // store your computed value of being in each state (x, y)
    double[][] Vs;
    
    public theRobot(String _manual, int _decisionDelay) {
        // initialize variables as specified from the command-line
        if (_manual.equals("automatic"))
            isManual = false;
        else
            isManual = true;
        decisionDelay = _decisionDelay;
        
        // get a connection to the server and get initial information about the world
        initClient();
    
        // Read in the world
        mundo = new WorldServer(mundoName);
        
        // set up the GUI that displays the information you compute
        int width = 500;
        int height = 500;
        int bar = 20;
        setSize(width,height+bar);
        getContentPane().setBackground(bkgroundColor);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, width, height+bar);
        myMaps = new mySmartMap(width, height, mundo);
        getContentPane().add(myMaps);
        
        setVisible(true);
        setTitle("Probability and Value Maps");
        
        doStuff(); // Function to have the robot move about its world until it gets to its goal or falls in a stairwell
    }
    
    // this function establishes a connection with the server and learns
    //   1 -- which world it is in
    //   2 -- it's transition model (specified by moveProb)
    //   3 -- it's sensor model (specified by sensorAccuracy)
    //   4 -- whether it's initial position is known.  if known, its position is stored in (startX, startY)
    public void initClient() {
        int portNumber = 3333;
        String host = "localhost";
        
        try {
			s = new Socket(host, portNumber);
            sout = new PrintWriter(s.getOutputStream(), true);
			sin = new BufferedReader(new InputStreamReader(s.getInputStream()));
            
            mundoName = sin.readLine();
            moveProb = Double.parseDouble(sin.readLine());
            sensorAccuracy = Double.parseDouble(sin.readLine());
            System.out.println("Need to open the mundo: " + mundoName);
            System.out.println("moveProb: " + moveProb);
            System.out.println("sensorAccuracy: " + sensorAccuracy);
            
            // find out of the robots position is know
            String _known = sin.readLine();
            if (_known.equals("known")) {
                knownPosition = true;
                startX = Integer.parseInt(sin.readLine());
                startY = Integer.parseInt(sin.readLine());
                System.out.println("Robot's initial position is known: " + startX + ", " + startY);
            }
            else {
                System.out.println("Robot's initial position is unknown");
            }
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
    }

    // function that gets human-specified actions
    // 'i' specifies the movement up
    // ',' specifies the movement down
    // 'l' specifies the movement right
    // 'j' specifies the movement left
    // 'k' specifies the movement stay
    int getHumanAction() {
        System.out.println("Reading the action selected by the user");
        while (myMaps.currentKey < 0) {
            try {
                Thread.sleep(50);
            }
            catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        int a = myMaps.currentKey;
        myMaps.currentKey = -1;
        
        System.out.println("Action: " + a);
        
        return a;
    }
    
    // initializes the probabilities of where the AI is
    void initializeProbabilities() {
        probs = new double[mundo.width][mundo.height];
        // if the robot's initial position is known, reflect that in the probability map
        if (knownPosition) {
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if ((x == startX) && (y == startY))
                        probs[x][y] = 1.0;
                    else
                        probs[x][y] = 0.0;
                }
            }
        }
        else {  // otherwise, set up a uniform prior over all the positions in the world that are open spaces
            int count = 0;
            
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if (mundo.grid[x][y] == 0)
                        count++;
                }
            }
            
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if (mundo.grid[x][y] == 0)
                        probs[x][y] = 1.0 / count;
                    else
                        probs[x][y] = 0;
                }
            }
        }
        
        myMaps.updateProbs(probs);
    }

    // Note: sonars is a bit string with four characters, specifying the sonar reading in the direction of North, South, East, and West
    //       For example, the sonar string 1001, specifies that the sonars found a wall in the North and West directions, but not in the South and East directions
    void updateProbabilities(int action, String sonars) {
        probs = predictBeliefs(action);
        System.out.println("Sonars: " + sonars);
        double total_denominator = 0;
        for (int i = 0; i < mundo.width; i++) {
            for (int j = 0; j < mundo.height; j++) {
                total_denominator += probs[i][j];
            }
        }
        for (int i = 0; i < mundo.width; i++) {
            for (int j = 0; j < mundo.height; j++) {
                probs[i][j] = probs[i][j] / total_denominator;
            }
        }
        probs = updateBeliefsBasedOnSonar(sonars);
        // normalize
        total_denominator = 0;
        for (int i = 0; i < mundo.width; i++) {
            for (int j = 0; j < mundo.height; j++) {
                total_denominator += probs[i][j];
            }
        }
        for (int i = 0; i < mundo.width; i++) {
            for (int j = 0; j < mundo.height; j++) {
                probs[i][j] = probs[i][j] / total_denominator;
            }
        }
        myMaps.updateProbs(probs); // call this function after updating your probabilities so that the
                                   //  new probabilities will show up in the probability map on the GUI
    }

    private double[][] predictBeliefs(int action) {
        System.out.println("Action: " + action);
        double[][] newBeliefs = new double[mundo.width][mundo.height];
        for (int i = 0; i < mundo.width; i++) {
            for (int j = 0; j < mundo.height; j++) {
                if (mundo.grid[i][j] != 0) {
                    continue;
                }
                // move up
                if ((j - 1) >= 0) {
                    if (action == NORTH) {
                        newBeliefs[i][j - 1] += (moveProb * probs[i][j]);
                    } else {
                        newBeliefs[i][j - 1] += (((1 - moveProb)/4) * probs[i][j]);
                    }
                }
                // move down
                if ((j + 1) < mundo.height) {
                    if (action == SOUTH) {
                        newBeliefs[i][j + 1] += (moveProb * probs[i][j]);
                    } else {
                        newBeliefs[i][j + 1] += (((1 - moveProb)/4) * probs[i][j]);
                    }
                }
                // move left
                if ((i - 1) >= 0) {
                    if (action == WEST) {
                        newBeliefs[i - 1][j] += (moveProb * probs[i][j]);
                    } else {
                        newBeliefs[i - 1][j] += (((1 - moveProb)/4) * probs[i][j]);
                    }
                }
                // move right
                if ((i + 1) < mundo.width) {
                    if (action == EAST) {
                        newBeliefs[i + 1][j] += (moveProb * probs[i][j]);
                    } else {
                        newBeliefs[i + 1][j] += (((1 - moveProb)/4) * probs[i][j]);
                    }
                }
                // stay put
                if (action == STAY) {
                    newBeliefs[i][j] += (moveProb * probs[i][j]);
                } else {
                    newBeliefs[i][j] += (((1 - moveProb)/4) * probs[i][j]);
                }
            }
        }
        probs = newBeliefs;
        return probs;
    }

    private double[][] updateBeliefsBasedOnSonar(String sonars) {
        // Update beliefs based on sonar measurements
        for (int i = 0; i < mundo.width; i++) {
            for (int j = 0; j < mundo.height; j++) {
                // Update probability based on sonar measurement at position i, j
                if (mundo.grid[i][j] != 0) {
                    continue;
                }
                // North Sensor
                if ((j - 1) >= 0) {
                    int grid_val = mundo.grid[i][j - 1];
                    int sensor_val = sonars.charAt(0) - '0';
                    if (grid_val == sensor_val) {
                        probs[i][j] += (probs[i][j] * sensorAccuracy);
                    } else {
                        probs[i][j] += probs[i][j] * (1 - sensorAccuracy)/4;
                    }
                }
                // South Sensor
                if ((j + 1) < mundo.height) {
                    int grid_val = mundo.grid[i][j + 1];
                    int sensor_val = sonars.charAt(1) - '0';
                    if (grid_val == sensor_val) {
                        probs[i][j] += (probs[i][j] * sensorAccuracy);
                    } else {
                        probs[i][j] += probs[i][j] * (1 - sensorAccuracy)/4;
                    }
                }
                // East Sensor
                if ((i + 1) < mundo.width) {
                    int grid_val = mundo.grid[i + 1][j];
                    int sensor_val = sonars.charAt(2) - '0';
                    if (grid_val == sensor_val) {
                        probs[i][j] += (probs[i][j] * sensorAccuracy);
                    } else {
                        probs[i][j] += probs[i][j] * (1 - sensorAccuracy)/4;
                    }
                }
                // West Sensor
                if ((i - 1) >= 0) {
                    int grid_val = mundo.grid[i - 1][j];
                    int sensor_val = sonars.charAt(3)  - '0';
                    if (grid_val == sensor_val) {
                        probs[i][j] += (probs[i][j] * sensorAccuracy);
                    } else {
                        probs[i][j] += probs[i][j] * (1 - sensorAccuracy)/4;
                    }
                }
            }
        }
        return probs;
    }


    // ***********************************************************************************************
    // Implement value iteration to compute the optimal policy
    void valueIteration() {
        double gamma = 0.9; // Discount factor
        int maxIterations = 1000; // Maximum number of iterations
        double epsilon = 0.01; // Convergence threshold

        Vs = new double[mundo.width][mundo.height]; // Initialize values
        for (int x = 0; x < mundo.width; x++) {
            for (int y = 0; y < mundo.height; y++) {
                if (mundo.grid[x][y] == GOAL) {
                    Vs[x][y] = 100;
                } else if (mundo.grid[x][y] == STAIRWELL || mundo.grid[x][y] == WALL) {
                    Vs[x][y] = -20;
                } else {
                    Vs[x][y] = -1;
                }
            }
        }

        // Perform value iteration
        for (int i = 0; i < maxIterations; i++) {
            double[][] Vsp = new double[mundo.width][mundo.height]; // Temporary array for updated values
            double delta = 0.0; // Change in value function

            for (int x = 0; x < mundo.width; x++) {
                for (int y = 0; y < mundo.height; y++) {
                    if (mundo.grid[x][y] != WALL && mundo.grid[x][y] != STAIRWELL && mundo.grid[x][y] != GOAL) {
                        double[] expectedRewards = new double[4]; // Rewards for each action

                        double immediateReward = Vs[x][y];
                        // Compute expected rewards for each action
                        for (int action = 0; action < 4; action++) {
                            double futureReward = 0.0;
                            // Compute immediate reward based on action
                            if (action == NORTH) {
                                if (y - 1 >= 0) {
                                    futureReward = Vs[x][y - 1];
                                } else { // stay in place
                                    futureReward = Vs[x][y];
                                }
                            } else if (action == SOUTH) {
                                if (y + 1 < mundo.height) {
                                    futureReward = Vs[x][y + 1];
                                } else { // stay in place
                                    futureReward = Vs[x][y];
                                }
                            } else if (action == EAST) {
                                if (x + 1 < mundo.width) {
                                    futureReward = Vs[x + 1][y];
                                } else { // stay in place
                                    futureReward = Vs[x][y];
                                }
                            } else { // action is WEST
                                if (x - 1 >= 0) {
                                    futureReward = Vs[x - 1][y];
                                } else { // stay in place
                                    futureReward = Vs[x][y];
                                }
                            }

                            expectedRewards[action] = immediateReward + gamma * futureReward;
                        }

                        // Update value function with the maximum expected reward
                        Vsp[x][y] = Arrays.stream(expectedRewards).max().getAsDouble();
                        delta = Math.max(delta, Math.abs(Vsp[x][y] - Vs[x][y]));
                    }
                }
            }

            // Update value function
            Vs = Vsp;

            // Check for convergence
            if (delta < epsilon) {
                break;
            }
        }
    }
    // ***********************************************************************************************

    // This is the function you'd need to write to make the robot move using your AI;
    // You do NOT need to write this function for this lab; it can remain as is
    int automaticAction() {
//        int robotX = -1;
//        int robotY = -1;
//
//        // Determine robot's current position based on probabilities
//        // For simplicity, assume the position with the highest probability
//        double maxProb = -1.0;
//        for (int x = 0; x < mundo.width; x++) {
//            for (int y = 0; y < mundo.height; y++) {
//                if (probs[x][y] > maxProb) {
//                    maxProb = probs[x][y];
//                    robotX = x;
//                    robotY = y;
//                }
//            }
//        }
//
//        // Determine the action with the maximum value
//        int bestAction = STAY;
//        double maxActionValue = -Double.MAX_VALUE;
//        for (int action = 0; action < 5; action++) {
//            if (isValidAction(robotX, robotY, action)) {
//                // Implement action-value calculation based on the learned value function
//                double actionValue = calculateActionValue(robotX, robotY, action);
//                if (actionValue > maxActionValue) {
//                    maxActionValue = actionValue;
//                    bestAction = action;
//                }
//            }
//        }
        int bestAction = STAY;
        if (knownPosition) {
            double bestActionValue = Double.NEGATIVE_INFINITY;
            for (int action = 0; action < 4; action ++) {
                double reward;
                if (action == NORTH) {
                    if (startY - 1 >= 0) {
                        reward = Vs[startX][startY - 1];
                    } else { // stay in place
                        reward = Vs[startX][startY];
                    }
                } else if (action == SOUTH) {
                    if (startY + 1 < mundo.height) {
                        reward = Vs[startX][startY + 1];
                    } else { // stay in place
                        reward = Vs[startX][startY];
                    }
                } else if (action == EAST) {
                    if (startX + 1 < mundo.width) {
                        reward = Vs[startX + 1][startY];
                    } else { // stay in place
                        reward = Vs[startX][startY];
                    }
                } else { // action is west
                    if (startX - 1 >= 0) {
                        reward = Vs[startX - 1][startY];
                    } else { // stay in place
                        reward = Vs[startX][startY];
                    }
                }

                if (reward >= bestActionValue) {
                    bestActionValue = reward;
                    bestAction = action;
                }
            }
            if (bestAction == NORTH) {
                startY -=  1;
            } else if (bestAction == SOUTH) {
                startY += 1;
            } else if (bestAction == EAST) {
                startX += 1;
            } else if (bestAction == WEST) {
                startX -= 1;
            }
        } else {
            // implement this later
        }

        return bestAction;
    }

    // **********************************************************************************************
    // Helper method to check if an action is valid from a given position
    boolean isValidAction(int x, int y, int action) {
        // Implement validation based on the grid world
        // Check if the action leads to a valid grid cell
        return true; // Placeholder
    }

    // Helper method to calculate the value of taking a specific action from a given position
    double calculateActionValue(int x, int y, int action) {
        // Implement calculation based on the learned value function
        return 0.0; // Placeholder
    }
    // ***********************************************************************************************
    
    void doStuff() {
        int action;
        
        //valueIteration();  // TODO: function you will write in Part II of the lab
        initializeProbabilities();  // Initializes the location (probability) map
        
        while (true) {
            try {
                if (isManual)
                    action = getHumanAction();  // get the action selected by the user (from the keyboard)
                else
                    valueIteration();
                    action = automaticAction(); // TODO: get the action selected by your AI;
                                                // you'll need to write this function for part III
                
                sout.println(action); // send the action to the Server
                
                // get sonar readings after the robot moves
                String sonars = sin.readLine();
                //System.out.println("Sonars: " + sonars);
            
                updateProbabilities(action, sonars); // TODO: this function should update the probabilities of where the AI thinks it is
                
                if (sonars.length() > 4) {  // check to see if the robot has reached its goal or fallen down stairs
                    if (sonars.charAt(4) == 'w') {
                        System.out.println("I won!");
                        myMaps.setWin();
                        break;
                    }
                    else if (sonars.charAt(4) == 'l') {
                        System.out.println("I lost!");
                        myMaps.setLoss();
                        break;
                    }
                }
                else {
                    // here, you'll want to update the position probabilities
                    // since you know that the result of the move as that the robot
                    // was not at the goal or in a stairwell
                }
                Thread.sleep(decisionDelay);  // delay that is useful to see what is happening when the AI selects actions
                                              // decisionDelay is specified by the send command-line argument, which is given in milliseconds
            }
            catch (IOException e) {
                System.out.println(e);
            }
            catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // java theRobot [manual/automatic] [delay]
    public static void main(String[] args) {
        theRobot robot = new theRobot(args[0], Integer.parseInt(args[1]));  // starts up the robot
    }
}