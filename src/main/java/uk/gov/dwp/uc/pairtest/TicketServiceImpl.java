package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.model.TicketCounts;

public class TicketServiceImpl implements TicketService {

    public static final int MAX_TICKETS = 25;
    private static final int INFANT_PRICE = 0;
    private static final int CHILD_PRICE = 15;
    private static final int ADULT_PRICE = 25;

    private final TicketPaymentService ticketPaymentService;
    private final SeatReservationService seatReservationService;
    public TicketServiceImpl(TicketPaymentService ticketPaymentService, SeatReservationService seatReservationService) {
        /**
         * Should only have private methods other than the one below.
         */
        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        // Validate inputs
        validatePurchaseRequest(accountId, ticketTypeRequests);

        TicketCounts ticketCounts = countTickets(ticketTypeRequests);
        validateTicketCounts(ticketCounts);

        int ticketPrice = calculateAmount(ticketCounts);
        int seatsToReserve = calculateSeatsToReserve(ticketCounts);


        ticketPaymentService.makePayment(accountId, ticketPrice);
        seatReservationService.reserveSeat(accountId, seatsToReserve);
    }

    public void validatePurchaseRequest(Long accountId, TicketTypeRequest... ticketTypeRequests){
        if(accountId<1){
            throw new InvalidPurchaseException(InvalidPurchaseException.INVALID_ACCOUNT_ID_MESSAGE);
        }
        if(ticketTypeRequests == null || ticketTypeRequests.length == 0){
            throw new InvalidPurchaseException(InvalidPurchaseException.NO_TICKET_REQUESTED_MESSAGE);
        }
    }

    private TicketCounts countTickets(TicketTypeRequest... ticketTypeRequests){
        TicketCounts ticketCounts = new TicketCounts();

        // Get ticket counts
        for(TicketTypeRequest request: ticketTypeRequests){
            if(request==null){
                throw new InvalidPurchaseException(InvalidPurchaseException.NO_TICKET_REQUESTED_MESSAGE);
            }
            int quantity = request.getNoOfTickets();
            if (quantity <= 0) {
                throw new InvalidPurchaseException(InvalidPurchaseException.INVALID_TICKET_QUANTITY_MESSAGE);
            }
            switch (request.getTicketType()) {
                case INFANT:
                    ticketCounts.infantCount += quantity;
                    break;
                case CHILD:
                    ticketCounts.childCount += quantity;
                    break;
                case ADULT:
                    ticketCounts.adultCount += quantity;
                    break;
                default:
                    throw new InvalidPurchaseException(InvalidPurchaseException.UNKNOWN_TICKET_TYPE_MESSAGE);
            }

            // Check if tickets more than max
            if(ticketCounts.getTotalTickets()>MAX_TICKETS){
                throw new InvalidPurchaseException(InvalidPurchaseException.EXCEEDS_MAX_TICKETS_MESSAGE);
            }
        }
        return ticketCounts;
    }

    public void validateTicketCounts(TicketCounts ticketCounts){
        // Check if adult ticket present to buy child and infant tickets
        if(ticketCounts.adultCount==0 && (ticketCounts.infantCount>0 || ticketCounts.childCount>0)){
            throw new InvalidPurchaseException(InvalidPurchaseException.MISSING_ADULT_TO_BUY_CHILD_INFANT_TICKETS_MESSAGE);
        }

        // Check if infant present with an adult
        if(ticketCounts.infantCount>ticketCounts.adultCount){
            throw new InvalidPurchaseException(InvalidPurchaseException.ADULT_NOT_PRESENT_WITH_INFANT_MESSAGE);
        }
    }



    private int calculateAmount(TicketCounts ticketCounts){
        return (ticketCounts.infantCount * INFANT_PRICE) +
                (ticketCounts.childCount * CHILD_PRICE) +
                (ticketCounts.adultCount * ADULT_PRICE);
    }

    private int calculateSeatsToReserve(TicketCounts ticketCounts){
        return ticketCounts.adultCount + ticketCounts.childCount;
    }
}
