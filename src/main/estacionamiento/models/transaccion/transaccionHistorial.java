package main.estacionamiento.models.transaccion;

import java.time.LocalDate;
import java.util.LinkedList;
import java.time.DayOfWeek;

//! There are no failsafes and has not been tested yet
public class transaccionHistorial {
    private LinkedList<transaccion> historial = new LinkedList<>();
    private transaccion lastTransaction;

    public void addTransaccion(transaccion newTransaccion) {
        this.historial.add(newTransaccion);
    }

    public void removerTransaccion(transaccion toDeleteTransaccion) {
        this.historial.remove(toDeleteTransaccion);
    }

    public LinkedList<transaccion> filterByStudentId(String studentId) {
        if (studentId == null) {
            throw new IllegalArgumentException("Student ID cannot be null.");
        }
        LinkedList<transaccion> filtered = new LinkedList<>();
        for (transaccion toCheck : historial) {
            if (toCheck.getEstudiante().getId_estudiante().equals(studentId)) {
                filtered.add(toCheck);
            }
        }
        return filtered;
    }

    public void undoTransaction() {
        // Deny undoing an empty history
        if (this.historial.isEmpty()) {
            throw new IllegalStateException("Ninguna transaccion para deshacer.");
        }
        this.lastTransaction = this.historial.removeLast();
    }

    public void redoTransaction() {
        // Deny redoing history if no undo occurred
        if (this.lastTransaction == null) {
            throw new IllegalStateException("Ninguna transaccion para deshacer.");
        }
        this.historial.addLast(this.lastTransaction);
        this.lastTransaction = null;

    }

    public LinkedList<transaccion> filterByThisWeek() {
        LocalDate today = LocalDate.now();

        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(7);

        return filterByDateRange(startOfWeek, endOfWeek);
    }

    public LinkedList<transaccion> filterByDate(LocalDate date) {
        LinkedList<transaccion> filtered = new LinkedList<>();

        for (transaccion toCheck : this.historial) {
            if (toCheck.getFechaDeTransaccion().toLocalDate().equals(date)) {
                filtered.add(toCheck);
            }
        }

        return filtered;
    }

    public LinkedList<transaccion> filterByDateRange(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start and end dates cannot be null.");
        }

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("start must be before or equal to end.");
        }

        LinkedList<transaccion> filtered = new LinkedList<>();

        for (transaccion toCheck : this.historial) {
            LocalDate fecha = toCheck.getFechaReservado();

            if (!fecha.isBefore(start) && fecha.isBefore(end)) {
                filtered.add(toCheck);
            }
        }

        return filtered;
    }

    public LinkedList<transaccion> filterMoreThanTwoHoursInDay(LocalDate day) {
        LinkedList<transaccion> filtered = new LinkedList<>();

        for (transaccion toCheck : this.historial) {
            LocalDate transactionDay = toCheck.getFechaDeTransaccion().toLocalDate();
            double hoursUsed = toCheck.getHoras();

            if (transactionDay.equals(day) && hoursUsed > 2) {
                filtered.add(toCheck);
            }
        }

        return filtered;
    }

    public LinkedList<transaccion> filterByPrice(float price) {
        LinkedList<transaccion> filtered = new LinkedList<>();
        for (transaccion toCheck : this.historial) {
            float priceOf = toCheck.getCantidad();

            if (price == priceOf) {
                filtered.add(toCheck);
            }
        }
        return filtered;
    }

    public LinkedList<transaccion> filterByPriceRange(float startPrice, float endPrice) {
        if (startPrice > endPrice) {
            throw new IllegalArgumentException("startPrice must be less than or equal to endPrice.");
        }
        LinkedList<transaccion> filtered = new LinkedList<>();
        for (transaccion toCheck : this.historial) {
            float priceOf = toCheck.getCantidad();

            if (startPrice <= priceOf && priceOf <= endPrice) {
                filtered.add(toCheck);
            }
        }
        return filtered;
    }

    public LinkedList<transaccion> filterByHoursUsed(int hours) {
        if (hours < 0) {
            throw new IllegalArgumentException("Hours must not be negative.");
        }
        LinkedList<transaccion> filtered = new LinkedList<>();
        for (transaccion toCheck : this.historial) {
            int hoursOf = toCheck.getHoras();

            if (hours == hoursOf) {
                filtered.add(toCheck);
            }
        }
        return filtered;
    }

    public LinkedList<transaccion> filterByAllNextWeek() {
        return null;
    }

    public LinkedList<transaccion> filterByStudentName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("String can not be null.");
        }
        LinkedList<transaccion> filtered = new LinkedList<>();
        String target = name.trim().toLowerCase();
        for (transaccion toCheck : this.historial) {
            String studentName = toCheck.getEstudiante().getNombre();
            if (studentName != null && studentName.toLowerCase().contains(target)) {
                filtered.add(toCheck);
            }
        }
        return filtered;
    }

    public LinkedList<transaccion> filterMoreThanAmountofHours(int hours) {
        LinkedList<transaccion> filtered = new LinkedList<>();
        for (transaccion toCheck : this.historial) {
            if (toCheck.getHoras() > hours) {
                filtered.add(toCheck);
            }
        }
        return filtered;
    }

    public void clearHistory() {
        this.historial.clear();
    }

    public LinkedList<transaccion> getHistorial() {
        return this.historial;
    }
}
