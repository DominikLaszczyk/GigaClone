package project.Models.Algorithms;

import javafx.collections.ObservableList;
import project.Models.CloneClass;
import project.Models.FileExtended;
import project.Models.Method;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.util.*;

public class ParseTreeCloneDetection extends CloneDetection{

    private static final Double MIN_SUBTREE_SIMILARITY = 0.9;
    private static final Double MIN_FUNCTION_SIMILARITY = 0.8;
    private static final Integer MIN_SUBTREE_MASS = 3;


    public ParseTreeCloneDetection(ObservableList<FileExtended> files) {
        super(files);
    }

    @Override
    public Set<CloneClass> detectClones() throws IOException {
        this.cloneClasses.clear();

        Set<CloneClass> tempCloneClasses = new HashSet<>();

        double progressIterator = 0.0;

        List<Method> methodsWithSubTrees = new ArrayList<>();

        for (FileExtended file : this.files) {
            for(Method currentMethod : file.getMethods()) {


                ParseTree currentMethodPT = currentMethod.getParserRuleContext();

                //get all subtrees from current parse tree
                List<ParseTree> subtrees = new ArrayList<>();
                getSubTrees(currentMethodPT, subtrees);

                //calculate mass of every parse tree, and convert to ParseTreeExtended
                List<ParseTreeExtended> subTreesWithMass = new ArrayList<>();
                for(ParseTree subTree : subtrees) {
                    ParseTreeExtended parseTreeExtended = new ParseTreeExtended(subTree);
                    if(parseTreeExtended.mass > MIN_SUBTREE_MASS) {
                        subTreesWithMass.add(parseTreeExtended);
                    }
                }


                currentMethod.setSubTrees(subTreesWithMass);
                methodsWithSubTrees.add(currentMethod);
            }
        }


        for(Method methodsWithSubTree : methodsWithSubTrees) {

            boolean belongsToCC = false;

            for(CloneClass currentCloneClass : tempCloneClasses) {
                for(Method clone : currentCloneClass.getClones()) {
                    if(areType3Clones(methodsWithSubTree, clone)) {
                        belongsToCC = true;
                        break;
                    }
                }
            }


            if(!belongsToCC) {
                CloneClass newCloneClass = new CloneClass(methodsWithSubTree, CloneClass.Type.THREE);
                newCloneClass.addClone(methodsWithSubTree);
                tempCloneClasses.add(newCloneClass);
            }
        }


        return tempCloneClasses;
    }


    private static boolean areType3Clones(Method method1, Method method2) {
        List<SubTreePair> subTreePairs = new ArrayList<>();
        List<ParseTreeExtended> subTrees1 = method1.getSubTrees();
        List<ParseTreeExtended> subTrees2 = method2.getSubTrees();

        for(ParseTreeExtended subTree1 : subTrees1) {
            for(ParseTreeExtended subTree2 : subTrees2) {
                Double similarity = calculateSimilarity(subTree1, subTree2);
                if(similarity > MIN_SUBTREE_SIMILARITY) {

                    SubTreePair.removeSubTreePairs(subTreePairs, subTree1);
                    SubTreePair.removeSubTreePairs(subTreePairs, subTree2);

                    subTreePairs.add(new SubTreePair(subTree1, subTree2, similarity));
                }
            }
        }

        double method1PercentCovered;
        double method2PercentCovered;

        int method1NumOfNodes = method1.getNumOfParseTreeNodes();
        int method2NumOfNodes = method2.getNumOfParseTreeNodes();

        int method1CloneNodeCounter = 0;
        int method2CloneNodeCounter = 0;

        for(SubTreePair subTreePair : subTreePairs) {
            method1CloneNodeCounter += subTreePair.subTree1.mass;
            method2CloneNodeCounter += subTreePair.subTree2.mass;
        }

        method1PercentCovered = (double)method1CloneNodeCounter/method1NumOfNodes;
        method2PercentCovered = (double)method2CloneNodeCounter/method2NumOfNodes;

        System.out.println(method1CloneNodeCounter + " - " + method1NumOfNodes);
        System.out.println(method2CloneNodeCounter + " - " + method2NumOfNodes);

        return false;
    }

    private static Double calculateSimilarity(ParseTreeExtended subTree1, ParseTreeExtended subTree2) {
        /*
        Similarity = 2 x S / (2 x S + L + R)
        where:
            S = number of shared nodes
            L = number of different nodes in sub-tree 1
            R = number of different nodes in sub-tree 2
        */

        double S,L,R;

        List<String> nodes1 = subTree1.nodes;
        List<String> nodes2 = subTree2.nodes;

        Set<String> similar = new HashSet<>(nodes1);
        similar.retainAll(nodes2);

        Set<String> different1 = new HashSet<>();
        Set<String> different2 = new HashSet<>();

        different1.addAll(nodes1);
        different1.addAll(nodes2);
        different2.addAll(nodes1);
        different2.addAll(nodes2);

        different1.removeAll(nodes2);
        different2.removeAll(nodes1);

        S = similar.size();
        L = different1.size();
        R = different2.size();

        return (2.0 * S) / (2.0 * S + L + R);
    }

    private static void getSubTrees(ParseTree root, List<ParseTree> subTrees) {
        for(int i=0; i<root.getChildCount(); i++) {
            if(root.getChild(i).getChildCount() > 0) {
                subTrees.add(root.getChild(i));
                getSubTrees(root.getChild(i), subTrees);
            }
        }
    }

    public static class ParseTreeExtended extends ParserRuleContext {
        private Integer mass;
        private ParseTree root;
        private List<String> nodes;

        public ParseTreeExtended(ParseTree root) {
            this.root = root;
            this.mass = 0;
            this.nodes = new ArrayList<>();
            getNodes(root);
        }

        @Override
        public int getChildCount() {
            return this.root.getChildCount();
        }

        @Override
        public ParseTree getChild(int index) {
            return this.root.getChild(index);
        }

        private void getNodes(ParseTree treeRoot) {
            for(int i=0; i<treeRoot.getChildCount(); i++) {
                if(treeRoot.getChild(i).getChildCount() > 0) {
                    this.nodes.add(treeRoot.getChild(i).toString());
                    getNodes(treeRoot.getChild(i));
                }
                else {
                    this.mass++;
                }
            }
        }

        static void getSubTrees(ParseTreeExtended treeRoot, List<ParseTreeExtended> subTrees) {
            for(int i=0; i<treeRoot.getChildCount(); i++) {
                ParseTreeExtended currentChild = new ParseTreeExtended(treeRoot.getChild(i));
                if(currentChild.getChildCount() > 0) {
                    subTrees.add(currentChild);
                    getSubTrees(currentChild, subTrees);
                }
            }
        }

        public String toString() {
            return root + ", MASS: " + this.mass;
        }
    }

    private static class SubTreePair {
        ParseTreeExtended subTree1;
        ParseTreeExtended subTree2;
        Double similarity;

        SubTreePair(ParseTreeExtended subTree1, ParseTreeExtended subTree2, Double similarity) {
            this.subTree1 = subTree1;
            this.subTree2 = subTree2;
            this.similarity = similarity;
        }

        static void removeSubTreePairs(List<SubTreePair> subTreePairs, ParseTreeExtended subTree) {
            List<SubTreePair> subTreePairsToRemove = new ArrayList<>();
            List<ParseTreeExtended> subSubTrees = new ArrayList<>();
            ParseTreeExtended.getSubTrees(subTree, subSubTrees);

            for(SubTreePair subTreePair : subTreePairs) {
                ParseTreeExtended st1 = subTreePair.subTree1;
                ParseTreeExtended st2 = subTreePair.subTree2;
                for(ParseTreeExtended subSubTree : subSubTrees) {
                    if(st1.nodes.equals(subSubTree.nodes) || st2.nodes.equals(subSubTree.nodes)) {
                        subTreePairsToRemove.add(subTreePair);
                    }
                }

            }

            subTreePairs.removeAll(subTreePairsToRemove);
        }

        public String toString() {
            return this.similarity + ", st1 mass: " + subTree1.mass + ", st2 mass: " + subTree2.mass;
        }

    }
}
