package uk.gov.dwp.uc.pairtest.exception;

import uk.gov.dwp.uc.pairtest.TicketServiceImpl;

public class InvalidPurchaseException extends RuntimeException {
    public static final String INVALID_ACCOUNT_ID_MESSAGE = "Invalid account ID";
    public static final String NO_TICKET_REQUESTED_MESSAGE = "No ticket requested";
    public static final String INVALID_TICKET_QUANTITY_MESSAGE = "Invalid Ticket Quantity";
    public static final String UNKNOWN_TICKET_TYPE_MESSAGE = "Unknown ticket type";
    public static final String EXCEEDS_MAX_TICKETS_MESSAGE = "Cannot purchase more than " + TicketServiceImpl.MAX_TICKETS + " tickets";
    public static final String MISSING_ADULT_TO_BUY_CHILD_INFANT_TICKETS_MESSAGE="Child/Infant tickets cannot be purchased without an Adult ticket";
    public static final String ADULT_NOT_PRESENT_WITH_INFANT_MESSAGE="An Infant must be accompanied by an Adult";
    public InvalidPurchaseException(String message){
        super(message);
    }
}
