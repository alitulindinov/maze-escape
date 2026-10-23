/**
 * Missions 4 and 5. The slides: a directed graph. Cells are 0 .. n-1, slide i goes from s.from()[i] to s.to()[i]
 * and takes s.time()[i]. There are no slides from a cell to itself, and the exit can always be reached from the start.
 *
 * Ready for you: Graphs.slidesFrom(s)[u] is the list of the slides that leave cell u, and Graphs.slidesInto(s)[v] is the list
 * of the slides that enter cell v (reversed graph). The lists hold slide numbers i; the cell at the other end is s.to()[i]
 * or s.from()[i].
 */
import java.util.Arrays;
public class Zones {

    /**
     * Mission 4. Kosaraju. Returns zoneOf: zoneOf[cell] = number of the zone, numbers 0 .. zones-1.
     * Number the zones in the order in which the second pass finds them. That order is a topological order
     * of the zone graph: every slide between two zones goes from a smaller number to a larger number.
     *
     * Plan: 1) DFS on the slides, write the cells in the order in which they are finished (use a stack, not recursion);
     * 2) go through the cells from the last finished to the first; if a cell has no zone yet, DFS from it on the reversed
     * slides (slidesInto) and give every cell it reaches the next zone number.
     * Is DAG in Main is 1 when the number of zones equals the number of cells (no cycle), so you do not need a separate check.
     */

    public static int[] kosaraju(Input.Slides s) {
        int n = s.n();
        int[][] from = Graphs.slidesFrom(s);
        int[][] into = Graphs.slidesInto(s);

        boolean[] visited = new boolean[n];
        int[] order = new int[n];
        int orderCount = 0;

        int[] stack = new int[n];
        int[] nextIndex = new int[n];

        // First pass: DFS on the original graph.
        for (int start = 0; start < n; start++) {
            if (visited[start]) {
                continue;
            }

            int top = 0;
            stack[0] = start;
            visited[start] = true;
            nextIndex[start] = 0;

            while (top >= 0) {
                int cell = stack[top];

                if (nextIndex[cell] < from[cell].length) {
                    int slide = from[cell][nextIndex[cell]++];
                    int next = s.to()[slide];

                    if (!visited[next]) {
                        visited[next] = true;
                        nextIndex[next] = 0;
                        stack[++top] = next;
                    }
                } else {
                    order[orderCount++] = cell;
                    top--;
                }
            }
        }

        // Second pass: DFS on the reversed graph.
        int[] zoneOf = new int[n];
        Arrays.fill(zoneOf, -1);
        int zoneCount = 0;

        for (int i = orderCount - 1; i >= 0; i--) {
            int start = order[i];

            if (zoneOf[start] != -1) {
                continue;
            }

            int top = 0;
            stack[0] = start;
            zoneOf[start] = zoneCount;

            while (top >= 0) {
                int cell = stack[top--];

                for (int slide : into[cell]) {
                    int next = s.from()[slide];

                    if (zoneOf[next] == -1) {
                        zoneOf[next] = zoneCount;
                        stack[++top] = next;
                    }
                }
            }

            zoneCount++;
        }

        return zoneOf;
    }


    /**
     * Mission 5. The graph of zones: the edge X to Y costs the cheapest slide from X to Y, walking inside a zone is free.
     * Go through the zones in the order of their numbers (no priority queue). Returns two numbers:
     * [0] flow stages: the number of zones on the longest chain of zones;
     * [1] the cost of the cheapest route from the zone of cell 0 to the zone of the last cell.
     */

    public static long[] routes(Input.Slides s, int[] zoneOf, int zoneCount) {
        long INF = Long.MAX_VALUE / 4;

        long[][] edgeCost = new long[zoneCount][zoneCount];

        for (int i = 0; i < zoneCount; i++) {
            Arrays.fill(edgeCost[i], INF);
        }

        for (int i = 0; i < s.count(); i++) {
            int fromZone = zoneOf[s.from()[i]];
            int toZone = zoneOf[s.to()[i]];

            if (fromZone != toZone) {
                edgeCost[fromZone][toZone] =
                        Math.min(edgeCost[fromZone][toZone], s.time()[i]);
            }
        }

        long[] dist = new long[zoneCount];
        long[] stages = new long[zoneCount];

        Arrays.fill(dist, INF);
        int startZone = zoneOf[0];
        int exitZone = zoneOf[s.n() - 1];

        dist[startZone] = 0;
        stages[startZone] = 1;

        for (int from = 0; from < zoneCount; from++) {
            for (int to = from + 1; to < zoneCount; to++) {
                if (edgeCost[from][to] == INF) {
                    continue;
                }

                if (stages[from] > 0) {
                    stages[to] = Math.max(stages[to], stages[from] + 1);
                }

                if (dist[from] != INF) {
                    dist[to] = Math.min(
                            dist[to],
                            dist[from] + edgeCost[from][to]
                    );
                }
            }
        }

        return new long[]{
                stages[exitZone],
                dist[exitZone]
        };
    }

}
