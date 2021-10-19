package cangjie.necer.calendar;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.AttributeSet;

import cangjie.necer.adapter.BasePagerAdapter;
import cangjie.necer.adapter.MonthPagerAdapter;
import cangjie.necer.enumeration.CalendarBuild;
import cangjie.necer.utils.Attrs;
import cangjie.necer.utils.CalendarUtil;

import org.joda.time.LocalDate;

import java.util.List;

import cangjie.necer.utils.CalendarUtil;

/**
 *
 * @author necer
 * @date 2018/9/11
 * qq群：127278900
 */
public class MonthCalendar extends BaseCalendar {

    public MonthCalendar(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected BasePagerAdapter getPagerAdapter(Context context, BaseCalendar baseCalendar) {
        return new MonthPagerAdapter(context,baseCalendar);
    }

    @Override
    protected int getTwoDateCount(LocalDate startDate, LocalDate endDate, int type) {
        return CalendarUtil.getIntervalMonths(startDate, endDate);
    }

    @Override
    protected LocalDate getIntervalDate(LocalDate localDate, int count) {
        return localDate.plusMonths(count);
    }

}
