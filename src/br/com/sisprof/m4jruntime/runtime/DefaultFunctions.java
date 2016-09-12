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

}
