package uk.gov.dwp.uc.pairtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.junit.jupiter.api.Assertions.*;

class TicketServiceImplTest {

    private TicketServiceImpl ticketService;

    @BeforeEach
    void setUp() {
        ticketService = new TicketServiceImpl();
    }

    @Test
    public void testValidAccountId_shouldNotThrowException(){
        Long validAccountId=1L;
        TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

        assertDoesNotThrow(()-> ticketService.purchaseTickets(validAccountId, adultTicket));
    }

    @Test
    public void testInvalidAccountId_shouldThrowException(){
        Long invalidAccountId = 0L;
        TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, ()-> ticketService.purchaseTickets(invalidAccountId, adultTicket));
        assertEquals(InvalidPurchaseException.INVALID_ACCOUNT_ID_MESSAGE, exception.getMessage());
    }

    @Test
    public void testInvalidNegativeAccountId_shouldThrowException(){
        Long invalidAccountId = -1L;
        TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, ()-> ticketService.purchaseTickets(invalidAccountId, adultTicket));
        assertEquals(InvalidPurchaseException.INVALID_ACCOUNT_ID_MESSAGE, exception.getMessage());
    }

    @Test
    public void testNullTicketRequest_shouldThrowException(){
        Long invalidAccountId = 0L;

        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, ()-> ticketService.purchaseTickets(invalidAccountId, (TicketTypeRequest) null));
        assertEquals(InvalidPurchaseException.NO_TICKET_REQUESTED_MESSAGE,exception.getMessage());
    }

    //Count Test
    @Test
    public void testTicketsOverMax_shouldThrowException(){
        Long validAccountId=1L;
        TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 26);
        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, ()-> ticketService.purchaseTickets(validAccountId, adultTicket));

        assertEquals(InvalidPurchaseException.EXCEEDS_MAX_TICKETS_MESSAGE,exception.getMessage());
    }

    @Test
    public void testTicketMissingAdultWithChildInfant(){
        Long validAccountId=1L;
        TicketTypeRequest infantTicket = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);
        TicketTypeRequest childTicket = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);

        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, ()-> ticketService.purchaseTickets(validAccountId, infantTicket, childTicket));
        assertEquals(InvalidPurchaseException.MISSING_ADULT_TO_BUY_CHILD_INFANT_TICKETS_MESSAGE,exception.getMessage());
    }

    @Test
    public void testTicketsWithInfantMissingAccompanyingAdult(){
        Long validAccountId=1L;

        TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest infantTicket = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2);

        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, ()-> ticketService.purchaseTickets(validAccountId, adultTicket, infantTicket));
        assertEquals(InvalidPurchaseException.ADULT_NOT_PRESENT_WITH_INFANT_MESSAGE,exception.getMessage());
    }
}