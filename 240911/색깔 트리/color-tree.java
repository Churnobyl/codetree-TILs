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

    public UserSolution() {}

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
        int latestVersion = node.version;
        int finalColor = node.color;

        while (node.pId != -1) {
            Node parent = nodes[node.pId];
            if (parent.version > latestVersion) {
                latestVersion = parent.version;
                finalColor = parent.color;
            }
            node = parent;
        }

        return finalColor;
    }

    public long printAllPoints() {
        allSum = 0;

        for (Node rootNode : rootNodes) {
            boolean[] usedColors = new boolean[6]; // 각 루트마다 초기화
            dfs(rootNode, rootNode.color, rootNode.version, usedColors);
        }

        return allSum;
    }

    private void dfs(Node node, int parentColor, int parentVersion, boolean[] usedColors) {
        // 부모의 버전이 더 최신인 경우, 부모의 색상으로 갱신
        if (node.version < parentVersion) {
            node.color = parentColor;
            node.version = parentVersion;
        }

        // 현재 노드의 색상을 사용 중으로 표시
        usedColors[node.color] = true;

        // 자식 노드를 순회하며 DFS 재귀 호출
        for (Node child : node.children) {
            boolean[] childUsedColors = new boolean[6]; // 각 자식 호출마다 초기화
            dfs(child, node.color, node.version, childUsedColors);

            // 자식에서 사용된 색상을 현재 노드에 누적
            for (int i = 1; i <= 5; i++) {
                if (childUsedColors[i]) usedColors[i] = true;
            }
        }

        // 색상 개수 계산
        int colorCount = 0;
        for (boolean used : usedColors) {
            if (used) colorCount++;
        }

        allSum += (long) colorCount * colorCount;
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