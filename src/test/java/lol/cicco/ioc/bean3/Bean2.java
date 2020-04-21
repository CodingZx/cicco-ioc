package lol.cicco.ioc.bean3;

import lol.cicco.ioc.annotation.Registration;
import lombok.Getter;

@Registration
public class Bean2 {
    @Getter
    private Bean3 bean3;

    public Bean2(Bean3 bean3) {
        this.bean3 = bean3;
    }
}
