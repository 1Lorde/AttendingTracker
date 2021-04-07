# AttendingTracker

**AttendingTracker**  - visitors check-in application , that consists of two part: admin management panel and face scanner view.  In this case,  cadets considering as a visitors, which must pass face recognition check at the military bases checkpoint. This app written in Java using Vaadin framework for frontend, MongoDB database for storing data and FacePlusPlus API for face recognition.

![Screenshot](https://github.com/1Lorde/AttendingTracker/blob/dev/demo_pictures/add_cadet.png?raw=true)
## Build

### Prerequisites

1.  OpenJDK 11.
2.  Vaadin 14.
3.  MongoDB Compass.
4.  Maven.
5.  Intellij IDEA.

### Installation

1.  Clone repository.
2.  Open project via Intellij IDEA.
3.  Wait until Maven download and install all dependencies from *pom.xml* .
4.  Navigate to *Maven --> Plugins --> jetty* and run *jetty:run* command.

**Admin's credentials**
![Login page](https://github.com/1Lorde/AttendingTracker/blob/dev/demo_pictures/admin_login.png?raw=true)

**Scanner's credentials**
![Login page](https://github.com/1Lorde/AttendingTracker/blob/dev/demo_pictures/scanner_login.png?raw=true)
