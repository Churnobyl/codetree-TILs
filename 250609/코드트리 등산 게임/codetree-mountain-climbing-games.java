import java.io.*;
import java.util.*;

public class Main {
    static final int MAX_H = 1000000;
    static final int TREE_SIZE = 1 << 21; // 2^21 = 2,097,152 > 1,000,000
    static int Q, N, mcnt, sp;
    static int[] mountain = new int[505000];
    static Node[] tree = new Node[TREE_SIZE];
    static Node[] stack = new Node[505000];
    static long[] answer = new long[505000];
    static int[] heights = new int[505000];

    static class Node {
        long value;
        int height;

        Node(long v, int h) {
            value = v;
            height = h;
        }

        Node() {
            value = 0;
            height = 0;
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Q = Integer.parseInt(br.readLine());
        StringBuilder sb = new StringBuilder();
        for (int q = 0; q < Q; q++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            int cmd = Integer.parseInt(st.nextToken());
            if (cmd == 100) {
                N = Integer.parseInt(st.nextToken());
                int[] arr = new int[N + 1];
                for (int i = 1; i <= N; i++) arr[i] = Integer.parseInt(st.nextToken());
                input(N, arr);
            } else if (cmd == 200) {
                int h = Integer.parseInt(st.nextToken());
                moveMountain(h);
            } else if (cmd == 300) {
                earthquake();
            } else if (cmd == 400) {
                int m_index = Integer.parseInt(st.nextToken());
                sb.append(simulate(m_index)).append('\n');
            }
        }
        System.out.print(sb);
    }

    private static void update(int node, int l, int r, int idx, Node val) {
        if (idx < l || idx > r) return;
        if (l == r) {
            tree[node] = val;
            return;
        }
        int m = (l + r) / 2;
        update(node * 2, l, m, idx, val);
        update(node * 2 + 1, m + 1, r, idx, val);
        tree[node] = maxNode(tree[node * 2], tree[node * 2 + 1]);
    }

    private static Node query(int node, int l, int r, int ql, int qr) {
        if (qr < l || r < ql) return new Node(0, 0);
        if (ql <= l && r <= qr) return tree[node];
        int m = (l + r) / 2;
        Node left = query(node * 2, l, m, ql, qr);
        Node right = query(node * 2 + 1, m + 1, r, ql, qr);
        return maxNode(left, right);
    }

    private static Node maxNode(Node a, Node b) {
        if (a.value > b.value) return a;
        if (a.value < b.value) return b;
        return a.height > b.height ? a : b;
    }

    private static void input(int n, int[] arr) {
        N = n;
        mcnt = N + 1;
        sp = 0;
        for (int i = 1; i <= N; i++) {
            mountain[i] = arr[i];
        }
        Arrays.fill(tree, new Node(0, 0));
        for (int i = 1; i <= N; i++) {
            Node current = query(1, 1, MAX_H, mountain[i], mountain[i]);
            stack[sp++] = new Node(current.value, mountain[i]);
            Node max = query(1, 1, MAX_H, 1, mountain[i] - 1);
            update(1, 1, MAX_H, mountain[i], new Node(max.value + 1, mountain[i]));
            answer[i] = max.value + 1;
        }
    }

    private static void moveMountain(int h) {
        mountain[mcnt++] = h;
        Node current = query(1, 1, MAX_H, h, h);
        stack[sp++] = new Node(current.value, h);
        Node max = query(1, 1, MAX_H, 1, h - 1);
        update(1, 1, MAX_H, h, new Node(max.value + 1, h));
        answer[mcnt - 1] = max.value + 1;
    }

    private static void earthquake() {
        int h = mountain[--mcnt];
        Node before = stack[--sp];
        update(1, 1, MAX_H, h, new Node(before.value, before.height));
    }

    private static long simulate(int m_index) {
        Node total = query(1, 1, MAX_H, 1, MAX_H);
        int maxHeight = 0;
        for (int i = 1; i < mcnt; i++) {
            maxHeight = Math.max(maxHeight, mountain[i]);
        }
        return (answer[m_index] + (total.value - 1)) * 1_000_000 + maxHeight;
    }
}
