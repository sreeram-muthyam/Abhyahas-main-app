package Model;

public class UserModel {

    private  String Full_Name, Email, Phone, Year_of_Study, User_ID, Followers, Following, Image, College_Name;

    public  UserModel() {

    }

    public UserModel(String full_Name, String email, String phone, String year_of_Study, String user_ID, String followers, String following, String image, String college_name) {
        Full_Name = full_Name;
        Email = email;
        Phone = phone;
        Year_of_Study = year_of_Study;
        User_ID = user_ID;
        Followers = followers;
        Following = following;
        Image = image;
        College_Name = college_name;
    }

    public String getFull_Name() {
        return Full_Name;
    }

    public void setFull_Name(String full_Name) {
        Full_Name = full_Name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getYear_of_Study() {
        return Year_of_Study;
    }

    public void setYear_of_Study(String year_of_Study) {
        Year_of_Study = year_of_Study;
    }

    public String getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(String user_ID) {
        User_ID = user_ID;
    }

    public String getFollowers() {
        return Followers;
    }

    public void setFollowers(String followers) {
        Followers = followers;
    }

    public String getFollowing() {
        return Following;
    }

    public void setFollowing(String following) {
        Following = following;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getCollege_Name() {
        return College_Name;
    }

    public void setCollege_Name(String college_Name) {
        College_Name = college_Name;
    }
}
