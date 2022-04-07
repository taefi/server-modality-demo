# Server Modality Demo

This project is about showing how server-modality feature of Vaadin 23+ works. It contains 3 views which shows some 
possibilities of using server-modality:
* **Dialog Modality Show-case**: Basic usage and comparison between Modeless vs. Client Modality vs. Server Modality. 
* **Grid With Large Dataset**: Shows a server-modal dialog on the view while a long-running process is in progress.
* **Modality on Top of Dialog**: Shows how to use `UI#addToModalComponent` to add a modal component to the *current* 
modal component.


## Running the application

The project is a standard Maven project. To run it from the command line,
type `mvnw` (Windows), or `./mvnw` (Mac & Linux), then open
http://localhost:8080 in your browser.

You can also import the project to your IDE of choice as you would with any
Maven project. Read more on [how to import Vaadin projects to different 
IDEs](https://vaadin.com/docs/latest/flow/guide/step-by-step/importing) (Eclipse, IntelliJ IDEA, NetBeans, and VS Code).

