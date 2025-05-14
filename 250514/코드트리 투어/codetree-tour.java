import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

class Item implements Comparable<Item> {
    int id;
    int revenue;
    int dest;
    int value;
    boolean isOut;

    @Override
    public int compareTo(Item o) {
        if (this.value == o.value) {
            return Integer.compare(this.id, o.id);
        }

        return Integer.compare(o.value, this.value);
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", revenue=" + revenue +
                ", dest=" + dest +
                ", value=" + value +
                ", isOut=" + isOut +
                '}';
    }
}

class Node {
    int id, dist;
    Node(int id, int dist) {
        this.id = id;
        this.dist = dist;
    }
}

public class Main {
    static int departure;
    static TreeMap<Integer, Integer>[] edges;
    static Map<Integer, Item> itemList = new HashMap<>();
    static PriorityQueue<Item> optimalItems = new PriorityQueue<>();
    static List<Item> idleItems = new ArrayList<>();
    static int[] minDist;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder sb = new StringBuilder();

        int N = Integer.parseInt(br.readLine());

        for (int i = 0; i < N; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());

            int comm = Integer.parseInt(st.nextToken());
            int id;

            switch (comm) {
                case 100:
                    int n = Integer.parseInt(st.nextToken());
                    int m = Integer.parseInt(st.nextToken());
                    int[][] edgesInfo = new int[m][3];

                    for (int j = 0; j < m; j++) {
                        edgesInfo[j][0] = Integer.parseInt(st.nextToken());
                        edgesInfo[j][1] = Integer.parseInt(st.nextToken());
                        edgesInfo[j][2] = Integer.parseInt(st.nextToken());
                    }

                    buildCodeTreeLand(n, m, edgesInfo);
                    break;
                case 200:
                    id = Integer.parseInt(st.nextToken());
                    int revenue = Integer.parseInt(st.nextToken());
                    int dest = Integer.parseInt(st.nextToken());

                    createItem(id, revenue, dest);
                    break;
                case 300:
                    id = Integer.parseInt(st.nextToken());
                    deleteItem(id);
                    break;
                case 400:
                    int result2 = sellOptimalItem();
                    sb.append(result2).append("\n");
                    break;
                case 500:
                    int newDeparture = Integer.parseInt(st.nextToken());
                    changeDeparture(newDeparture);
                    break;
            }
        }

        System.out.println(sb);
    }

    private static void changeDeparture(int newDeparture) {
        departure = newDeparture;
        dijkstra(newDeparture);
        PriorityQueue<Item> newOptimalItems = new PriorityQueue<>();
        List<Item> newIdleItems = new ArrayList<>();

        while (!optimalItems.isEmpty()) {
            Item item = optimalItems.poll();
            if (item.isOut) continue;

            item.value = calcValue(item.dest, item.revenue);
            if (item.value == Integer.MIN_VALUE) {
                newIdleItems.add(item);
            } else {
                newOptimalItems.add(item);
            }
        }

        for (Item idleItem : idleItems) {
            if (idleItem.isOut) continue;

            idleItem.value = calcValue(idleItem.dest, idleItem.revenue);
            if (idleItem.value == Integer.MIN_VALUE) {
                newIdleItems.add(idleItem);
            } else {
                newOptimalItems.add(idleItem);
            }
        }

        idleItems = newIdleItems;
        optimalItems = newOptimalItems;
    }

    private static int sellOptimalItem() {
        boolean canFind = false;
        Item item = null;

        while (!optimalItems.isEmpty()) {
            item = optimalItems.peek();

            if (item.isOut) {
                optimalItems.poll();
                continue;
            }

            if (item.value < 0) {
                break;
            }

            canFind = true;
            optimalItems.poll();
            break;
        }

        if (canFind) {
            item.isOut = true;
            return item.id;
        } else {

            return -1;
        }
    }

    private static void deleteItem(int id) {
        Item item = itemList.get(id);
        if (item != null) {
            item.isOut = true;
        }
    }

    private static void createItem(int id, int revenue, int dest) {
        Item item = new Item();
        item.id = id;
        item.revenue = revenue;
        item.dest = dest;
        item.value = calcValue(dest, revenue);

        itemList.put(id, item);

        if (item.value == Integer.MIN_VALUE) {
            idleItems.add(item);
        } else {
            optimalItems.add(item);
        }
    }

    private static int calcValue(int dest, int revenue) {
        if (minDist[dest] == Integer.MAX_VALUE) {
            return Integer.MIN_VALUE;
        }

        return revenue - minDist[dest];
    }

    private static void buildCodeTreeLand(int n, int m, int[][] edgesInfo) {
        edges = new TreeMap[n];
        minDist = new int[n];

        for (int i = 0; i < n; i++) {
            edges[i] = new TreeMap<>();
        }

        for (int i = 0; i < m; i++) {
            int vi = edgesInfo[i][0];
            int ui = edgesInfo[i][1];
            int wi = edgesInfo[i][2];

            if (vi == ui) continue;

            if (edges[vi].containsKey(ui)) {
                if (edges[vi].get(ui) > wi) {
                    edges[vi].put(ui, wi);
                    edges[ui].put(vi, wi);
                }
            } else {
                edges[vi].put(ui, wi);
                edges[ui].put(vi, wi);
            }
        }

        dijkstra(0);
    }

    private static void dijkstra(int departure) {
        Arrays.fill(minDist, Integer.MAX_VALUE);
        minDist[departure] = 0;

        PriorityQueue<Node> pq = new PriorityQueue<>((o1, o2) -> Integer.compare(o1.dist, o2.dist));
        pq.add(new Node(departure, 0));

        while(!pq.isEmpty()) {
            Node nxt = pq.poll();
            int id = nxt.id;
            int dist = nxt.dist;

            if (minDist[id] < dist) continue;

            TreeMap<Integer, Integer> edge = edges[id];

            for (Map.Entry<Integer, Integer> comp : edge.entrySet()) {
                int arrival = comp.getKey();
                int weight = comp.getValue();

                if (minDist[arrival] > minDist[id] + weight) {
                    minDist[arrival] = minDist[id] + weight;
                    pq.add(new Node(arrival, minDist[arrival]));
                }
            }
        }
    }
}
