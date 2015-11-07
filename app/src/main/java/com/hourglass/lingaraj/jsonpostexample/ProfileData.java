package com.hourglass.lingaraj.jsonpostexample;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lingaraj on 11/7/15.
 */
public class ProfileData implements Serializable {

    public List<UserProfiles> userProfiles;
}
class UserProfiles
{
    public String firstName;
    public String lastName;
    public int age;
    public int mobileNumber;
    public String eMail;

}
