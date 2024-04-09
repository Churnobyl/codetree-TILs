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
	
	public Conveyor() {}
	
	public void add(Box box) {
		size++;
		box.next = null;
		
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

	public Box remove(int r_id) {
		if (isEmpty()) return null;
		
		Box box = find(r_id);
		
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
		Box iterator = head;
		
		while (iterator != null) {
			if (iterator.id == id) return iterator;
			iterator = iterator.next;
		}
		
		return null;
	}

	public void addAll(Conveyor from) {
		if (tail == null) {
			head = from.head;
			tail = from.tail;
			size += from.size;
			return;
		}
		
		tail.next = from.head;
		from.head.prev = tail;
		tail = from.tail;
		size += from.size;
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

	public void check(int f_id) {
		while (conveyor.peek().id != f_id) {
			conveyor.add(conveyor.poll());
		}
	}
	
	public boolean containsKey(int id) {
		return conveyor.find(id) != null;
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