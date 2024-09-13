import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Node {
	int mId, pId, color, maxDepth, version;
	boolean isChanged;
	List<Node> children = new ArrayList<>();
	
	public Node(int mId, int pId, int color, int maxDepth, int version) {
		this.mId = mId;
		this.pId = pId;
		this.color = color;
		this.maxDepth = maxDepth;
		this.version = version;
	}

	@Override
	public String toString() {
		return "Node [mId=" + mId + ", pId=" + pId + ", color=" + color + ", maxDepth=" + maxDepth + ", version="
				+ version + ", isChanged=" + isChanged + "]";
	}
	
	
}

public class Main {
	
	static int Q;
	static Node[] nodes = new Node[100_001];
	static List<Node> rootNodes = new ArrayList<>();
	static int version;
	static long sumResult;
	
	public static void main(String[] args) throws IOException {
		Scanner sc = new Scanner(System.in);
		
		Q = sc.nextInt();
		int comm = 0;
		
		for (int i = 0; i < Q; i++) {
			comm = sc.nextInt();
			
			switch (comm) {
			case 100:
				addNode(sc.nextInt(), sc.nextInt(), sc.nextInt(), sc.nextInt());
				break;
			case 200:
				changeColor(sc.nextInt(), sc.nextInt());
				break;
			case 300:
				int result1 = findColor(sc.nextInt());
				System.out.println(result1);
				break;
			case 400:
				long result2 =search();
				System.out.println(result2);
				break;
			}
		}
	}
	
	public static void addNode(int mId, int pId, int color, int maxDepth) {
		Node newNode = new Node(mId, pId, color, maxDepth, version++);
		
		if (pId == -1) { // 루트 노트일 경우
			nodes[mId] = newNode;
			rootNodes.add(newNode);
		} else { // 아닐 경우
			if (isAvailable(newNode)) {
				nodes[mId] = newNode;
				nodes[pId].children.add(newNode);
			}
		}
	}
	
	public static void changeColor(int mId, int color) {
		nodes[mId].color = color;
		nodes[mId].version = version++;
		nodes[mId].isChanged = true;
	}
	
	public static int findColor(int mId) {
		Node node = nodes[mId];
		
		int latestVersion = Integer.MIN_VALUE;
		int color = node.color;
		
		while (node.pId != -1) {
			if (latestVersion < node.version) {
				color = node.color;
				latestVersion = node.version;
			}
			
			node = nodes[node.pId];
		}
		
		return color;
	}
	
	public static long search() {
		int result = 0;
		
		for (Node node : rootNodes) {
			sumResult = 0;
			dfs(node, node.color, node.version, node.isChanged);
			result += sumResult;
		}
		
		return result;
	}
	
	private static boolean isAvailable(Node node) {
		int cnt = 1;
		
		while (true) {
			if (nodes[node.pId].maxDepth <= cnt) return false;
			
			node = nodes[node.pId];
			cnt++;
			
			if (node.pId == -1) break;
		}
		
		return true;
	}
	
	private static int dfs(Node node, int canColor, int canVersion, boolean canChange) {
		if (canChange) {
			if (node.version < canVersion) {
				node.color = canColor;
				node.version = canVersion;
			} else if (node.version > canVersion) {
				if (node.isChanged) {
					canColor = node.color;
					canVersion = node.version;					
				} else {
					canChange = false;
				}
			}
			
			node.isChanged = false;
		} else {
			if (node.isChanged) {
				canChange = true;
				canColor = node.color;
				canVersion = node.version;
			}
		}
		
		if (node.children.size() == 0) {
			sumResult += 1;
			return (1 << node.color);
		}
		
		int b = (1 << node.color);
		int c = 0;
		
		for (Node n : node.children) {
			b |= dfs(n, canColor, canVersion, canChange);
		}
		
		for (int i = 1; i < 6; i++) {
			if ((b & (1 << i)) > 0) {
				c++;
			}
		}
		
		sumResult += c * c;
		return b;
	}
}