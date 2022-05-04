package project.Models;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ArrayCloneBuilder extends JSONCloneBuilder{

    public ArrayCloneBuilder(Set<CloneClass> cloneClasses) {
        super(cloneClasses);
    }

    @Override
    public String buildClones(Set<CloneClass> cloneClasses) {

        StringBuilder finalClones = new StringBuilder("data = [");
        StringBuilder labels = new StringBuilder("labels = [");
        Integer id = 0;
        HashMap<Integer, File> cloneFilesIds = new HashMap<>();
        HashMap<File, Integer> cloneFilesIdsRev = new HashMap<>();
        ArrayList<ClonePair> clonePairs = new ArrayList<>();

        for(CloneClass cc : cloneClasses) {
            for(Method clone : cc.getClones()) {
                if(!cloneFilesIds.containsValue(clone.getFile())) {
                    cloneFilesIds.put(id, clone.getFile());
                    cloneFilesIdsRev.put(clone.getFile(), id);
                    id++;
                }
            }
        }

        for(int k = 0; k<id; k++) {
            File currentCloneFile = cloneFilesIds.get(k);

            labels
                    .append("\"")
                    .append(currentCloneFile.getName())
                    .append("\",");

            for(CloneClass cc : cloneClasses) {
                List<Method> clonesInCurrentCC = cc.getClones();

                boolean isInCC = false;
                for(Method clone : clonesInCurrentCC) {
                    if(clone.getFile().equals(currentCloneFile)) {
                        isInCC = true;
                        break;
                    }
                }

                if(isInCC) {
                    for(Method clone : clonesInCurrentCC) {
                        ClonePair cp = new ClonePair(
                                k,
                                cloneFilesIdsRev.get(clone.getFile()),
                                currentCloneFile.getName(),
                                clone.getFile().getName(),
                                Integer.parseInt(cc.getType().toString()),
                                clonesInCurrentCC.size()
                        );

                        clonePairs.add(cp);
                    }
                }
            }
        }

        ArrayList<Integer> clonePairsToDelete = new ArrayList<>();
        for(int i=0; i<clonePairs.size()-1; i++) {
            for(int j=i+1; j<clonePairs.size(); j++) {
                if(clonePairs.get(i).sameFiles(clonePairs.get(j))) {
                    clonePairs.get(i).setSize(clonePairs.get(i).getSize() + clonePairs.get(j).getSize());
                    Integer clonePairIndex = j;
                    clonePairsToDelete.add(clonePairIndex);
                }
            }
        }

        for(int i=clonePairs.size()-1; i>=0; i--) {
            if(clonePairsToDelete.contains(i)) {
                clonePairs.remove(i);
            }
        }

        int maxSize = 0;
        for(ClonePair cp : clonePairs) {
            finalClones.append(cp.toJSON()).append(",\n");
            if(cp.getSize()>maxSize) maxSize = cp.getSize();
        }


        if(finalClones.charAt(finalClones.length()-1) == ',') { finalClones.setLength(finalClones.length() - 1); }
        finalClones.append("]");

        if(labels.charAt(labels.length()-1) == ',') { labels.setLength(labels.length() - 1); }
        labels.append("]");

        String maxSizeString = "maxSize = " + maxSize + "\n";

        String highestFileIndex = "highestFileIndex = " + id + "\n";

        return highestFileIndex + maxSizeString + labels + "\n" + finalClones;
    }
}
