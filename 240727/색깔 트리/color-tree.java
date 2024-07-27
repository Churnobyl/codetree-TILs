import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

class Node {
    int pId, color, maxDepth, version;
    List<Node> children = new ArrayList<>();

    public Node(int pId, int color, int maxDepth, int version) {
        this.pId = pId;
        this.color = color;
        this.maxDepth = maxDepth;
        this.version = version;
    }

    @Override
    public String toString() {
        return "Node{" +
                "pId=" + pId +
                ", color=" + color +
                ", maxDepth=" + maxDepth +
                ", version=" + version +
                '}';
    }
}

class UserSolution {
    Node[] nodes = new Node[100_001];
    int version = 0;
    List<Node> rootNodes = new LinkedList<>();
    long allSum;

    public UserSolution() {
    }

    public void addNode(int mId, int pId, int color, int maxDepth) {
        int depth = 1;

        if (pId != -1) {
            Node iterator = nodes[pId];

            while (iterator.pId != -1) {
                if (iterator.maxDepth <= depth) {
                    return;
                }

                iterator = nodes[iterator.pId];
                depth++;
            }

            if (iterator.maxDepth <= depth) {
                return;
            }

            nodes[mId] = new Node(pId, color, maxDepth, version++);
            nodes[pId].children.add(nodes[mId]);
        } else {
            nodes[mId] = new Node(pId, color, maxDepth, version++);
            rootNodes.add(nodes[mId]);
        }
    }

    public void changeColor(int mId, int color) {
        Node node = nodes[mId];
        node.color = color;
        node.version = version++;
    }

    public int printOneColor(int mId) {
        Node node = nodes[mId];
        int v = node.version;
        int thatColor = node.color;

        if (node.pId == -1) {
            return thatColor;
        }

        Node iterator = nodes[node.pId];

        while (iterator.pId != -1) {
            if (v < iterator.version) {
                thatColor = iterator.color;
            }

            iterator = nodes[iterator.pId];
        }

        return thatColor;
    }

    public long printAllPoints() {
        allSum = 0;

        for (Node rootNode : rootNodes) {
            dfs(rootNode, rootNode.color, rootNode.version);
        }

        return allSum;
    }

    private int dfs(Node node, int prevColor, int prevVersion) {

        // 위의 버전이 더 최신일 경우
        if (node.version < prevVersion) {
            node.color = prevColor;
            node.version = prevVersion;
        }

        int bit = (1 << node.color);

        List<Node> children = node.children;

        if (children.size() == 0) {
            bit |= (1 << node.color);
            allSum += 1;
            return bit;
        } else {
            for (Node child : children) {
                bit |= dfs(child, node.color, node.version);
            }

            int result = bitCounter(bit);
            allSum += result * result;
            return bit;
        }
    }

    private int bitCounter(int bit) {
        int count = 0;
        while (bit > 0) {
            bit &= bit - 1;
            count++;
        }
        return count;
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        UserSolution userSolution = new UserSolution();

        int Q = sc.nextInt();

        for (int i = 0; i < Q; i++) {
            int comm = sc.nextInt();

            int m_id;
            int color;

            switch (comm) {
                case 100:
                    m_id = sc.nextInt();
                    int p_id = sc.nextInt();
                    color = sc.nextInt();
                    int max_depth = sc.nextInt();
                    userSolution.addNode(m_id, p_id, color, max_depth);
                    break;
                case 200:
                    m_id = sc.nextInt();
                    color = sc.nextInt();
                    userSolution.changeColor(m_id, color);
                    break;
                case 300:
                    m_id = sc.nextInt();
                    System.out.println(userSolution.printOneColor(m_id));
                    break;
                case 400:
                    System.out.println(userSolution.printAllPoints());
                    break;
            }
        }
    }
}