package com.gridnine.testing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FlightFiltersTest {

    private List<Flight> testFlights;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.of(2025, 1, 1, 12, 0); // fixed time for deterministic tests
        testFlights = new ArrayList<>();

        // 1) Normal single-segment flight (kept by all)
        testFlights.add(createFlight(now.plusHours(1), now.plusHours(3)));

        // 2) Departed flight (removed by departed filter)
        testFlights.add(createFlight(now.minusHours(2), now.plusHours(1)));

        // 3) Incorrect segment (arrival before departure) (removed by incorrect segment filter)
        testFlights.add(createFlight(now.plusHours(2), now.plusHours(1)));

        // 4) Multi-segment with short ground time (kept by long ground time filter)
        testFlights.add(createFlight(now.plusHours(1), now.plusHours(2),
                                    now.plusHours(3), now.plusHours(4)));

        // 5) Multi-segment with long ground time (>2h) (removed by long ground time filter)
        testFlights.add(createFlight(now.plusHours(1), now.plusHours(2),
                                    now.plusHours(5), now.plusHours(6)));
    }

    @Test
    void removeDepartedFlights_filtersPastDepartures() {
        List<Flight> result = FlightFilters.removeDepartedFlights(testFlights, now);

        assertEquals(4, result.size());
        assertTrue(result.stream()
                .allMatch(f -> f.getSegments().get(0).getDepartureDate().isAfter(now)));
    }

    @Test
    void removeIncorrectSegments_filtersArrivalBeforeDeparture() {
        List<Flight> result = FlightFilters.removeIncorrectSegments(testFlights);
        assertEquals(4, result.size());
        assertTrue(result.stream().allMatch(f ->
                f.getSegments().stream().allMatch(s -> s.getArrivalDate().isAfter(s.getDepartureDate()))));
    }

    @Test
    void removeLongGroundTimeFlights_removesFlightsWithMoreThanTwoHoursOnGround() {
        List<Flight> valid = FlightFilters.removeDepartedFlights(testFlights, now);
        valid = FlightFilters.removeIncorrectSegments(valid);

        List<Flight> result = FlightFilters.removeLongGroundTimeFlights(valid);
        assertEquals(2, result.size()); // single-segment and short ground time
    }

    @Test
    void allFiltersTogether_produceExpectedSet() {
        List<Flight> result = FlightFilters.removeDepartedFlights(testFlights, now);
        result = FlightFilters.removeIncorrectSegments(result);
        result = FlightFilters.removeLongGroundTimeFlights(result);
        assertEquals(2, result.size());
    }

    @Test
    void emptyInput_returnsEmptyLists() {
        List<Flight> empty = List.of();
        assertTrue(FlightFilters.removeDepartedFlights(empty, now).isEmpty());
        assertTrue(FlightFilters.removeIncorrectSegments(empty).isEmpty());
        assertTrue(FlightFilters.removeLongGroundTimeFlights(empty).isEmpty());
    }

    @Test
    void singleSegment_notAffectedByGroundTimeFilter() {
        Flight f = createFlight(now.plusHours(1), now.plusHours(2));
        List<Flight> result = FlightFilters.removeLongGroundTimeFlights(List.of(f));
        assertEquals(1, result.size());
        assertEquals(f, result.get(0));
    }

    @Test
    void exactlyTwoHoursGroundTime_isKept() {
        Flight f = createFlight(now.plusHours(1), now.plusHours(2),
                                now.plusHours(4), now.plusHours(5));
        List<Flight> result = FlightFilters.removeLongGroundTimeFlights(List.of(f));
        assertEquals(1, result.size());
    }

    private Flight createFlight(LocalDateTime... dates) {
        if ((dates.length % 2) != 0) throw new IllegalArgumentException("even number of dates required");
        List<Segment> segments = new ArrayList<>(dates.length / 2);
        for (int i = 0; i < dates.length - 1; i += 2) {
            segments.add(new Segment(dates[i], dates[i + 1]));
        }
        return new Flight(segments);
    }
}
