package com.project.mohammedsiddiq.guessfour;

import java.util.ArrayList;
import java.util.List;

public class Response {
    //Number of non exact match
    int numberOfCows;

    String forTheGuess;

    List<Boolean> bullsLocation;

    Response() {
        bullsLocation = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            bullsLocation.add(i, Boolean.FALSE);
        }
    }


    //number of exact match
    int numberOfBulls;


    @Override
    public String toString() {
        return "\t Guess: " + forTheGuess + "\n Exact matches: " + numberOfBulls + " " +
                "Non-Exact matches: " + numberOfCows;
    }
}

