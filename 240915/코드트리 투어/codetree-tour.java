import java.util.*;

public class Main {
    final static int INF = 0x7fffffff; // INT 최대값을 정의합니다
    final static int MAX_N = 2000; // 코드트리 랜드의 최대 도시 개수입니다
    final static int MAX_ID = 30005; // 여행상품 ID의 최대값입니다

    static int N, M; // 도시의 개수 N과 간선의 개수 M 입니다
    static int[][] A = new int[MAX_N][MAX_N]; // 코드트리 랜드의 간선을 인접 행렬로 저장합니다
    static int[] D = new int[MAX_N]; // Dijkstra 알고리즘을 통해 시작도시 S부터 각 도시까지의 최단경로를 저장합니다
    static boolean[] isMade = new boolean[MAX_ID]; // 여행상품이 만들어졌는지 저장합니다
    static boolean[] isCancel = new boolean[MAX_ID]; // 여행상품이 취소되었는지 저장합니다
    static int S; // 여행 상품의 출발지 입니다

    // 여행 상품을 정의합니다
    static class Package implements Comparable<Package> {
        int id; // 고유 식별자 ID
        int revenue; // 매출
        int dest; // 도착도시
        int profit; // 여행사가 벌어들이는 수익

        public Package(int id, int revenue, int dest, int profit) {
            this.id = id;
            this.revenue = revenue;
            this.dest = dest;
            this.profit = profit;
        }

        // 우선순위 큐 비교를 위한 compareTo 메서드를 오버라이드합니다
        @Override
        public int compareTo(Package other) {
            if (this.profit == other.profit) {
                return Integer.compare(this.id, other.id); // profit이 같으면 id가 작은 순으로
            }
            return Integer.compare(other.profit, this.profit); // profit이 클수록 우선 순위 높게
        }
    }

    static PriorityQueue<Package> pq = new PriorityQueue<>(); // 최적의 여행 상품을 찾기 위한 우선순위 큐를 사용합니다

    static void dijkstra() {
        boolean[] visit = new boolean[N];
        Arrays.fill(D, INF);
        D[S] = 0;

        for (int i = 0; i < N - 1; i++) {
            int v = -1, minDist = INF;
            for (int j = 0; j < N; j++) {
                if (!visit[j] && D[j] < minDist) {
                    v = j;
                    minDist = D[j];
                }
            }
            if (v == -1) break; // 더 이상 방문할 도시가 없으면 종료
            visit[v] = true;
            for (int j = 0; j < N; j++) {
                if (!visit[j] && A[v][j] != INF && D[j] > D[v] + A[v][j]) {
                    D[j] = D[v] + A[v][j];
                }
            }
        }
    }

    static void buildLand(Scanner sc) {
        N = sc.nextInt();
        M = sc.nextInt();
        for (int i = 0; i < N; i++) {
            Arrays.fill(A[i], INF);
            A[i][i] = 0; // 자기 자신으로 가는 거리는 0
        }
        for (int i = 0; i < M; i++) {
            int u = sc.nextInt();
            int v = sc.nextInt();
            int w = sc.nextInt();
            A[u][v] = Math.min(A[u][v], w);
            A[v][u] = Math.min(A[v][u], w);
        }
    }

    static void addPackage(int id, int revenue, int dest) {
        isMade[id] = true;
        int profit = revenue - D[dest];
        pq.offer(new Package(id, revenue, dest, profit));
    }

    static void cancelPackage(int id) {
        if (isMade[id]) isCancel[id] = true;
    }

    static int sellPackage() {
        while (!pq.isEmpty()) {
            Package p = pq.peek();
            if (p.profit < 0) break;
            pq.poll();
            if (!isCancel[p.id]) {
                return p.id;
            }
        }
        return -1;
    }

    static void changeStart(Scanner sc) {
        S = sc.nextInt();
        dijkstra();
        List<Package> packages = new ArrayList<>();
        while (!pq.isEmpty()) {
            packages.add(pq.poll());
        }
        for (Package p : packages) {
            addPackage(p.id, p.revenue, p.dest);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int Q = sc.nextInt();
        while (Q-- > 0) {
            int T = sc.nextInt();
            switch (T) {
                case 100:
                    buildLand(sc);
                    dijkstra();
                    break;
                case 200:
                    int id = sc.nextInt();
                    int revenue = sc.nextInt();
                    int dest = sc.nextInt();
                    addPackage(id, revenue, dest);
                    break;
                case 300:
                    int cancelId = sc.nextInt();
                    cancelPackage(cancelId);
                    break;
                case 400:
                    System.out.println(sellPackage());
                    break;
                case 500:
                    changeStart(sc);
                    break;
            }
        }
        sc.close();
    }
}