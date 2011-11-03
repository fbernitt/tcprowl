package de.fbernitt.teamcity.plugins.tcprowl.prowl.api;

import java.util.Date;

/**
 * The prowl result as defined by the Prowl API
 * <p>
 * http://www.prowlapp.com/api.php
 */
public class ProwlResult {

    private final int remaining;
    private final Date resetDate;

    public ProwlResult(int remaining, Date resetDate) {
        this.remaining = remaining;
        this.resetDate = resetDate;
    }


    /**
     * Returns the reset date. On reset date the number of allowed API calls gets reset.
     *
     * @return The reset date
     */
    public Date getResetDate() {
        return resetDate;
    }

    /**
     * Returns the remaining number of allowed API calls until reset date.
     *
     * @return remaining number of API calls.
     */
    public int getRemaining() {
        return remaining;
    }
}
