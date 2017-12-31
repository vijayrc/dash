package com.vijayrc.tools.dash.source;

import org.junit.Test;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Created by vxr63 on 8/7/2015.
 */
public class PollerTaskTest {

    @Test
    public void shouldParseDates(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HHMMss");
        System.out.println(formatter.format(LocalDateTime.now()));
    }

    @Test
    public void shouldCompareDates() throws InterruptedException {
        LocalDateTime start = LocalDateTime.now();
        Thread.sleep(2000);
        LocalDateTime end = LocalDateTime.now();

        long p2 = ChronoUnit.SECONDS.between(start, end);
        System.out.println(p2);
    }


}