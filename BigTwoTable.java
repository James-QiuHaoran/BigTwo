import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;
import javax.swing.text.DefaultCaret;

/**
 * This is designed for GUI of Big Two Game.
 * It implement CardGameTable interface.
 * @author HAORAN
 * @since 2016/11/09
 * @version 1.0
 *
 */
public class BigTwoTable implements CardGameTable {

	private CardGame game; // a card game associated with this table
	private boolean[] selected; // a boolean array indicating which cards are being selected
	private int activePlayer; // an integer specifying the index of the active player.
	private JFrame frame; // the main window of the application
	private JPanel bigTwoPanel; // a panel for showing the cards of each player and the cards played on the table
	private JButton playButton; // a "Play" button for the active player to play the selected cards
	private JButton passButton; // a "Pass" button for the active player to pass his/her turn to the next player.
	private JTextArea msgArea; // a text area for showing the current game status as well as end of game messages.
	private Image[][] cardImages; // a 2D array storing the images for the faces of the cards.
	private Image cardBackImage; // an image for the backs of the cards
	private Image[] avatars; // an array storing the images for the avatars
	
	/**
	 * No-argument constructor for BigTwoTable
	 */
	public BigTwoTable() {
		
	}
	
	/**
	 * This constructor takes CardGame game as parameter, which is a reference to the card game associated with this table.
	 * @param game
	 */
	public BigTwoTable(CardGame game) {
		this.game = game;
		setActivePlayer(game.getCurrentIdx());
		selected = new boolean[13];
		resetSelected();
		// load all the images
		avatars = new Image[4];
		avatars[0] = new ImageIcon("images/batman_128.png").getImage();
		avatars[1] = new ImageIcon("images/flash_128.png").getImage();
		avatars[2] = new ImageIcon("images/green_lantern_128.png").getImage();
		avatars[3] = new ImageIcon("images/superman_128.png").getImage();
		cardBackImage = new ImageIcon("images/b.gif").getImage();
		cardImages = new Image[4][];
		char suit[] = {'d', 'c', 'h', 's'};
		for (int i = 0; i < 4; i++) {
			cardImages[i] = new Image[13];
			for (int j = 0; j < 13; j++) {
				cardImages[i][j] = new ImageIcon("images/" + (j+1) + suit[i] + ".gif").getImage();
			}
		}
		
		// build the GUI
		// create the frame
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		frame.setTitle("Big Two");
		frame.getContentPane().setBackground(new Color(25, 115, 25));
		final JSplitPane hSplitPane = new JSplitPane();
		hSplitPane.setDividerLocation(900);
		hSplitPane.setDividerSize(5);
		frame.add(hSplitPane, BorderLayout.CENTER);
		
		// create the menu bar
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Game");
		JMenuItem restart = new JMenuItem("Restart");
		restart.addActionListener(new RestartMenuItemListener());
		JMenuItem quit = new JMenuItem("Quit");
		quit.addActionListener(new QuitMenuItemListener());
		menu.add(restart);
		menu.add(quit);
		menuBar.add(menu);
		frame.setJMenuBar(menuBar);
		
		// create the text area on the right hand side
		msgArea = new JTextArea(100, 30);
		DefaultCaret caret = (DefaultCaret)msgArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		msgArea.append("Message Board\n");
		msgArea.append("Information about the Game:\n\n");
		msgArea.setEditable(false);
		msgArea.setFont(new Font("Calibri", Font.PLAIN, 20));
		JScrollPane scroller = new JScrollPane(msgArea);   
		msgArea.setLineWrap(true); 
		scroller.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);    
		
		// Test for Assignment 5
		JPanel msg = new JPanel();
		msg.setLayout(new BoxLayout(msg, BoxLayout.Y_AXIS));
		JTextArea msgArea1 = new JTextArea(100, 30);
		DefaultCaret caret1 = (DefaultCaret)msgArea1.getCaret();
		caret1.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		msgArea1.append("This is the chat area! You can communicate with other players or the server.\n\n");
		msgArea1.setEditable(false);
		msgArea1.setFont(new Font("Calibri", Font.PLAIN, 20));
		JScrollPane scroller1 = new JScrollPane(msgArea1);   
		msgArea1.setLineWrap(true); 
		scroller1.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		JLabel label = new JLabel("Message: ");
		JTextField field = new JTextField(43);
		JPanel typeInMsg = new JPanel();
		typeInMsg.setLayout(new FlowLayout(FlowLayout.LEFT));
		typeInMsg.add(label);
		typeInMsg.add(field);
		
		msg.add(scroller);
		msg.add(scroller1);
		msg.add(typeInMsg);
		
		//hSplitPane.setRightComponent(scroller);
		hSplitPane.setRightComponent(msg);
		hSplitPane.getRightComponent().setMinimumSize(new Dimension(100, 30));
		
		bigTwoPanel = new BigTwoPanel();
		
		JPanel buttons = new JPanel();
		buttons.setLayout(new FlowLayout());
		playButton = new JButton(" Play ");
		playButton.addActionListener(new PlayButtonListener());
		passButton = new JButton(" Pass ");
		passButton.addActionListener(new PassButtonListener());
		buttons.add(playButton);
		buttons.add(passButton);
		buttons.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		buttons.setSize(900, 35);
		buttons.setMinimumSize(new Dimension(900, 35));
		
		final JSplitPane vSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		vSplitPane.setDividerSize(5);
		vSplitPane.setDividerLocation(850);;
		vSplitPane.setTopComponent(bigTwoPanel);
		vSplitPane.setBackground(new Color(25, 115, 25));
		vSplitPane.setBottomComponent(buttons);
		vSplitPane.getBottomComponent().setMinimumSize(new Dimension(900, 35));
		
		hSplitPane.setLeftComponent(vSplitPane);
		
		frame.setSize(1500, 975);
		frame.setMinimumSize(new Dimension(1500, 975));
		frame.setVisible(true);
	}

	/**
	 * An inner class - BigTwoPanel
	 * Draw the cards of each player and the hands on table.
	 * @author HAORAN
	 *
	 */
	class BigTwoPanel extends JPanel implements MouseListener {
		
		/**
		 * Constructor for BigTwoPanel
		 */
		public BigTwoPanel() {
			this.addMouseListener(this);
		}
		
		/**
		 * Inherited from JPanel class to draw the card game table
		 */
		public void paintComponent(Graphics g) {
			this.setOpaque(true);
			Graphics2D g2 = (Graphics2D) g;
			int fontSize = 20;
	        Font f = new Font("Comic Sans MS", Font.BOLD, fontSize);
	        g2.setFont(f);
	        g2.setStroke(new BasicStroke(2));
	        
	        setActivePlayer(game.getCurrentIdx());
	        
	        // player 0 to player 3
	        if (activePlayer == 0)
	        	g.setColor(Color.BLUE);
			g.drawString("Player0", 10, 20);
			g.setColor(Color.BLACK);
			g.drawImage(avatars[0], 10, 20, this);
		    g2.drawLine(0, 170, 1600, 170);
		    if (activePlayer == 0) {
		    	for (int i = 0; i < game.getPlayerList().get(0).getNumOfCards(); i++) {
		    		if (!selected[i])
		    			g.drawImage(cardImages[game.getPlayerList().get(0).getCardsInHand().getCard(i).getSuit()][game.getPlayerList().get(0).getCardsInHand().getCard(i).getRank()], 155+40*i, 43, this);
		    		else
		    			g.drawImage(cardImages[game.getPlayerList().get(0).getCardsInHand().getCard(i).getSuit()][game.getPlayerList().get(0).getCardsInHand().getCard(i).getRank()], 155+40*i, 43-20, this);
		    	}
		    } else {
		    	for (int i = 0; i < game.getPlayerList().get(0).getCardsInHand().size(); i++) {
		    		g.drawImage(cardBackImage, 155 + 40*i, 43, this);
		    	}
		    }
		    
		    if (activePlayer == 1)
	        	g.setColor(Color.BLUE);
		    g.drawString("Player1", 10, 190);
		    g.setColor(Color.BLACK);
		    g.drawImage(avatars[1], 10, 190, this);
		    g2.drawLine(0, 340, 1600, 340);
		    if (activePlayer == 1) {
		    	for (int i = 0; i < game.getPlayerList().get(1).getNumOfCards(); i++) {
		    		if (!selected[i])
		    			g.drawImage(cardImages[game.getPlayerList().get(1).getCardsInHand().getCard(i).getSuit()][game.getPlayerList().get(1).getCardsInHand().getCard(i).getRank()], 155+40*i, 213, this);
		    		else
		    			g.drawImage(cardImages[game.getPlayerList().get(1).getCardsInHand().getCard(i).getSuit()][game.getPlayerList().get(1).getCardsInHand().getCard(i).getRank()], 155+40*i, 213-20, this);
		    	}
		    } else {
		    	for (int i = 0; i < game.getPlayerList().get(1).getCardsInHand().size(); i++) {
		    		g.drawImage(cardBackImage, 155 + 40*i, 213, this);
		    	}
		    }
		    
		    if (activePlayer == 2)
	        	g.setColor(Color.BLUE);
		    g.drawString("Player2", 10, 360);
		    g.setColor(Color.BLACK);
		    g.drawImage(avatars[2], 10, 360, this);
		    g2.drawLine(0, 510, 1600, 510);
		    if (activePlayer == 2) {
		    	for (int i = 0; i < game.getPlayerList().get(2).getNumOfCards(); i++) {
		    		if (!selected[i])
		    			g.drawImage(cardImages[game.getPlayerList().get(2).getCardsInHand().getCard(i).getSuit()][game.getPlayerList().get(2).getCardsInHand().getCard(i).getRank()], 155+40*i, 383, this);
		    		else
		    			g.drawImage(cardImages[game.getPlayerList().get(2).getCardsInHand().getCard(i).getSuit()][game.getPlayerList().get(2).getCardsInHand().getCard(i).getRank()], 155+40*i, 383-20, this);
		    	}
		    } else {
		    	for (int i = 0; i < game.getPlayerList().get(2).getCardsInHand().size(); i++) {
		    		g.drawImage(cardBackImage, 155 + 40*i, 383, this);
		    	}
		    }
		    
		    if (activePlayer == 3)
	        	g.setColor(Color.BLUE);
		    g.drawString("Player3", 10, 530);
		    g.setColor(Color.BLACK);
		    g.drawImage(avatars[3], 10, 530, this);
		    g2.drawLine(0, 680, 1600, 680);
		    if (activePlayer == 3) {
		    	for (int i = 0; i < game.getPlayerList().get(3).getNumOfCards(); i++) {
		    		if (!selected[i])
		    			g.drawImage(cardImages[game.getPlayerList().get(3).getCardsInHand().getCard(i).getSuit()][game.getPlayerList().get(3).getCardsInHand().getCard(i).getRank()], 155+40*i, 553, this);
		    		else
		    			g.drawImage(cardImages[game.getPlayerList().get(3).getCardsInHand().getCard(i).getSuit()][game.getPlayerList().get(3).getCardsInHand().getCard(i).getRank()], 155+40*i, 553-20, this);
		    	}
		    } else {
		    	for (int i = 0; i < game.getPlayerList().get(3).getCardsInHand().size(); i++) {
		    		g.drawImage(cardBackImage, 155 + 40*i, 553, this);
		    	}
		    }
		    
		    g.drawString("Current Hand on Table", 10, 700);
		    if (game.getHandsOnTable().size() == 0)
		    	g.drawString("No Hand on Table.", 10, 760);
		    else {
		    	Hand handOnTable = game.getHandsOnTable().get(game.getHandsOnTable().size()-1);
		    	g.drawString("Hand Type:\n ", 10, 760);
		    	if (game.getPlayerList().get(game.getCurrentIdx()) != game.getHandsOnTable().get(game.getHandsOnTable().size()-1).getPlayer()) {
		    		g.drawString(game.getHandsOnTable().get(game.getHandsOnTable().size()-1).getPlayer().getName(), 10, 785);
		    		for (int i = 0; i < handOnTable.size(); i++)
		    			g.drawImage(cardImages[handOnTable.getCard(i).getSuit()][handOnTable.getCard(i).getRank()], 160 + 40*i, 730, this);
		    	}
		    }
		    g2.drawLine(0, 850, 1600, 850);
		}
		
		/**
		 * Handle mouse click event.
		 */
		public void mouseClicked(MouseEvent event) { // all stand, no jump !!!
			int startY = 43 + 170*activePlayer;
			boolean found = false; // flag for marking whether the card is found or not
			int size = game.getPlayerList().get(activePlayer).getNumOfCards(); // num of cards
			int i = size-1;
			if (event.getX() >= (155+i*40) && event.getX() <= (155+i*40+73)) {
				if(selected[i] == false && event.getY() >= startY && event.getY() <= startY+97) {
					found = true;
					selected[i] = true;
				} else if (selected[i] == true && event.getY() >= startY-20 && event.getY() <= startY+97-20) {
					found = true;
					selected[i] = false;
				}
			}
			for (i = size-2; i >= 0 && !found; i--) {
				if (event.getX() >= (155+i*40) && event.getX() <= (155+i*40+40)) {
					if(selected[i] == false && event.getY() >= startY && event.getY() <= startY+97) {
						found = true;
						selected[i] = true;
					} else if (selected[i] == true && event.getY() >= startY-20 && event.getY() <= startY+97-20) {
						found = true;
						selected[i] = false;
					}
				}
				else if (event.getX() >= (155+i*40+40) && event.getX() <= (155+i*40+73) && event.getY() >= startY && event.getY() <= startY+97) {
					if (selected[i+1] == true && selected[i] == false) {
						found = true;
						selected[i] = true;
					}
				}
				else if (event.getX() >= (155+i*40+40) && event.getX() <= (155+i*40+73) && event.getY() >= startY-20 && event.getY() <= startY+97-20) {
					if (selected[i+1] == false && selected[i] == true) {
						found = true;
						selected[i] = false;
					}
				}
			}
			frame.repaint();
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
	}
	
	/**
	 * An inner class.
	 * Handle the playButton listener.
	 * @author HAORAN
	 *
	 */
	class PlayButtonListener implements ActionListener {
		/**
		 * Handle button click events for "Play" button
		 */
		@Override
		public void actionPerformed(ActionEvent event) {
			game.checkMove(getSelected());
			resetSelected();
			repaint();
		}
	}
	
	/**
	 * An inner class.
	 * Handle the passButton listener.
	 * @author HAORAN
	 *
	 */
	class PassButtonListener implements ActionListener {
		/**
		 * Handle button click events for "Pass" button
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			int[] cardIdx = new int[0];
			game.checkMove(cardIdx);
			resetSelected();
			repaint();
		}
	}
	
	/**
	 * An inner class.
	 * Handle the manipulation after you click restart.
	 * @author HAORAN
	 *
	 */
	class RestartMenuItemListener implements ActionListener{
		/**
		 * Handle menu-item-click events for "Restart" menu item
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			game.getDeck().shuffle();
			game.start(game.getDeck());
			reset();
		}
	}
	
	/**
	 * An inner class.
	 * Handle the manipulation after you click quit.
	 * @author HAORAN
	 *
	 */
	class QuitMenuItemListener implements ActionListener{
		/**
		 * Handle menu-item-click events for "Quit" menu item
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			printMsg("Game Ended by the User!\n");
			System.exit(0);
		}
	}
	
	/**
	 * Sets the index of the active player (i.e., the current player).
	 * 
	 * @param activePlayer
	 * an int value representing the index of the active player
	 */
	public void setActivePlayer(int activePlayer) {
		this.activePlayer = activePlayer;
	}

	/**
	 * Returns an array of indices of the cards selected.
	 * 
	 * @return an array of indices of the cards selected
	 */
	public int[] getSelected() {
		int numOfSelected = 0;
		for (int i = 0; i < 13; i++) {
			if (selected[i])
				numOfSelected++;
		}
		int[] selectedCards = new int[numOfSelected];
		int counter = 0;
		for (int i = 0; i < 13; i++) {
			if (selected[i]) {
				selectedCards[counter] = i;
				counter++;
			}
		}
		return selectedCards;  // E.g. selectedCards = {0, 4, 7, 9};
	}

	/**
	 * Resets the list of selected cards to an empty list.
	 */
	public void resetSelected() {
		for (int i = 0; i < 13; i++)
			selected[i] = false;
	}

	/**
	 * Repaints the GUI.
	 */
	public void repaint() {
		frame.repaint();
	}

	/**
	 * Prints the specified string to the message area of the card game table.
	 * 
	 * @param msg
	 *            the string to be printed to the message area of the card game
	 *            table
	 */
	public void printMsg(String msg) {
		msgArea.append(msg);
	}

	/**
	 * Clears the message area of the card game table.
	 */
	public void clearMsgArea() {
		msgArea.setText("");
	}

	/**
	 * Resets the GUI.
	 */
	public void reset() {
		frame.setVisible(false);
		game = new BigTwo();
		BigTwoDeck deck = new BigTwoDeck();
		deck.shuffle();
		game.start(deck);
		printMsg("Game Restarted by User!");
	}

	/**
	 * Enables user interactions.
	 */
	public void enable() {
		bigTwoPanel.setEnabled(true);
		playButton.setEnabled(true);
		passButton.setEnabled(true);
	}

	/**
	 * Disables user interactions.
	 */
	public void disable() {
		bigTwoPanel.setEnabled(false);
		playButton.setEnabled(false);
		passButton.setEnabled(false);
	}

}
