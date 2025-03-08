package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {

    public static final int MAX_TICKETS = 25;

    public TicketServiceImpl() {
        /**
         * Should only have private methods other than the one below.
         */
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        if(accountId<1){
            throw new InvalidPurchaseException(InvalidPurchaseException.INVALID_ACCOUNT_ID_MESSAGE);
        }

        if(ticketTypeRequests == null){
            throw new InvalidPurchaseException(InvalidPurchaseException.NO_TICKET_REQUESTED_MESSAGE);
        }

        countTickets(ticketTypeRequests);

    }

    private void countTickets( TicketTypeRequest... ticketTypeRequests){
        int infantCount = 0;
        int childCount = 0;
        int adultCount = 0;
        int totalCount = 0;

        //Get ticket counts
        for(TicketTypeRequest request: ticketTypeRequests){
            if(request==null){
                throw new InvalidPurchaseException(InvalidPurchaseException.NO_TICKET_REQUESTED_MESSAGE);
            }
            else if(request.getTicketType() == TicketTypeRequest.Type.INFANT){
                infantCount += request.getNoOfTickets();
            }
            else if(request.getTicketType() == TicketTypeRequest.Type.CHILD){
                childCount += request.getNoOfTickets();
            }
            else if(request.getTicketType() == TicketTypeRequest.Type.ADULT){
                adultCount += request.getNoOfTickets();
            }
            else{
                throw new InvalidPurchaseException(InvalidPurchaseException.UNKNOWN_TICKET_TYPE_MESSAGE);
            }
            totalCount += request.getNoOfTickets();
        }

        //Check if tickets more than max
        if(totalCount>MAX_TICKETS){
            throw new InvalidPurchaseException(InvalidPurchaseException.EXCEEDS_MAX_TICKETS_MESSAGE);
        }

        //Check if adult ticket present to buy child and infant tickets
        if(adultCount==0 && (infantCount>0 || childCount>0)){
            throw new InvalidPurchaseException(InvalidPurchaseException.MISSING_ADULT_TO_BUY_CHILD_INFANT_TICKETS_MESSAGE);
        }

        //Check if infant present with an adult
        if(infantCount>adultCount){
            throw new InvalidPurchaseException(InvalidPurchaseException.ADULT_NOT_PRESENT_WITH_INFANT_MESSAGE);
        }
    }

}
