package com.example.picture2pixel;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author 张天野
 */
@SpringBootApplication
public class Picture2pixelApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(Picture2pixelApplication.class);
        builder.headless(false).run(args);
    }

    /**
     * @param strings
     */
    @Override
    public void run(String... strings) {
        new ButtonUtil();
    }
}
