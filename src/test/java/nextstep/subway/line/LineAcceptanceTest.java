package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

@DisplayName("지하철 노선 관련 기능")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest extends LineAcceptanceTestFixture {

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다")
    @Test
    void 지하철_노선_생성_확인() {
        //When
        ExtractableResponse<Response> response = 지하철_노선_생성("2호선");

        //Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        //Then
        List<String> lineNames = 지하철_노선_목록_조회().jsonPath().getList("name", String.class);
        assertThat(lineNames).containsAnyOf("2호선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다")
    @Test
    void 지하철_노선_목록_조회_확인() {
        //Given
        List<String> createLines = Arrays.asList("2호선", "3호선");
        createLines.forEach(LineAcceptanceTestFixture::지하철_노선_생성);

        //When
        ExtractableResponse<Response> response = 지하철_노선_목록_조회();

        //Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        //Then
        List<String> lineNames = response.jsonPath().getList("name", String.class);
        createLines.forEach(l -> assertThat(lineNames.contains(l)).isTrue());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다")
    @Test
    void 지하철_노선_조회_확인() {
        //Given
        int lineId = 지하철_노선_생성("2호선").jsonPath().get("id");

        //When
        ExtractableResponse<Response> response = 지하철_노선_조회(lineId);

        //Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        //Then
        String lineName = response.jsonPath().get("name");
        assertThat(lineName).isEqualTo("2호선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다")
    @Test
    void 지하철_노선_수정_확인() {
        //Given
        int lineId = 지하철_노선_생성("2호선").jsonPath().get("id");

        //When
        Map<String, String> patchParams = new HashMap<>();
        patchParams.put("name", "3호선");
        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .body(patchParams)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().patch("/lines/" + lineId)
                        .then().log().all()
                        .extract();

        //Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        //Then
        String lineName = 지하철_노선_조회(lineId).jsonPath().get("name");
        assertThat(lineName).isEqualTo("3호선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 삭제한다")
    @Test
    void 지하철_노선_삭제_확인() {
        //Given
        int lineId = 지하철_노선_생성("2호선").jsonPath().get("id");

        //When
        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().delete("/lines/" + lineId)
                        .then().log().all()
                        .extract();

        //Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        //Then
        List<String> lineNames = 지하철_노선_목록_조회().jsonPath().getList("name", String.class);
        assertThat(lineNames.contains("2호선")).isFalse();
    }
}