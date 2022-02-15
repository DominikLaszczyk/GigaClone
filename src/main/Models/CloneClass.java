package main.Models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CloneClass {

    private List<Method> clones;
    private Method cloneMethod;

    public CloneClass(Method cloneMethod) {
        this.clones = new ArrayList<>();
        this.cloneMethod = cloneMethod;
    }

    @Override
    public String toString() {
        String result = "";

        result += this.clones.size() + " -- " + cloneMethod;
        return result;
    }

    public List<Method> getClones() {
        return this.clones;
    }

    public void addClone(Method clone) {
        this.clones.add(clone);
    }

    public Method getCloneMethod() {
        return this.cloneMethod;
    }
}
