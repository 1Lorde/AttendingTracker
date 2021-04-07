package com.savchuk.app.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;


@Route("")
@PageTitle("Home - Attendance Tracker")
@Theme(value = Lumo.class, variant = Lumo.DARK)
@CssImport("./styles/style.css")
@PWA(name = "Attendance Tracker", shortName = "Attendance Tracker by Vlad Savchuk")
public class MainView extends AppLayout {
    private final Button button = new Button("Open Login");
    private final H1 header = new H1("Welcome to the Attendance Tracker!");
    public MainView() {
        button.addClickListener(e -> {
            UI.getCurrent().navigate(LoginView.class);
        });
        Image logoImage = new Image("/images/logo.png", "Logo");
        logoImage.setWidth("384px");
        logoImage.setHeight("96px");

        addToNavbar(logoImage);

        Image unicornImage = new Image("/images/unicorn.gif", "special for cap Kyrgan :)");
        unicornImage.setHeight("300px");
        unicornImage.setWidth("300px");
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.add(unicornImage);
        layout.add(header);
        layout.add(button);
        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, header);
        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, button);
        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, unicornImage);
        setContent(layout);
    }


}
