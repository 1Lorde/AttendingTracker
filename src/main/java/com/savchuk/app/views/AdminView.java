// created by Vlad Savchuk 11.11.2019 1:26
package com.savchuk.app.views;

import com.mongodb.MongoWriteException;
import com.savchuk.app.models.*;
import com.savchuk.app.services.*;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;

import com.vaadin.flow.component.upload.Upload;

import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import com.vaadin.flow.server.StreamResource;
import org.bson.types.ObjectId;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@PageTitle("Admin panel - Attendance Tracker")
@Route("admin")
public class AdminView extends AppLayout implements BeforeEnterObserver {
    private CadetService cadetService = CadetService.getInstance();
    private AttendingService attendingService = AttendingService.getInstance();
    private LoginService loginService = LoginService.getInstance();
    private final Button logout;
    private File file = new File("./temp_data/last.jpg");
    private FaceService faceService;


    public AdminView() {
        faceService = FaceService.getInstance();

        Image logoImage = new Image("/images/logo.png", "Logo");
        logoImage.setWidth("384px");
        logoImage.setHeight("96px");

        Tabs tabs = new Tabs(
                new Tab(new HorizontalLayout(new Icon(VaadinIcon.HOME), new Span("Home"))),
                new Tab(new HorizontalLayout(new Icon(VaadinIcon.USERS), new Span("List of cadets"))),
                new Tab(new HorizontalLayout(new Icon(VaadinIcon.ENTER_ARROW), new Span("Attendings")))
        );

        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addSelectedChangeListener(event -> {
            switch (tabs.getSelectedIndex()){
                case 0:
                    setContent(new HomePage());
                    break;

                case 1:
                    setContent(new ListOfCadetsPage());
                    break;

                case 2:
                    setContent(new AttendingsPage());
                    break;
            }
        });


        logout = new Button("Logout");
        logout.setIcon(new Icon(VaadinIcon.EXIT));
        logout.addClickListener(e ->{
            loginService.logout();
            UI.getCurrent().navigate("login");
            UiUtils.showNotification("Goodbye :)", NotificationVariant.LUMO_CONTRAST);
        });


        HorizontalLayout leftSide = new HorizontalLayout(logoImage,  new H4("adminPanel"));
        leftSide.setWidthFull();
        leftSide.setSpacing(false);
        leftSide.setAlignItems(FlexComponent.Alignment.CENTER);
        leftSide.setJustifyContentMode(FlexComponent.JustifyContentMode.START);

        HorizontalLayout rightSide = new HorizontalLayout(logout);
        rightSide.setWidthFull();
        rightSide.setPadding(true);
        rightSide.setAlignItems(FlexComponent.Alignment.CENTER);
        rightSide.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        HorizontalLayout navbar = new HorizontalLayout(leftSide, rightSide);
        navbar.setWidthFull();
        navbar.setJustifyContentMode(FlexComponent.JustifyContentMode.EVENLY);

        addToNavbar(true, navbar);

        VerticalLayout drawer = new VerticalLayout(tabs);
        drawer.setHeightFull();
        drawer.setAlignItems(FlexComponent.Alignment.CENTER);

        addToDrawer(drawer);

        setContent(new HomePage());
    }


    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        String localSession = loginService.checkCookies();
        if (localSession != null){
            Session session = loginService.checkSession(localSession);
            if (session == null){
                beforeEnterEvent.forwardTo("login");
            }else if (!session.getAccess().equals(Access.Admin)){
                beforeEnterEvent.forwardTo("scanner");
            }
        }else {
            beforeEnterEvent.forwardTo("login");
        }

    }


    private class HomePage extends VerticalLayout {
        HomePage() {
            setSizeFull();
            Chart chart = new Chart(ChartType.PIE);
            Configuration conf = chart.getConfiguration();
            conf.setTitle("Cadet`s presence");
            conf.setTooltip(new Tooltip());
            DataSeries series = new DataSeries("DataSeries");
            series.add(new DataSeriesItem("Inside ", attendingService.getInside()));
            series.add(new DataSeriesItem("Outside", attendingService.getOutside()));
            conf.setExporting(true);
            chart.setSizeFull();
            conf.addSeries(series);
            PlotOptionsPie plotOptionsPie = new PlotOptionsPie();
            plotOptionsPie.setAllowPointSelect(true);
            add(chart);
        }
    }
    private class ListOfCadetsPage extends VerticalLayout {
        private TextField filterText = new TextField();
        private Button addButton = new Button("Add cadet", new Icon(VaadinIcon.USER));
        private Button refreshButton = new Button("Refresh", new Icon(VaadinIcon.REFRESH));

        private Grid<Cadet> grid;
        private GridContextMenu<Cadet> contextMenu;
        ListOfCadetsPage() {
            setPadding(true);
            filterText.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
            filterText.setPlaceholder("Search by anything..");
            filterText.setClearButtonVisible(true);
            filterText.setValueChangeMode(ValueChangeMode.EAGER);
            filterText.addValueChangeListener(e -> grid.setItems(cadetService.findAll(filterText.getValue())));
            filterText.setWidth("600px");
            addButton.addClickListener(e -> {
                grid.asSingleSelect().clear();
                AddCadetDialog dialog = new AddCadetDialog(cadetService, grid);
                dialog.open();
            });

            refreshButton.addClickListener(e -> {
                grid.setItems(cadetService.findAll());
                UiUtils.showNotification("Refreshed!", NotificationVariant.LUMO_PRIMARY);
            });

            grid = new Grid<>(Cadet.class);
            grid.setWidthFull();
            grid.setHeightByRows(true);
            grid.setColumns("rank", "surname", "name", "group", "phone");

            grid.addColumn(
                    new LocalDateRenderer<>(Cadet::getBirthday, "dd.MM.yyyy")
            ).setHeader("Birthday");

            grid.addColumn(new ComponentRenderer<>(cadet -> {
                if (cadet.isPresent()){
                    Icon insideIcon = new Icon(VaadinIcon.HOME_O);
                    insideIcon.setColor("#4BB543");
                    return insideIcon;
                }
                else{
                    Icon outsideIcon =new Icon(VaadinIcon.OUT);
                    outsideIcon.setColor("#FF4238");
                    return outsideIcon;
                }
            })).setHeader("Status");



            grid.getColumns().forEach(cadetColumn -> {
                cadetColumn.setTextAlign(ColumnTextAlign.CENTER);
                cadetColumn.setAutoWidth(true);
            });

            grid.setItems(cadetService.findAll());

            contextMenu = new GridContextMenu<>(grid);
            contextMenu.addItem("Edit", e ->{
                EditCadetDialog dialog = new EditCadetDialog(e.getItem().get(), cadetService, grid);
                dialog.open();
            });
            contextMenu.addItem("Delete", e -> {
                cadetService.delete(e.getItem().get());
                try {
                    faceService.removeFace(e.getItem().get().getFaceToken());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                grid.setItems(cadetService.findAll());
                UiUtils.showNotification("Successfully deleted!", NotificationVariant.LUMO_SUCCESS);
            });

            contextMenu.addItem("Info", e -> {
                HttpService httpService = HttpService.getInstance();
                try {
                    httpService.getFaceSetDetailsRequest("myFaceSet");
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            });

            HorizontalLayout buttonsLayout = new HorizontalLayout(filterText, addButton, refreshButton);
            buttonsLayout.setAlignItems(Alignment.BASELINE);
            buttonsLayout.setJustifyContentMode(JustifyContentMode.CENTER);
            buttonsLayout.setWidthFull();

            HorizontalLayout gridLayout = new HorizontalLayout(grid);
            gridLayout.setWidthFull();

            add(buttonsLayout, gridLayout);
        }
    }
    private class AttendingsPage extends VerticalLayout {
        private Grid grid;
        private TextField filterText = new TextField();
        private Button refreshButton = new Button("Refresh", new Icon(VaadinIcon.REFRESH));

        AttendingsPage() {
            setPadding(true);

            filterText.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
            filterText.setPlaceholder("Search by anything..");
            filterText.setClearButtonVisible(true);
            filterText.setValueChangeMode(ValueChangeMode.EAGER);
            filterText.addValueChangeListener(e -> grid.setItems(attendingService.findAll(filterText.getValue())));
            filterText.setWidth("600px");
            refreshButton.addClickListener(e -> {
                grid.setItems(attendingService.findAll());
                UiUtils.showNotification("Refreshed!", NotificationVariant.LUMO_PRIMARY);
            });

            HorizontalLayout buttonsLayout = new HorizontalLayout(filterText, refreshButton);
            buttonsLayout.setAlignItems(Alignment.BASELINE);
            buttonsLayout.setJustifyContentMode(JustifyContentMode.CENTER);
            buttonsLayout.setWidthFull();


            grid = new Grid<>(Attending.class);
            grid.setWidthFull();
            grid.setHeightByRows(true);
            grid.setColumns("cadet", "departure", "arrival");
            grid.setItems(attendingService.findAll());


            add(buttonsLayout, grid);
        }
    }

    private class AddCadetDialog extends Dialog {
        private FormLayout layout;
        private TextField surname = new TextField("Surname");
        private TextField name = new TextField("Name");
        private ComboBox<Ranks> rank = new ComboBox<>("Rank");
        private DatePicker birthday = new DatePicker("Birthday");
        private NumberField group = new NumberField("Group");
        private TextField phone = new TextField("Phone number");
        private Checkbox present = new Checkbox("In institute");
        private MemoryBuffer memoryBuffer = new MemoryBuffer();
        private Upload upload = new Upload(memoryBuffer);
        private Button scan = new Button("Add Face", new Icon(VaadinIcon.CAMERA));

        private Button add = new Button("Add");
        private Button cancel = new Button("Cancel");
        private Binder<Cadet> binder = new Binder<>(Cadet.class);

        AddCadetDialog(CadetService cadetService, Grid<Cadet> gridToResfresh) {
            surname.setRequired(true);
            name.setRequired(true);
            rank.setRequired(true);
            group.setRequiredIndicatorVisible(true);
            present.setReadOnly(true);
            rank.setItems(Ranks.values());
            rank.setAllowCustomValue(false);
            add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            upload.setAcceptedFileTypes(".png", ".jpeg", ".jpg");
            upload.setUploadButton(scan);
            upload.setWidthFull();
            upload.addSucceededListener(e ->{
                InputStream inputStream = memoryBuffer.getInputStream();
                try {
                    Files.copy(inputStream, Paths.get(file.getPath()), StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            });

            layout = new FormLayout();
            layout.setResponsiveSteps(new FormLayout.ResponsiveStep("500px", 2));
            layout.setWidth("500px");

            HorizontalLayout buttons = new HorizontalLayout(add, cancel);
            buttons.setWidthFull();
            buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            layout.add(surname, name, rank, group, birthday, phone);
            HorizontalLayout header = new HorizontalLayout(new Icon(VaadinIcon.USER), new H2("Adding cadet"));
            header.setAlignItems(FlexComponent.Alignment.BASELINE);

            HorizontalLayout uploadLayout = new HorizontalLayout(upload);
            uploadLayout.setWidthFull();
            uploadLayout.setPadding(true);
            add(header, layout, uploadLayout, new Hr(), buttons);

            binder.setBean(new Cadet());
            binder.forField(group)
                    .withConverter(Double::intValue, Integer::doubleValue, "Enter a number")
                    .bind(Cadet::getGroup, Cadet::setGroup);
            binder.bindInstanceFields(this);

            add.addClickListener(e -> {
                try {
                    Cadet bean = binder.getBean();
                    bean.setId(ObjectId.get());
                    faceService.addFace(file, bean);
                    cadetService.save(bean);

                    UiUtils.showNotification("Successfully added!", NotificationVariant.LUMO_SUCCESS);
                    if (gridToResfresh != null)
                        gridToResfresh.setItems(cadetService.findAll());

                    close();
                }catch (MongoWriteException error){
                    UiUtils.showNotification(error.getError().getMessage(), NotificationVariant.LUMO_ERROR);

                } catch (Exception e1) {
                    UiUtils.showNotification(e1.getMessage(), NotificationVariant.LUMO_ERROR);
                }
            });

            cancel.addClickListener(e -> {
                close();
            });
        }
    }
    private class EditCadetDialog extends Dialog {
        private FormLayout layout;
        private TextField surname = new TextField("Surname");
        private TextField name = new TextField("Name");
        private ComboBox<Ranks> rank = new ComboBox<>("Rank");
        private DatePicker birthday = new DatePicker("Birthday");
        private NumberField group = new NumberField("Group");
        private TextField phone = new TextField("Phone number");
        private Checkbox present = new Checkbox("In institute");

        private Button edit = new Button("Edit");
        private Button cancel = new Button("Cancel");
        private Binder<Cadet> binder = new Binder<>(Cadet.class);

        EditCadetDialog(Cadet cadet, CadetService cadetService, Grid<Cadet> gridToRefresh) {
            surname.setRequired(true);
            name.setRequired(true);
            rank.setRequired(true);
            group.setRequiredIndicatorVisible(true);
            present.setReadOnly(true);
            rank.setItems(Ranks.values());
            edit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            layout = new FormLayout();
            layout.setResponsiveSteps(new FormLayout.ResponsiveStep("500px", 2));
            layout.setWidth("500px");

            HorizontalLayout buttons = new HorizontalLayout(edit, cancel);
            buttons.setWidthFull();
            buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            layout.add(surname, name, rank, group, birthday, phone);
            HorizontalLayout header = new HorizontalLayout(new Icon(VaadinIcon.EDIT), new H2("Editing cadet`s info"));
            header.setAlignItems(FlexComponent.Alignment.BASELINE);
            add(header, layout, new Hr(), buttons);

            binder.setBean(cadet);
            binder.forField(group)
                    .withConverter(Double::intValue, Integer::doubleValue, "Enter a number")
                    .bind(Cadet::getGroup, Cadet::setGroup);
            binder.bindInstanceFields(this);

            edit.addClickListener(e -> {
                try {
                    cadetService.update(binder.getBean());
                    UiUtils.showNotification("Successfully edited!", NotificationVariant.LUMO_SUCCESS);
                    if (gridToRefresh != null)
                        gridToRefresh.getDataProvider().refreshItem(binder.getBean());

                    close();
                }catch (MongoWriteException error){
                    UiUtils.showNotification(error.getError().getMessage(), NotificationVariant.LUMO_ERROR);
                }

            });

            cancel.addClickListener(e -> {
                close();
            });
        }
    }


}
