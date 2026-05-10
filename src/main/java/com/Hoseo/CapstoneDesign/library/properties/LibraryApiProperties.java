package com.Hoseo.CapstoneDesign.library.properties;

import org.springframework.beans.factory.annotation.Value;

public class LibraryApiProperties {

    @Value("${library.KMOOC}")
    private String K_MOOK_KEY;

    @Value("${library.YOUTUBE}")
    private String YOUTUBE_KEY;

}
