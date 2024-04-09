import java.util.AbstractQueue;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;

class Box {
	int id, W;
	Box prev, next;

	public Box(int id, int w) {
		this.id = id;
		W = w;
	}
}

class Conveyor {
	Map<Integer, Box> checkList = new HashMap<>();
	Box head, tail;
	int size;
	
	public Conveyor() {}
	
	public void add(Box box) {
		checkList.put(box.id, box);
		
		size++;
		
		if (head == null) {
			head = box;
			tail = box;
			return;
		}
		
		tail.next = box;
		box.prev = tail;
		tail = box;
	}
	
	public boolean isEmpty() {
		return this.size == 0;
	}
	
	public Box poll() {
		if (isEmpty()) return null;
		
		Box out = head;
		size--;
		checkList.remove(out.id);
		
		if (head == tail) {
			head = null;
			tail = null;
			return out;
		}
		
		head = head.next;
		head.prev = null;
		
		return out;
	}

	public Box remove(int r_id) {
		if (isEmpty()) return null;
		
		Box box = checkList.get(r_id);
		checkList.remove(r_id);
		
		if (size < 2) {
			head = null;
			tail = null;
			size--;
			return box;
		}
		
		box.prev.next = box.next;
		box.next.prev = box.prev;
		size--;
		
		return box;
	}
	
	public Box peek() {
		return head;
	}
}

class ConveyorManager {
	
	Conveyor conveyor = new Conveyor();
	
	public ConveyorManager() {}
	
	public void add(Box box) {
		conveyor.add(box);
	}

	public long offload(int w_max) {
		if (!conveyor.isEmpty()) {
			Box front = conveyor.poll();
			
			if (front.W <= w_max) {
				return front.W;
			} else {
				conveyor.add(front);
			}
		}
		return 0;
	}

	public Box remove(int r_id) {
		return conveyor.remove(r_id);
	}
	
	public boolean containsKey(int id) {
		return conveyor.checkList.containsKey(id);
	}

	public void check(int f_id) {
		while (conveyor.peek().id != f_id) {
			conveyor.add(conveyor.poll());
		}
	}
}

class Solution {
	static int n, m;
	static ConveyorManager[] conveyorManagers;
	
	public void init(Scanner sc) {
		n = sc.nextInt();
		m = sc.nextInt();
		
		int length = n / m;
		
		conveyorManagers = new ConveyorManager[m];
		
		for (int i = 0; i < m; i++) {
			conveyorManagers[i] = new ConveyorManager();
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
			conveyorManagers[i / length].add(newBox);
		}
	}
	
	public void offload(Scanner sc) {
		int w_max = sc.nextInt();
		
		long result = 0;
		
		for (int i = 0; i < m; i++) {
			if (conveyorManagers[i] == null) continue;
			
			result += conveyorManagers[i].offload(w_max);
		}
		
		System.out.println(result);
	}

	public void remove(Scanner sc) {
		int r_id = sc.nextInt();
		
		Box box = null;
		
		for (int i = 0; i < m; i++) {
			if (conveyorManagers[i] != null && conveyorManagers[i].containsKey(r_id)) {
				box = conveyorManagers[i].remove(r_id);
			}
		}
		
		if (box != null) System.out.println(box.id);
		else System.out.println(-1);
	}

	public void check(Scanner sc) {
		int f_id = sc.nextInt();
		
		int conNum = -1;
		
		for (int i = 0; i < m; i++) {
			if (conveyorManagers[i] != null && conveyorManagers[i].containsKey(f_id)) {
				conNum = i + 1;
				conveyorManagers[i].check(f_id);
			}
		}
		
		System.out.println(conNum);
	}

	public void breaked(Scanner sc) {
		int b_num = sc.nextInt() - 1;
		
		if (conveyorManagers[b_num] == null) {
			System.out.println(-1);
			return;
		}
		
		for (int i = 1; i < m; i++) {
			if (conveyorManagers[(b_num + i) % m] != null) {
				while (!conveyorManagers[b_num].conveyor.isEmpty()) {
					conveyorManagers[(b_num + i) % m].add(conveyorManagers[b_num].conveyor.poll());
				}
			}
		}
		
		conveyorManagers[b_num] = null;
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