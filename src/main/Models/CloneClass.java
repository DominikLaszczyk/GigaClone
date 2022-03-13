package main.Models;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CloneClass {
    public enum Type {
        ONE("1"),
        TWO("2"),
        THREE("3");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return  name;
        }
    }

    private List<Method> clones;
    private Method cloneMethod;
    private String highestPath;
    private Type type;

    public CloneClass(Method cloneMethod, Type type) {
        this.clones = new ArrayList<>();
        this.cloneMethod = cloneMethod;
        this.type = type;
    }

    public CloneClass(Method cloneMethod) {
        this.clones = new ArrayList<>();
        this.cloneMethod = cloneMethod;
        this.type = null;
    }

    public void computeHighestPath() {
        List<String> paths = new ArrayList<>();
        for(Method method : clones) {
            paths.add(method.getFile().getPath());
        }
        int k = paths.get(0).length();
        for (int i = 1; i < paths.size(); i++)
        {
            k = Math.min(k, paths.get(i).length());
            for (int j = 0; j < k; j++)
                if (paths.get(i).charAt(j) != paths.get(0).charAt(j))
                {
                    k = j;
                    break;
                }
        }

        String tempPath = paths.get(0).substring(0, k);

        if((tempPath.charAt(tempPath.length()-1)) == '\\') {
            this.highestPath = tempPath.substring(0,tempPath.length()-1);
        }
        else {
            this.highestPath = tempPath;
        }

    }

    @Override
    public String toString() {
        String result = "";

        result += this.clones.size() + " -- " + this.type.name + " -- " +cloneMethod;
        return result;
    }

    public String getHighestPath() {
        return this.highestPath;
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

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean hasType() {
        return this.type != null;
    }

}
