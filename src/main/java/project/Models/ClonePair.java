package project.Models;

public class ClonePair {
    private int fileIndex1;
    private int fileIndex2;

    private String fileName1;
    private String fileName2;

    private int type;
    private int size;

    public ClonePair(int fileIndex1, int fileIndex2, String fileName1, String fileName2, int type, int size) {
        this.fileIndex1 = fileIndex1;
        this.fileIndex2 = fileIndex2;
        this.fileName1 = fileName1;
        this.fileName2 = fileName2;
        this.type = type;
        this.size = size;
    }

    public boolean sameFiles(ClonePair otherClonePair) {
        return (this.fileIndex1 == otherClonePair.fileIndex1 && this.fileIndex2 == otherClonePair.fileIndex2) &&
                this.type == otherClonePair.type;
                //|| (this.fileIndex1 == otherClonePair.fileIndex2 && this.fileIndex2 == otherClonePair.fileIndex1);
    }

    public String toJSON() {
        return "{fileIndex1:" + this.fileIndex1 + ",fileIndex2:" + this.fileIndex2 + ",fileName1:\"" + this.fileName1 +
                "\",fileName2:\"" + this.fileName2 + "\",type:" + this.type + ",size:" + this.size + "}";
    }

    public int getFileIndex1() {
        return fileIndex1;
    }

    public void setFileIndex1(int fileIndex1) {
        this.fileIndex1 = fileIndex1;
    }

    public int getFileIndex2() {
        return fileIndex2;
    }

    public void setFileIndex2(int fileIndex2) {
        this.fileIndex2 = fileIndex2;
    }

    public String getFileName1() {
        return fileName1;
    }

    public void setFileName1(String fileName1) {
        this.fileName1 = fileName1;
    }

    public String getFileName2() {
        return fileName2;
    }

    public void setFileName2(String fileName2) {
        this.fileName2 = fileName2;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
