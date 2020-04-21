package lol.cicco.ioc.bean3;

import lol.cicco.ioc.annotation.Registration;
import lombok.Getter;

@Registration
public class Bean3 {
    @Getter
    private Bean2 bean2;

    public Bean3(Bean2 bean2) {
        this.bean2 = bean2;
    }
}
