package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;
class PrimoProbableTest {

    PrimoProbable prob;

    @BeforeEach
    void setUp() {
        prob = new PrimoProbable();
    }

    @Test
    void otroProbablePrimo() {
        BigInteger p = new BigInteger("1" + "0".repeat(300));
        BigInteger q = prob.otroProbablePrimo(p);
        System.out.println(q);
    }
}