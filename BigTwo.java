import java.util.ArrayList;
/**
 * This class is used to model a Big Two card game.
 * It has private instance variables for storing a deck of cards, a list of hands played on the table, 
 * an index of the current player, and a console for providing the user interface.
 * @author HAORAN
 * @version 1.0
 * @since 2016.10.13
 *
 */
public class BigTwo {
	private Deck deck;
	private ArrayList<CardGamePlayer> playerList;
	private ArrayList<Hand> handsOnTable;
	private int currentIdx;
	private BigTwoConsole bigTwoConsole;
	
	/**
	 * Constructor
	 * Create and return an instance of class BigTwo.
	 * Game players and the game console are created.
	 */
	public BigTwo() {
		playerList = new ArrayList<CardGamePlayer>();
		CardGamePlayer a = new CardGamePlayer();
		CardGamePlayer b = new CardGamePlayer();
		CardGamePlayer c = new CardGamePlayer();
		CardGamePlayer d = new CardGamePlayer();
		playerList.add(a);
		playerList.add(b);
		playerList.add(c);
		playerList.add(d);
		handsOnTable = new ArrayList<Hand>();
		bigTwoConsole = new BigTwoConsole(this);
	}
	
	/**
	 * This is a getter method for private instance variable deck.
	 * @return deck
	 */
	public Deck getDeck() {
		return deck;
	}
	
	/**
	 * This is a getter method for private instance variable playerList.
	 * @return playerList
	 */
	public ArrayList<CardGamePlayer> getPlayerList() {
		return playerList;
	}
	
	/**
	 * This is a getter method for private instance variable handsOnTable.
	 * @return handsOnTable
	 */
	public ArrayList<Hand> getHandsOnTable() {
		return handsOnTable;
	}
	
	/**
	 * This is a getter method for private instance variable currentIdx which represent the current player
	 * @return currentIdx
	 */
	public int getCurrentIdx() {
		return currentIdx;
	}
	
	/**
	 * This is a method for starting a Big Two card game.
	 * It should create a Big Two card game, create and shuffle a deck of cards, and start the game with the deck of cards.
	 * @param deck
	 */
	public void start(BigTwoDeck deck) {
		this.deck = deck;
		// divide 52 cards to 4 players
		for (int i = 0; i < 13; i++) {
			for (int j = 0; j < 4; j++) {
				getPlayerList().get(j).addCard(this.deck.getCard(i*4+j));
				if (this.deck.getCard(i*4+j).getRank() == 2 && this.deck.getCard(i*4+j).getSuit() == 0)
					currentIdx = j; // set the first player
			}
		}
		
		// sort the cards 
		for (int i = 0; i < 4; i++)
			getPlayerList().get(i).getCardsInHand().sort();
		
		// first round
		// the first player should choose a hand with 3-Diamond
		CardList temp;
		boolean illegal = true;
		BigTwoCard key = new BigTwoCard(0,2);
		bigTwoConsole.setActivePlayer(currentIdx);
		bigTwoConsole.repaint();
		while (illegal) {
			temp = new CardList();
			int[] list = bigTwoConsole.getSelected();
			if (list != null) {
				for (int i = 0; i < list.length; i++) {
				    temp.addCard(getPlayerList().get(currentIdx).getCardsInHand().getCard(list[i]));
				}
				
				if (temp.contains(key)) {
					handsOnTable.add(composeHand(getPlayerList().get(currentIdx), temp));
					if (handsOnTable.get(handsOnTable.size()-1) == null) {
					    System.out.println("Not a legal move!!!");
					    handsOnTable.remove(handsOnTable.size()-1);
					}
					else {
						System.out.print("{" + handsOnTable.get(handsOnTable.size()-1).getType() + "} ");
						for (int i = 0; i < handsOnTable.get(handsOnTable.size()-1).size(); i++)
							System.out.print("[" + handsOnTable.get(handsOnTable.size()-1).getCard(i).toString() + "] ");
						System.out.println("");
						System.out.println("");
						getPlayerList().get(currentIdx).removeCards(handsOnTable.get(handsOnTable.size()-1));
						currentIdx++;
						currentIdx = currentIdx % 4;
						illegal = false;
					}
				}
				else {
					System.out.println("Not a legal move!!!");
				}
			}
			else
				System.out.println("Not a legal move!!!");
		}
		
		
		boolean onGoing = true;
		illegal = false;
		while(onGoing) {
			// procedures
			if (!illegal) {
				bigTwoConsole.setActivePlayer(currentIdx);
				bigTwoConsole.repaint();
			}
			illegal = false;
			temp = new CardList();
			int[] list = bigTwoConsole.getSelected();
			
			if (list == null && handsOnTable.get(handsOnTable.size()-1).getPlayer() != getPlayerList().get(currentIdx)) {
				System.out.println("{pass}");
				System.out.print("\n");
				currentIdx++;
				currentIdx = currentIdx % 4;
				temp = null;
			} else if (list == null && handsOnTable.get(handsOnTable.size()-1).getPlayer() == getPlayerList().get(currentIdx)) {
				System.out.println("Not a legal move!!!");
				illegal = true;
			} else {
				  boolean noNeedToCompare = false;
				  if (handsOnTable.get(handsOnTable.size()-1).getPlayer() == getPlayerList().get(currentIdx))
					  noNeedToCompare = true;
				  for (int i = 0; i < list.length; i++) {
					  temp.addCard(getPlayerList().get(currentIdx).getCardsInHand().getCard(list[i]));
				  }
				  // compose a Hand
				  handsOnTable.add(composeHand(getPlayerList().get(currentIdx), temp));
				  
				  if (handsOnTable.get(handsOnTable.size()-1) == null) {
					  System.out.println("Not a legal move!!!");
					  handsOnTable.remove(handsOnTable.size()-1);
					  illegal = true;
				  }
				  else if (!noNeedToCompare && !handsOnTable.get(handsOnTable.size()-1).beats(handsOnTable.get(handsOnTable.size()-2))) {
					  System.out.println("Not a legal move!!!");
					  illegal = true;
					  handsOnTable.remove(handsOnTable.size()-1);
				  }
				  else {
				      System.out.print("{" + handsOnTable.get(handsOnTable.size()-1).getType() + "} ");
					  for (int i = 0; i < handsOnTable.get(handsOnTable.size()-1).size(); i++)
						  System.out.print("[" + handsOnTable.get(handsOnTable.size()-1).getCard(i).toString() + "] ");
					  System.out.println(" ");
					  System.out.println(" ");
					  getPlayerList().get(currentIdx).removeCards(handsOnTable.get(handsOnTable.size()-1));
					  
					  //judge the end
					  if (this.playerList.get(currentIdx).getNumOfCards() == 0)
						  onGoing = false; // player i wins!
					  else {
						  currentIdx++;
						  currentIdx = currentIdx % 4;
					  }
				  }	
		    }
		}
		
		System.out.println("Game ends");
		for (int i = 0; i < 4; i++) {
			if (playerList.get(i).getNumOfCards() == 0)
				System.out.println("Player " + i + " wins the game.");
			else
				System.out.println("Player " + i + " has " + playerList.get(i).getNumOfCards() + " cards in hand");
		}
	}
	
	/**
	 * This is a method for starting a Big Two card game.
	 * @param args
	 */
	public static void main(String[] args) {
		BigTwo game = new BigTwo();
		
		// set 52 cards
		BigTwoDeck deck = new BigTwoDeck();
		
		deck.shuffle();
		
		// start the game
		game.start(deck);
		
	}
	
	public static Hand composeHand(CardGamePlayer player, CardList cards) {
		Hand test;
		test = new Single(player, cards);
		if (test.isValid())
			return test;
		test = new Pair(player, cards);
		if (test.isValid())
			return test;
		test = new Triple(player, cards);
		if (test.isValid())
			return test;
		test = new Straight(player, cards);
		if (test.isValid())
			return test;
		test = new Flush(player, cards);
		if (test.isValid())
			return test;
		test = new FullHouse(player, cards);
		if (test.isValid())
			return test;
		test = new Quad(player, cards);
		if (test.isValid())
			return test;
		test = new StraightFlush(player, cards);
		if (test.isValid())
			return test;
		
		// none of the hands is valid
		return null;
	}
}