// created by Vlad Savchuk 10.11.2019 22:05
package com.savchuk.app.views;

import com.savchuk.app.models.Access;
import com.savchuk.app.models.Session;
import com.savchuk.app.models.User;
import com.savchuk.app.services.LoginService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.bson.types.ObjectId;

@PageTitle("Login - Attendance Tracker")
@Route("login")
public class LoginView extends AppLayout implements BeforeEnterObserver {
    private LoginService loginService = LoginService.getInstance();
    final LoginForm loginForm = new LoginForm();

    public LoginView(){
        Image logoImage = new Image("/images/logo.png", "Logo");
        logoImage.setWidth("384px");
        logoImage.setHeight("96px");

        addToNavbar(logoImage);

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, loginForm);

        loginForm.setForgotPasswordButtonVisible(false);
        loginForm.addLoginListener(e -> {
            User user = new User(e.getUsername(), e.getPassword());
            if (loginService.login(user)) {
                UI.getCurrent().getPage().reload();
            }else{
                loginForm.setError(true);
            }
            loginForm.setEnabled(true);
        });
        layout.add(loginForm);
        setContent(layout);

    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        String localSession = loginService.checkCookies();
        if (localSession != null){
            Session session = loginService.checkSession(localSession);
            if (session != null){
                if (loginService.checkSession(localSession).getAccess().equals(Access.Admin))
                    beforeEnterEvent.forwardTo("admin");
                else
                    beforeEnterEvent.forwardTo("scanner");
            }
        }
    }



}
