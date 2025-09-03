package com.gridnine.testing;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import com.gridnine.testing.Flight;
import com.gridnine.testing.Segment;

public class FlightFilters {
    
    // 1. Filter out flights that have already departed
    public static List<Flight> removeDepartedFlights(List<Flight> flights) {
        LocalDateTime now = LocalDateTime.now();
        return flights.stream()
                .filter(flight -> flight.getSegments().get(0).getDepartureDate().isAfter(now))
                .collect(Collectors.toList());
    }
    
    // 2. Filter out segments where arrival is before departure
    public static List<Flight> removeIncorrectSegments(List<Flight> flights) {
        return flights.stream()
                .filter(flight -> flight.getSegments().stream()
                        .allMatch(segment -> segment.getArrivalDate().isAfter(segment.getDepartureDate())))
                .collect(Collectors.toList());
    }
    
    // 3. Filter out flights with more than 2 hours ground time between segments
    public static List<Flight> removeLongGroundTimeFlights(List<Flight> flights) {
        return flights.stream()
                .filter(flight -> {
                    List<Segment> segments = flight.getSegments();
                    if (segments.size() <= 1) return true; // No ground time for single segment flights
                    
                    long totalGroundTime = 0;
                    for (int i = 0; i < segments.size() - 1; i++) {
                        LocalDateTime currentArrival = segments.get(i).getArrivalDate();
                        LocalDateTime nextDeparture = segments.get(i + 1).getDepartureDate();
                        totalGroundTime += Duration.between(currentArrival, nextDeparture).toHours();
                        
                        if (totalGroundTime > 2) {
                            return false;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }
    
    // Helper method to print filtered flights with a title
    public static void printFilteredFlights(String title, List<Flight> flights) {
        System.out.println("\n" + title + ":");
        if (flights.isEmpty()) {
            System.out.println("No flights match the criteria.");
        } else {
            for (int i = 0; i < flights.size(); i++) {
                System.out.printf("Flight %d: %s%n", i + 1, flights.get(i));
            }
        }
    }
}
