import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;

class Box {
	int id, W;

	public Box(int id, int w) {
		this.id = id;
		W = w;
	}
}

class Conveyor {
	Map<Integer, Box> checkList = new HashMap<>();
	Queue<Box> conveyor = new LinkedList<>();
	
	public Conveyor() {}
	
	public void add(Box box) {
		checkList.put(box.id, box);
		conveyor.add(box);
	}

	public long offload(int w_max) {
		if (!conveyor.isEmpty()) {
			Box front = conveyor.poll();
			
			if (front.W <= w_max) {
				checkList.remove(front.id);
				return front.W;
			} else {
				conveyor.add(front);
			}
		}
		return 0;
	}

	public Box remove(int r_id) {
		Box box = checkList.get(r_id);
		conveyor.remove(box);
		checkList.remove(r_id);
		return box;
	}

	public void check(int f_id) {
		while (conveyor.peek().id != f_id) {
			conveyor.add(conveyor.poll());
		}
	}
}

class Solution {
	static int n, m;
	static Conveyor[] conveyors;
	
	public void init(Scanner sc) {
		n = sc.nextInt();
		m = sc.nextInt();
		
		int length = n / m;
		
		conveyors = new Conveyor[m];
		
		for (int i = 0; i < m; i++) {
			conveyors[i] = new Conveyor();
		}
		
		int[][] data = new int[n][2];
		
		for (int i = 0; i < n; i++) {
			data[i][0] = sc.nextInt();
		}
		
		for (int i = 0; i < n; i++) {
			data[i][1] = sc.nextInt();
		}
		
		for (int i = 0; i < n; i++) {
			Box newBox = new Box(data[i][0], data[i][1]);
			conveyors[i / length].add(newBox);
		}
	}
	
	public void offload(Scanner sc) {
		int w_max = sc.nextInt();
		
		long result = 0;
		
		for (int i = 0; i < m; i++) {
			if (conveyors[i] == null) continue;
			
			result += conveyors[i].offload(w_max);
		}
		
		System.out.println(result);
	}

	public void remove(Scanner sc) {
		int r_id = sc.nextInt();
		
		Box box = null;
		
		for (int i = 0; i < m; i++) {
			if (conveyors[i] != null && conveyors[i].checkList.containsKey(r_id)) {
				box = conveyors[i].remove(r_id);
			}
		}
		
		if (box != null) System.out.println(box.id);
		else System.out.println(-1);
	}

	public void check(Scanner sc) {
		int f_id = sc.nextInt();
		
		int conNum = -1;
		
		for (int i = 0; i < m; i++) {
			if (conveyors[i] != null && conveyors[i].checkList.containsKey(f_id)) {
				conNum = i + 1;
				conveyors[i].check(f_id);
			}
		}
		
		System.out.println(conNum);
	}

	public void breaked(Scanner sc) {
		int b_num = sc.nextInt() - 1;
		
		if (conveyors[b_num] == null) {
			System.out.println(-1);
			return;
		}
		
		for (int i = 1; i < m; i++) {
			if (conveyors[(b_num + i) % m] != null) {
				while (!conveyors[b_num].conveyor.isEmpty()) {
					conveyors[(b_num + i) % m].add(conveyors[b_num].conveyor.poll());
				}
			}
		}
		
		conveyors[b_num] = null;
		System.out.println(b_num + 1);
	}
}

public class Main {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int q = sc.nextInt();
		
		Solution solution = new Solution();

		for (int i = 0; i < q; i++) {
			switch (sc.nextInt()) {
			case 100:
				solution.init(sc);
				break;
			case 200:
				solution.offload(sc);
				break;
			case 300:
				solution.remove(sc);
				break;
			case 400:
				solution.check(sc);
				break;
			case 500:
				solution.breaked(sc);
				break;
			}
		}
	}
}