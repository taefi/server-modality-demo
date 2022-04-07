package com.vaadin.demo.servermodailty.views;

import com.vaadin.demo.servermodailty.data.entity.SamplePerson;
import com.vaadin.demo.servermodailty.data.service.SamplePersonService;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.function.SerializableRunnable;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import javax.swing.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

@PageTitle("Modality on Top of a Modal Dialog")
@Route(value = "dialog-on-dialog", layout = MainLayout.class)
public class ModalityOnModalDialogView extends VerticalLayout {

    private final Grid<SamplePerson> grid = new Grid<>(SamplePerson.class, false);

    private final SamplePersonService samplePersonService;

    private Dialog gridDialog;

    private ProgressBar progress;

    @Autowired
    public ModalityOnModalDialogView(SamplePersonService samplePersonService) {
        this.samplePersonService = samplePersonService;

        Button openProcessDialog = new Button("Open Grid Dialog", e -> {
            gridDialog.open();
            getUI().ifPresent(ui -> ui.addModal(gridDialog));
        });
        openProcessDialog.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button showNotification = new Button("Show Notification!", e -> Notification.show("Notification!!!"));
        showNotification.addClickShortcut(Key.KEY_S, KeyModifier.CONTROL, KeyModifier.ALT);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        buttonLayout.add(openProcessDialog, showNotification);

        add(buttonLayout);

        initGridDialog();
    }

    private void initGridDialog() {
        gridDialog = new Dialog();
        gridDialog.setModal(false);
        gridDialog.add(new H2("Interaction with main page is disabled while this dialog is opened!"));
        gridDialog.add(new Html("<span>" +
                "When you start loading the data, a progress-bar would show up which is set as server-modal on top " +
                "of this dialog <br>which prevents interaction with this dialog as well e.g. closing it. <br> "));

        Button loadButton = new Button("Load Data!", e -> populateData());
        loadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        gridDialog.add(loadButton);

        progress = new ProgressBar();
        progress.setIndeterminate(true);
        progress.setVisible(false);
        gridDialog.add(progress);

        configureGrid();
        gridDialog.add(grid);

        HorizontalLayout btnLayout = new HorizontalLayout();
        btnLayout.setJustifyContentMode(JustifyContentMode.END);
        btnLayout.add(new Button("Close", e -> {
            gridDialog.close();
            // Remove the server-side modality curtain:
            getUI().ifPresent(ui -> ui.remove(gridDialog));
        }));
        gridDialog.add(btnLayout);
    }

    private void configureGrid() {
        grid.addColumn("firstName").setAutoWidth(true);
        grid.addColumn("lastName").setAutoWidth(true);
        grid.addColumn("email").setAutoWidth(true);
        grid.addColumn("phone").setAutoWidth(true);
        grid.addColumn("dateOfBirth").setAutoWidth(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.getDataCommunicator().enablePushUpdates(Executors.newCachedThreadPool());
    }

    private void populateData() {
        openServersideModalDialog();

        grid.setItems(query -> samplePersonService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());

        removeServersideModalityAfterDataPopulated();
    }

    private void openServersideModalDialog() {
        progress.setVisible(true);
        // This is setting the progress bar as the new server modal on top of gridDialog:
        getUI().ifPresent(ui -> ui.setChildComponentModal(progress, true));
    }

    private void removeServersideModalityAfterDataPopulated() {
        UI ui = getUI().get();
        SerializableRunnable removeModalTask = ui.accessLater(() -> {
                    progress.setVisible(false);
                    // removes the server modality of progress bar, now gridDialog is the server modal component again:
                    ui.setChildComponentModal(progress, false);
            }
            , () -> {});

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
