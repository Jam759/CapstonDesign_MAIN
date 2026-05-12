package com.Hoseo.CapstoneDesign.library.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class LibraryApiProperties {

    @Value("${library.KMOOC}")
    private String K_MOOK_KEY;

    @Value("${library.YOUTUBE}")
    private String YOUTUBE_KEY;

    @Value("${library.kmooc-base-url:https://apis.data.go.kr/B552881/kmooc_v2_0}")
    private String kmoocBaseUrl;

    @Value("${library.youtube-base-url:https://www.googleapis.com}")
    private String youtubeBaseUrl;
}
