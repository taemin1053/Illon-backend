package com.career.analytics.service;

import com.career.analytics.dto.JobDetailResponseDto;
import com.career.analytics.exception.JobNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobDetailCacheService {

    private final RestClient aiServerRestClient;

    private record RawJobDetail(
            String id, String title, String company, String location, String job_type,
            String occupation, String career_type, String education, String salary,
            String description, String requirements, String preferred, String benefits,
            String process, String deadline, String source, String url, int view_count, String created_at
    ) {}

    /**
     * 💡 Caffeine 캐싱이 적용된 공고 상세 조회 메서드
     */
    @Cacheable(value = "jobDetail", key = "#jobId")
    public JobDetailResponseDto getJobDetailCached(String jobId) {
        log.info("🚀 [Cache Miss] AI 서버에서 공고 상세 데이터를 실시간 조회하여 캐싱합니다. ID: {}", jobId);

        RawJobDetail raw = aiServerRestClient.get()
                .uri("/api/v1/jobs/{job_id}", jobId)
                .retrieve()
                .onStatus(status -> status.value() == 404, (request, response) -> {
                    throw new JobNotFoundException(jobId);
                })
                .body(RawJobDetail.class);

        if (raw == null) throw new JobNotFoundException(jobId);

        return new JobDetailResponseDto(
                raw.id(), raw.title(), raw.company(), raw.location(),
                raw.job_type(), raw.occupation(), raw.career_type(), raw.education(),
                raw.salary(), raw.description(), raw.requirements(), raw.preferred(),
                raw.benefits(), raw.process(), raw.deadline(), raw.source(),
                raw.url(), raw.view_count(), raw.created_at(),
                "37.5665", "126.9780"
        );
    }
}
