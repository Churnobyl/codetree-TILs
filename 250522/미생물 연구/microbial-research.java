import java.io.*;
import java.util.*;

public class Main {
    static int[] dy = {1, -1, 0, 0};
    static int[] dx = {0, 0, 1, -1};

    static class MicroOrganism implements Comparable<MicroOrganism> {
        int id, y, x, num, width, height;
        boolean[][] deletedMap;

        public MicroOrganism(int id, int y, int x, int num, int width, int height, boolean[][] deletedMap) {
            this.id = id;
            this.y = y;
            this.x = x;
            this.num = num;
            this.width = width;
            this.height = height;
            this.deletedMap = deletedMap;
        }

        public boolean isSplit() {
            int cnt = 0;
            boolean[][] visited = new boolean[height][width];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (!deletedMap[i][j] && !visited[i][j]) {
                        bfs(i, j, visited);
                        cnt++;
                        if (cnt == 2) return true;
                    }
                }
            }
            return false;
        }

        public void reduce() {
            List<Integer> rowsToKeep = new ArrayList<>();
            List<Integer> colsToKeep = new ArrayList<>();

            for (int i = 0; i < height; i++) {
                boolean allDeleted = true;
                for (int j = 0; j < width; j++) {
                    if (!deletedMap[i][j]) {
                        allDeleted = false;
                        break;
                    }
                }
                if (!allDeleted) rowsToKeep.add(i);
            }

            for (int j = 0; j < width; j++) {
                boolean allDeleted = true;
                for (int i = 0; i < height; i++) {
                    if (!deletedMap[i][j]) {
                        allDeleted = false;
                        break;
                    }
                }
                if (!allDeleted) colsToKeep.add(j);
            }

            boolean[][] newDeletedMap = new boolean[rowsToKeep.size()][colsToKeep.size()];
            int newNum = 0;

            for (int i = 0; i < rowsToKeep.size(); i++) {
                for (int j = 0; j < colsToKeep.size(); j++) {
                    int oldI = rowsToKeep.get(i);
                    int oldJ = colsToKeep.get(j);
                    newDeletedMap[i][j] = deletedMap[oldI][oldJ];
                    if (!newDeletedMap[i][j]) newNum++;
                }
            }

            this.deletedMap = newDeletedMap;
            this.height = rowsToKeep.size();
            this.width = colsToKeep.size();
            this.num = newNum;
        }

        private void bfs(int y, int x, boolean[][] visited) {
            Queue<int[]> queue = new ArrayDeque<>();
            queue.add(new int[]{y, x});
            visited[y][x] = true;

            while (!queue.isEmpty()) {
                int[] cur = queue.poll();
                for (int d = 0; d < 4; d++) {
                    int ny = cur[0] + dy[d];
                    int nx = cur[1] + dx[d];
                    if (ny < 0 || ny >= height || nx < 0 || nx >= width) continue;
                    if (!deletedMap[ny][nx] && !visited[ny][nx]) {
                        visited[ny][nx] = true;
                        queue.add(new int[]{ny, nx});
                    }
                }
            }
        }

        @Override
        public int compareTo(MicroOrganism o) {
            if (this.num == o.num) return Integer.compare(this.id, o.id);
            return Integer.compare(o.num, this.num);
        }
    }

    static int N, Q;
    static int[][] map;
    static Map<Integer, MicroOrganism> microOrganismList = new HashMap<>();
    static TreeSet<MicroOrganism> microOrganisms = new TreeSet<>();

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken());
        Q = Integer.parseInt(st.nextToken());

        map = new int[N][N];

        for (int i = 1; i <= Q; i++) {
            st = new StringTokenizer(br.readLine());
            int r1 = Integer.parseInt(st.nextToken());
            int c1 = Integer.parseInt(st.nextToken());
            int r2 = Integer.parseInt(st.nextToken());
            int c2 = Integer.parseInt(st.nextToken());

            addMicroOrganism(i, r1, c1, r2, c2);
            moveNewPetriDish();
            System.out.println(calculateInteractionSum());
        }
    }

    private static void addMicroOrganism(int id, int r1, int c1, int r2, int c2) {
        int height = r2 - r1;
        int width = c2 - c1;
        MicroOrganism mo = new MicroOrganism(id, r1, c1, height * width, width, height, new boolean[height][width]);
        microOrganisms.add(mo);
        microOrganismList.put(id, mo);
        placeNewOrganism(mo);
    }

    private static void placeNewOrganism(MicroOrganism mo) {
        Set<MicroOrganism> affected = new HashSet<>();

        for (int i = mo.y; i < mo.y + mo.height; i++) {
            for (int j = mo.x; j < mo.x + mo.width; j++) {
                if (map[i][j] != 0 && map[i][j] != mo.id) {
                    int otherId = map[i][j];
                    MicroOrganism other = microOrganismList.get(otherId);
                    int oy = i - other.y;
                    int ox = j - other.x;
                    if (oy >= 0 && oy < other.height && ox >= 0 && ox < other.width) {
                        if (!other.deletedMap[oy][ox]) {
                            other.deletedMap[oy][ox] = true;
                            other.num--;
                            affected.add(other);
                        }
                    }
                }
                map[i][j] = mo.id;
            }
        }

        for (MicroOrganism m : affected) {
            microOrganisms.remove(m);
            m.reduce();
            if (m.num > 0 && !m.isSplit()) {
                microOrganisms.add(m);
            } else {
                microOrganismList.remove(m.id);
                removeFromMap(m);
            }
        }

        microOrganisms.add(mo);
        microOrganismList.put(mo.id, mo);
    }

    private static void removeFromMap(MicroOrganism mo) {
        for (int i = 0; i < mo.height; i++) {
            for (int j = 0; j < mo.width; j++) {
                if (!mo.deletedMap[i][j]) {
                    int y = mo.y + i;
                    int x = mo.x + j;
                    if (map[y][x] == mo.id) map[y][x] = 0;
                }
            }
        }
    }

    private static void moveNewPetriDish() {
        int[][] newMap = new int[N][N];
        TreeSet<MicroOrganism> newSet = new TreeSet<>();
        Map<Integer, MicroOrganism> newList = new HashMap<>();

        for (MicroOrganism mo : microOrganisms) {
            mo.reduce();
            if (mo.num == 0 || mo.height == 0 || mo.width == 0) continue;

            boolean placed = false;
            for (int sy = 0; sy <= N - mo.height && !placed; sy++) {
                for (int sx = 0; sx <= N - mo.width && !placed; sx++) {
                    if (canPlace(newMap, mo, sy, sx)) {
                        placeOnMap(newMap, mo, sy, sx);
                        mo.y = sy;
                        mo.x = sx;
                        newSet.add(mo);
                        newList.put(mo.id, mo);
                        placed = true;
                    }
                }
            }
        }

        map = newMap;
        microOrganisms = newSet;
        microOrganismList = newList;
    }

    private static boolean canPlace(int[][] newMap, MicroOrganism mo, int sy, int sx) {
        for (int i = 0; i < mo.height; i++) {
            for (int j = 0; j < mo.width; j++) {
                if (mo.deletedMap[i][j]) continue;
                if (newMap[sy + i][sx + j] != 0) return false;
            }
        }
        return true;
    }

    private static void placeOnMap(int[][] newMap, MicroOrganism mo, int sy, int sx) {
        for (int i = 0; i < mo.height; i++) {
            for (int j = 0; j < mo.width; j++) {
                if (!mo.deletedMap[i][j]) newMap[sy + i][sx + j] = mo.id;
            }
        }
    }

    private static long calculateInteractionSum() {
        long total = 0;
        Set<String> visited = new HashSet<>();

        for (MicroOrganism a : microOrganisms) {
            for (int i = 0; i < a.height; i++) {
                for (int j = 0; j < a.width; j++) {
                    if (a.deletedMap[i][j]) continue;
                    int y = a.y + i;
                    int x = a.x + j;

                    for (int d = 0; d < 4; d++) {
                        int ny = y + dy[d];
                        int nx = x + dx[d];
                        if (ny < 0 || nx < 0 || ny >= N || nx >= N) continue;
                        int otherId = map[ny][nx];
                        if (otherId != 0 && otherId != a.id) {
                            MicroOrganism b = microOrganismList.get(otherId);
                            String key = Math.min(a.id, b.id) + "-" + Math.max(a.id, b.id);
                            if (!visited.contains(key)) {
                                visited.add(key);
                                total += (long) a.num * b.num;
                            }
                        }
                    }
                }
            }
        }

        return total;
    }
}
