## 1. Project Title - UPRM Parking Management System

## 2. Description  

It's a Java Swing desktop application for managing a university parking lot. It allows users to create reservations, cancel them, check available spaces, review the transaction history, and undo the last action performed.

The main model uses data structures to organize the system information:

- LinkedHashSet for the available spaces.
- HashMap to associate cars with their reservations.
- LinkedList for the history and the waiting list.
- Stack to record actions and allow undoing the last operation.

## 3. Installation

Tip: 
  - Download "Better Comments" extension for better experience while reading the code

1. Make sure the src directory is marked as the source code root.
2. Run the main class main.estacionamiento.app.ventanaPrincipal.

No additional configuration or external dependencies are required.

## 4. Usage

When starting the application, the main window opens with these options:

- Make reservation.
- Cancel or change reservation.
- Show available spaces.
- Show transactions.
- Undo last action.
- Exit.

- Debug button (tarzan): button to fill the application with DUMMY DATA.

### General Flow

1. Open the application from the main class.
2. Select the action you want to perform.
3. Complete the requested information in the corresponding secondary window.
4. Review the result in the history, availability, or action list.


## 6. Members

- Gian M. Miranda Roman         | gian.miranda1@upr.edu
- Edwin Y. Almodovar Rivera     | edwin.almodovar3@upr.edu

## Project Structure

/src/main/estacionamiento/        - Main application package containing:

- *app/*                        - User interface components
  - ventanaPrincipal.java         - Main application window
  - ventanas_segundarias/         - Secondary windows for various operations:
    - Reservacion.java            - Parking reservation window
    - Cancelacion_Cambiar.java    - Reservation cancellation and changing window
    - Disponibilidad.java         - Available spaces display
    - ListaEspera.java            - Waitlist management window
    - Transacciones.java          - Transaction history window
    - Deshacer.java               - Undo recent operations window


- *models/*                     - Hold Main classes
  - auto/                         - Vehicle class
  - estudiante/                   - Student profile class
  - espacio/                      - Parking space class
  - estacionamiento/              - Main parking lot management class
  - servicios_especiales/         - Special services class
  - transaccion/                  - Transaction Class

- *utils/*                      - Utility components
  - LogoUtils.java                - Logo loading and scaling helpers
  - RoundedButton.java            - Styled button component
  - RoundedPanel.java             - Styled panel component
  - UiColors.java                 - Color scheme definitions
  - UiFonts.java                  - Font definitions
  - UiSizes.java                  - Size/dimension constants
  - UiWindowUtils.java            - Shared window and header styling helpers