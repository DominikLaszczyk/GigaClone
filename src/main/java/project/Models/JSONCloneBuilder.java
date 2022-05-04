package project.Models;

import java.io.IOException;
import java.util.Set;

public abstract class JSONCloneBuilder {

    Set<CloneClass> cloneClasses;

    public JSONCloneBuilder(Set<CloneClass> cloneClasses) {
        this.cloneClasses = cloneClasses;
    }

    public abstract String buildClones(Set<CloneClass> cloneClasses) throws IOException;
}
