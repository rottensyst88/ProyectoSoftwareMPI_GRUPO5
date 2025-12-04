# Project Overview

This is a simple Java Swing application for managing bank clients, accounts, and contracts. The application provides a graphical user interface (GUI) for performing various banking operations.

## Architecture

The project follows a simple Model-View-Controller (MVC) like architecture:

*   **Model:** The `modelo` package contains the data classes for the application, such as `Cliente`, `Cuenta`, and `Contrato`.
*   **View:** The `vista` package contains the Swing GUI classes that make up the user interface.
*   **Controller:** The `controlador` package contains the `ControladorSistema` class, which handles the application's business logic and acts as a bridge between the model and the view.

Data persistence is handled by the `persistencia` package, which uses Java serialization to save the application's state to a file named `SVPObjetos.obj`.

## Building and Running

This project does not use a standard build tool like Maven or Gradle. To build and run the application, you will need to compile the Java source files and then run the `VistaLoader` class.

**1. Compile the code:**

Open a terminal in the project's root directory and run the following command. This will compile all the `.java` files and place the compiled `.class` files in the `out/production/ProyectoSoftwareMPI` directory.

```bash
javac -d out/production/ProyectoSoftwareMPI/ $(find src -name "*.java")
```

**2. Run the application:**

After compiling the code, you can run the application with the following command. This command will execute the `main` method in the `VistaLoader` class, which will start the application's GUI.

```bash
java -cp out/production/ProyectoSoftwareMPI/ VistaLoader
```

## Development Conventions

*   **GUI:** The application uses Java Swing for its graphical user interface.
*   **IDE:** The project appears to be developed using JetBrains IntelliJ IDEA, as indicated by the `.idea` directory and the `.iml` file.
*   **Persistence:** The application uses Java serialization for data persistence. The serialized data is stored in the `SVPObjetos.obj` file.
*   **Error Handling:** Custom exceptions are defined in the `excepcion` package and are used to handle application-specific errors.
