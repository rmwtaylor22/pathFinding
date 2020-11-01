import java.util.Iterator;

/**
 * Walker takes an Iterable of Coords and simulates an individual
 * walking along the path over the given Terrain
 */
public class Walker {
    private Terrain terrain;
    private Iterable<Coord> path;
    private Iterator<Coord> iterator;
    private Coord position;
    private float currentCost;
    private Coord previous = null;
    float runningCost = 0;

    // terrain: the Terrain the Walker traverses
    // path: the sequence of Coords the Walker follows
    public Walker(Terrain terrain, Iterable<Coord> path) {
        this.terrain = terrain;
        this.path = path;
        iterator = path.iterator();
    }

    // returns the Walker's current location
    public Coord getLocation() {
        /*if(previous == null){
            currentCost = 0;
        } else {
            currentCost = terrain.computeTravelCost(previous, position);
        }

        previous = this.position;
        */
        return this.position;
    }

    // returns true if Walker has reached the end Coord (last in path)
    public boolean doneWalking() {
        if(iterator.hasNext()){
            return false;
        }
        return true;
    }

    // advances the Walker along path
    // byTime: how long the Walker should traverse (may be any non-negative value)
    public void advance(float byTime) {
        /*
        if(runningCost >= currentCost){
            this.position = iterator.next();
            runningCost = 0;
        } else {
            runningCost+= byTime;
        }
        */
        this.position = iterator.next();
    }
}
//compute travel cost to know how long it takes to go to each spot
//start walker at first coord
//getlocation always calls beginning until advance is called
//when advance is called, move walker to next thing in iterable


//byTime take into account travel cost
//keep track of how many times advance is called and then compare that to the travel cost.