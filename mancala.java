import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class mancala {

	
	
	ArrayList<Integer> box = new ArrayList<Integer>();
	ArrayList<Node> Tree = new ArrayList<Node>();
	ArrayList<String> nodearray = new ArrayList<String>();
	int cutOffDepth;
	FileWriter fw, fw2;
	String newLine = System.getProperty("line.separator");

	public static void main(String[] args) {

		mancala ob = new mancala();
		FileReader fileReader = null;

		try {
			fileReader = new FileReader(args[0]);
			ob.fw = new FileWriter("next_state.txt");

			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String s = "";
			while ((s = bufferedReader.readLine()).equals(""))
				continue;
			String typeOfCase = s.trim();
			String player = bufferedReader.readLine().trim();
			ob.cutOffDepth = Integer.parseInt(bufferedReader.readLine().trim());

			ArrayList<Integer> A = new ArrayList<Integer>();
			ArrayList<Integer> B = new ArrayList<Integer>();

			String[] As = bufferedReader.readLine().split(" ");
			int boxWidth = As.length;
			int size = (boxWidth * 2) + 2;
			for (int i = 0; i < As.length; i++)
				A.add(Integer.parseInt(As[i]));

			String[] Bs = bufferedReader.readLine().split(" ");
			for (int i = 0; i < Bs.length; i++)
				B.add(Integer.parseInt(Bs[i]));

			int mancala_A = Integer.parseInt(bufferedReader.readLine().trim());
			int mancala_B = Integer.parseInt(bufferedReader.readLine().trim());

			if (player.equals("1")) {
				for (Integer b : B)
					ob.box.add(b);
				ob.box.add(mancala_B);
				for (int i = boxWidth - 1; i >= 0; i--)
					ob.box.add(A.get(i));
				ob.box.add(mancala_A);
			} else {
				for (int i = boxWidth - 1; i >= 0; i--)
					ob.box.add(A.get(i));
				ob.box.add(mancala_A);
				for (Integer b : B)
					ob.box.add(b);
				ob.box.add(mancala_B);
			}

			if (typeOfCase.equals("1")) {
				System.out.println("greedy before: " + ob.box);
				ArrayList<Integer> greedybox = ob.greedy(player, ob.box,
						boxWidth);
				System.out.println("greedy after:  " + greedybox);
				ob.writeNextStateFile(greedybox, player);
			}
			if (typeOfCase.equals("2")) {
				ob.fw2 = new FileWriter("traverse_log.txt");
				ob.fw2.write("Node,Depth,Value" + ob.newLine);

				ob.nodearray = createNodeArray(boxWidth, player);
				System.out.println(ob.nodearray);

				System.out.println("minimax before: " + ob.box);

				// creation of tree
				Node root = new Node();
				root.board = ob.setbox1(ob.box);
				root.name = "root";
				root.parent = null;
				root.eval = Integer.MIN_VALUE;
				root.depth = 0;
				root.typeOfNode = "max";

				int val = ob.maxValue(root, 0, player);

				Node ans = ob.finalMove(root, val);

				ob.writeNextStateFile(ans.board, player);
				ob.fw2.close();

			} else if (typeOfCase.equals("3")) {

				ob.fw2 = new FileWriter("traverse_log.txt");
				ob.fw2.write("Node,Depth,Value,Alpha,Beta" + ob.newLine);

				ob.nodearray = createNodeArray(boxWidth, player);
				System.out.println(ob.nodearray);

				System.out.println("alpha beta before: " + ob.box);

				// creation of tree
				Node root = new Node();
				root.board = ob.setbox1(ob.box);
				root.name = "root";
				root.parent = null;
				root.eval = Integer.MIN_VALUE;
				root.depth = 0;
				root.typeOfNode = "max";

				int val = ob.maxValueAB(root, 0, player, Integer.MIN_VALUE,
						Integer.MAX_VALUE);

				Node ans = ob.finalMove(root, val);
				ob.writeNextStateFile(ans.board, player);
				ob.fw2.close();
			}

			ob.fw.close();
			System.out.println("End");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void writeNextStateFile(ArrayList<Integer> board, String player) {
		int size = board.size();
		int boxWidth = (size - 2) / 2;
		String L1 = "", L2 = "", L3 = "", L4 = "";
		if (player.equals("1")) {
			L2 = "";
			for (int i = 0; i < boxWidth; i++)
				L2 = L2 + board.get(i) + " ";
			L2.trim();
			L4 = board.get(boxWidth) + "";
			L3 = board.get(size - 1) + "";
			L1 = "";
			for (int i = size - 2; i > boxWidth; i--)
				L1 = L1 + board.get(i) + " ";
			L1.trim();

		} else if (player.equals("2")) {
			L1 = "";
			for (int i = boxWidth - 1; i >= 0; i--)
				L1 = L1 + board.get(i) + " ";
			L1.trim();
			L3 = board.get(boxWidth) + "";
			L4 = board.get(size - 1) + "";
			L2 = "";
			for (int i = boxWidth + 1; i < size - 1; i++)
				L2 = L2 + board.get(i) + " ";
			L2.trim();

		}
		try {
			fw.write(L1 + newLine);
			fw.write(L2 + newLine);
			fw.write(L3 + newLine);
			fw.write(L4 + newLine);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	private Node finalMove(Node state, Integer v) {
		Node f = new Node();
		List<Node> l = getSuccessorNodes(state);
		if (l.size() != 0) {
			for (Node n : l) {
				if (n.typeOfNode.equals("min")) {
					if (n.eval == v) {
						f = finalMove(n, v);
						return f;
					}
				}
			}
		}
		return state;
	}

	void writeLog(Node state) {
		try {

			fw2.write("" + state.name + "," + state.depth + ","
					+ printeval(state.eval) + newLine);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void writeLogAB(Node state, int alpha, int beta) {
		try {
			fw2.write("" + state.name + "," + state.depth + ","
					+ printeval(state.eval) + "," + printeval(alpha) + ","
					+ printeval(beta) + newLine);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String printeval(int eval) {

		if (eval == Integer.MAX_VALUE)
			return "Infinity";
		else if (eval == Integer.MIN_VALUE)
			return "-Infinity";
		else
			return eval + "";
	}

	List<Node> getSuccessorNodes(Node par) {
		List<Node> l = new ArrayList<Node>();
		for (Node n : this.Tree) {
			if (n.parent != null && n.parent == par)
				l.add(n);
		}
		return l;

	}

	private int maxValue(Node state, int depth, String player) {
		int v = Integer.MIN_VALUE;
		state.eval = v;

		String p;
		if (player.equals("1"))
			p = "2";
		else
			p = "1";

		int boxwidth = (state.board.size() - 2) / 2;
		ArrayList<Integer> box1 = new ArrayList<Integer>();
		int mod = (2 * boxwidth) + 2;

		if (player.equals("1")) {

			for (int i = 0; i < boxwidth; i++) {
				box1 = setbox1(state.board);

				String s = ifAllEmpty(box1);
				if (s.equals("1")) {
					int sum = 0;
					for (int x = boxwidth + 1; x < box1.size() - 1; x++) {
						sum = sum + box1.get(x);
						box1.set(x, 0);
					}
					int or = box1.get(box1.size() - 1);
					box1.set(box1.size() - 1, sum + or);

					state.board = setbox1(box1);

					if (state.depth != cutOffDepth
							|| (state.depth == cutOffDepth && state.typeOfNode
									.equals(state.parent.typeOfNode))) {
						state.eval = v;
						writeLog(state);

					}

					v = state.board.get(boxwidth) - state.board.get(mod - 1);
					state.eval = v;
					writeLog(state);
					if (state.depth == 1)
						Tree.add(state);

					return v;
				} else if (s.equals("2")) {
					int sum = 0;
					for (int x = 0; x < boxwidth; x++) {
						sum = sum + box1.get(x);
						box1.set(x, 0);
					}
					int or = box1.get(boxwidth);
					box1.set(boxwidth, sum + or);

					state.board = setbox1(box1);

					if (state.depth != cutOffDepth
							|| (state.depth == cutOffDepth && state.typeOfNode
									.equals(state.parent.typeOfNode))) {
						state.eval = v;
						writeLog(state);
					}

					v = state.board.get(boxwidth) - state.board.get(mod - 1);
					state.eval = v;
					writeLog(state);
					if (state.depth == 1)
						Tree.add(state);

					return v;

				} else {

					// writeLog(state);
					// /////////
					int j = i;
					int stones = state.board.get(i);
					if (stones == 0)
						continue;
					box1.set(i, 0);
					while (stones != 0) {
						j = (j + 1) % mod;
						if (j == mod - 1)
							continue;
						box1.set(j, box1.get(j) + 1);
						stones--;
					}
					if (box1.get(j) == 1 && j < boxwidth && j >= 0) {
						int oppoStones = box1.get(mod - 2 - j) + 1
								+ box1.get(boxwidth);
						box1.set(j, 0);
						box1.set(mod - 2 - j, 0);
						box1.set(boxwidth, oppoStones);

					}
					s = ifAllEmpty(box1);
					if (s.equals("1")) {
						int sum = 0;
						for (int x = boxwidth + 1; x < box1.size() - 1; x++) {
							sum = sum + box1.get(x);
							box1.set(x, 0);
						}
						int or = box1.get(box1.size() - 1);
						box1.set(box1.size() - 1, sum + or);

					} else if (s.equals("2")) {
						int sum = 0;
						for (int x = 0; x < boxwidth; x++) {
							sum = sum + box1.get(x);
							box1.set(x, 0);
						}
						int or = box1.get(boxwidth);
						box1.set(boxwidth, sum + or);

					}

					// ///////////

					Node n = new Node();
					n.board = setbox1(box1);
					n.name = this.nodearray.get(i);
					n.parent = state;
					n.typeOfNode = "min";

					if (state.typeOfNode.equals("min"))
						n.depth = state.depth;
					else
						n.depth = state.depth + 1;
					writeLog(state);
					if (j == boxwidth) {

						v = Math.max(v, maxValue(n, n.depth, player));
						state.eval = v;

					} else {

						if (n.depth == cutOffDepth) {

							n.eval = n.board.get(boxwidth)
									- n.board.get(mod - 1);
							writeLog(n);
							v = Math.max(v, n.eval);
							state.eval = v;
							if (n.depth == 1)
								Tree.add(n);
							// writeLog(state);

						} else {
							v = Math.max(v, minValue(n, n.depth, p));
							state.eval = v;

						}

					}
				}
			}
			if (state.depth == 1)
				Tree.add(state);
			writeLog(state);
			return v;

		} else if (player.equals("2")) {

			for (int i = boxwidth - 1; i >= 0; i--) {
				box1 = setbox1(state.board);

				String s = ifAllEmpty(box1);
				if (s.equals("1")) {
					int sum = 0;
					for (int x = boxwidth + 1; x < box1.size() - 1; x++) {
						sum = sum + box1.get(x);
						box1.set(x, 0);
					}
					int or = box1.get(box1.size() - 1);
					box1.set(box1.size() - 1, sum + or);

					state.board = setbox1(box1);

					if (state.depth != cutOffDepth
							|| (state.depth == cutOffDepth && state.typeOfNode
									.equals(state.parent.typeOfNode))) {
						state.eval = v;
						writeLog(state);

					}

					v = state.board.get(boxwidth) - state.board.get(mod - 1);
					state.eval = v;
					writeLog(state);
					if (state.depth == 1)
						Tree.add(state);

					return v;
				} else if (s.equals("2")) {
					int sum = 0;
					for (int x = 0; x < boxwidth; x++) {
						sum = sum + box1.get(x);
						box1.set(x, 0);
					}
					int or = box1.get(boxwidth);
					box1.set(boxwidth, sum + or);

					state.board = setbox1(box1);

					if (state.depth != cutOffDepth
							|| (state.depth == cutOffDepth && state.typeOfNode
									.equals(state.parent.typeOfNode))) {
						state.eval = v;
						writeLog(state);

					}

					v = state.board.get(boxwidth) - state.board.get(mod - 1);
					state.eval = v;
					writeLog(state);
					if (state.depth == 1)
						Tree.add(state);

					return v;

				} else {

					// writeLog(state);
					// //////////
					int j = i;
					int stones = state.board.get(i);
					if (stones == 0)
						continue;
					box1.set(i, 0);
					while (stones != 0) {
						j = (j + 1) % mod;
						if (j == mod - 1)
							continue;
						box1.set(j, box1.get(j) + 1);
						stones--;
					}
					if (box1.get(j) == 1 && j < boxwidth && j >= 0) {
						int oppoStones = box1.get(mod - 2 - j) + 1
								+ box1.get(boxwidth);
						box1.set(j, 0);
						box1.set(mod - 2 - j, 0);
						box1.set(boxwidth, oppoStones);

					}

					s = ifAllEmpty(box1);
					if (s.equals("1")) {
						int sum = 0;
						for (int x = boxwidth + 1; x < box1.size() - 1; x++) {
							sum = sum + box1.get(x);
							box1.set(x, 0);
						}
						int or = box1.get(box1.size() - 1);
						box1.set(box1.size() - 1, sum + or);

					} else if (s.equals("2")) {
						int sum = 0;
						for (int x = 0; x < boxwidth; x++) {
							sum = sum + box1.get(x);
							box1.set(x, 0);
						}
						int or = box1.get(boxwidth);
						box1.set(boxwidth, sum + or);

					}
					// ////////////

					Node n = new Node();
					n.board = setbox1(box1);
					n.name = this.nodearray.get(i);
					n.parent = state;
					n.typeOfNode = "min";

					if (state.typeOfNode.equals("min"))
						n.depth = state.depth;
					else
						n.depth = state.depth + 1;
					writeLog(state);
					if (j == boxwidth) {

						v = Math.max(v, maxValue(n, n.depth, player));
						state.eval = v;

					} else {

						if (n.depth == cutOffDepth) {

							n.eval = n.board.get(boxwidth)
									- n.board.get(mod - 1);
							writeLog(n);
							v = Math.max(v, n.eval);
							state.eval = v;
							if (n.depth == 1)
								Tree.add(n);
							// writeLog(state);
						} else {
							v = Math.max(v, minValue(n, n.depth, p));
							state.eval = v;

						}
					}

				}
			}

			if (state.depth == 1)
				Tree.add(state);
			writeLog(state);
			return v;
		}

		return v;
	}

	private int minValue(Node state, int depth, String player) {

		int v = Integer.MAX_VALUE;
		state.eval = v;
		String p;

		if (player.equals("1"))
			p = "2";
		else
			p = "1";

		int boxwidth = (state.board.size() - 2) / 2;
		ArrayList<Integer> box1 = new ArrayList<Integer>();
		int mod = (2 * boxwidth) + 2;

		if (player.equals("1")) {

			for (int i = boxwidth + 1; i < mod - 1; i++) {
				box1 = setbox1(state.board);

				String s = ifAllEmpty(box1);
				if (s.equals("1")) {
					int sum = 0;
					for (int x = boxwidth + 1; x < box1.size() - 1; x++) {
						sum = sum + box1.get(x);
						box1.set(x, 0);
					}
					int or = box1.get(box1.size() - 1);
					box1.set(box1.size() - 1, sum + or);

					state.board = setbox1(box1);

					if (state.depth != cutOffDepth
							|| (state.depth == cutOffDepth && state.typeOfNode
									.equals(state.parent.typeOfNode))) {
						state.eval = v;
						writeLog(state);

					}

					v = state.board.get(boxwidth) - state.board.get(mod - 1);
					state.eval = v;
					writeLog(state);
					if (state.depth == 1)
						Tree.add(state);

					return v;

				} else if (s.equals("2")) {
					int sum = 0;
					for (int x = 0; x < boxwidth; x++) {
						sum = sum + box1.get(x);
						box1.set(x, 0);
					}
					int or = box1.get(boxwidth);
					box1.set(boxwidth, sum + or);

					state.board = setbox1(box1);

					if (state.depth != cutOffDepth
							|| (state.depth == cutOffDepth && state.typeOfNode
									.equals(state.parent.typeOfNode))) {
						state.eval = v;
						writeLog(state);

					}

					v = state.board.get(boxwidth) - state.board.get(mod - 1);
					state.eval = v;
					writeLog(state);
					if (state.depth == 1)
						Tree.add(state);

					return v;
				} else {

					// //////////

					int j = i;

					int stones = state.board.get(i);
					if (stones == 0)
						continue;
					box1.set(i, 0);
					while (stones != 0) {
						j = (j + 1) % mod;
						if (j == boxwidth)
							continue;
						box1.set(j, box1.get(j) + 1);
						stones--;
					}
					if (box1.get(j) == 1 && j > boxwidth && j < mod - 1) {
						int oppoStones = box1.get(mod - 1 - j - 1) + 1
								+ box1.get(mod - 1);

						box1.set(j, 0);
						box1.set(mod - 2 - j, 0);
						box1.set(mod - 1, oppoStones);

					}

					s = ifAllEmpty(box1);
					if (s.equals("1")) {
						int sum = 0;
						for (int x = boxwidth + 1; x < box1.size() - 1; x++) {
							sum = sum + box1.get(x);
							box1.set(x, 0);
						}
						int or = box1.get(box1.size() - 1);
						box1.set(box1.size() - 1, sum + or);

					} else if (s.equals("2")) {
						int sum = 0;
						for (int x = 0; x < boxwidth; x++) {
							sum = sum + box1.get(x);
							box1.set(x, 0);
						}
						int or = box1.get(boxwidth);
						box1.set(boxwidth, sum + or);

					}
					// ////////////

					Node n = new Node();
					n.board = setbox1(box1);
					n.name = this.nodearray.get(i);
					n.parent = state;
					n.typeOfNode = "max";

					if (state.typeOfNode.equals("max"))
						n.depth = state.depth;
					else
						n.depth = state.depth + 1;
					writeLog(state);
					if (j == mod - 1) {

						// writeLog(state);
						v = Math.min(v, minValue(n, n.depth, player));
						state.eval = v;

					} else {
						// writeLog(state);
						if (n.depth == cutOffDepth) {

							n.eval = n.board.get(boxwidth)
									- n.board.get(mod - 1);
							writeLog(n);
							v = Math.min(v, n.eval);
							state.eval = v;
							if (n.depth == 1)
								Tree.add(n);
							// writeLog(state);

						} else {
							v = Math.min(v, maxValue(n, n.depth, p));
							state.eval = v;

						}
					}

				}
			}
			if (state.depth == 1)
				Tree.add(state);
			writeLog(state);
			return v;

		} else if (player.equals("2")) {

			for (int i = mod - 2; i > boxwidth; i--) {
				box1 = setbox1(state.board);

				String s = ifAllEmpty(box1);
				if (s.equals("1")) {
					int sum = 0;
					for (int x = boxwidth + 1; x < box1.size() - 1; x++) {
						sum = sum + box1.get(x);
						box1.set(x, 0);
					}
					int or = box1.get(box1.size() - 1);
					box1.set(box1.size() - 1, sum + or);
					state.board = setbox1(box1);

					if (state.depth != cutOffDepth
							|| (state.depth == cutOffDepth && state.typeOfNode
									.equals(state.parent.typeOfNode))) {
						state.eval = v;
						writeLog(state);

					}

					v = state.board.get(boxwidth) - state.board.get(mod - 1);
					state.eval = v;
					writeLog(state);
					if (state.depth == 1)
						Tree.add(state);

					return v;

				} else if (s.equals("2")) {
					int sum = 0;
					for (int x = 0; x < boxwidth; x++) {
						sum = sum + box1.get(x);
						box1.set(x, 0);
					}
					int or = box1.get(boxwidth);
					box1.set(boxwidth, sum + or);

					state.board = setbox1(box1);

					if (state.depth != cutOffDepth
							|| (state.depth == cutOffDepth && state.typeOfNode
									.equals(state.parent.typeOfNode))) {
						state.eval = v;
						writeLog(state);

					}

					v = state.board.get(boxwidth) - state.board.get(mod - 1);
					state.eval = v;
					writeLog(state);
					if (state.depth == 1)
						Tree.add(state);

					return v;
				} else {

					// //////////
					int j = i;
					int stones = state.board.get(i);
					if (stones == 0)
						continue;
					box1.set(i, 0);
					while (stones != 0) {
						j = (j + 1) % mod;
						if (j == boxwidth)
							continue;
						box1.set(j, box1.get(j) + 1);
						stones--;
					}
					if (box1.get(j) == 1 && j > boxwidth && j < mod - 1) {
						int oppoStones = box1.get(mod - 1 - j - 1) + 1
								+ box1.get(mod - 1);
						box1.set(j, 0);
						box1.set(mod - 2 - j, 0);
						box1.set(mod - 1, oppoStones);

					}

					s = ifAllEmpty(box1);
					if (s.equals("1")) {
						int sum = 0;
						for (int x = boxwidth + 1; x < box1.size() - 1; x++) {
							sum = sum + box1.get(x);
							box1.set(x, 0);
						}
						int or = box1.get(box1.size() - 1);
						box1.set(box1.size() - 1, sum + or);

					} else if (s.equals("2")) {
						int sum = 0;
						for (int x = 0; x < boxwidth; x++) {
							sum = sum + box1.get(x);
							box1.set(x, 0);
						}
						int or = box1.get(boxwidth);
						box1.set(boxwidth, sum + or);

					}
					// ////////////

					Node n = new Node();
					n.board = setbox1(box1);
					n.name = this.nodearray.get(i);
					n.parent = state;
					n.typeOfNode = "max";

					if (state.typeOfNode.equals("max"))
						n.depth = state.depth;
					else
						n.depth = state.depth + 1;
					writeLog(state);
					if (j == mod - 1) {

						// writeLog(state);
						v = Math.min(v, minValue(n, n.depth, player));
						state.eval = v;

					} else {

						// writeLog(state);
						if (n.depth == cutOffDepth) {

							n.eval = n.board.get(boxwidth)
									- n.board.get(mod - 1);
							writeLog(n);
							v = Math.min(v, n.eval);
							state.eval = v;
							if (n.depth == 1)
								Tree.add(n);

						} else {
							v = Math.min(v, maxValue(n, n.depth, p));
							state.eval = v;

						}
					}

				}
			}
			if (state.depth == 1)
				Tree.add(state);

			writeLog(state);
			return v;

		}
		return v;
	}

	private int maxValueAB(Node state, int depth, String player, int alpha,
			int beta) {
		int v = Integer.MIN_VALUE;
		state.eval = v;

		String p;
		if (player.equals("1"))
			p = "2";
		else
			p = "1";

		int boxwidth = (state.board.size() - 2) / 2;
		ArrayList<Integer> box1 = new ArrayList<Integer>();
		int mod = (2 * boxwidth) + 2;

		if (player.equals("1")) {
			writeLogAB(state, alpha, beta);
			for (int i = 0; i < boxwidth; i++) {
				box1 = setbox1(state.board);

				String s = ifAllEmpty(box1);
				if (s.equals("1")) {
					int sum = 0;
					for (int x = boxwidth + 1; x < box1.size() - 1; x++) {
						sum = sum + box1.get(x);
						box1.set(x, 0);
					}
					int or = box1.get(box1.size() - 1);
					box1.set(box1.size() - 1, sum + or);

					state.board = setbox1(box1);

					if (state.depth != cutOffDepth
							|| (state.depth == cutOffDepth && state.typeOfNode
									.equals(state.parent.typeOfNode))) {
						// state.eval = v;
						// writeLog(state);

					}

					v = state.board.get(boxwidth) - state.board.get(mod - 1);
					state.eval = v;
					writeLogAB(state, alpha, beta);
					if (state.depth == 1)
						Tree.add(state);

					return v;
				} else if (s.equals("2")) {
					int sum = 0;
					for (int x = 0; x < boxwidth; x++) {
						sum = sum + box1.get(x);
						box1.set(x, 0);
					}
					int or = box1.get(boxwidth);
					box1.set(boxwidth, sum + or);

					state.board = setbox1(box1);

					if (state.depth != cutOffDepth
							|| (state.depth == cutOffDepth && state.typeOfNode
									.equals(state.parent.typeOfNode))) {
						// state.eval = v;
						// writeLog(state);
					}

					v = state.board.get(boxwidth) - state.board.get(mod - 1);
					state.eval = v;
					writeLogAB(state, alpha, beta);
					if (state.depth == 1)
						Tree.add(state);

					return v;

				} else {

					// writeLog(state);
					// /////////
					int j = i;
					int stones = state.board.get(i);
					if (stones == 0)
						continue;
					box1.set(i, 0);
					while (stones != 0) {
						j = (j + 1) % mod;
						if (j == mod - 1)
							continue;
						box1.set(j, box1.get(j) + 1);
						stones--;
					}
					if (box1.get(j) == 1 && j < boxwidth && j >= 0) {
						int oppoStones = box1.get(mod - 2 - j) + 1
								+ box1.get(boxwidth);
						box1.set(j, 0);
						box1.set(mod - 2 - j, 0);
						box1.set(boxwidth, oppoStones);

					}
					s = ifAllEmpty(box1);
					if (s.equals("1")) {
						int sum = 0;
						for (int x = boxwidth + 1; x < box1.size() - 1; x++) {
							sum = sum + box1.get(x);
							box1.set(x, 0);
						}
						int or = box1.get(box1.size() - 1);
						box1.set(box1.size() - 1, sum + or);

					} else if (s.equals("2")) {
						int sum = 0;
						for (int x = 0; x < boxwidth; x++) {
							sum = sum + box1.get(x);
							box1.set(x, 0);
						}
						int or = box1.get(boxwidth);
						box1.set(boxwidth, sum + or);

					}

					// ///////////

					Node n = new Node();
					n.board = setbox1(box1);
					n.name = this.nodearray.get(i);
					n.parent = state;
					n.typeOfNode = "min";

					if (state.typeOfNode.equals("min"))
						n.depth = state.depth;
					else
						n.depth = state.depth + 1;

					if (j == boxwidth) {
						v = Math.max(v,
								maxValueAB(n, n.depth, player, alpha, beta));
						state.eval = v;

					} else {

						if (n.depth == cutOffDepth) {

							n.eval = n.board.get(boxwidth)
									- n.board.get(mod - 1);
							writeLogAB(n, alpha, beta);
							v = Math.max(v, n.eval);
							state.eval = v;
							if (n.depth == 1)
								Tree.add(n);

						} else {
							v = Math.max(v,
									minValueAB(n, n.depth, p, alpha, beta));
							state.eval = v;

						}

					}

				}

				if (v >= beta) {
					writeLogAB(state, alpha, beta);
					return v;
				}

				alpha = Math.max(alpha, v);
				writeLogAB(state, alpha, beta);
			}
			if (state.depth == 1)
				Tree.add(state);
			return v;

		} else if (player.equals("2")) {
			writeLogAB(state, alpha, beta);
			for (int i = boxwidth - 1; i >= 0; i--) {
				box1 = setbox1(state.board);

				String s = ifAllEmpty(box1);
				if (s.equals("1")) {
					int sum = 0;
					for (int x = boxwidth + 1; x < box1.size() - 1; x++) {
						sum = sum + box1.get(x);
						box1.set(x, 0);
					}
					int or = box1.get(box1.size() - 1);
					box1.set(box1.size() - 1, sum + or);

					state.board = setbox1(box1);

					if (state.depth != cutOffDepth
							|| (state.depth == cutOffDepth && state.typeOfNode
									.equals(state.parent.typeOfNode))) {
						// state.eval = v;
						// writeLog(state);

					}

					v = state.board.get(boxwidth) - state.board.get(mod - 1);
					state.eval = v;
					writeLogAB(state, alpha, beta);
					if (state.depth == 1)
						Tree.add(state);

					return v;
				} else if (s.equals("2")) {
					int sum = 0;
					for (int x = 0; x < boxwidth; x++) {
						sum = sum + box1.get(x);
						box1.set(x, 0);
					}
					int or = box1.get(boxwidth);
					box1.set(boxwidth, sum + or);

					state.board = setbox1(box1);

					if (state.depth != cutOffDepth
							|| (state.depth == cutOffDepth && state.typeOfNode
									.equals(state.parent.typeOfNode))) {
						// state.eval = v;
						// writeLog(state);

					}

					v = state.board.get(boxwidth) - state.board.get(mod - 1);
					state.eval = v;
					writeLogAB(state, alpha, beta);
					if (state.depth == 1)
						Tree.add(state);

					return v;

				} else {

					// writeLog(state);
					// //////////
					int j = i;
					int stones = state.board.get(i);
					if (stones == 0)
						continue;
					box1.set(i, 0);
					while (stones != 0) {
						j = (j + 1) % mod;
						if (j == mod - 1)
							continue;
						box1.set(j, box1.get(j) + 1);
						stones--;
					}
					if (box1.get(j) == 1 && j < boxwidth && j >= 0) {
						int oppoStones = box1.get(mod - 2 - j) + 1
								+ box1.get(boxwidth);
						box1.set(j, 0);
						box1.set(mod - 2 - j, 0);
						box1.set(boxwidth, oppoStones);

					}

					s = ifAllEmpty(box1);
					if (s.equals("1")) {
						int sum = 0;
						for (int x = boxwidth + 1; x < box1.size() - 1; x++) {
							sum = sum + box1.get(x);
							box1.set(x, 0);
						}
						int or = box1.get(box1.size() - 1);
						box1.set(box1.size() - 1, sum + or);

					} else if (s.equals("2")) {
						int sum = 0;
						for (int x = 0; x < boxwidth; x++) {
							sum = sum + box1.get(x);
							box1.set(x, 0);
						}
						int or = box1.get(boxwidth);
						box1.set(boxwidth, sum + or);

					}
					// ////////////

					Node n = new Node();
					n.board = setbox1(box1);
					n.name = this.nodearray.get(i);
					n.parent = state;
					n.typeOfNode = "min";

					if (state.typeOfNode.equals("min"))
						n.depth = state.depth;
					else
						n.depth = state.depth + 1;

					if (j == boxwidth) {

						v = Math.max(v,
								maxValueAB(n, n.depth, player, alpha, beta));
						state.eval = v;

					} else {

						if (n.depth == cutOffDepth) {

							n.eval = n.board.get(boxwidth)
									- n.board.get(mod - 1);
							writeLogAB(n, alpha, beta);
							v = Math.max(v, n.eval);
							state.eval = v;
							if (n.depth == 1)
								Tree.add(n);

						} else {
							v = Math.max(v,
									minValueAB(n, n.depth, p, alpha, beta));
							state.eval = v;

						}

					}

				}

				if (v >= beta) {
					writeLogAB(state, alpha, beta);
					return v;
				}

				alpha = Math.max(alpha, v);
				writeLogAB(state, alpha, beta);
			}

			if (state.depth == 1)
				Tree.add(state);

			return v;
		}
		return v;
	}

	private int minValueAB(Node state, int depth, String player, int alpha,
			int beta) {

		int v = Integer.MAX_VALUE;
		state.eval = v;
		String p;

		if (player.equals("1"))
			p = "2";
		else
			p = "1";

		int boxwidth = (state.board.size() - 2) / 2;
		ArrayList<Integer> box1 = new ArrayList<Integer>();
		int mod = (2 * boxwidth) + 2;

		if (player.equals("1")) {
			writeLogAB(state, alpha, beta);
			for (int i = boxwidth + 1; i < mod - 1; i++) {
				box1 = setbox1(state.board);

				String s = ifAllEmpty(box1);
				if (s.equals("1")) {
					int sum = 0;
					for (int x = boxwidth + 1; x < box1.size() - 1; x++) {
						sum = sum + box1.get(x);
						box1.set(x, 0);
					}
					int or = box1.get(box1.size() - 1);
					box1.set(box1.size() - 1, sum + or);

					state.board = setbox1(box1);

					if (state.depth != cutOffDepth
							|| (state.depth == cutOffDepth && state.typeOfNode
									.equals(state.parent.typeOfNode))) {
						// state.eval = v;
						// writeLog(state);

					}

					v = state.board.get(boxwidth) - state.board.get(mod - 1);
					state.eval = v;
					writeLogAB(state, alpha, beta);
					if (state.depth == 1)
						Tree.add(state);

					return v;

				} else if (s.equals("2")) {
					int sum = 0;
					for (int x = 0; x < boxwidth; x++) {
						sum = sum + box1.get(x);
						box1.set(x, 0);
					}
					int or = box1.get(boxwidth);
					box1.set(boxwidth, sum + or);

					state.board = setbox1(box1);

					if (state.depth != cutOffDepth
							|| (state.depth == cutOffDepth && state.typeOfNode
									.equals(state.parent.typeOfNode))) {
						// state.eval = v;
						// writeLog(state);

					}

					v = state.board.get(boxwidth) - state.board.get(mod - 1);
					state.eval = v;
					writeLogAB(state, alpha, beta);
					if (state.depth == 1)
						Tree.add(state);

					return v;
				} else {

					// //////////

					int j = i;

					int stones = state.board.get(i);
					if (stones == 0)
						continue;
					box1.set(i, 0);
					while (stones != 0) {
						j = (j + 1) % mod;
						if (j == boxwidth)
							continue;
						box1.set(j, box1.get(j) + 1);
						stones--;
					}
					if (box1.get(j) == 1 && j > boxwidth && j < mod - 1) {
						int oppoStones = box1.get(mod - 1 - j - 1) + 1
								+ box1.get(mod - 1);

						box1.set(j, 0);
						box1.set(mod - 2 - j, 0);
						box1.set(mod - 1, oppoStones);

					}

					s = ifAllEmpty(box1);
					if (s.equals("1")) {
						int sum = 0;
						for (int x = boxwidth + 1; x < box1.size() - 1; x++) {
							sum = sum + box1.get(x);
							box1.set(x, 0);
						}
						int or = box1.get(box1.size() - 1);
						box1.set(box1.size() - 1, sum + or);

					} else if (s.equals("2")) {
						int sum = 0;
						for (int x = 0; x < boxwidth; x++) {
							sum = sum + box1.get(x);
							box1.set(x, 0);
						}
						int or = box1.get(boxwidth);
						box1.set(boxwidth, sum + or);

					}
					// ////////////

					Node n = new Node();
					n.board = setbox1(box1);
					n.name = this.nodearray.get(i);
					n.parent = state;
					n.typeOfNode = "max";

					if (state.typeOfNode.equals("max"))
						n.depth = state.depth;
					else
						n.depth = state.depth + 1;

					if (j == mod - 1) {

						v = Math.min(v,
								minValueAB(n, n.depth, player, alpha, beta));
						state.eval = v;

					} else {

						if (n.depth == cutOffDepth) {

							n.eval = n.board.get(boxwidth)
									- n.board.get(mod - 1);
							writeLogAB(n, alpha, beta);
							v = Math.min(v, n.eval);
							state.eval = v;
							if (n.depth == 1)
								Tree.add(n);

						} else {
							v = Math.min(v,
									maxValueAB(n, n.depth, p, alpha, beta));
							state.eval = v;

						}

					}

				}

				if (v <= alpha) {
					writeLogAB(state, alpha, beta);
					return v;
				}
				beta = Math.min(beta, v);
				writeLogAB(state, alpha, beta);

			}
			if (state.depth == 1)
				Tree.add(state);
			return v;

		} else if (player.equals("2")) {
			writeLogAB(state, alpha, beta);
			for (int i = mod - 2; i > boxwidth; i--) {
				box1 = setbox1(state.board);

				String s = ifAllEmpty(box1);
				if (s.equals("1")) {
					int sum = 0;
					for (int x = boxwidth + 1; x < box1.size() - 1; x++) {
						sum = sum + box1.get(x);
						box1.set(x, 0);
					}
					int or = box1.get(box1.size() - 1);
					box1.set(box1.size() - 1, sum + or);
					state.board = setbox1(box1);

					if (state.depth != cutOffDepth
							|| (state.depth == cutOffDepth && state.typeOfNode
									.equals(state.parent.typeOfNode))) {
						// state.eval = v;
						// writeLog(state);

					}

					v = state.board.get(boxwidth) - state.board.get(mod - 1);
					state.eval = v;
					writeLogAB(state, alpha, beta);
					if (state.depth == 1)
						Tree.add(state);

					return v;

				} else if (s.equals("2")) {
					int sum = 0;
					for (int x = 0; x < boxwidth; x++) {
						sum = sum + box1.get(x);
						box1.set(x, 0);
					}
					int or = box1.get(boxwidth);
					box1.set(boxwidth, sum + or);

					state.board = setbox1(box1);

					if (state.depth != cutOffDepth
							|| (state.depth == cutOffDepth && state.typeOfNode
									.equals(state.parent.typeOfNode))) {
						// state.eval = v;
						// writeLog(state);

					}

					v = state.board.get(boxwidth) - state.board.get(mod - 1);
					state.eval = v;
					writeLogAB(state, alpha, beta);
					if (state.depth == 1)
						Tree.add(state);

					return v;
				} else {

					// //////////
					int j = i;
					int stones = state.board.get(i);
					if (stones == 0)
						continue;
					box1.set(i, 0);
					while (stones != 0) {
						j = (j + 1) % mod;
						if (j == boxwidth)
							continue;
						box1.set(j, box1.get(j) + 1);
						stones--;
					}
					if (box1.get(j) == 1 && j > boxwidth && j < mod - 1) {
						int oppoStones = box1.get(mod - 1 - j - 1) + 1
								+ box1.get(mod - 1);
						box1.set(j, 0);
						box1.set(mod - 2 - j, 0);
						box1.set(mod - 1, oppoStones);

					}

					s = ifAllEmpty(box1);
					if (s.equals("1")) {
						int sum = 0;
						for (int x = boxwidth + 1; x < box1.size() - 1; x++) {
							sum = sum + box1.get(x);
							box1.set(x, 0);
						}
						int or = box1.get(box1.size() - 1);
						box1.set(box1.size() - 1, sum + or);

					} else if (s.equals("2")) {
						int sum = 0;
						for (int x = 0; x < boxwidth; x++) {
							sum = sum + box1.get(x);
							box1.set(x, 0);
						}
						int or = box1.get(boxwidth);
						box1.set(boxwidth, sum + or);

					}
					// ////////////

					Node n = new Node();
					n.board = setbox1(box1);
					n.name = this.nodearray.get(i);
					n.parent = state;
					n.typeOfNode = "max";

					if (state.typeOfNode.equals("max"))
						n.depth = state.depth;
					else
						n.depth = state.depth + 1;

					if (j == mod - 1) {

						v = Math.min(v,
								minValueAB(n, n.depth, player, alpha, beta));
						state.eval = v;

					} else {

						if (n.depth == cutOffDepth) {

							n.eval = n.board.get(boxwidth)
									- n.board.get(mod - 1);
							writeLogAB(n, alpha, beta);
							v = Math.min(v, n.eval);
							state.eval = v;
							if (n.depth == 1)
								Tree.add(n);
						} else {
							v = Math.min(v,
									maxValueAB(n, n.depth, p, alpha, beta));
							state.eval = v;
						}
					}

				}

				if (v <= alpha) {
					writeLogAB(state, alpha, beta);
					return v;
				}
				beta = Math.min(beta, v);
				writeLogAB(state, alpha, beta);

			}
			if (state.depth == 1)
				Tree.add(state);
			return v;

		}
		return v;
	}

	private static ArrayList<String> createNodeArray(int boxWidth, String player) {

		int size = (boxWidth * 2) + 2;
		if (player.equals("1")) {
			int i, f = 2;
			String s = "";
			ArrayList<String> nodearray = new ArrayList<String>();

			for (i = 0; i < boxWidth; i++) {

				s = "B" + f++;
				nodearray.add(i, s);
			}
			i++;
			i++;
			nodearray.add(boxWidth, "B" + i + "");

			f = boxWidth + 1;
			for (i = boxWidth + 1; i < size - 1; i++) {
				s = "A" + f--;
				nodearray.add(i, s);
			}

			nodearray.add(size - 1, "A1");

			return nodearray;
		} else {
			int i, f = boxWidth + 1;
			String s = "";
			ArrayList<String> nodearray = new ArrayList<String>();

			for (i = 0; i < boxWidth; i++) {

				s = "A" + f--;
				nodearray.add(i, s);
			}

			nodearray.add(boxWidth, "A" + f + "");

			f = 2;
			for (i = boxWidth + 1; i < size; i++) {
				s = "B" + f++;
				nodearray.add(i, s);
			}

			return nodearray;
		}
	}

	ArrayList<Integer> setbox1(ArrayList<Integer> box) {
		ArrayList<Integer> box1 = new ArrayList<Integer>();

		for (Integer b : box) {
			box1.add(b);
		}
		return box1;

	}

	private String ifAllEmpty(ArrayList<Integer> box1) {

		int size = (box1.size() - 2) / 2;
		int count1 = 0, count2 = 0;
		for (int i = 0; i < size; i++) {
			if (box1.get(i) == 0)
				count1++;
		}
		if (count1 == size)
			return "1";

		for (int i = size + 1; i < box1.size() - 1; i++) {
			if (box1.get(i) == 0)
				count2++;
		}
		if (count2 == size)
			return "2";

		else
			return "0";
	}

	ArrayList<Integer> greedy(String player, ArrayList<Integer> box,
			int boxwidth) {

		ArrayList<Integer> box1 = new ArrayList<Integer>();
		ArrayList<ArrayList<Integer>> allBoxes = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> evals = new ArrayList<Integer>();

		int mod = (2 * boxwidth) + 2;

		if (player.equals("1")) {
			for (int i = 0; i < boxwidth; i++) {

				box1 = setbox1(box);

				String s = ifAllEmpty(box1);
				if (s.equals("1")) {
					int sum = 0;
					for (int x = boxwidth + 1; x < box1.size() - 1; x++) {
						sum = sum + box1.get(x);
						box1.set(x, 0);
					}
					int or = box1.get(box1.size() - 1);
					box1.set(box1.size() - 1, sum + or);
					return box1;
				} else if (s.equals("2")) {
					int sum = 0;
					for (int x = 0; x < boxwidth; x++) {
						sum = sum + box1.get(x);
						box1.set(x, 0);
					}
					int or = box1.get(boxwidth);
					box1.set(boxwidth, sum + or);
					return box1;
				} else {
					// //////////
					int j = i;
					int stones = box.get(i);
					if (stones == 0)
						continue;
					box1.set(i, 0);
					while (stones != 0) {
						j = (j + 1) % mod;
						if (j == mod - 1)
							continue;
						box1.set(j, box1.get(j) + 1);
						stones--;
					}
					if (box1.get(j) == 1 && j < boxwidth && j >= 0) {
						int oppoStones = box1.get(mod - 2 - j) + 1
								+ box1.get(boxwidth);
						box1.set(j, 0);
						box1.set(mod - 2 - j, 0);
						box1.set(boxwidth, oppoStones);

					}

					s = ifAllEmpty(box1);
					if (s.equals("1")) {
						int sum = 0;
						for (int x = boxwidth + 1; x < box1.size() - 1; x++) {
							sum = sum + box1.get(x);
							box1.set(x, 0);
						}
						int or = box1.get(box1.size() - 1);
						box1.set(box1.size() - 1, sum + or);
						return box1;
					} else if (s.equals("2")) {
						int sum = 0;
						for (int x = 0; x < boxwidth; x++) {
							sum = sum + box1.get(x);
							box1.set(x, 0);
						}
						int or = box1.get(boxwidth);
						box1.set(boxwidth, sum + or);
						return box1;
					}
					// ///////////

					if (j == boxwidth) {
						box1 = greedy(player, box1, boxwidth);
						evals.add(box1.get(boxwidth) - box1.get(box.size() - 1));
						allBoxes.add(box1);
					} else {
						evals.add(box1.get(boxwidth) - box1.get(box.size() - 1));
						allBoxes.add(box1);
					}

					if (i == boxwidth - 1) {
						return maximum(allBoxes, evals);
					}
				}
			}
		} else if (player.equals("2")) {
			for (int i = boxwidth - 1; i >= 0; i--) {

				box1 = setbox1(box);

				String s = ifAllEmpty(box1);
				if (s.equals("1")) {
					int sum = 0;
					for (int x = boxwidth + 1; x < box1.size() - 1; x++) {
						sum = sum + box1.get(x);
						box1.set(x, 0);
					}
					int or = box1.get(box1.size() - 1);
					box1.set(box1.size() - 1, sum + or);
					return box1;
				} else if (s.equals("2")) {
					int sum = 0;
					for (int x = 0; x < boxwidth; x++) {
						sum = sum + box1.get(x);
						box1.set(x, 0);
					}
					int or = box1.get(boxwidth);
					box1.set(boxwidth, sum + or);
					return box1;
				} else {
					// //////////
					int j = i;
					int stones = box.get(i);
					if (stones == 0)
						continue;
					box1.set(i, 0);
					while (stones != 0) {
						j = (j + 1) % mod;
						if (j == mod - 1)
							continue;
						box1.set(j, box1.get(j) + 1);
						stones--;
					}
					if (box1.get(j) == 1 && j < boxwidth && j >= 0) {
						int oppoStones = box1.get(mod - 2 - j) + 1
								+ box1.get(boxwidth);
						box1.set(j, 0);
						box1.set(mod - 2 - j, 0);
						box1.set(boxwidth, oppoStones);

					}
					s = ifAllEmpty(box1);
					if (s.equals("1")) {
						int sum = 0;
						for (int x = boxwidth + 1; x < box1.size() - 1; x++) {
							sum = sum + box1.get(x);
							box1.set(x, 0);
						}
						int or = box1.get(box1.size() - 1);
						box1.set(box1.size() - 1, sum + or);
						return box1;
					} else if (s.equals("2")) {
						int sum = 0;
						for (int x = 0; x < boxwidth; x++) {
							sum = sum + box1.get(x);
							box1.set(x, 0);
						}
						int or = box1.get(boxwidth);
						box1.set(boxwidth, sum + or);
						return box1;
					}

					// ////////////
					if (j == boxwidth) {
						box1 = greedy(player, box1, boxwidth);
						evals.add(box1.get(boxwidth) - box1.get(box.size() - 1));
						allBoxes.add(box1);
					} else {
						evals.add(box1.get(boxwidth) - box1.get(box.size() - 1));
						allBoxes.add(box1);
					}

					if (i == 0) {
						return maximum(allBoxes, evals);
					}
				}
			}
		}
		return maximum(allBoxes, evals);

	}

	private ArrayList<Integer> maximum(ArrayList<ArrayList<Integer>> allBoxes,
			ArrayList<Integer> evals) {

		ArrayList<Integer> a = new ArrayList<Integer>();
		int max = 0;
		for (int j = 0; j < evals.size(); j++) {

			if (j == 0) {
				a = allBoxes.get(j);
				max = evals.get(j);
			} else {
				if (evals.get(j) > max) {
					a = allBoxes.get(j);
					max = evals.get(j);
				}
			}
		}
		return a;
	}

}

// //////////

class Node {

	String name;
	int eval;
	Node parent;
	int depth;
	String typeOfNode;
	ArrayList<Integer> board = new ArrayList<Integer>();

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		if (this.parent == null)
			return name + " " + board + "  " + depth + "  " + eval;
		else
			return name + " " + board + "  " + depth + "  " + eval
					+ "    parent " + parent.name + "   " + parent.depth + "";
	}

	public int getEval() {
		return eval;
	}

	public void setEval(int eval) {
		this.eval = eval;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public String getTypeOfNode() {
		return typeOfNode;
	}

	public void setTypeOfNode(String typeOfNode) {
		this.typeOfNode = typeOfNode;
	}

}
