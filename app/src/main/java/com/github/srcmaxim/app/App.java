package com.github.srcmaxim.app;

import java.util.logging.Logger;

public class App {

    private static Logger LOG = Logger.getAnonymousLogger();

    public static void main(String...s) {
        var movie = MovieBuilder.builder()
            .name("Die Hard")
            .year(1995)
            .build();
        LOG.info(movie.toString());
    }

}
