package sample.Module;

import sample.Method.UtilsUsable;

import java.io.Serializable;

/**
 * Created by WangMingming on 2017/3/6.
 */
public class Person implements Serializable{
    private static final long serialVersionUID = 1011L;
    private String email;
    private String fullName;
    private boolean isValid;

    public Person(String email,String fullName){
        this.email = email;
        this.fullName = fullName;
        isValid = UtilsUsable.checkEmail(email);
    }

    public String getEmail(){
        return email;
    }

    public String getFullName(){
        return fullName;
    }

    public boolean getIsValid(){
        return isValid;
    }

    @Override
    public String toString() {
        String info = fullName + ":  " + email;
        return info;
    }
}
