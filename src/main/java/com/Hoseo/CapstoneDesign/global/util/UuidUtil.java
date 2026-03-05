package com.Hoseo.CapstoneDesign.global.util;

import com.github.f4b6a3.uuid.UuidCreator;

import java.util.UUID;

public class UuidUtil {

    public static UUID getUuidv7() {
        return UuidCreator.getTimeOrderedEpoch();
    }
}
