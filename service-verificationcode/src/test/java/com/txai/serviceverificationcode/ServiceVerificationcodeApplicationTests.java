package com.txai.serviceverificationcode;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ServiceVerificationcodeApplicationTests {

    @Test
    void contextLoads() {
        for (int i = 0; i < 10000; i++) {
            int code = (int) ((Math.random() * 9 + 1) * Math.pow(10, 5));
            System.out.print(code+";");
            if (code / 1000000 > 0 || code / 10000 < 10){
                System.out.println("\ncode = "+code+"\n");
            }
        }
    }

}
