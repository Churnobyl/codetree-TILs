import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
    static int N;
    static TreeMap<Integer, Integer> map = new TreeMap<>();

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder sb = new StringBuilder();

        int Q = Integer.parseInt(br.readLine());

        for (int i = 0; i < Q; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());

            int comm = Integer.parseInt(st.nextToken());

            switch (comm) {
                case 100:
                    N = Integer.parseInt(st.nextToken());

                    int[] positions = new int[N];

                    for (int j = 0; j < N; j++) {
                        positions[j] = Integer.parseInt(st.nextToken());
                    }

                    buildVillage(N, positions);
                    break;
                case 200:
                    int p = Integer.parseInt(st.nextToken());

                    buildHouse(p);
                    break;
                case 300:
                    int q = Integer.parseInt(st.nextToken());
                    breakHouse(q);
                    break;
                case 400:
                    int r = Integer.parseInt(st.nextToken());
                    sb.append(scout(r)).append("\n");
                    break;
            }
        }

        System.out.println(sb);
    }

    private static int scout(int ants) {
        int l = 0;
        int r = 1_000_000_000;

        while (l < r) {
            int mid = (l + r) / 2;
            int required = placeAnts(mid);

            if (required > ants) {
                l = mid + 1;
            } else {
                r = mid;
            }
        }

        return l;
    }

    private static int placeAnts(int dist) {
        int cnt = 1;

        int prevIndex = 0;
        int prevX = 0;

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            int num = entry.getKey();
            int x = entry.getValue();

            if (prevIndex == 0) {
                prevIndex = num;
                prevX = x;
                continue;
            }

            if (x - prevX > dist) {
                prevIndex = num;
                prevX = x;
                cnt++;
            }
        }

        return cnt;
    }

    private static void breakHouse(int q) {
        map.remove(q);
    }

    private static void buildHouse(int p) {
        map.put(N++, p);
    }

    private static void buildVillage(int n, int[] positions) {
        for (int i = 1; i <= n; i++) {
            map.put(i, positions[i - 1]);
        }

        N++;
    }
}
