package deepnets;

import java.util.*;

public class BiasNode {

	public static final Node INSTANCE = ((ArrayList<Node>)Layer.createInputLayer(1, Node.BASIC_NODE_FACTORY).getNodes()).get(0);
	private static final Map<Node, Connection> toNodesMap = new HashMap<Node, Connection>();
	
	static {
		INSTANCE.clamp(1);
		INSTANCE.setName("BIAS");
	}
	
	public static void connectToLayer(Layer<? extends Node> outputLayer) {
		for (Node n : outputLayer.getNodes()) connectToNode(n);
	}

	public static void connectToNode(Node node) {
		Connection conn = Connection.getOrCreate(INSTANCE, node);
		toNodesMap.put(node, conn);
	}
	public static void connectToNode(Node node, AccruingWeight weight) {
		Connection conn = Connection.getOrCreate(INSTANCE, node, weight);
		toNodesMap.put(node, conn);
	}
	
	public static void clearConnections() {
		INSTANCE.getOutputConnections().clear();
		toNodesMap.clear();
	}
	
	public static Connection getBiasConnection(Node n) {
		return toNodesMap.get(n);
	}
	
}