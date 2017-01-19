import java.util.ArrayList;
/**
 * This class is used to model a Big Two card game.
 * It has private instance variables for storing a deck of cards, a list of hands played on the table, 
 * an index of the current player, and a console for providing the user interface.
 * @author HAORAN
 * @version 2.0
 * @since 2016.11.10
 *
 */
public class BigTwo implements CardGame {
	private Deck deck;
	private ArrayList<CardGamePlayer> playerList;
	private ArrayList<Hand> handsOnTable;
	private int currentIdx;
	private BigTwoTable table; // build the GUI and handle all user actions
	
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
		
		// Create GUI
		table = new BigTwoTable(this);
	}
	
	/**
	 * A method for getting the deck of cards being used.
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
	 * @param args
	 */
	public static void main(String[] args) {
		BigTwo game = new BigTwo();
		// set 52 cards
		BigTwoDeck deck = new BigTwoDeck();
		deck.shuffle();
		game.start(deck);
	}
	
	/**
	 * A method for returning a valid hand from the specified list of cards of the player.
	 * @param player
	 * @param cards
	 * @return the valid hand or null if no hand is valid
	 */
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

	@Override
	public int getNumOfPlayers() {
		return this.playerList.size();
	}

	@Override
	/**
	 * A method for starting the game with a shuffled deck of cards supplied as the argument
	 * @param Deck
	 */
	public void start(Deck deck) {
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
		
	}

	@Override
	/**
	 * A method for checking the move made by the current player given the list of the indices of the cards selected by the player.
	 */
	public void checkMove(int[] cardIdx) {
		if (cardIdx.length == 0) {
			// the player choose pass in this turn
			table.printMsg("Player" + currentIdx + "'s turn: ");
			table.printMsg("{Pass}");
			if (handsOnTable.size() == 0)
				table.printMsg(" ==> Illegal Move! Since you are the first one to play, you can't choose pass!\n\n");
			else if (handsOnTable.get(handsOnTable.size()-1).getPlayer() == playerList.get(currentIdx)) 
				table.printMsg(" ==> Illegal Move!\n\n");
			else {
				table.printMsg("\n\n");
				currentIdx++;
				currentIdx = currentIdx % 4;
			}
		} else if (cardIdx.length > 0) {
			CardList temp;
			int[] list = table.getSelected();
			if (handsOnTable.size() == 0) {
				// this is the first guy who must choose a hand that contains 3 diamond
				BigTwoCard key = new BigTwoCard(0,2);
				temp = new CardList();
				for (int i = 0; i < list.length; i++) {
				    temp.addCard(getPlayerList().get(currentIdx).getCardsInHand().getCard(list[i]));
				}
				if (temp.contains(key) == false) {
					table.printMsg("Illegal Move! Since you are the first one to play, you must choose 3 Diamond!\n\n");
				}
				else if (temp.contains(key)) {
					handsOnTable.add(composeHand(getPlayerList().get(currentIdx), temp));
					if (handsOnTable.get(handsOnTable.size()-1) == null) {
						// Illegal move
						handsOnTable.remove(handsOnTable.size()-1);
						table.printMsg("Illegal Move!\n\n");
					} else {
						// Legal move
						table.printMsg("Player" + currentIdx + "'s turn: ");
						table.printMsg("{" + handsOnTable.get(handsOnTable.size()-1).getType() + "} ");
						for (int i = 0; i < handsOnTable.get(handsOnTable.size()-1).size(); i++)
							table.printMsg("[" + handsOnTable.get(handsOnTable.size()-1).getCard(i).toString() + "] ");
						table.printMsg("\n\n");
						getPlayerList().get(currentIdx).removeCards(handsOnTable.get(handsOnTable.size()-1));
						currentIdx++;
						currentIdx = currentIdx % 4;
					}
				}
			} else {
				// not the first one to play
				temp = new CardList();
				for (int i = 0; i < list.length; i++) {
				    temp.addCard(getPlayerList().get(currentIdx).getCardsInHand().getCard(list[i]));
				}
				handsOnTable.add(composeHand(getPlayerList().get(currentIdx), temp));
				if (handsOnTable.get(handsOnTable.size()-1) == null) {
					// Illegal move
					handsOnTable.remove(handsOnTable.size()-1);
					table.printMsg("Illegal Move!\n\n");
				} else {
					// Legal hand, still need to check whether it is able to beat the last hand
					boolean noNeedToCompare = false;
					//table.printMsg("" + currentIdx + "\n");
					//table.printMsg(handsOnTable.get(handsOnTable.size()-1).getPlayer().getName()+"\n");
					if (handsOnTable.get(handsOnTable.size()-2).getPlayer() == getPlayerList().get(currentIdx))
						noNeedToCompare = true;
					table.printMsg("Player" + currentIdx + "'s turn: ");
					table.printMsg("{" + handsOnTable.get(handsOnTable.size()-1).getType() + "} ");
					//table.printMsg("Last Hand: " + "{" + handsOnTable.get(handsOnTable.size()-2).getType() + "} ");
					for (int i = 0; i < handsOnTable.get(handsOnTable.size()-1).size(); i++)
						table.printMsg("[" + handsOnTable.get(handsOnTable.size()-1).getCard(i).toString() + "] ");
					if (!noNeedToCompare && handsOnTable.get(handsOnTable.size()-1).beats(handsOnTable.get(handsOnTable.size()-2))) {
						//table.printMsg("Has been here!!!\n");
						getPlayerList().get(currentIdx).removeCards(handsOnTable.get(handsOnTable.size()-1));
						table.printMsg("\n\n");
						if (endOfGame() == true) {
							table.printMsg("Game Ended!!\n");
							table.printMsg("The Winner is: " + playerList.get(getCurrentIdx()).getName());
							table.disable();
						}
						currentIdx++;
						currentIdx = currentIdx % 4;
					} else if (noNeedToCompare) {
						//table.printMsg("No need to compare!\n\n");
						getPlayerList().get(currentIdx).removeCards(handsOnTable.get(handsOnTable.size()-1));
						table.printMsg("\n\n");
						if (endOfGame() == true) {
							table.printMsg("Game Ended!!\n");
							table.printMsg("The Winner is: " + playerList.get(getCurrentIdx()).getName());
							table.disable();
						}
						currentIdx++;
						currentIdx = currentIdx % 4;
					} else {
						table.printMsg("Illegal Move!\n\n");
						handsOnTable.remove(handsOnTable.size()-1);
					}
				}
			}
		}
	}

	@Override
	/**
	 * A method for checking if the game ends
	 */
	public boolean endOfGame() {
		for (int i = 0; i < 4; i++)
			if (this.getPlayerList().get(i).getNumOfCards() == 0)
				return true;
		return false;
	}
}