package edu.up.cs301.card;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class MeldTest {

    @Test
    public void getName() {
        Meld m1 = Meld.DOUBLE_RUN;
        Meld m2 = Meld.COMMON_MARRIAGE;
        Meld m3 = Meld.PINOCHLE;
        ArrayList a1 = new ArrayList<>();
        ArrayList a2 = new ArrayList<>();
        ArrayList a3 = new ArrayList<>();
        a1.add(m1); a1.add(m2);
        a2.add(m1); a2.add(m3);
        assertEquals(Meld.totalPoints(a1),  1520);
        assertEquals(Meld.totalPoints(a2), 1540);
        assertEquals(Meld.totalPoints(a3), 0);
    }
}