import java.lang.IndexOutOfBoundsException;
import java.lang.IllegalArgumentException;

/**
 * Pathfinder uses A* search to find a near optimal path
 * between to locations with given terrain.
 */

public class Pathfinder {
    private Coord end;
    private Coord start;
    private Terrain terrain;
    private float heur;
    private boolean pathFound = false;  //implement in compute path, set to true when found
    private int searchSize;
    private PFNode[][] board;
    private PFNode endNode;
    private Stack<Coord> stack = new Stack<>();
    //set up Minimum Priority Queue
    private MinPQ<PFNode> pq = new MinPQ<>();
    private float pathCost;

    /**
     * PFNode will be the key for MinPQ (used in computePath())
     */
    private class PFNode implements Comparable<PFNode> {
        private boolean isUsed;
        private boolean invalid;
        private Coord loc;
        private PFNode fromNode;
        private float cost;
        // loc: the location of the PFNode
        // fromNode: how did we get here? (linked list back to start)

        public PFNode(Coord loc, PFNode fromNode) {
            this.loc = loc;
            this.fromNode = fromNode;
            if(fromNode != null)
                cost = fromNode.cost + terrain.computeTravelCost(loc, fromNode.loc);
        }

        // compares this with that, used to find minimum cost PFNode
        public int compareTo(PFNode that) {
            if(this.getCost(heur) < that.getCost(heur)) return -1;  //this < that
            if(that.getCost(heur) < this.getCost(heur)) return 1;   //that < this
            return 0;  //this == that
        }

        // returns the cost to travel from starting point to this
        // via the fromNode chain
        public float getCost(float heuristic) {  //call getCost with heuristic of 0, get travel cost, do A* search
            //return fromNode.getCost(0) + terrain.computeTravelCost(fromNode.loc, loc) + heuristic*terrain.computeDistance(loc.getI(), loc.getJ(), endNode.loc.getI(), endNode.loc.getJ());
            //StdOut.println(heuristic);
            //StdOut.println(terrain.computeDistance(loc.getI(), loc.getJ(), end.getI(), end.getJ()));
            //StdOut.println("Cost:" + cost);
            return cost + heuristic*terrain.computeDistance(loc.getI(), loc.getJ(), end.getI(), end.getJ());
        }

        // returns if this PFNode is not marked invalid
        public boolean isValid() {
            return invalid;
        }

        // marks the PFNode as invalid
        public void invalidate() {
            invalid = true;
        }

        // returns if the PFNode is marked as used
        public boolean isUsed() {  //help
            return isUsed;
        }

        // marks the PFNode as used
        public void use(PFNode node) {
            node.isUsed = true;
        }

        // returns an Iterable of PFNodes that surround this
        public Iterable<PFNode> neighbors() {
            Stack<PFNode> s = new Stack<>();
            s.push(new PFNode(null, null));
            return s;
        }
    }

    public Pathfinder(Terrain terrain) {
        this.terrain = terrain;
    }

    public void setPathStart(Coord loc) { // I need a check to see how it can stay on the white area.
        if(loc == null) throw new IllegalArgumentException("The location is null.");
        if(!loc.isInBounds(0,0,terrain.getN()-1, terrain.getN()-1)) throw new IndexOutOfBoundsException("The location is out of bounds.");
        start = loc;
    }

    public Coord getPathStart() {
        return start;
    }

    public void setPathEnd(Coord loc) {
        if(loc == null) throw new IllegalArgumentException("The location is null.");
        if(!loc.isInBounds(0,0,terrain.getN()-1, terrain.getN()-1)) throw new IndexOutOfBoundsException("The location is out of bounds.");
        end = loc;
    }

    public Coord getPathEnd() {
        return end;
    }

    public void setHeuristic(float v) { // is this correct?
        heur = v;
    }

    public float getHeuristic() { return heur; }

    public void resetPath() {
        start = getPathStart(); //how do I tell it to find where the mouse is at? setPathStart?
        end = getPathEnd();
        endNode = null;
        pathFound = false;
        searchSize = 0;

        pq = new MinPQ<>();

        stack = new Stack<>();

        //computePath();
    }

    public void computePath() {
        if(getPathEnd() == null || getPathStart() == null) throw new IllegalArgumentException("Either or both of start path or end path have NOT been set.");

        //set up logic board with 2D array
        int N = terrain.getN();
        board = new PFNode[N][N];

        //I added this... is it needed?
        //setHeuristic(3);

        PFNode beginNode = new PFNode(getPathStart(), null);
        board[getPathStart().getI()][getPathStart().getJ()] = beginNode;
        pq.insert(beginNode);
        beginNode.use(beginNode);
        beginNode.cost = 0;
        searchSize = 0;
        while(!pathFound){
            //StdOut.println("path not found");
            //remove from pq
            PFNode removed = pq.delMin();
            //StdOut.println("The minimum has been removed.");  //bug check
            //check if node is at end
            if(removed.loc.equals(getPathEnd())){  //correct?
                pq.insert(removed);
                endNode = removed;
                pathFound = true;
                endNode.use(endNode);
                endNode.cost = removed.cost + terrain.computeTravelCost(removed.loc, endNode.loc);
                pathCost = endNode.cost;
                searchSize ++;
                break;
            }
            //"process" node just removed
            //see if the locations around the removed position are in bounds
            //now have them point to removed, "from = removed";  ?
            if(removed.loc.getJ() -1 >= 0 ){
               if(board[removed.loc.getI()][removed.loc.getJ()-1] == null){
                    PFNode node = new PFNode(removed.loc.add(0, -1), removed); //good
                    board[removed.loc.getI()][removed.loc.getJ() - 1] = node;
                    pq.insert(node);
                    node.use(node);
                    searchSize ++;
                    //node.cost = removed.cost + terrain.computeTravelCost(removed.loc, node.loc);
                }
            }
            if(removed.loc.getI() - 1 >= 0){
                if(board[removed.loc.getI()-1][removed.loc.getJ()] == null) {
                    PFNode node = new PFNode(removed.loc.add(-1, 0), removed); //good
                    board[removed.loc.getI() - 1][removed.loc.getJ()] = node;
                    pq.insert(node);
                    node.use(node);
                    searchSize ++;
                    //node.cost = removed.cost + terrain.computeTravelCost(removed.loc, node.loc);
                }
            }
            if(removed.loc.getJ() + 1 < N-1){
                if(board[removed.loc.getI()][removed.loc.getJ()+1] == null) {
                    PFNode node = new PFNode(removed.loc.add(0, 1), removed); //good
                    board[removed.loc.getI()][removed.loc.getJ() + 1] = node;
                    pq.insert(node);
                    node.use(node);
                    searchSize ++;
                    //node.cost = removed.cost + terrain.computeTravelCost(removed.loc, node.loc);
                }
            }
            if(removed.loc.getI() + 1 < N-1){
                if(board[removed.loc.getI()+1][removed.loc.getJ()] == null) {
                    PFNode node = new PFNode(removed.loc.add(1, 0), removed); //good
                    board[removed.loc.getI() + 1][removed.loc.getJ()] = node;
                    pq.insert(node);
                    node.use(node);
                    searchSize ++;
                    //node.cost = removed.cost + terrain.computeTravelCost(removed.loc, node.loc);
                }
            }
        }
        StdOut.println("A connection has been found!");

        stack.push(endNode.loc);
        PFNode nextNode = endNode.fromNode;
        while(nextNode.fromNode != beginNode){
            stack.push(nextNode.loc);
            nextNode = nextNode.fromNode;
        }
        //this should be the start node
        stack.push(nextNode.fromNode.loc);
    }

    public boolean foundPath() { //good
        return pathFound;
    }

    public float getPathCost() { //good
        return pathCost;
    }

    public int getSearchSize() { //good
        return searchSize;
    }

    public Iterable<Coord> getPathSolution() {
        return stack;
    } //good

    public boolean wasSearched(Coord loc) { //good
        return board[loc.getI()][loc.getJ()] != null;
    }

}
