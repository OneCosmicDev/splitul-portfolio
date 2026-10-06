package ca.ulaval.glo2003.api;

public class ErrorDTO {
    private final String error;
    private final String description;

    public ErrorDTO(String error, String description) {
        this.error = error;
        this.description = description;
    }

    public String getError() {
        return error;
    }

    public String getDescription() {
        return description;
    }
}
