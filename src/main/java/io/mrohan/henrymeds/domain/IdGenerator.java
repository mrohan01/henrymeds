package io.mrohan.henrymeds.domain;

import com.github.ksuid.Ksuid;
import com.github.ksuid.KsuidGenerator;

import java.security.SecureRandom;

/**
 * ID generator which generates KSUID ids. See https://github.com/ksuid/ksuid for further information..
 */
public final class IdGenerator {

    private static final KsuidGenerator KSUID_GENERATOR = new KsuidGenerator(new SecureRandom());

    private IdGenerator() {
        // private to prevent instantiation
    }

    /**
     * @return A new KSUID string representation.
     */
    public static String generate() {
        final Ksuid ksuid = KSUID_GENERATOR.newKsuid();
        return ksuid.toString();
    }
}
