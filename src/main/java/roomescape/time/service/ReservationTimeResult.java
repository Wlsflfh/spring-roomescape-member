package roomescape.time.service;

import roomescape.time.domain.ReservationTime;

public record ReservationTimeResult(ReservationTime time, boolean isBooked) {

    public static ReservationTimeResult from(ReservationTime time, boolean isBooked) {
        return new ReservationTimeResult(time, isBooked);
    }
}
