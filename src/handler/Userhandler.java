package handler;

import Modules.User;
//macht validierungen

public class Userhandler {

    public boolean isValid(User user){

        if(user==null){
            return true;
        }

        if(user.getPassword() == null || user.getUsername() == null) {
            return false;
        }

        return true;
    }
}
