package nextstep.subway.dto;

import nextstep.subway.domain.line.Distance;

public class SectionRequest {

    private Long upStationId; // 상행역 아이디
    private Long downStationId; // 하행역 아이디
    private int distance; // 구간 길이

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Distance getDistance() {
        return new Distance(distance);
    }
}
