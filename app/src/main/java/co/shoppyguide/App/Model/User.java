package co.shoppyguide.App.Model;
import java.util.HashMap;
import java.util.Map;

public class User {

    private String name, email, phone, profilePhotoUrl;

    public User(){}

    public User(String name, String email, String phone, String profilePhotoUrl) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.profilePhotoUrl = profilePhotoUrl;
    }

    // Getter and Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }
    public Map<String, String> toUserMap()
    {
        Map<String,String> user=new HashMap<>();
        user.put("name",name);
        user.put("phone",phone);
        user.put("email",email);
        return user;
    }

}
