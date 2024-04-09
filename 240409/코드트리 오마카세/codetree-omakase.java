import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;

class Sushi {
	int t, x;
	String name;

	public Sushi(int t, int x, String name) {
		this.t = t;
		this.x = x;
		this.name = name;
	}

	@Override
	public String toString() {
		return "Sushi [t=" + t + ", x=" + x + ", name=" + name + "]";
	}
	
	
}

class Solution {
	static int L, h, m;
	static Map<String, int[]> customers = new HashMap<>();
	static Queue<Sushi> sushies = new PriorityQueue<>(new Comparator<Sushi>() {

		@Override
		public int compare(Sushi o1, Sushi o2) {
			if (o1.t == o2.t)
				return Integer.compare(o1.x, o2.x);
			return Integer.compare(o1.t, o2.t);
		}
	});

	public Solution(int L) {
		this.L = L;
	}

	public void inputSushi(Scanner sc) {
		int t = sc.nextInt();
		int x = sc.nextInt();
		String name = sc.next();

		sushies.add(new Sushi(t, x, name));
		m++;
	}

	public void enter(Scanner sc) {
		int t = sc.nextInt();
		int x = sc.nextInt();
		String name = sc.next();
		int n = sc.nextInt();
		customers.put(name, new int[] { t, x, n });
		h++;
	}

	public void takePicture(Scanner sc) {
		int t = sc.nextInt();
		
		while (!sushies.isEmpty() && sushies.peek().t < t) {
			Sushi next = sushies.poll();
			
			if (!customers.containsKey(next.name)) {
				next.x = (next.x + (t - next.t)) % L;
				next.t = t;
				sushies.add(next);
				continue;
			}
			
			int[] customer = customers.get(next.name);
			
			if (customer[0] > next.t) { // 손님이 늦게 앉은 경우
				int cacheX = (next.x + (customer[0] - next.t)) % L; // 손님 시점 스시 위치
				
				if (cacheX <= customer[1]) { // 스시 위치가 손님 왼쪽
					if (cacheX + (t - customer[0]) >= customer[1]) {
						customer[2]--;
						m--;
						
						if (customer[2] == 0) {
							customers.remove(next.name);
							h--;
						}
						
						continue;
					}
				} else { // 스시 위치가 손님 오른쪽
					if (L - (cacheX - customer[1]) <= (t - customer[0])) {
						customer[2]--;
						m--;
						
						if (customer[2] == 0) {
							customers.remove(next.name);
							h--;
						}
						
						continue;
					}
				}
				
				next.t = t;
				next.x = (cacheX + (t - customer[0])) % L;
				sushies.add(next);
				
			} else { // 손님이 일찍 앉은 경우
				if (next.x <= customer[1]) { // 스시 위치가 손님 왼쪽
					if (next.x + (t - next.t) >= customer[1]) {
						customer[2]--;
						m--;
						
						if (customer[2] == 0) {
							customers.remove(next.name);
							h--;
						}
						
						continue;
					}
				} else { // 스시 위치가 손님 오른쪽
					if (L - (next.x - customer[1]) <= (t - next.t)) {
						customer[2]--;
						m--;
						
						if (customer[2] == 0) {
							customers.remove(next.name);
							h--;
						}
						
						continue;
					}
				}
				
				next.t = t;
				next.x = (next.x + (t - customer[0])) % L;
				sushies.add(next);
			}
		}
		
		for (String name : customers.keySet()) {
			customers.get(name)[0] = t;
		}
		
		System.out.println(h + " " + m);
	}

}

public class Main {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int L = sc.nextInt();
		int Q = sc.nextInt();

		Solution solution = new Solution(L);

		for (int i = 0; i < Q; i++) {
			switch (sc.nextInt()) {
			case 100:
				solution.inputSushi(sc);
				break;
			case 200:
				solution.enter(sc);
				break;
			case 300:
				solution.takePicture(sc);
				break;
			}
		}
	}
}