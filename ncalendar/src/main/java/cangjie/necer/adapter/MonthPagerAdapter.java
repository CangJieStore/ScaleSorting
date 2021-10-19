package cangjie.necer.adapter;

import android.content.Context;

import cangjie.necer.calendar.BaseCalendar;
import cangjie.necer.enumeration.CalendarType;

import org.joda.time.LocalDate;

import cangjie.necer.calendar.BaseCalendar;
import cangjie.necer.enumeration.CalendarType;

/**
 *
 * @author necer
 * @date 2018/9/11
 * qq群：127278900
 */
public class MonthPagerAdapter extends BasePagerAdapter {

    public MonthPagerAdapter(Context context, BaseCalendar baseCalendar) {
        super(context, baseCalendar);
    }

    @Override
    protected LocalDate getPageInitializeDate(int position) {
        return getInitializeDate().plusMonths(position - getPageCurrIndex());
    }

    @Override
    protected CalendarType getCalendarType() {
        return CalendarType.MONTH;
    }
}
