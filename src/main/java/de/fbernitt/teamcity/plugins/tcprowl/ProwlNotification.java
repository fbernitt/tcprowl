package de.fbernitt.teamcity.plugins.tcprowl;

/**
 * Defines a prowl notification.
 */
public class ProwlNotification {

    private final String apiKey;
    private final String title;
    private final String message;

    public ProwlNotification(String apiKey, String title, String message) {
        this.apiKey = apiKey;
        this.title = title;
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProwlNotification that = (ProwlNotification) o;

        if (!this.apiKey.equals(that.apiKey)) return false;
        if (!this.message.equals(that.message)) return false;
        if (!this.title.equals(that.title)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = this.apiKey.hashCode();
        result = 31 * result + this.title.hashCode();
        result = 31 * result + this.message.hashCode();
        return result;
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public String getTitle() {
        return this.title;
    }

    public String getMessage() {
        return this.message;
    }
}
