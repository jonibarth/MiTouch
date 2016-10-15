package com.example.grupo110.mitouchmobile.envioEmail;


import java.security.SecureRandom;
import java.math.BigInteger;

public final class EmailIdentifierGenerator {
    private SecureRandom random = new SecureRandom();

    public String nextSessionId() {
        return new BigInteger(32, random).toString(32);
    }
}
