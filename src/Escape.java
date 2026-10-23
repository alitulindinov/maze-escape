/**
 * Mission 3. Escape: DFS from cell 0 to the last cell (rows * cols - 1) in the maze of Mission 1.
 * Two neighbouring cells are connected when the wall between them is broken.
 * The neighbours are tried in this order: up, right, down, left (cell - cols, cell + 1, cell + cols, cell - 1).
 * The ready method Graphs.open(w, broken) gives you the open neighbours of every cell, in no particular order:
 * to visit them in the required order, go through the four cells above one by one and take those that are in the list.
 * The program must not overflow the stack on a maze of 1000 by 1000 cells.
 */
import java.util.Arrays;
public class Escape {

    /**
     * path: the cells of the way out, from 0 to the last cell.
     * visited: how many cells are marked as visited at the moment the exit is marked (the start and the exit are counted).
     * deadEnds: how many cells of the maze have exactly one open neighbour.
     */
    public record Result(int[] path, int visited, int deadEnds) {}


    public static Result escape(Input.Walls w, int[] broken) {
        int n = w.rows() * w.cols();
        int cols = w.cols();

        int[][] open = Graphs.open(w, broken);
        boolean[] visited = new boolean[n];
        int[] parent = new int[n];
        Arrays.fill(parent, -1);

        int deadEnds = 0;

        for (int cell = 0; cell < n; cell++) {
            if (open[cell].length == 1) {
                deadEnds++;
            }
        }

        int[] stack = new int[n];
        int top = 0;
        stack[top] = 0;
        visited[0] = true;
        int visitedCount = 1;

        while (top >= 0) {
            int cell = stack[top];

            if (cell == n - 1) {
                break;
            }

            int[] neighbours = {
                    cell - cols,
                    cell + 1,
                    cell + cols,
                    cell - 1
            };

            boolean moved = false;

            for (int next : neighbours) {
                if (next < 0 || next >= n) {
                    continue;
                }

                if (Math.abs(cell / cols - next / cols)
                        + Math.abs(cell % cols - next % cols) != 1) {
                    continue;
                }

                boolean connected = false;
                for (int neighbour : open[cell]) {
                    if (neighbour == next) {
                        connected = true;
                        break;
                    }
                }

                if (connected && !visited[next]) {
                    visited[next] = true;
                    visitedCount++;
                    parent[next] = cell;
                    stack[++top] = next;
                    moved = true;
                    break;
                }
            }

            if (!moved) {
                top--;
            }
        }

        int length = 0;
        for (int cell = n - 1; cell != -1; cell = parent[cell]) {
            length++;
            if (cell == 0) {
                break;
            }
        }

        int[] path = new int[length];
        int cell = n - 1;

        for (int i = length - 1; i >= 0; i--) {
            path[i] = cell;
            cell = parent[cell];
        }

        return new Result(path, visitedCount, deadEnds);
    }

}
