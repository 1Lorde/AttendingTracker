// created by Vlad Savchuk 26.11.2019 1:03
package com.savchuk.app.services;

import com.savchuk.app.models.Access;
import com.savchuk.app.models.Session;
import com.savchuk.app.models.User;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.server.VaadinService;
import org.bson.types.ObjectId;

import javax.servlet.http.Cookie;
import java.io.ObjectInput;
import java.util.AbstractMap;

public class LoginService {
    private DatabaseService databaseService;
    private static LoginService instance;

    private LoginService() {
        databaseService = DatabaseService.getInstance();
        databaseService.init();
    }

    public static LoginService getInstance(){
        if (instance == null){
            instance = new LoginService();
        }
        return instance;
    }


    public boolean login(User user){
        Session session = databaseService.auth(user);
        if (session != null){
            user.setAccess(session.getAccess());
            // Create a new cookie
            Cookie myCookie = new Cookie("sessionID", session.getId().toString());

            // Make cookie expire in 2 minutes
            myCookie.setMaxAge(60*60*6);

            // Set the cookie path.
            myCookie.setPath(VaadinService.getCurrentRequest().getContextPath());

            // Save cookie
            VaadinService.getCurrentResponse().addCookie(myCookie);
            return true;
        }

        return false;
    }

    public void logout(){
        Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();
        for (Cookie cookie : cookies) {
            if ("sessionID".equals(cookie.getName())) {
                databaseService.deAuth(new ObjectId(cookie.getValue()));
            }
        }
        clearCookies();
    }

    public String checkCookies(){
        Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();
        for (Cookie cookie : cookies) {
            if ("sessionID".equals(cookie.getName()) && !cookie.getValue().equals("")) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private void clearCookies(){
        Cookie myCookie = new Cookie("sessionID", "");

        // Make cookie expire in 2 minutes
        myCookie.setMaxAge(60*60*6);

        // Set the cookie path.
        myCookie.setPath(VaadinService.getCurrentRequest().getContextPath());

        // Save cookie
        VaadinService.getCurrentResponse().addCookie(myCookie);
    }

    public Session checkSession(String sessionId){
        Session session = databaseService.checkSession(sessionId);
        if (session == null){
            clearCookies();
        }
        return session;
    }



}
