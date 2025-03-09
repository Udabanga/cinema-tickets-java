package uk.gov.dwp.uc.pairtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.junit.jupiter.api.Assertions.*;

class TicketServiceImplTest {
    private TicketServiceImpl ticketService;
    private Long validAccountId;

    @BeforeEach
    void setUp() {
        TicketPaymentService ticketPaymentService = Mockito.mock(TicketPaymentService.class);
        SeatReservationService seatReservationService = Mockito.mock(SeatReservationService.class);
        ticketService = new TicketServiceImpl(ticketPaymentService, seatReservationService);
        validAccountId = 1L;
    }

    @Nested
    @DisplayName("Account ID Validation Tests")
    class AccountIdValidationTests{
        @Test
        @DisplayName("Valid account ID should not throw exception")
        public void testValidAccountId_shouldNotThrowException(){
            TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

            assertDoesNotThrow(()-> ticketService.purchaseTickets(validAccountId, adultTicket));
        }

        @Test
        @DisplayName("Zero account ID should throw exception")
        public void testInvalidAccountId_shouldThrowException(){
            Long invalidAccountId = 0L;
            TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

            InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, ()-> ticketService.purchaseTickets(invalidAccountId, adultTicket));
            assertEquals(InvalidPurchaseException.INVALID_ACCOUNT_ID_MESSAGE, exception.getMessage());
        }

        @Test
        @DisplayName("Negative account ID should throw exception")
        public void testInvalidNegativeAccountId_shouldThrowException(){
            Long invalidAccountId = -1L;
            TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

            InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, ()-> ticketService.purchaseTickets(invalidAccountId, adultTicket));
            assertEquals(InvalidPurchaseException.INVALID_ACCOUNT_ID_MESSAGE, exception.getMessage());
        }

    }

    @Nested
    @DisplayName("Ticket Request Validation Tests")
    class TicketRequestValidationTests {
        @Test
        @DisplayName("Null ticket request should throw exception")
        public void testNullTicketRequest_shouldThrowException() {
            InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(validAccountId, (TicketTypeRequest) null));
            assertEquals(InvalidPurchaseException.NO_TICKET_REQUESTED_MESSAGE, exception.getMessage());
        }

        @Test
        @DisplayName("No ticket request should throw exception")
        public void testNoTicketRequest_shouldThrowException() {
            Long validAccountId = 1L;

            InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(validAccountId));
            assertEquals(InvalidPurchaseException.NO_TICKET_REQUESTED_MESSAGE, exception.getMessage());
        }

        @Test
        @DisplayName("Negative ticket quantity should throw exception")
        public void testNegativeTicketRequest_shouldThrowException() {
            TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, -1);

            InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(validAccountId, adultTicket));
            assertEquals(InvalidPurchaseException.INVALID_TICKET_QUANTITY_MESSAGE, exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Ticket Count/Business Rule Validation Tests")
    class TicketCountValidationTests{
        @Test
        @DisplayName("Over max ticket quantity should throw exception")
        public void testOverMaxTicketRequest_shouldThrowException(){
            TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, TicketServiceImpl.MAX_TICKETS+1);

            InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, ()-> ticketService.purchaseTickets(validAccountId, adultTicket));
            assertEquals(InvalidPurchaseException.EXCEEDS_MAX_TICKETS_MESSAGE,exception.getMessage());
        }

        @Test
        @DisplayName("Max ticket quantity should not throw exception")
        public void testMaxTicketRequest_shouldThrowException(){
            TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, TicketServiceImpl.MAX_TICKETS);

            assertDoesNotThrow(()-> ticketService.purchaseTickets(validAccountId, adultTicket));
        }

        @Test
        @DisplayName("Combined count over max ticket quantity should throw exception")
        public void testCombineCountOverMaxTicketRequest_shouldThrowException(){
            TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, TicketServiceImpl.MAX_TICKETS);
            TicketTypeRequest childTicket = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);

            InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, ()-> ticketService.purchaseTickets(validAccountId, adultTicket, childTicket));
            assertEquals(InvalidPurchaseException.EXCEEDS_MAX_TICKETS_MESSAGE,exception.getMessage());
        }

        @Test
        @DisplayName("Tickets missing Adult with Child and Infant should throw exception")
        public void testTicketMissingAdultWithChildInfant_shouldThrowException(){
            TicketTypeRequest infantTicket = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);
            TicketTypeRequest childTicket = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);

            InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, ()-> ticketService.purchaseTickets(validAccountId, infantTicket, childTicket));
            assertEquals(InvalidPurchaseException.MISSING_ADULT_TO_BUY_CHILD_INFANT_TICKETS_MESSAGE,exception.getMessage());
        }

        @Test
        @DisplayName("Tickets with Infant missing accompanying Adult should throw exception")
        public void testTicketsWithInfantMissingAccompanyingAdult_shouldThrowException(){
            TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
            TicketTypeRequest infantTicket = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2);

            InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, ()-> ticketService.purchaseTickets(validAccountId, adultTicket, infantTicket));
            assertEquals(InvalidPurchaseException.ADULT_NOT_PRESENT_WITH_INFANT_MESSAGE,exception.getMessage());
        }

    }


    @Nested
    @DisplayName("Successful Ticket Purchase Tests")
    class SuccessfulTicketPurchaseTests {

        @Test
        @DisplayName("Valid adult ticket purchase should make payment and reserve seat")
        public void validAdultTicketPurchase_shouldNotThrowException() {
            TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

            assertDoesNotThrow(()-> ticketService.purchaseTickets(validAccountId, adultTicket));
        }

        @Test
        @DisplayName("Valid mixed ticket purchase should make correct payment and reserve correct seats")
        public void validMixedTicketPurchase_shouldNotThrowException() {
            TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
            TicketTypeRequest childTicket = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 3);
            TicketTypeRequest infantTicket = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);

            assertDoesNotThrow(()-> ticketService.purchaseTickets(validAccountId, adultTicket, childTicket, infantTicket));
        }

        @Test
        @DisplayName("Equal number of adults and infants should be valid")
        public void equalNumberOfAdultsAndInfants_shouldNotThrowException() {
            TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 3);
            TicketTypeRequest infantTicket = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 3);

            assertDoesNotThrow(()-> ticketService.purchaseTickets(validAccountId, adultTicket, infantTicket));
        }
    }
}