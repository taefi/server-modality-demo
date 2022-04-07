package com.vaadin.demo.servermodailty.views;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@PageTitle("Dialog Modality Show-case")
@Route(value = "dialog-modality-show-case", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class ServerModalDialogView extends VerticalLayout {

    private Dialog modelessDialog;
    private Dialog clientSideModalDialog;
    private Dialog serverSideModalDialog;

    public ServerModalDialogView() {

        add(new H2("This is a simple show-case for Server-Modality feature!"));
        add(new Span("There are three dialogs in this view to show the difference between different types of " +
                "modality:"));
        UnorderedList types = new UnorderedList();
        types.add(new ListItem("Complete Modeless (No Modality)"));
        types.add(new ListItem("Client-side Modal (Shows a Modality Curtain)"));
        types.add(new ListItem("Server-side Modal (Does not show Modality Curtain)"));
        add(types);

        add(new Html("<span>There is a <b>Show Notification</b> button at the bottom of this page that you can " +
                "click on or use the <b><kbd>Ctrl + Alt + S</kbd></b> to show a <b>Notification !!!</b> message. " +
                "This is only to simulate a server-side action that can be blocked via modality.</span>"));

        addModelessDialogSection();

        addClientsideModalDialogSection();

        addServersideModalDialogSection();

        addShowNotificationButton();
    }

    private void addModelessDialogSection() {
        add(new H3("Complete Modeless (No Modality):"));
        add(new Html("<span>By pressing the 'Open Modeless Dialog' a dialog with no modality opens that blocks no " +
                "server-side actions. While it is open, you can try and see that clicking on the Show Notification " +
                "button is working normally. The <kbd>Ctrl + Alt + S</kbd> keyboard shortcut should also work as " +
                "expected.</span>"));
        initModelessDialog();
        add(createOpenModelessDialogButton());
    }

    private void initModelessDialog() {
        modelessDialog = new Dialog();
        modelessDialog.setModal(false); // This makes it client-side modal -> client-side modality curtain appears
        modelessDialog.setCloseOnOutsideClick(false); // Still will be closed via pressing Esc
        modelessDialog.add(new H1("A Modeless Dialog!"));
        HorizontalLayout btnLayout = new HorizontalLayout();
        btnLayout.setJustifyContentMode(JustifyContentMode.END);
        btnLayout.add(new Button("Close", e -> modelessDialog.close()));
        modelessDialog.add(btnLayout);
    }

    private Button createOpenModelessDialogButton() {
        Button openServerSideModalDialog = new Button("Open Modeless Dialog",
                e -> modelessDialog.open());
        openServerSideModalDialog.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return openServerSideModalDialog;
    }

    private void addClientsideModalDialogSection() {
        add(new H3("Client-side Modal (Shows a Modality Curtain):"));
        add(new Html("<span>By pressing the 'Open Client-side Modal Dialog' a dialog opens that blocks " +
                "user-interactions with components and controls on the underlying view. While it is open, you cannot " +
                "click on the Show Notification button nor using the <kbd>Ctrl + Alt + S</kbd> keyboard shortcut to " +
                "initiate server interactions.</span>"));
        initClientSideModalDialog();
        add(createOpenClientsideModalDialogButton());
    }

    private void initClientSideModalDialog() {
        clientSideModalDialog = new Dialog();
        clientSideModalDialog.setModal(true); // This makes it client-side modal -> client-side modality curtain appears
        clientSideModalDialog.setCloseOnOutsideClick(false); // Still will be closed via pressing Esc
        clientSideModalDialog.add(new H1("A Client-side Modal Dialog!"));
        HorizontalLayout btnLayout = new HorizontalLayout();
        btnLayout.setJustifyContentMode(JustifyContentMode.END);
        btnLayout.add(new Button("Close", e -> clientSideModalDialog.close()));
        clientSideModalDialog.add(btnLayout);
    }

    private Button createOpenClientsideModalDialogButton() {
        Button openServerSideModalDialog = new Button("Open Client-side Modal Dialog",
                e -> clientSideModalDialog.open());
        openServerSideModalDialog.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return openServerSideModalDialog;
    }

    private void addServersideModalDialogSection() {
        add(new H3("Server-side Modal (Does not show Modality Curtain):"));
        add(new Html("<span>By pressing the 'Open Server-side Modal Dialog' a dialog with no client-side modality " +
                "opens that blocks user interactions not using a modality curtain on the view, but via a so called " +
                "server-side modality curtain. This means that while this dialog is open, user can interact with " +
                "components and controls on the underlying page (e.g. to click on Show Notification button, or using " +
                "the <kbd>Ctrl + Alt + S</kbd> keyboard shortcut) but the server-side would reject to response and " +
                "update the UI as long as the current UI is under the protection of a virtual server-side modality " +
                "curtain.</span>"));
        initServerSideModalDialog();
        add(createOpenServersideModalDialogButton());
    }

    private void initServerSideModalDialog() {
        serverSideModalDialog = new Dialog();
        serverSideModalDialog.setModal(false); // This makes it client-side modeless -> no client-side modality curtain
        serverSideModalDialog.add(new H1("A Server-side Modal Dialog!"));
        HorizontalLayout btnLayout = new HorizontalLayout();
        btnLayout.setJustifyContentMode(JustifyContentMode.END);
        btnLayout.add(new Button("Close", e -> {
            serverSideModalDialog.close();
            // Remove the server-side modality curtain:
            getUI().ifPresent(ui -> ui.remove(serverSideModalDialog));
        }));
        serverSideModalDialog.add(btnLayout);
    }

    private Button createOpenServersideModalDialogButton() {
        Button openServerSideModalDialog = new Button("Open Server-side Modal Dialog", e -> {
            serverSideModalDialog.open();
            getUI().ifPresent(ui -> ui.addModal(serverSideModalDialog));
        });
        openServerSideModalDialog.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return openServerSideModalDialog;
    }

    private void addShowNotificationButton() {
        Button showNotification = new Button("Show Notification", e -> Notification.show("Notification !!!"));
        showNotification.addClickShortcut(Key.KEY_S, KeyModifier.CONTROL, KeyModifier.ALT);
        add(showNotification);
    }
}
