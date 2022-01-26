package main.Models;

public class CloneGraph {

    public enum Type{
        RADIALTREE("Radial Tree", "../CloneGraphs/RadialTree.js", "../Data/clonesRadialTree.js"),
        HEB("Hierarchical Edge Bundling", "", ""),
        SCATTER("Scatter Plot", "", "");

        private final String name;
        private final String scriptPath;
        private final String dataPath;

        Type(String name, String scriptPath, String dataPath) {
            this.name = name;
            this.scriptPath = scriptPath;
            this.dataPath = dataPath;
        }

        public String getName() {
            return name;
        }

        public String getScriptPath() {
            return scriptPath;
        }

        public String getDataPath() {
            return dataPath;
        }

        @Override
        public String toString() {
            return  name;
        }
    }

    private static String indexPage = "../CloneGraphs/index.html";

    public static String getIndexPage() {
        return indexPage;
    }
}
