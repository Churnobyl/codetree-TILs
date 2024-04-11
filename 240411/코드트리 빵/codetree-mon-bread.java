import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

class Person {
	int y, x, t;
	int[] dest = new int[2];

	public Person(int y, int x, int t) {
		this.y = y;
		this.x = x;
		this.t = t;
	}

	@Override
	public String toString() {
		return "Person [y=" + y + ", x=" + x + ", t=" + t + ", dest=" + Arrays.toString(dest) + "]";
	}

	
}

public class Main {
	static int n, m, t;
	static int[][] map;
	static Queue<Person> queue = new LinkedList<>();
	static int[] dy = {-1, 0, 0, 1};
	static int[] dx = {0, -1, 1, 0};
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		n = sc.nextInt();
		m = sc.nextInt();
		
		map = new int[n][n];

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				map[i][j] = sc.nextInt();
			}
		}
		
		for (int i = 1; i < m + 1; i++) {
			t++;
			move();
			enter(sc);
		}
		
		while (!queue.isEmpty()) {
			t++;
			move();
		}
		
		System.out.println(t);
	}

	private static void enter(Scanner sc) {
		int y = sc.nextInt() - 1;
		int x = sc.nextInt() - 1;
		
		int[][] dp = new int[n][n];
		
		for (int i = 0; i < n; i++) {
			Arrays.fill(dp[i], Integer.MAX_VALUE);
		}
		
		dp[y][x] = 0;
		
		int dist = Integer.MAX_VALUE;
		int distY = Integer.MAX_VALUE, distX = Integer.MAX_VALUE;
		
		Queue<int[]> findQueue = new LinkedList<>();
		findQueue.add(new int[] {y, x});
		
		while (!findQueue.isEmpty()) {
			int[] next = findQueue.poll();
			
			int ky = next[0];
			int kx = next[1];
			
			if (map[ky][kx] < 0) continue;
			
			if (map[ky][kx] == 1) {
				boolean isChange = false;
				
				if (dp[ky][kx] < dist) {
					isChange = true;
				} else if (dp[ky][kx] == dist) {
					if (ky < distY) {						
						isChange = true;
					} else if (ky == distY) {
						if (kx < distX) {
							isChange = true;							
						}
					}
				}
				
				if (isChange) {
					dist = dp[ky][kx];
					distY = ky;
					distX = kx;					
				}
				continue;
			}
			
			for (int i = 0; i < 4; i++) {
				int ny = ky + dy[i];
				int nx = kx + dx[i];
				
				if (0 <= ny && ny < n && 0 <= nx && nx < n) {
					if (map[ny][nx] >= 0) {
						if (dp[ky][kx] + 1 < dp[ny][nx]) {
							dp[ny][nx] = dp[ky][kx] + 1;
							findQueue.add(new int[] {ny, nx});							
						} else if (map[ny][nx] == 1 && dp[ky][kx] + 1 == dp[ny][nx]) {
							findQueue.add(new int[] {ny, nx});
						}
					}
				}
			}
		}
		
		Person newPerson = new Person(distY, distX, t);
		newPerson.dest[0] = y;
		newPerson.dest[1] = x;
		map[distY][distX] = -1 * t;
		queue.add(newPerson);
	}

	private static void move() {
		while (!queue.isEmpty() && queue.peek().t < t) {
			Person person = queue.poll();
			
			int dist = Integer.MAX_VALUE;
			int distY = Integer.MAX_VALUE;
			int distX = Integer.MAX_VALUE;
			
			for (int i = 0; i < 4; i++) {
				int ny = person.y + dy[i];
				int nx = person.x + dx[i];
				
				if (0 > ny || ny >= n || 0 > nx || nx >= n) continue;
				
				if (map[ny][nx] < 0) continue;
				
				int cache = Math.abs(person.dest[0] - ny) + Math.abs(person.dest[1] - nx);
				
				if (cache < dist) {
					dist = cache;
					distY = ny;
					distX = nx;
				}
			}
			
			if (dist != 0) {
				person.y = distY;
				person.x = distX;
				person.t++;

				queue.add(person);
			} else {
				map[distY][distX] = -t;
			}
		}
	}
}