package com.spring.book.tobyspring.calculator;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

public class CalculatorTest {

    Calculator calculator;
    String numFilepath;

    @BeforeEach
    public void setup() {
        this.calculator = new Calculator();
        this.numFilepath = getClass().getResource("/numbers.txt").getPath();
    }

    @Test
    void sumOfNumbers() throws IOException {
        Assertions.assertEquals(calculator.calcSum(this.numFilepath), 10);
    }

    @Test
    void multiplyOfNumbers() throws IOException {
        Assertions.assertEquals(calculator.calcMultiply(this.numFilepath), 24);
    }

    @Test
    public void concatenateStrings() throws IOException {
        Assertions.assertEquals(calculator.concatenate(this.numFilepath), "1234");
    }

}
