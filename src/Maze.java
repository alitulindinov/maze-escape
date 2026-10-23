/**
 * Missions 1 and 2. Building the maze out of the walls.
 * A wall k joins the cells w.a()[k] and w.b()[k] and costs w.cost()[k]. Indexes start from 0,
 * the line number in walls.txt is the index + 1. The cells are connected by DisjointSet.
 */
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Arrays;
public class Maze {

    /**
     * Mission 1. Take the walls in the order of the file. Break a wall when its two cells are in different sets.
     * Returns the indexes of the broken walls, in the order of breaking.
     */
    public static int[] breakWalls(Input.Walls w) {
        int n = w.rows() * w.cols();
        DisjointSet ds = new DisjointSet(n);

        int[] broken = new int[n - 1];
        int count = 0;

        for (int k = 0; k < w.a().length; k++) {
            if (ds.union(w.a()[k], w.b()[k])) {
                broken[count] = k;
                count++;
            }
        }

        int[] result = new int[count];
        System.arraycopy(broken, 0, result, 0, count);

        return result;
    }

    /**
     * Mission 2. Kruskal: the same, but take the walls from the cheapest to the most expensive.
     * Returns the indexes of the broken walls.
     */

    public static int[] cheapestMaze(Input.Walls w) {
        int n = w.rows() * w.cols();
        DisjointSet ds = new DisjointSet(n);

        Integer[] indexes = new Integer[w.count()];

        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = i;
        }

        Arrays.sort(indexes, (i, j) ->
                Integer.compare(w.cost()[i], w.cost()[j]));

        int[] broken = new int[n - 1];
        int count = 0;

        for (int k : indexes) {
            if (ds.union(w.a()[k], w.b()[k])) {
                broken[count] = k;
                count++;

                if (count == n - 1) {
                    break;
                }
            }
        }

        return Arrays.copyOf(broken, count);
    }


    /**
     * Mission 2. Prim with a priority queue. Returns the total cost of the cheapest maze.
     * It must be equal to the total cost of cheapestMaze.
     */

    public static long primCost(Input.Walls w) {
        int n = w.rows() * w.cols();

        if (n <= 1) {
            return 0;
        }

        List<Integer>[] incident = new List[n];

        for (int i = 0; i < n; i++) {
            incident[i] = new ArrayList<>();
        }

        for (int i = 0; i < w.count(); i++) {
            incident[w.a()[i]].add(i);
            incident[w.b()[i]].add(i);
        }

        boolean[] visited = new boolean[n];

        PriorityQueue<Integer> pq = new PriorityQueue<>(
                (i, j) -> Integer.compare(w.cost()[i], w.cost()[j])
        );

        visited[0] = true;
        pq.addAll(incident[0]);

        long totalCost = 0;
        int edgesUsed = 0;

        while (!pq.isEmpty() && edgesUsed < n - 1) {
            int wall = pq.poll();

            int a = w.a()[wall];
            int b = w.b()[wall];

            if (visited[a] && visited[b]) {
                continue;
            }

            int next = visited[a] ? b : a;

            visited[next] = true;
            totalCost += w.cost()[wall];
            edgesUsed++;

            for (int edge : incident[next]) {
                pq.add(edge);
            }
        }

        return totalCost;
    }

}
