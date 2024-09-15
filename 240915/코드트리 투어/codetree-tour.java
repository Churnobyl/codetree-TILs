import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;

class Item {
	int id, revenue, dest, earn;
	boolean isOut;

	public Item(int id, int revenue, int dest, boolean isOut) {
		super();
		this.id = id;
		this.revenue = revenue;
		this.dest = dest;
		this.isOut = isOut;
	}

	@Override
	public String toString() {
		return "Item [id=" + id + ", revenue=" + revenue + ", dest=" + dest + ", earn=" + earn + ", isOut=" + isOut
				+ "]";
	}

}

public class Main {

	static int Q, n, m;
	static int[][] map;
	static int[] dp;
	static int startCity = 0;
	static Map<Integer, Item> items = new HashMap<>();
	static int maxId;
	static PriorityQueue<Item> itemQueue = new PriorityQueue<>(new Comparator<Item>() {

		@Override
		public int compare(Item o1, Item o2) {
			if (o1.earn == o2.earn) {
				return Integer.compare(o1.id, o2.id);
			}
			return Integer.compare(o2.earn, o1.earn);
		}
	});

	public static void main(String[] args) throws IOException {
		Scanner sc = new Scanner(System.in);

		Q = sc.nextInt();

		int comm = -1;

		for (int i = 0; i < Q; i++) {
			comm = sc.nextInt();

			switch (comm) {
			case 100:
				n = sc.nextInt();
				m = sc.nextInt();

				map = new int[n][n];

				for (int j = 0; j < n; j++) {
					Arrays.fill(map[j], Integer.MAX_VALUE);
				}

				for (int j = 0; j < m; j++) {
					int s = sc.nextInt();
					int e = sc.nextInt();
					int w = sc.nextInt();

					if (map[s][e] > w)
						map[s][e] = w;

					if (map[e][s] > w)
						map[e][s] = w;

				}

				dijkstra();

				break;
			case 200:
				addItem(sc.nextInt(), sc.nextInt(), sc.nextInt(), false);
				break;
			case 300:
				removeItem(sc.nextInt());
				break;
			case 400:
				System.out.println(sellGreatItem());
				break;
			case 500:
				changeStartPoint(sc.nextInt());
				break;
			}
		}

		sc.close();
	}

	private static void changeStartPoint(int s) {
		startCity = s;

		dijkstra();

		List<Item> packages = new ArrayList<>();

		while (!itemQueue.isEmpty()) {
			packages.add(itemQueue.poll());
		}
		
		for (Item item : packages) {
			addItem(item.id, item.revenue, item.dest, item.isOut);
		}
	}

	private static int sellGreatItem() {
		while (!itemQueue.isEmpty()) {
			Item candi = itemQueue.peek();

			if (candi.earn < 0) {
				break;
			}
			
			itemQueue.poll();
			
			if (!candi.isOut) {
				return candi.id;
			}
		}

		return -1;
	}

	private static void removeItem(int id) {
		if (items.containsKey(id))
			items.get(id).isOut = true;
	}

	private static void addItem(int id, int revenue, int dest, boolean isOut) {
		if (maxId < id) {
			maxId = id;
		}
		Item item = new Item(id, revenue, dest, isOut);
		item.earn = revenue - dp[dest];
		items.put(id, item);
		itemQueue.add(item);
	}

	private static void dijkstra() {
		boolean[] visited = new boolean[n];

		dp = new int[n];
		Arrays.fill(dp, Integer.MAX_VALUE);
		dp[startCity] = 0;

		for (int i = 0; i < n - 1; i++) {
			int v = 0, minDist = Integer.MAX_VALUE;
			for (int j = 0; j < n; j++) {
				if (!visited[j] && minDist > dp[j]) {
					v = j;
					minDist = dp[j];
				}
			}
			visited[v] = true;
			for (int j = 0; j < n; j++) {
				if (!visited[j] && dp[v] != Integer.MAX_VALUE && map[v][j] != Integer.MAX_VALUE
						&& dp[j] > dp[v] + map[v][j]) {
					dp[j] = dp[v] + map[v][j];
				}
			}
		}
	}
}