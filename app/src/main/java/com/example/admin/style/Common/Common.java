package com.example.admin.style.Common;

import com.example.admin.style.Model.User;

public class Common {

    public static User currentUser;

    public static  String convertCodeToStatus(String status) {

        if(status.equals("0"))
            return "Placed";
        else if(status.equals("1"))
            return "On my way";
        else
            return "Shipped";
    }
}
