package roomescape.time.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.DuplicateResourceException;
import roomescape.exception.ResourceNotFoundException;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.time.controller.dto.ReservationTimeRequest;
import roomescape.time.domain.ReservationTime;
import roomescape.time.repository.ReservationTimeRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReservationTimeService {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationRepository reservationRepository;

    public ReservationTimeService(
            ReservationTimeRepository reservationTimeRepository,
            ReservationRepository reservationRepository
    ) {
        this.reservationTimeRepository = reservationTimeRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public ReservationTime save(ReservationTimeRequest request) {
        validateDuplicateTime(request);

        ReservationTime reservationTime = ReservationTime.create(request.startAt());
        return reservationTimeRepository.save(reservationTime);
    }

    @Transactional
    public void deleteById(Long id) {
        if (reservationRepository.existsByTimeId(id)) {
            throw new DuplicateResourceException("이 시간을 참조하는 예약이 있어 삭제할 수 없습니다. ID: " + id);
        }

        reservationTimeRepository.deleteById(id);
    }

    public List<ReservationTime> findAll() {
        return reservationTimeRepository.findAll();
    }

    public List<ReservationTimeResult> getTimesWithBooked(Long themeId, LocalDate date) {
        List<ReservationTime> allTimes = reservationTimeRepository.findAll();
        List<ReservationTime> availableTimes = reservationTimeRepository.findAvailableTimes(themeId, date);
        Set<Long> availableTimeIds = availableTimes.stream()
                .map(ReservationTime::getId)
                .collect(Collectors.toSet());

        return allTimes.stream()
                .map(time -> {
                    boolean isBooked = !availableTimeIds.contains(time.getId());
                    return ReservationTimeResult.from(time, isBooked);
                })
                .toList();
    }

    private void validateDuplicateTime(ReservationTimeRequest request) {
        if (reservationTimeRepository.existsByStartAt(request.startAt())) {
            throw new DuplicateResourceException("해당 시간이 이미 존재합니다.");
        }
    }

    public ReservationTime getById(Long id) {
        return reservationTimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 ID의 예약 시간이 존재하지 않습니다. ID: " + id));
    }
}
