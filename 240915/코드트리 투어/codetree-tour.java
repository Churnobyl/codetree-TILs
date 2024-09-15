import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;

class Item {
	int id, revenue, dest, earn;
	boolean isOut;

	public Item(int id, int revenue, int dest) {
		super();
		this.id = id;
		this.revenue = revenue;
		this.dest = dest;
	}

	@Override
	public String toString() {
		return "Item [id=" + id + ", revenue=" + revenue + ", dest=" + dest + ", earn=" + earn + ", isOut=" + isOut
				+ "]";
	}

}

public class Main {

	static int Q, n, m;
	static Map<Integer, Integer>[] map;
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

				map = new HashMap[n];

				for (int j = 0; j < n; j++) {
					map[j] = new HashMap<>();
				}

				for (int j = 0; j < m; j++) {
					int s = sc.nextInt();
					int e = sc.nextInt();
					int w = sc.nextInt();

					if (map[s].containsKey(e)) {
						if (map[s].get(e) > w)
							map[s].put(e, w);
					} else {
						map[s].put(e, w);
					}

					if (map[e].containsKey(s)) {
						if (map[e].get(s) > w)
							map[e].put(s, w);
					} else {
						map[e].put(s, w);
					}

				}

				dijkstra();

				break;
			case 200:
				addItem(sc.nextInt(), sc.nextInt(), sc.nextInt());
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

	}

	private static void changeStartPoint(int s) {
		startCity = s;

		dijkstra();
		
		itemQueue.clear();

		for (int i = 0; i <= maxId; i++) {
			if (items.containsKey(i)) {
				Item nextItem = items.get(i);

				if (!nextItem.isOut) {
					nextItem.earn = nextItem.revenue - dp[nextItem.dest];
					itemQueue.add(nextItem);
				}
			}

		}
	}

	private static int sellGreatItem() {
		int result = -1;

		LinkedList<Item> dim = new LinkedList<>();

		while (!itemQueue.isEmpty()) {
			Item candi = itemQueue.poll();

			if (candi.isOut) {
				continue;
			} else if (candi.earn < 0) {
				dim.add(candi);
				continue;
			} else {
				result = candi.id;
				candi.isOut = true;
				break;
			}
		}

		while (!dim.isEmpty()) {
			itemQueue.add(dim.pollFirst());
		}

		return result;
	}

	private static void removeItem(int id) {
		if (items.containsKey(id))
			items.get(id).isOut = true;
	}

	private static void addItem(int id, int revenue, int dest) {
		if (maxId < id) {
			maxId = id;
		}
		Item item = new Item(id, revenue, dest);
		item.earn = revenue - dp[dest];
		items.put(id, item);
		itemQueue.add(item);
	}

	private static void dijkstra() {
		dp = new int[n];

		Arrays.fill(dp, Integer.MAX_VALUE);

		dp[startCity] = 0;

		Queue<Integer> queue = new LinkedList<>();

		queue.add(startCity);

		while (!queue.isEmpty()) {
			int nxt = queue.poll();

			for (int i = 0; i < n; i++) {
				if (map[nxt].containsKey(i)) {
					int weight = map[nxt].get(i);

					if (dp[nxt] + weight < dp[i]) {
						dp[i] = dp[nxt] + weight;
						queue.add(i);
					}
				}
			}
		}
	}
}