package main.Models;

public class CloneGraph {

    public enum Type{
        RADIALTREE("Radial Tree"),
        SCATTERPLOT("Scatter Plot"),
        TREEMAP("Tree Map");


        private final String name;


        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
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
