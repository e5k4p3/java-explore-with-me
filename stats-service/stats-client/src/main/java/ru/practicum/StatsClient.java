package ru.practicum;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.HitRequestDto;
import ru.practicum.dto.StatsResponseDto;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatsClient {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String appName;
    private final String start = "1970-01-01 00:00:00";
    private final String end = "2100-01-01 00:00:00";

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String url,
                       @Value("${application.name}") String appName,
                       RestTemplateBuilder restTemplateBuilder,
                       ObjectMapper objectMapper) {
        this.restTemplate = restTemplateBuilder
                .uriTemplateHandler(new DefaultUriBuilderFactory(url))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
        this.objectMapper = objectMapper;
        this.appName = appName;
    }

    public void addHit(String uri, String ip) {
        String path = "/hit";
        HitRequestDto hitRequestDto = new HitRequestDto(appName, uri, ip, LocalDateTime.now());
        HttpEntity<Object> requestEntity = new HttpEntity<>(hitRequestDto, defaultHeaders());
        restTemplate.exchange(path, HttpMethod.POST, requestEntity, Object.class);
    }

    public List<StatsResponseDto> getAllStatsWithUniqueIp(Set<String> uris) {
        String path = "/stats?start={start}&end={end}&uris={uris}&unique={unique}";

        Map<String, Object> params = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", true
        );

        return sendStatsRequest(path, params);
    }

    public List<StatsResponseDto> getAllStats(Set<String> uris) {
        String path = "/stats?start={start}&end={end}&uris={uris}";

        Map<String, Object> params = Map.of(
                "start", start,
                "end", end,
                "uris", uris
        );

        return sendStatsRequest(path, params);
    }

    private List<StatsResponseDto> sendStatsRequest(String path, Map<String, Object> parameters) {
        ResponseEntity<Object[]> response = restTemplate.getForEntity(path, Object[].class, parameters);
        Object[] objects = response.getBody();
        if (objects != null) {
            return Arrays.stream(objects)
                    .map(object -> objectMapper.convertValue(object, StatsResponseDto.class))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}
