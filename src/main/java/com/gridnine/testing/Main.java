package com.gridnine.testing;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Get test flights
        List<Flight> allFlights = FlightBuilder.createFlights();
        
        // 1. Print all flights
        FlightFilters.printFilteredFlights("=== All flights ===", allFlights);
        
        // 2. Filter out flights that have already departed
        List<Flight> futureFlights = FlightFilters.removeDepartedFlights(allFlights);
        FlightFilters.printFilteredFlights("\n=== Flights after removing departed flights ===", futureFlights);
        
        // 3. Filter out segments with arrival before departure
        List<Flight> validTimeFlights = FlightFilters.removeIncorrectSegments(allFlights);
        FlightFilters.printFilteredFlights("\n=== Flights after removing incorrect segments ===", validTimeFlights);
        
        // 4. Filter out flights with more than 2 hours ground time
        List<Flight> shortGroundTimeFlights = FlightFilters.removeLongGroundTimeFlights(allFlights);
        FlightFilters.printFilteredFlights("\n=== Flights with ground time <= 2 hours ===", shortGroundTimeFlights);
    }
}
