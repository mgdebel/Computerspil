import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.util.stream.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * A GUI written in Java Swing which wraps around a Game instance.
 * Is used to get a graphical view of the data in a Game.
 * 
 * Keyboard controls and <space> to turnAround.
 * 
 * @author Nikolaj Ignatieff Schwartzbach
 * @version 1.1.0
 *
 */
public class GUI {

    /** The JPanel where the world are drawn. */
    private WorldPanel panel;
    private JPanel superpanel, buttons;
    
    /** The JFrame, the main GUI. */
    private JFrame mainFrame, options;
    
    /** The JFileChooser object used for I/O (logs) */
    private JFileChooser fileChooser;

    /** More graphical components */
    private JRadioButton slowButton, medButton, fastButton, sonicButton;
    
    /** Check-boxes */
    private JCheckBox random, greedy, smart;
    
    /** Textfields */
    private JTextField tollSizeTextField, robberyTextField;
    
    /** Buttons */
    private JButton optionsButton, newGameButton, pauseResumeButton, abortButton;
    
    /** Reference to the Game instance */
    private Game game;
    
    /** Delay in ms between subsequent frames */
    private int frameDelay = 500;
    
    /** Width and height of the inner window, in pixels */
    public int WIDTH = 520,
               HEIGHT = 635;
    
    /** Main game Timer */
    private Timer timer;
    
    /** Whether or not this game is currently paused */
    private boolean paused = false;
    
    /** Reference to the City which is currently under the mouse */
    public static City hover;
    
    /** The current game speed (0 = stop, 1 = slow, .. ) */
    public static int speed = 2;
    
    /** Maps which keys are being held down at this step */
    private Map<Integer, Boolean> press = new HashMap<>();
    
    private boolean hasGameStarted = false;
    
    private double currentDirection = 0;
    
    private boolean usedKeyboard = false;
    
    private boolean optionsShowing = false;

    private int CTRL = Event.CTRL_MASK,                                // CTRL
                CTRL_SHIFT = Event.CTRL_MASK | Event.SHIFT_MASK;    // CTRL+SHIFT
    
    /**
     * Constructor for the GUI class.
     * Creates a Game instance autonomously.
     */
    private GUI(){
        
        //Initialize Game
        game = Game.fromFile("network.dat");
        
        //Initialize buttons
        buttons = createButtonPanel();      
        options = createOptionsDialogBox();
        
        //Initialize ActorPanel
        panel = new WorldPanel(game);
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        
        
        //Handle mouse click events in the inner window
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //Click on cities
                //System.out.println("Clicked at: " + e.getX() + ", " + e.getY());
                for(Country country : game.getCountries()){
                    for(City c : country.getCities()){
                        Point p = game.getPosition(c);
                        double dist = Math.hypot(p.getX() - e.getX(), p.getY() - e.getY());
                        if(dist < WorldPanel.MIN_CIRCLE_RADIUS + 5){
                            clickCity(c);
                        }
                    }
                }
                
                //Click to change game speed
                if(e.getX()>280 && e.getX()<280+39*4+6 && e.getY()>590 && e.getY()<610){
                    speed = 1+Math.min((e.getX()-280) / 39, 4);
                    mainFrame.repaint();
                    setSpeed(speed);
                }
            }
        });
        
        //Hovering over cities
        panel.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent arg0) { }

            @Override
            public void mouseMoved(MouseEvent e){
                //System.out.println("Mouse: " + e.getX() + ", " + e.getY());
                //Assume not hovering 
                int i=0;
                panel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                
                //Hover over cities
                for(Country country : game.getCountries()){
                    for(City c : country.getCities()){
                        Point p = game.getPosition(c);
                        double dist = Math.hypot(p.getX() - e.getX(), p.getY() - e.getY());
                        if(dist < WorldPanel.MIN_CIRCLE_RADIUS + 5){
                            panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                            i++;
                            hover = c;
                            //System.out.println(p);
                            break;
                        }
                    }
                }
                if(i==0)
                    hover = null;
                
                //Hovering over game speed
                if(e.getX()>280 && e.getX()<280+39*4+6 && e.getY()>590 && e.getY()<610){
                    panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
                mainFrame.repaint();
            }
            
        });
        
        //Initialize file chooser
        fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Log files", "log");
        fileChooser.setFileFilter(filter);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        
        //Initialize the super JPanel (which contains the other JPanels)
        superpanel = new JPanel();
        superpanel.setLayout(new BoxLayout(superpanel, BoxLayout.Y_AXIS));
        superpanel.add(panel);
        superpanel.add(buttons);
        
        //Initialize and setup the the JFrame
        mainFrame = new JFrame("Nordic Traveller - Introduktion til Programmering");
        mainFrame.add(superpanel);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);
        mainFrame.setContentPane(superpanel);
        mainFrame.setVisible(true);
        
        panel.requestFocusInWindow();
        KeyListener kl = new KeyListener(){
            
            public void keyTyped(KeyEvent e) {}

            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_SPACE){
                    game.getGUIPlayer().turnAround();
                    return;
                }
                press.put(e.getKeyCode(), true);
                updateDirection();
            }

            public void keyReleased(KeyEvent e) {
                press.put(e.getKeyCode(), false);
            }
        };
        for(Component j : buttons.getComponents()){
            j.addKeyListener(kl);
        }
        panel.addKeyListener(kl);
        
        //Initialize the game timer
        timer = createDefaultTimer();

        //Apply existing settings to current game
        applyExistingSettings();
                
    }
    
    private void clickCity(City c) {
        game.clickCity(c);
    }
    
    /**
     * Returns the unsigned difference between two angles in the interval [-pi, pi].
     * @param a First angle
     * @param b Second angle
     * @return The unsigned difference in radians between a and b.
     */
    private double angleDiff(double a, double b){
       double d = Math.atan2(Math.sin(a-b), Math.cos(a-b));
       double d2 = d < 0 ? d + 2*Math.PI : d;
       return d2 > Math.PI ? 2*Math.PI - d2 : d2;
    }
    
    /**
     * Changes the state of the GUI elements
     */
    public void applyExistingSettings(){
        SwingUtilities.invokeLater(() -> {
            //Active players
            random.setSelected(game.getSettings().isActive(0));
            greedy.setSelected(game.getSettings().isActive(1));
            smart.setSelected(game.getSettings().isActive(2));
        
            //Text-fields
            tollSizeTextField.setText(""+game.getSettings().getTollToBePaid());
            robberyTextField.setText(""+game.getSettings().getRisk());    
        
            //Game speed
            speed = game.getSettings().getGameSpeed();
            setSpeed(speed);
        });
        
    }
    
    /**
     * Changes the game speed
     * @param speed The new speed of the game. 0 <= speed <= 4
     */
    public void setSpeed(int speed){
        this.speed = speed;
        SwingUtilities.invokeLater(() -> {
            //Stop the game timer, and unselect all GUI buttons
            timer.stop();
            slowButton.setSelected(false);
            medButton.setSelected(false);
            fastButton.setSelected(false);
            sonicButton.setSelected(false);
        
            //Change the speed
            switch(speed){
                case 1:
                    slowButton.setSelected(true);
                    timer.setDelay(200);
                    if(!paused)
                        timer.start();
                    break;
                case 2:
                    medButton.setSelected(true);
                    timer.setDelay(80);
                    if(!paused)
                        timer.start();
                    break;
                case 3:
                    fastButton.setSelected(true);
                    timer.setDelay(30);
                    if(!paused)
                        timer.start();
                    break;
                case 4:
                    sonicButton.setSelected(true);
                    timer.setDelay(10);
                    if(!paused)
                        timer.start();
                    break;
            }
            game.getSettings().setGameSpeed(speed);
        });
    }
    
    public void pauseResume() {
        SwingUtilities.invokeLater(() -> {
                paused = !paused;
                if(paused){
                    timer.stop();
                    pauseResumeButton.setText("Resume game");
                } else {
                    timer.start();
                    pauseResumeButton.setText("Pause game");
                }
        });
    }
    
    public void showOptions() {
        optionsShowing = true;
        
        //Stop the game timer
        timer.stop(); 
        
        //Hide the main window
        //mainFrame.setVisible(false); 
        
        //Show the options window
        options.setVisible(true);
    }
    
    private Timer createDefaultTimer() {
        Timer t = new Timer(frameDelay, e->{
            Player p = game.getGUIPlayer();
            if(usedKeyboard && p.getPosition().hasArrived()){
                City playerCity = p.getPosition().getTo();
                
                City best = null;
                double bestAngle = 2*Math.PI;
                Point posPlayer = game.getPosition(playerCity);
                for(Road r : p.getCountry().getRoads(playerCity)){
                    Point posCity = game.getPosition(r.getTo());
                    double cityAngle = Math.atan2(-posCity.y + posPlayer.y, posCity.x - posPlayer.x);
                    double newAngle = angleDiff(cityAngle, currentDirection);
                    if(newAngle < bestAngle){
                        best = r.getTo();
                        bestAngle = newAngle;
                    }
                }
                if(best != null && bestAngle < Math.PI/4){
                    clickCity(best);
                }
                
                currentDirection = -1;
                usedKeyboard = false;
            }
            game.step();
            updateButtonsAvailabillity();
            mainFrame.repaint();
        });
        
        return t;
    }
    
    private void updateButtonsAvailabillity() {
            optionsButton.setEnabled(!game.ongoing());
            pauseResumeButton.setEnabled(game.ongoing());
            abortButton.setEnabled(game.ongoing());
    }
    
    public void newGame() {
        game.reset(); 
        mainFrame.repaint();
    }
    
    /**
     * Creates the JPanel which contains the buttons in the bottom of the GUI
     * @return A JPanel containing some buttons to control the game
     */
    public JPanel createButtonPanel(){
        //Initialize the JPanel, using a GridLayout
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(1,4));                                  
        
        //Instantiate the 'New'-button
        newGameButton = new JButton("New game");
        //Connect an ActionListener
        newGameButton.addActionListener(e -> newGame());
        //Add it to the button panel
        buttons.add(newGameButton);

        //Add the 'Pause game'-button
        pauseResumeButton = new JButton("Pause game");
        //Connect an ActionListener
        pauseResumeButton.addActionListener(e -> pauseResume());
        //Add it to the button panel
        buttons.add(pauseResumeButton);
        
        //Add the 'Abort game'-button
        abortButton = new JButton("Abort game");
        abortButton.addActionListener(e -> {
            game.abort();
            updateButtonsAvailabillity();
        });
        buttons.add(abortButton);

        //Add the 'Options...'-button
        optionsButton = new JButton("Options...");
        optionsButton.setEnabled(false);
        optionsButton.addActionListener(e -> showOptions());
        buttons.add(optionsButton); 
        
        //Return the JPanel
        return buttons;
    }
    
    private void applyOptions() {
        //optionsShowing = false;
        options.dispatchEvent(new WindowEvent(options, WindowEvent.WINDOW_CLOSING));
        game.reset();
        
        //Enabled players
        game.getSettings().setActive(0, random.isSelected());
        game.getSettings().setActive(1, greedy.isSelected());
        game.getSettings().setActive(2, smart.isSelected());
        
        //Toll size & robbery
        int tollSize, riskRob = 0;
        try{
            tollSize = Integer.parseInt(tollSizeTextField.getText());
            riskRob  = Integer.parseInt(robberyTextField.getText());
            if(tollSize < 0 || riskRob < 0 || tollSize > 50 || riskRob > 50){
                JOptionPane.showMessageDialog(mainFrame, "'Toll size' and 'Risk rob' must be between 0 and 50.", "Malformed input", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e){
            JOptionPane.showMessageDialog(mainFrame, "'Toll size' and 'Risk rob' must be integers.", "Malformed input", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        game.getSettings().setRisk(riskRob);
        game.getSettings().setTollToBePaid(tollSize);
       
        
        mainFrame.setVisible(false);
        mainFrame.setVisible(true);
        mainFrame.repaint();
        
        if(slowButton.isSelected())
            speed = 1;

        if(medButton.isSelected())
            speed = 2;

        if(fastButton.isSelected())
            speed = 3;

        if(sonicButton.isSelected())
            speed = 4;
        
        setSpeed(speed);
    }
        
    /**
     * Updates the next direction based on the 'press' map.
     */
    private void updateDirection(){
        boolean l = press.getOrDefault(KeyEvent.VK_LEFT, false),
                r = press.getOrDefault(KeyEvent.VK_RIGHT, false),
                u = press.getOrDefault(KeyEvent.VK_UP, false),
                d = press.getOrDefault(KeyEvent.VK_DOWN, false);
                
        usedKeyboard = l || r || u || d && !(l && r && u && d);
        
        if(!usedKeyboard)
            return;
            
        int h = 0, v = 0;
        
        //Horizontal
        if(l && !r) h = -1;
        if(r && !l) h = 1;
        
        //Vertical
        if(u && !d) v = -1;
        if(d && !u) v = 1;
        
        currentDirection = Math.atan2(-v,h);
    }
    
    /**
     * Creates the JFrame which represents the Options...-menu.
     * @return  A JFrame representing the Options...-menu.
     */
    public JFrame createOptionsDialogBox(){
        JPanel superpanel = new JPanel();
        superpanel.setLayout(new BorderLayout());
        
        JPanel options = new JPanel();
        options.setLayout(new BorderLayout());
        
        //Active players
        JPanel playerPanel = new JPanel();
        playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));
        
        random = new JCheckBox("Random Player");
        random.setSelected(true);
        playerPanel.add(random);

        greedy = new JCheckBox("Greedy Player");
        greedy.setSelected(true);
        playerPanel.add(greedy);

        smart = new JCheckBox("Smart Player");
        smart.setSelected(true);
        playerPanel.add(smart);
        
        //Text input
        JPanel tollAndRobberyPanel = new JPanel();
        tollAndRobberyPanel.setLayout(new GridLayout(2,3,5,5));                     
        
        //Toll size
        JLabel tollSizeLabel = new JLabel("Toll to be paid:");
        tollAndRobberyPanel.add(tollSizeLabel);
        
        tollSizeTextField = new JTextField("20", 10);
        tollAndRobberyPanel.add(tollSizeTextField);

        JLabel percTollSize = new JLabel("% in [0,50]");
        tollAndRobberyPanel.add(percTollSize);
                
        //Rob risk
        JLabel robberyLabel = new JLabel("Risk of robbery:");                       
        tollAndRobberyPanel.add(robberyLabel);

        robberyTextField = new JTextField("20", 10);
        tollAndRobberyPanel.add(robberyTextField);

        JLabel percrobbery = new JLabel("% in [0,50]");
        tollAndRobberyPanel.add(percrobbery);
        
        
        //Speed options
        JPanel speedPanel = new JPanel();
        speedPanel.setLayout(new FlowLayout());

        slowButton = new JRadioButton("SLOW");
        speedPanel.add(slowButton);

        medButton = new JRadioButton("MED");
        medButton.setSelected(true);
        speedPanel.add(medButton);

        fastButton = new JRadioButton("FAST");
        speedPanel.add(fastButton);
        
        sonicButton = new JRadioButton("SONIC");
        speedPanel.add(sonicButton);
        
        ButtonGroup group = new ButtonGroup();
        group.add(slowButton);
        group.add(medButton);
        group.add(fastButton);
        group.add(sonicButton);
        
        //Add panels to superpanel
        JPanel superPlayerPanel = new JPanel();
        superPlayerPanel.setLayout(new BorderLayout());
        playerPanel.setBorder(new EmptyBorder(5,5,5,5));
        superPlayerPanel.setBorder(new TitledBorder("Active automatic players"));
        superPlayerPanel.add(playerPanel, BorderLayout.WEST);
        options.add(superPlayerPanel, BorderLayout.NORTH);

        JPanel superTextPanel = new JPanel();
        superTextPanel.add(tollAndRobberyPanel);
        tollAndRobberyPanel.setBorder(new EmptyBorder(0,5,0,5));
        superTextPanel.setBorder(new TitledBorder("Toll and robbery"));
        options.add(superTextPanel, BorderLayout.CENTER);

        options.add(speedPanel, BorderLayout.SOUTH);
        TitledBorder bSpeed = new TitledBorder("Game speed");
        speedPanel.setBorder(bSpeed);
        
        JButton applyButton = new JButton("Apply changes");
        
        superpanel.add(options, BorderLayout.NORTH);
        superpanel.add(applyButton, BorderLayout.SOUTH);
        
        JFrame frame = new JFrame("(options) Nordic Traveller - Introduktion til Programmering");
        frame.add(superpanel);
        frame.setLocationRelativeTo(null);
        frame.addWindowListener(new WindowAdapter() {
           public void windowClosing(WindowEvent e){
               optionsShowing = false;
               //Why is this code here? Shouldn't the options do nothing, if closed on the 'x' button??
               // - Asger
               /*game.reset();
               mainFrame.setVisible(false);
               mainFrame.setVisible(true);
               mainFrame.repaint();
               if(slowButton.isSelected())
                   speed = 1;

               if(medButton.isSelected())
                   speed = 2;

               if(fastButton.isSelected())
                   speed = 3;

               if(sonicButton.isSelected())
                   speed = 4;
                
               setSpeed(speed);*/
           }
        });
        frame.setResizable(false);
        frame.setContentPane(superpanel);
        frame.pack();

        applyButton.addActionListener(e -> applyOptions());
        
        return frame;
    }
    
    /**
     * Tests the Save button.
     * This method is invoked when testing the functionality of the Save button.
     */
    private void testSaveButton(){
        JOptionPane.showMessageDialog(mainFrame, "You have clicked the 'Save' button.", "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Tests the Play button.
     * This method is invoked when testing the functionality of the Play button.
     */
    private void testPlayButton(){
        JOptionPane.showMessageDialog(mainFrame, "You have clicked the 'Play' button.", "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Tests the Repeat button.
     * This method is invoked when testing the functionality of the Repeat button.
     */
    private void testRepeatButton(){
        JOptionPane.showMessageDialog(mainFrame, "You have clicked the 'Repeat' button.", "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void startGUI(){
        mainFrame.pack();
        mainFrame.repaint();
    }
    
    /**
     * Starts the game.
     */
    public static void createGameBoard() {
        if(!Files.exists(Paths.get("network.dat"))){
            JOptionPane.showMessageDialog(null, "'network.dat' does not exist in the current project. Game closing.", "Unable to start NordicTraveller", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(!Files.exists(Paths.get("map.png"))){
            JOptionPane.showMessageDialog(null, "'map.png' does not exist in the current project. Game closing.", "Unable to start NordicTraveller", JOptionPane.ERROR_MESSAGE);
            return;
        }
        GUI g = new GUI();
        g.game.abort();
        g.startGUI();
    }
}

/**
 * This class models the JPanel upon which the graphics are actually drawn.
 * This class handles the "nitty gritty" details of drawing all the roads, player icons etc.
 * @author Nikolaj Ignatieff Schwartzbach
 * @version August, 2019
 */
class WorldPanel extends JPanel {

    private static final long serialVersionUID = -4313765288063966250L;

    /* Sizes of various circles: one for city, road-dots and for players. */
    public final static int MIN_CIRCLE_RADIUS = 7,
    ROAD_CIRCLE_RADIUS = 2,
    PLAYER_RADIUS = 4;

    /* Various colors used throughout. */
    private final static Color
    COLOR_BACKGROUND     = new Color(116, 204, 244),
    COLOR_CITY_STROKE    = Color.BLACK,
    COLOR_ROAD           = Color.WHITE,
    COLOR_BORDER_ROAD    = Color.WHITE,
    COLOR_PLAYER_STROKE  = Color.BLACK,
    COLOR_TEXT           = Color.BLACK,
    COLOR_BAR_OUTLINE    = Color.BLACK,
    COLOR_BAR_TIME_FILL  = Color.BLUE,
    COLOR_BAR_SPEED_FILL = new Color(211,211,211),
    COLOR_BAR_BACKGROUND = Color.WHITE;

    /* Various fonts used throughout. */
    private final static Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 12),
    FONT_HEADER = new Font("SansSerif", Font.BOLD, 16),
    FONT_SC = new Font("SansSerif", Font.BOLD, 10);

    /* The first stroke is used for text; the second is used for drawing the roads. */
    private final static Stroke STROKE_DEFAULT = new BasicStroke(0.9f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND),
    STROKE_THICK = new BasicStroke(1.1f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);

    /* Reference to the Game object */
    private Game game;

    /* Raster of the current image displayed */
    private BufferedImage img, cityIcon, capitalIcon;
    private Map<String, BufferedImage> playerIcons = new HashMap<>();

    /**
     * Initializes a new world panel of a given size.
     * @param game Reference to the game object.
     * @param width Width (in px) of the window.
     * @param height Height (in px) of the window.
     */
    public WorldPanel(Game game) {
        this.game = game;

        for(Player p : game.getPlayers()){
            String name = p.getClass().getName();
            try{
                playerIcons.put("LogPlayer", ImageIO.read(new File("guiplayer.png")));
                playerIcons.put(name, ImageIO.read(new File(name.toLowerCase()+".png")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            this.img = ImageIO.read(new File("map.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ImageLabel extends JLabel{
        private Image _myimage;

        public ImageLabel(String text){
            super(text);
        }

        public void setIcon(Icon icon) {
            super.setIcon(icon);
            if (icon instanceof ImageIcon)
            {
                _myimage = ((ImageIcon) icon).getImage();
            }
        }

        @Override
        public void paint(Graphics g){
            g.drawImage(_myimage, 0, 0, this.getWidth(), this.getHeight(), null);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Clear the screen
        super.paintComponent(g);

        // Get the Graphics2D object and enable anti-aliasing
        Graphics2D g2d = (Graphics2D) g;
        g2d.scale(getWidth() / 520.0, getHeight() / 635.0);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(STROKE_DEFAULT);

        // Draw white background
        g2d.setColor(COLOR_BACKGROUND);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Draw map
        g2d.drawImage(img, 0, 0, null);

        // Time bar
        g2d.setColor(COLOR_TEXT);
        g2d.setFont(FONT_BODY);
        g2d.drawString("Steps left:", 15, 20);
        g2d.drawString(""+game.getStepsLeft(), 150, 35);
        double ticks = game.getStepsLeft() / (double)game.getTotalSteps();
        g2d.setColor(COLOR_BAR_BACKGROUND);
        g2d.fillRect(15, 25, 130, 10);
        g2d.setColor(COLOR_BAR_TIME_FILL);
        g2d.fillRect(15, 25, (int)(130 * ticks), 10);
        g2d.setColor(COLOR_BAR_OUTLINE);
        g2d.drawRect(15, 25, 130, 10);

        // Hi-score
        List<Player> players = new ArrayList<Player>(game.getPlayers());
        Collections.sort(players, (p0, p1) ->
            { return (p1.getMoney() + (p1.getName().equals("GUI Player")?1000:0)) -
                (p0.getMoney() + (p0.getName().equals("GUI Player")?1000:0)); });
        int i=0;
        for(Player p : players) {
            if(!isEnabled(p)) { continue; }
            int y = 75 + 33 * i++;

            // Draw text
            g2d.setColor(COLOR_TEXT);
            g2d.setFont(FONT_BODY);
            g2d.drawString(p.getName()+":", 15, y - 5);
            g2d.drawString(p.getMoney()+" €", 150, y + 10);

            // Draw bar
            double money = p.getMoney() / 1400.0;
            g2d.setColor(COLOR_BAR_BACKGROUND);
            g2d.fillRect(15, y, 130, 10);
            g2d.setColor(p.getColor());
            g2d.fillRect(15, y, (int)(130 * money), 10);
            g2d.setColor(COLOR_BAR_OUTLINE);
            g2d.drawRect(15, y, 130, 10);
        }

        // Speed
        g2d.setFont(FONT_HEADER);
        g2d.drawString("Game speed", 280, 580);
        g2d.setColor(COLOR_BAR_BACKGROUND);
        g2d.fillRect(280, 590, 160, 20);

        g2d.setColor(COLOR_BAR_SPEED_FILL);
        g2d.fillRect(280+(GUI.speed-1)*39, 590, 39+(GUI.speed==4?6:0), 20);

        g2d.setColor(COLOR_BAR_OUTLINE);
        g2d.drawRect(280, 590, 160, 20);
        g2d.setFont(FONT_SC);
        g2d.drawString("SLOW", 286, 605);
        g2d.drawRect(280, 590, 39, 20);
        g2d.drawString("MED", 328, 605);
        g2d.drawRect(280, 590, 39 * 2, 20);
        g2d.drawString("FAST", 366, 605);
        g2d.drawRect(280, 590, 39 * 3, 20);
        g2d.drawString("SONIC", 403, 605);

        // City info
        if(GUI.hover != null) {
            g2d.setFont(FONT_SC);
            if(GUI.hover.getClass() == CapitalCity.class) {
                g2d.drawString("Capital of "+GUI.hover.getCountry().getName().toUpperCase(), 350, 470);
            }
            else {
                g2d.drawString(GUI.hover.getCountry().getName().toUpperCase(), 350, 470);
            }
            g2d.setFont(FONT_BODY);
            g2d.drawString(GUI.hover.getValue()+" €", 350, 486);
            g2d.setFont(FONT_HEADER);
            g2d.drawString(GUI.hover.getName(), 350, 458);
        }

        g2d.setStroke(STROKE_THICK);

        // Draw all roads
        for(Country country : game.getCountries()) {
            for(City city : country.getCities()) {
                for(Road road : country.getRoads(city)) {
                    drawRoad(g2d, road);
                }
            }
        }

        // Draw all cities
        for(Country country : game.getCountries()) {
            for(City city : country.getCities()) {
                drawCity(g2d, city);
            }
        }

        g2d.setStroke(STROKE_DEFAULT);

        // Draw all players
        for(Player player : game.getPlayers()) {
            drawPlayer(g2d, player, true);
        }

    }

    /**
     * Returns the 2D position of a given city.
     * @param c The city in question.
     */
    private Point getPosition(City c) {
        return game.getPosition(c);
    }

    /**
     * Draws a road (including intermediate dots) on a given G2D object.
     * @param g2d   The graphics object upon which to draw.
     * @param r     The road to draw.
     */
    private void drawRoad(Graphics2D g2d, Road r) {
        if(r.getFrom().getName().compareTo(r.getTo().getName()) > 0) return;
        Point posFrom = getPosition(r.getFrom()),
        posTo = getPosition(r.getTo());

        g2d.setColor(COLOR_ROAD);
        if(!r.getFrom().getCountry().equals(r.getTo().getCountry()))
            g2d.setColor(COLOR_BORDER_ROAD);
        g2d.drawLine(posFrom.x, posFrom.y, posTo.x, posTo.y);

        for(int i=0; i<r.getLength(); i++)
            drawRoadDot(g2d, r, i);
    }

    /**
     * Determines if a given player object is enabled in this current game.
     * @param p The player to check.
     * @return True iff the player 'p' if being drawn on the panel.
     */
    private boolean isEnabled(Player p) {
        if(p.getClass() == RandomPlayer.class) { return game.getSettings().isActive(0); }
        if(p.getClass() == GreedyPlayer.class) { return game.getSettings().isActive(1); }
        if(p.getClass() == SmartPlayer.class)  { return game.getSettings().isActive(2); }
        return true;
    }

    /**
     * Draws a player on a given graphics G2D object.
     * @param g2d   The graphics object upon which to draw.
     * @param p     The player to draw.
     * @param ai    Whether the player object is player-controlled or not.
     */
    private void drawPlayer(Graphics2D g2d, Player p, boolean ai) {
        if(!isEnabled(p)) { return; }

        Point from = getPosition(p.getPosition().getFrom()),
        to   = getPosition(p.getPosition().getTo());

        double f = (p.getPosition().getTotal()-p.getPosition().getDistance())/(double)p.getPosition().getTotal();
        int dx = (to.x - from.x),
        dy = (to.y - from.y),
        x = from.x + (int)(f * dx),
        y = from.y + (int)(f * dy),
        x$ = 2*x,
        y$ = 2*y;
        double dt = (Math.atan2(dy,dx)+2*Math.PI)%(2*Math.PI);
        int mirror = 1;
        if(dt > Math.PI/2 && dt < 3*Math.PI/2 ){
            dt += Math.PI;
            mirror = -1;
        }

        g2d.scale(0.5,0.5);
        g2d.rotate(dt,x$,y$);
        g2d.drawImage(playerIcons.get(p.getClass().getName()),x$-22+(mirror==1?0:64),y$-28,64*mirror,32,null);
        g2d.rotate(-dt,x$,y$);
        g2d.scale(2.0,2.0);
    }

    /**
     * Draws the i'th road dot on the G2D object g2d.
     * @param g2d   The graphics object upon which to draw.
     * @param r     The road from which to draw a dot.
     * @param i     The i'th dot is drawn.
     */
    private void drawRoadDot(Graphics2D g2d, Road r, int i) {
        Point from = getPosition(r.getFrom()),
        to = getPosition(r.getTo());

        double f = (i)/(double)r.getLength();
        int x = from.x + (int)Math.round(f * (to.x - from.x)),
        y = from.y + (int)Math.round(f * (to.y - from.y));

        int radius = ROAD_CIRCLE_RADIUS;
        Ellipse2D.Double shape = new Ellipse2D.Double(x - radius, y - radius, 2 * radius, 2 * radius);
        g2d.setColor(COLOR_ROAD);
        if(!r.getFrom().getCountry().equals(r.getTo().getCountry()))
            g2d.setColor(COLOR_BORDER_ROAD);
        g2d.fill(shape);
    }

    /**
     * Truncates its argument to the intermediate val [0,255] (used for color interpolations).
     * @param col   The argument to truncate.
     * @return The truncated argument.
     */
    private int makeLegal(int col) {
        if(col<0) { return 0; }
        if(col>255) {return 255; }
        return col;
    }

    /**
     * Computes the color of a given city, based on its value.
     * Uses the red-yellow-green color code.
     * @param x The value of the city.
     * @return The color of the city.
     */
    private Color cityColor(double x) {
        Color from = Color.WHITE, to = Color.WHITE;
        if(x < 0.33) {to = Color.RED; }
        if(0.33 <= x && x < 0.66) {to = Color.YELLOW; }
        if(0.66 <= x) {to = Color.GREEN; }

        int r = from.getRed() + (int)(x * (to.getRed() - from.getRed()));
        int g = from.getGreen() + (int)(x * (to.getGreen() - from.getGreen()));
        int b = from.getBlue() + (int)(x * (to.getBlue() - from.getBlue()));

        return new Color(makeLegal(r),makeLegal(g),makeLegal(b));
    }

    /**
     * Draws a city object on a graphics object.
     * @param g2d   The graphics object upon which to draw.
     * @param c     The city to draw.
     */
    private void drawCity(Graphics2D g2d, City c) {
        Point pos = getPosition(c);
        int radius = MIN_CIRCLE_RADIUS;

        if(c.equals(GUI.hover)) { radius = radius + 2; }
        if(c.getClass() == CapitalCity.class) { radius = radius + 3; }

        Ellipse2D.Double shape = new Ellipse2D.Double(pos.x - radius, pos.y - radius, 2*radius, 2*radius);
        double val = Math.pow(c.getValue() / 250.0, 1.0);
        Color col = cityColor(val);
        g2d.setColor(col);
        g2d.fill(shape);
        g2d.setColor(COLOR_CITY_STROKE);
        g2d.draw(shape);
    }
}
