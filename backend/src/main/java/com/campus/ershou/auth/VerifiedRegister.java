package com.campus.ershou.auth;

public class VerifiedRegister {

    private String studentNo;
    private String username;
    private String email;

    public VerifiedRegister() {
    }

    public VerifiedRegister(String studentNo, String username, String email) {
        this.studentNo = studentNo;
        this.username = username;
        this.email = email;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
