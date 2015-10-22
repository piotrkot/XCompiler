# XCompiler

Preconditions :

* Use Java technologies.
* Use a dependency management tool of your choice (Maven, Gradle or SBT).
* Work on your machine.

Requirement :

Create architecture & design for client-server cloud application that compile Java applications. Your "Java Applications Compiler" should compose of a client to connect to the server providing source code as an input, and receive the compile output from server.

* All messages and errors from server and client must be logged into a log server.
* Client should be a simple web application with login/logout and upload files (in a common zip format) option for source code, and output area to show log errors and messages.
* Data transfer between server and client should be through web services.
* Server should unzip files before compiling it and reply with appropriate error message if files are corrupted.
* Server should run Maven test phase and return output if the upload application is Maven based.
* Your system should follow maturity level 3 of SOA.
* Develop the above requirements. Provide detailed steps for building, deploying and running your system on Windows and Linux.

Nonfunctional requirement:

* Your application should be very light.
* Response time should be less than 5 seconds.

To be evaluated :

* Architecture and design.
* Used technologies.
* Fulfillment of building, deploying and running your application.
* Database design.
* Functionalities and output.
* Code quality (style, documentation and performance).
