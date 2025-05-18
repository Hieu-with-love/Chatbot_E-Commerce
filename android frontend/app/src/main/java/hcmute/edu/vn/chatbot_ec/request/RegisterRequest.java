package hcmute.edu.vn.chatbot_ec.request;

public class RegisterRequest {
    private String email;
    private String fullName;
    private String phone;
    private String password;
    private String role;

    public RegisterRequest(String email, String fullName, String phone, String password, String role) {
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.password = password;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
