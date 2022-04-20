package project.Models;

public class ClonePair {
    private Method clone1;
    private Method clone2;

    public ClonePair(Method clone1, Method clone2) {
        this.clone1 = clone1;
        this.clone2 = clone2;
    }

    public Method getClone1() {
        return this.clone1;
    }

    public Method getClone2() {
        return this.clone2;
    }
}
