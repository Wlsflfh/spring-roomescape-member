package roomescape.time.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import roomescape.time.service.ReservationTimeResult;

import java.time.LocalTime;

/**
 * 사용자에게 노출되는 "특정 테마/날짜의 시간 슬롯 + 예약 여부" 응답.
 *
 * <p>설계 의도:
 * <ul>
 *   <li>service 의 {@code ReservationTimeResult} 가 {@code ReservationTime} 도메인을 직접 안고 있어,
 *       그대로 직렬화하면 도메인 내부가 응답에 노출된다. 컨트롤러 경계에서 평탄화한다.</li>
 *   <li>JSON 필드명 {@code booked} 로 정리 (record accessor {@code isBooked()} 가
 *       그대로 직렬화되어 {@code "isBooked"} 로 노출되는 것을 피한다).</li>
 * </ul>
 */
public record AvailableTimeResponse(
        Long id,
        @JsonFormat(pattern = "HH:mm") LocalTime startAt,
        boolean booked
) {

    public static AvailableTimeResponse from(ReservationTimeResult result) {
        return new AvailableTimeResponse(
                result.time().getId(),
                result.time().getStartAt(),
                result.isBooked()
        );
    }
}
