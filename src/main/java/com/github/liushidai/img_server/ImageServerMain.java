package com.github.liushidai.img_server;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class ImageServerMain {
    public static void main(String[] args) {
        System.out.println("Running main method");
        Quarkus.run(args);
    }
}
