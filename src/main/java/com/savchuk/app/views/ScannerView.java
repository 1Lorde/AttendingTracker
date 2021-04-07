// created by Vlad Savchuk 26.11.2019 0:44
package com.savchuk.app.views;

import com.savchuk.app.models.Access;
import com.savchuk.app.models.Attending;
import com.savchuk.app.models.Cadet;
import com.savchuk.app.models.Session;
import com.savchuk.app.services.*;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WebBrowser;
import elemental.json.Json;
import org.bson.types.ObjectId;


import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

@PageTitle("Scanner - Attendance Tracker")
@Route("scanner")
public class ScannerView extends AppLayout implements BeforeEnterObserver {
    private final CadetService cadetService;
    private final AttendingService attendingService;
    private LoginService loginService;
    private FaceService faceService;
    private File file = new File("./temp_data/last.jpg");


    public ScannerView() {
        faceService = FaceService.getInstance();
        attendingService = AttendingService.getInstance();
        cadetService = CadetService.getInstance();
        loginService = LoginService.getInstance();

        Image logoImage = new Image("/images/logo.png", "Logo");
        logoImage.setWidth("384px");
        logoImage.setHeight("96px");
        addToNavbar(logoImage);

        VerticalLayout status = new VerticalLayout();


        Image success = new Image("/images/success.png", "success");
        success.setHeight("64px");
        success.setWidth("64px");

        Image denied = new Image("/images/denied.png", "denied");
        denied.setHeight("64px");
        denied.setWidth("64px");


        MemoryBuffer memoryBuffer = new MemoryBuffer();
        Upload upload = new Upload(memoryBuffer);
        upload.setDropAllowed(false);
        upload.setAcceptedFileTypes(".png", ".jpeg", ".jpg");
        Button uploadButton = new Button("Open Camera", new Icon(VaadinIcon.CAMERA));
        uploadButton.addClickListener(e ->{
            status.removeAll();
        });
        upload.addStartedListener(e -> status.removeAll());
        upload.setUploadButton(uploadButton);
        upload.addSucceededListener(event ->{
            InputStream inputStream = memoryBuffer.getInputStream();
            try {
//                Files.copy(inputStream, Paths.get(file.getPath()), StandardCopyOption.REPLACE_EXISTING);

                BufferedImage image = ImageIO.read(inputStream);

                File compressedImageFile = new File(file.getPath());
                OutputStream os =new FileOutputStream(compressedImageFile);

                Iterator<ImageWriter> writers =  ImageIO.getImageWritersByFormatName("jpg");
                ImageWriter writer = (ImageWriter) writers.next();

                ImageOutputStream ios = ImageIO.createImageOutputStream(os);
                writer.setOutput(ios);

                ImageWriteParam param = writer.getDefaultWriteParam();

                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(0.2F);
                writer.write(null, new IIOImage(image, null, null), param);

                os.close();
                ios.close();
                writer.dispose();


                ObjectId findedId = faceService.searchFace(compressedImageFile);

                if (findedId != null){
                    status.add(success);
                    Cadet c = cadetService.findAll(findedId.toString()).get(0);
                    c.setPresent(!c.isPresent());
                    cadetService.update(c);
                    attendingService.checkIn(c);

                    if (c.isPresent()){
                        UiUtils.showCenterNotification(String.format("Welcome back, %s %s (group %s)!", c.getSurname(), c.getName(), c.getGroup()), NotificationVariant.LUMO_SUCCESS);
                    }else {
                        UiUtils.showCenterNotification(String.format("Have a good leave, %s %s (group %s)!", c.getSurname(), c.getName(), c.getGroup()), NotificationVariant.LUMO_PRIMARY);
                    }
                }else {
                    status.add(denied);
                    UiUtils.showCenterNotification("Person didn`t recognized", NotificationVariant.LUMO_ERROR);
                }

                upload.getElement().setPropertyJson("files", Json.createArray());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        Image scanImage = new Image("/images/face.png", "FaceID");
        scanImage.setWidth("256px");
        scanImage.setHeight("256px");

        Button scan = new Button("Scan", new Icon(VaadinIcon.CAMERA));
        scan.setWidth("220px");
        Button logout = new Button("Logout");
        logout.setIcon(new Icon(VaadinIcon.EXIT));
        logout.addClickListener(e ->{
            loginService.logout();
            UI.getCurrent().navigate("login");
            UiUtils.showNotification("Goodbye :)", NotificationVariant.LUMO_CONTRAST);
        });

        HorizontalLayout buttons = new HorizontalLayout(upload, logout);
        VerticalLayout layout = new VerticalLayout();
        status.setWidthFull();
        layout.setSizeFull();
        layout.add(scanImage);
        layout.add(status);
        layout.add(new Hr());
        layout.add(buttons);

        status.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, success);
        status.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, denied);

        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, scanImage);
        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, buttons);
        setContent(layout);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        WebBrowser webBrowser = VaadinSession.getCurrent().getBrowser();
        //webBrowser.isIPhone() || webBrowser.isAndroid() || webBrowser.isWindowsPhone()
        if (true) {
            String localSession = loginService.checkCookies();
            if (localSession != null){
                Session session = loginService.checkSession(localSession);
                if (session == null){
                    beforeEnterEvent.forwardTo("login");
                }else if (!session.getAccess().equals(Access.Scanner)){
                    beforeEnterEvent.forwardTo("admin");
                }
            }else {
                beforeEnterEvent.forwardTo("login");
            }
            UiUtils.showNotification("Welcome to the scanner!", NotificationVariant.LUMO_PRIMARY);
        } else {
            String localSession = loginService.checkCookies();
            if (localSession != null){
                Session session = loginService.checkSession(localSession);
                if (session == null){
                    beforeEnterEvent.forwardTo("login");
                }else if (session.getAccess().equals(Access.Scanner)){
                    loginService.logout();
                    UiUtils.showNotification("Sorry, but scanner available only on mobile devices.", NotificationVariant.LUMO_PRIMARY);
                }
            }

            beforeEnterEvent.forwardTo("login");
        }

    }


}

