package lol.cicco.ioc.bean3;

import lol.cicco.ioc.annotation.Inject;
import lol.cicco.ioc.annotation.Registration;
import lombok.Getter;

@Registration
public class Bean1 {

    @Getter
    private NoRegister noRegister;

    public Bean1(@Inject(required = false) NoRegister noRegister) {
        this.noRegister = noRegister;
    }
}
