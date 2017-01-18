import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * This class is used for modeling a console for the Big Two card game. It
 * provides the user interface for the game.
 * 
 * @author Kenneth Wong
 */
public class BigTwoConsole {
	private final static int MAX_CARD_NUM = 13; // max. no. of cards each player
												// holds
	private ArrayList<CardGamePlayer> playerList; // the list of players
	private ArrayList<Hand> handsOnTable; // the list of hands played on the
	private Scanner scanner; // the scanner for reading user in put
	private int activePlayer = -1; // the index of the active player

	/**
	 * Creates and returns an instance of the BigTwoConsole class.
	 * 
	 * @param game
	 *            a BigTwo object associated with this console
	 */
	public BigTwoConsole(BigTwo game) {
		playerList = game.getPlayerList();
		handsOnTable = game.getHandsOnTable();
		scanner = new Scanner(System.in);
	}

	/**
	 * Sets the index of the active player.
	 * 
	 * @param activePlayer
	 *            the index of the active player (i.e., the player who can make
	 *            a move)
	 */
	public void setActivePlayer(int activePlayer) {
		if (activePlayer < 0 || activePlayer >= playerList.size()) {
			this.activePlayer = -1;
		} else {
			this.activePlayer = activePlayer;
		}
	}

	/**
	 * Returns an array of indices of the cards selected through the console.
	 * 
	 * @return an array of indices of the cards selected, or null if no valid
	 *         cards have been selected
	 */
	public int[] getSelected() {
		boolean[] selected = new boolean[MAX_CARD_NUM];

		CardGamePlayer player = playerList.get(activePlayer);
		System.out.print(player.getName() + "'s turn: ");
		String input = scanner.nextLine();

		StringTokenizer st = new StringTokenizer(input);
		while (st.hasMoreTokens()) {
			try {
				int idx = Integer.parseInt(st.nextToken());
				if (idx >= 0 && idx < MAX_CARD_NUM
						&& idx < player.getCardsInHand().size()) {
					selected[idx] = true;
				}
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}

		int[] cardIdx = null;
		int count = 0;
		for (int j = 0; j < selected.length; j++) {
			if (selected[j]) {
				count++;
			}
		}

		if (count != 0) {
			cardIdx = new int[count];
			count = 0;
			for (int j = 0; j < selected.length; j++) {
				if (selected[j]) {
					cardIdx[count] = j;
					count++;
				}
			}
		}
		return cardIdx;
	}

	/**
	 * Redraws the console.
	 */
	public void repaint() {
		for (int i = 0; i < playerList.size(); i++) {
			CardGamePlayer player = playerList.get(i);
			String name = player.getName();
			if (activePlayer == i) {
				System.out.println("<" + name + ">");
				System.out.print("==> ");
				player.getCardsInHand().print(true, true);
			} else if (activePlayer == -1) {
				System.out.println("<" + name + ">");
				System.out.print("    ");
				player.getCardsInHand().print(true, true);
			} else {
				System.out.println("<" + name + ">");
				System.out.print("    ");
				player.getCardsInHand().print(false, true);
			}
		}
		System.out.println("<Table>");
		Hand lastHandOnTable = (handsOnTable.isEmpty()) ? null : handsOnTable
				.get(handsOnTable.size() - 1);
		if (lastHandOnTable != null) {
			System.out.print("    <" + lastHandOnTable.getPlayer().getName()
					+ "> {" + lastHandOnTable.getType() + "} ");
			lastHandOnTable.print(true, false);
		} else {
			System.out.println("  [Empty]");
		}
	}
}
