package uk.gov.dwp.uc.pairtest.model;

public class TicketCounts {
    public int infantCount;
    public int childCount;
    public int adultCount;

    public int getTotalTickets(){
        return infantCount + childCount + adultCount;
    }
}
