package lol.cicco.ioc.bean;

import lol.cicco.ioc.annotation.Binder;
import lol.cicco.ioc.annotation.Registration;
import lombok.Getter;

import java.time.*;

@Registration
public class BinderBean {

    @Binder("binder.year")
    private Year year;
    @Binder("binder.month")
    private Month month;
    @Binder("binder.year.month")
    private YearMonth yearMonth;
    @Binder("binder.month.day")
    private MonthDay monthDay;

    @Binder("binder.localdatetime")
    private LocalDateTime localDateTime;

    @Getter
    @Binder(value = "test-fk-value", defaultValue = "10", refresh = true)
    private int value;

    public void print() {
        System.out.println("year:" + year);
        System.out.println("month:" + month);
        System.out.println("yearMonth:" + yearMonth);
        System.out.println("monthDay:" + monthDay);
        System.out.println("localDateTime:" + localDateTime);
    }
}
