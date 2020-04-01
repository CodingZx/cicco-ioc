package lol.cicco.ioc.bean;

import lol.cicco.ioc.annotation.Binder;
import lol.cicco.ioc.annotation.Registration;

import java.time.Month;
import java.time.MonthDay;
import java.time.Year;
import java.time.YearMonth;

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

    public void print(){
        System.out.println("year:"+year);
        System.out.println("month:"+month);
        System.out.println("yearMonth:"+yearMonth);
        System.out.println("monthDay:"+monthDay);
    }
}
