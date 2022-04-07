package com.vaadin.demo.servermodailty.views;

import com.vaadin.demo.servermodailty.data.entity.SamplePerson;
import com.vaadin.demo.servermodailty.data.service.SamplePersonService;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.function.SerializableRunnable;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import javax.swing.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

@PageTitle("Grid With Large Dataset")
@Route(value = "grid-with-large-dataset", layout = MainLayout.class)
@Uses(Icon.class)
public class LargeDataGridView extends VerticalLayout {

    private final Grid<SamplePerson> grid = new Grid<>(SamplePerson.class, false);

    private final SamplePersonService samplePersonService;

    private final Dialog serverModalDialog;

    @Autowired
    public LargeDataGridView(SamplePersonService samplePersonService) {
        this.samplePersonService = samplePersonService;
        addClassNames("grid-with-large-dataset");


        Button loadButton = new Button("Populate Data!", e -> populateData());
        loadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button showNotification = new Button("Show Notification!", e -> Notification.show("Notification!!!"));
        showNotification.addClickShortcut(Key.KEY_S, KeyModifier.CONTROL, KeyModifier.ALT);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        buttonLayout.add(loadButton, showNotification);

        add(buttonLayout);

        // Configure Grid
        grid.addColumn("firstName").setAutoWidth(true);
        grid.addColumn("lastName").setAutoWidth(true);
        grid.addColumn("email").setAutoWidth(true);
        grid.addColumn("phone").setAutoWidth(true);
        grid.addColumn("dateOfBirth").setAutoWidth(true);
        grid.addColumn("occupation").setAutoWidth(true);
        LitRenderer<SamplePerson> importantRenderer = LitRenderer.<SamplePerson>of(
                        "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
                .withProperty("icon", important -> important.isImportant() ? "check" : "minus").withProperty("color",
                        important -> important.isImportant()
                                ? "var(--lumo-primary-text-color)"
                                : "var(--lumo-disabled-text-color)");

        grid.addColumn(importantRenderer).setHeader("Important").setAutoWidth(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        grid.getDataCommunicator().enablePushUpdates(Executors.newCachedThreadPool());

        add(grid);

        serverModalDialog = new Dialog();
        serverModalDialog.setModal(false);
        serverModalDialog.add(new H2("You cannot interact with UI until data is loaded!"));
        serverModalDialog.add(new H4("This is not because of a blocking long running task!"));
        serverModalDialog.add(new Html("<span>" +
                "As the result of Server-side modality: <br> <ul><li>You can navigate to other menus (Navigation is " +
                "not blocked), but</li><li>You cannot interact with the controls and components on the page</li></ul> " +
                "</span>"));
    }

    private void populateData() {
        openServersideModalDialog();

        grid.setItems(query -> samplePersonService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());

        removeServersideModalityAfterDataPopulated();
    }

    private void openServersideModalDialog() {
        serverModalDialog.open();
        getUI().ifPresent(ui -> ui.addModal(serverModalDialog));
    }

    private void removeServersideModalityAfterDataPopulated() {
        UI ui = getUI().get();
        SerializableRunnable removeModalTask = ui.accessLater(() -> ui.remove(serverModalDialog), () -> {});

        AtomicReference<Timer> timer = new AtomicReference<>();
        timer.set(new Timer(500, e -> {
            if (samplePersonService.count() < 10000) { // some arbitrary condition
                return;
            }
            timer.get().stop();
            removeModalTask.run();
        }));
        timer.get().start();
    }
}
