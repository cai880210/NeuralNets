package deepnets.testing;

import java.awt.*;
import java.util.List;

import javax.swing.*;

import modeler.*;
import reasoner.Planner;

import deepnets.*;

public abstract class GridGame {
	protected static JFrame frame = new JFrame();
	protected static GridPanel gridPanel = new GridPanel(frame, 500);
	protected static GridGameControlPanel controlPanel = new GridGameControlPanel(frame);
	static {
		frame.setLayout(new GridLayout(0,2));
		frame.add(gridPanel);
		frame.add(controlPanel);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
	}
	protected final int rows, cols;
	double[] chosenAction;
	public GridGame(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
	}
	protected static int getGridLoc(int col, int row, int[][] grid) {
		int cols = grid.length;
		int rows = grid[0].length;
		return grid[(col + cols) % cols][(row + rows) % rows];
	}
	public static double sum(double[][] grid) {
		double sum = 0;
		for (int c = 0; c < grid.length; c++) {
			for (int r = 0; r < grid[c].length; r++) {
				sum += grid[c][r];
			}
		}
		return sum;
	}
	public static double sum(double[] state) {
		double sum = 0;
		for (double d : state) sum += d;
		return sum;
	}
	public static double pctCorrect(int[][] grid1, double[][] grid2, boolean justOnes) {
		double sum = 0;
		int n = 0;
		for (int c = 0; c < grid1.length; c++) {
			for (int r = 0; r < grid1[c].length; r++) {
				int o1 = grid1[c][r];
				if (justOnes && o1 < 1) continue;
				double err = o1 - grid2[c][r];
				sum += Math.abs(err);
				n++;
			}
		}
		return n == 0 ? Double.NaN : Utils.round(1 - sum/n, 4) * 100;
	}
	
	public abstract double[] getState();

	public abstract void oneTurn();
	
	public void setupForTurn() {}
	
	protected static void trainModeler(ModelLearnerHeavy modeler, int turns, GridGame game, int sampleSizeMultiplier,
			int repaintMs, List<double[]> actionChoices, EnvTranslator actionTranslator) {
		Planner chimp = Planner.createRandomChimp();
		boolean repaint = repaintMs > 0;
		for (int t = 0; t < turns; t++) {
			long startMs = System.currentTimeMillis();
			double[] actionNN = chimp.getOptimalAction(game.getState(), actionChoices, 0, 0.1);
			game.chosenAction = actionTranslator.fromNN(actionNN);
			game.setupForTurn();
			modeler.observePreState(game.getState());
			modeler.observeAction(actionTranslator.toNN(game.chosenAction));
			game.oneTurn();
			frame.repaint();
			modeler.observePostState(game.getState());
			modeler.saveMemory();
			if (repaint) try {
				long elapsedMs = System.currentTimeMillis() - startMs;
				Thread.sleep(Math.max(0, repaintMs - elapsedMs));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected static ModelLearnerHeavy trainedModeler(int numVars, int numStates, GridGame game, int sampleSizeMultiplier,
			int repaintMs, List<double[]> actionChoices, EnvTranslator actionTranslator, int[] jdmHL) {
		int turns = numStates * sampleSizeMultiplier;
		ModelLearnerHeavy modeler = new ModelLearnerHeavy(500, new int[] {numVars,numVars},
				null, jdmHL, ActivationFunction.SIGMOID0p5, turns);
		trainModeler(modeler, turns, game, sampleSizeMultiplier, repaintMs, actionChoices, actionTranslator);
		return modeler;
	}
	
	protected abstract void paintGrid(Graphics g);
	
	public static void print(double[][] grid) {
		for (int r = 0; r < grid[0].length; r++) {
			String s = "";
			for (int c = 0; c < grid.length; c++) {
				s += Utils.round(grid[c][r], 2) + "	";
			}
			System.out.println(s);
		}
		System.out.println("***********");
	}
}

@SuppressWarnings("serial")
class GridPanel extends JPanel {
	final int gUnit;
	GridGame game;
	JFrame parent;
	public GridPanel(JFrame parent, int gUnit) {
		this.parent = parent;
		this.gUnit = gUnit;
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (game != null) game.paintGrid(g);
	}
	public Dimension getPreferredSize() {
		if (game == null) return new Dimension(0, 0);
		return new Dimension(gUnit, gUnit);
	}
	void setGame(GridGame game) {
		this.game = game;
		parent.pack();
	}
}
@SuppressWarnings("serial")
class GridGameControlPanel extends JPanel {
	JFrame parent;
	GridTagGame game;
	JButton b1 = new JButton("Learn");
	JButton b2 = new JButton("Follow");
	JButton b3 = new JButton("Evade");
	public GridGameControlPanel(JFrame parent) {this.parent = parent;}
	void setGame(GridTagGame game) {
		this.game = game;
		parent.pack();
	}
}