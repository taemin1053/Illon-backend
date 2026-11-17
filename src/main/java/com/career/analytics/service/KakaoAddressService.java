package com.career.analytics.service;

import com.career.analytics.dto.KakaoAddressResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@Service
public class KakaoAddressService {

    private final RestClient restClient;

    @Value("${kakao.rest-api-key}")
    private String restApiKey;

    public KakaoAddressService() {
        // RestClient 초기화
        this.restClient = RestClient.builder().build();
    }

    /**
     * 텍스트 주소를 위도와 경도로 변환합니다.
     * 
     * @param address 검색 대상 주소
     * @return 좌표를 포함한 DocumentDto
     */
    public KakaoAddressResponseDto.DocumentDto getCoordinate(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("주소 입력값이 비어 있습니다.");
        }

        // URI 빌드 (한글 주소 인코딩 적용)
        URI uri = UriComponentsBuilder
                .fromUriString("https://dapi.kakao.com/v2/local/search/address.json")
                .queryParam("query", address)
                .build()
                .encode()
                .toUri();

        try {
            KakaoAddressResponseDto response = restClient.get()
                    .uri(uri)
                    .header("Authorization", "KakaoAK " + restApiKey)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, responseEntity) -> {
                        log.error("카카오 API 호출 실패: 상태코드 {}", responseEntity.getStatusCode());
                        throw new RuntimeException("카카오 API 호출에 실패했습니다. 상태코드: " + responseEntity.getStatusCode());
                    })
                    .body(KakaoAddressResponseDto.class);

            // 방어 로직: 검색 결과가 없거나 응답이 비어있을 경우 예외 처리 혹은 기본 좌표값 반환
            if (response == null || response.documents() == null || response.documents().isEmpty()) {
                log.warn("주소 '{}'에 대한 검색 결과가 존재하지 않습니다. 기본 좌표를 반환합니다.", address);
                // 기본 좌표값 반환 (서울시청 좌표 x=126.9780, y=37.5665)
                return new KakaoAddressResponseDto.DocumentDto("기본 위치 (서울시청)", "126.9780", "37.5665");
            }

            // 첫 번째 매칭 결과 반환
            return response.documents().get(0);

        } catch (Exception e) {
            log.error("카카오 Local API 연동 중 오류 발생: ", e);
            throw new RuntimeException("주소 변환 처리 중 오류가 발생했습니다.", e);
        }
    }
}
