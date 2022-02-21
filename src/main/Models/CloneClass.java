package main.Models;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CloneClass {

    private List<Method> clones;
    private Method cloneMethod;
    private String highestPath;

    public CloneClass(Method cloneMethod) {
        this.clones = new ArrayList<>();
        this.cloneMethod = cloneMethod;
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

        result += this.clones.size() + " -- " + cloneMethod;
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

    public List<File> getFiles() {
        List<File> result = new ArrayList<>();

        for(Method method : this.clones) {
            result.add(method.getFile());
        }

        return result;
    }
}
