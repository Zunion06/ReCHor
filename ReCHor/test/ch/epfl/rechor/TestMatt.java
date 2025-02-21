package ch.epfl.rechor;

import org.junit.jupiter.api.Test;

public class TestMatt {

    @Test
    public void test(){
        StringBuilder builder = new StringBuilder(50);
        builder.append("test");
        builder.append(123);
        builder.append(true);
        System.out.println(builder.toString());

    }
}
