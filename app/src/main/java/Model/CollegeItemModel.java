package Model;

public class CollegeItemModel {
    private String College_Name;

    public CollegeItemModel() {
    }

    public CollegeItemModel(String college_Name) {
        College_Name = college_Name;
    }

    public String getCollege_Name() {
        return College_Name;
    }

    public void setCollege_Name(String college_Name) {
        College_Name = college_Name;
    }
}
