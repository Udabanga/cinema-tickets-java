package uk.gov.dwp.uc.pairtest.exception;

public class InvalidPurchaseException extends RuntimeException {
    public static final String INVALID_ACCOUNT_ID_MESSAGE = "Invalid account ID";
    public static final String NO_TICKET_REQUESTED_MESSAGE = "No ticket requested";
    public InvalidPurchaseException(String message){
        super(message);
    }
}
