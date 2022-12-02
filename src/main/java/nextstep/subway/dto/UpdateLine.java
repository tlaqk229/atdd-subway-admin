package nextstep.subway.dto;

import nextstep.subway.domain.Line;

public class UpdateLine {

    private String name;

    public String getName() {
        return name;
    }

    public Line toLine() {
        return new Line(name);
    }
}