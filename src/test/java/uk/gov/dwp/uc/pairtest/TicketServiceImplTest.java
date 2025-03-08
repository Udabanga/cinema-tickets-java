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
        assertThrows(InvalidPurchaseException.class, ()-> ticketService.purchaseTickets(invalidAccountId, adultTicket));
    }

    @Test
    public void testInvalidNegativeAccountId_shouldThrowException(){
        Long invalidAccountId = -1L;
        TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, ()-> ticketService.purchaseTickets(invalidAccountId, adultTicket));
        assertEquals("Invalid account ID", exception.getMessage());
    }

    @Test
    public void testNullTicketRequest_shouldThrowException(){
        Long invalidAccountId = 0L;
        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, ()-> ticketService.purchaseTickets(invalidAccountId, (TicketTypeRequest) null));
        assertEquals("No ticket requested",exception.getMessage());
    }
}