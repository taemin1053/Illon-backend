package com.career.analytics.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration //Bean 설정을 담당하는 클래스라고 선언하기
@EnableCaching //@Cacheable, CacheEvict 어노테이션을 사용하기위한 설정
public class CacheConfig {

    @Getter
    @RequiredArgsConstructor
    public enum CacheType {
        // 캐시 이름, 만료 시간, 시간 단위, 최대 저장 개수
        JOB_SUMMARIES("jobSummaries", 10, TimeUnit.MINUTES, 1),
        //메인 공고 30개는 main_top_30이라는 단 하나의 키(Key) 값으로 30개가 담긴 하나의 리스트(List) 덩어리를 저장
        // 따라서 이 캐시 공간에는 객체가 딱 1개만 들어가면 되므로 사이즈를 1로 제한하여 메모리 낭비를 원천 차단

        //지역 검색 결과 캐싱( 10분 유지, 최대 100개 지역 보관 )
        JOB_SEARCH("jobSearch", 10, TimeUnit.MINUTES, 100),

        // 동적 필터링 검색 캐시 (10분 유지, 최대 500개 검색조건 조합 보관)
        JOB_SEARCH_DYNAMIC("jobSearchDynamic", 10, TimeUnit.MINUTES, 500);

        private final String cacheName;
        private final long ttl;
        private final TimeUnit timeUnit;
        private final long maximumSize;
    }


    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        List<CaffeineCache> caches = Arrays.stream(CacheType.values()) //Enum에 정의된 모든 캐시 정책을 가져옴
                .map(cache -> new CaffeineCache( //스프링 캐시 규격으로 변환
                        cache.getCacheName(),
                        Caffeine.newBuilder() //Caffeine 캐시 엔진 생성
                                .recordStats() // 캐시 히트(hit) 획수, 캐시 미스(miss) 획수, 데이터가 방출된 횟수등의 메트릭(통계) 기록
                                .expireAfterWrite(cache.getTtl(), cache.getTimeUnit()) //캐시에 데이터가 저장된 직후부터 시간을 체크하고, 메모리에서 파기
                                .maximumSize(cache.getMaximumSize())// 지정된 개수를 넘어가면 Caffeine의 내부 알고리즘이 알아서 캐시를 밀어내고 새 캐시를 넣는다.( 메모리 누수 방지)
                                .build()
                ))
                .toList();
        cacheManager.setCaches(caches);
        return cacheManager;
    }
}