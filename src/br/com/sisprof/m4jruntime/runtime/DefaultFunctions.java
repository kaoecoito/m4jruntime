package br.com.sisprof.m4jruntime.runtime;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalDate;

/**
 * Created by kaoe on 12/09/16.
 */
public class DefaultFunctions {

    private static final DateTime EPOCH = new LocalDate(1840,12,31).toDateTimeAtStartOfDay();

    @MumpsFunction({"$HOROLOG","$H"})
    public static MValue horolog(MValue[] args) {
        DateTime now = DateTime.now();
        Duration days = new Duration(EPOCH, now);
        Duration seconds = new Duration(now.withTimeAtStartOfDay(), now);
        String ret = days.toStandardDays().getDays() + "," + seconds.toStandardSeconds().getSeconds();
        return new MValueString(ret);
    }

    @MumpsFunction({"$JOB","$J"})
    public static MValue job(MValue[] args) {
        return new MValueNumber(VirtualMachine.getCurrent().getJob());
    }

    @MumpsFunction({"$TEST","$T"})
    public static MValue test(MValue[] args) {
        VirtualMachine vm = VirtualMachine.getCurrent();
        Variable variable = vm.getFrame().getGlobalScope().getVariable("$TEST");
        int ret = 0;
        if (variable!=null) {
            if (!NumberOperations.isFalse(variable.getValue())) {
                ret = 1;
            }
        }
        return new MValueNumber(ret);
    }

}
