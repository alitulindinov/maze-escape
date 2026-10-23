
public class DisjointSet {

    private int[] parent;
    private int[] size;
    private int sets;

    public DisjointSet(int n) {
        parent = new int[n];
        size = new int[n];
        sets = n;

        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    /** The root of the set that contains x. */
    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    /** Joins the sets of a and b. */
    public boolean union(int a, int b) {
        int rootA = find(a);
        int rootB = find(b);

        if (rootA == rootB) {
            return false;
        }

        if (size[rootA] < size[rootB]) {
            int temp = rootA;
            rootA = rootB;
            rootB = temp;
        }

        parent[rootB] = rootA;
        size[rootA] += size[rootB];
        sets--;

        return true;
    }

    public boolean connected(int a, int b) {
        return find(a) == find(b);
    }

    /** Number of sets now. */
    public int count() {
        return sets;
    }
}
