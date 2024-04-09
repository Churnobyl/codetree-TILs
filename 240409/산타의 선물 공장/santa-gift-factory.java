import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
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
	Box head, tail;
	int size;
	Map<Integer, Box> checkList = new HashMap<>();
	
	public Conveyor() {}
	
	public void add(Box box) {
		size++;
		box.next = null;
		checkList.put(box.id, box);
		
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
		checkList.remove(out.id);
		
		if (size == 1) {
			head = null;
			tail = null;
			size--;
			return out;
		}
		
		size--;
		head = head.next;
		head.prev = null;
		
		return out;
	}

	public Box remove(Box box) {
		if (isEmpty()) return null;
		
		checkList.remove(box.id);
		
		if (size < 2) {
			head = null;
			tail = null;
			size--;
			return box;
		}
		
		if (box == tail) {
			tail = box.prev;
			tail.next = null;
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
	
	public Box find(int id) {
		return checkList.getOrDefault(id, null);
	}

	public void addAll(Conveyor from) {
		if (from.head == null) return;
		
		if (tail == null) {
			checkList.putAll(from.checkList);
			head = from.head;
			tail = from.tail;
			size += from.size;
			return;
		}
		checkList.putAll(from.checkList);
		tail.next = from.head;
		from.head.prev = tail;
		tail = from.tail;
		size += from.size;
	}

	public void splitAndAttach(Box box) {
		if (box.prev == null) return;
		
		head.prev = tail;
		tail.next = head;
		tail = box.prev;
		head = box;
		head.prev = null;
		tail.next = null;
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

	public Box remove(Box box) {
		return conveyor.remove(box);
	}

	public void check(Box box) {
		conveyor.splitAndAttach(box);
	}
	
	public void addAll(Conveyor from) {
		conveyor.addAll(from);
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
			if (conveyorManagers[i] != null) {
				box = conveyorManagers[i].conveyor.find(r_id);
				if (box == null) continue;
				
				box = conveyorManagers[i].remove(box);
				break;
			}
		}
				
		if (box != null) System.out.println(box.id);
		else System.out.println(-1);
	}

	public void check(Scanner sc) {
		int f_id = sc.nextInt();
		
		int conNum = -1;
		
		Box box = null;
		
		for (int i = 0; i < m; i++) {
			if (conveyorManagers[i] != null) {
				box = conveyorManagers[i].conveyor.find(f_id);
				
				if (box == null) continue;
				
				conNum = i + 1;
				
				conveyorManagers[i].check(box);
				break;
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
				conveyorManagers[(b_num + i) % m].addAll(conveyorManagers[b_num].conveyor);
				break;
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