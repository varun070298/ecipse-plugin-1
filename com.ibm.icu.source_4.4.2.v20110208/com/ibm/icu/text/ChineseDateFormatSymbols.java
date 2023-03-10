/****************************************************************************
 * Copyright (C) 2000-2010, International Business Machines Corporation and
 * others. All Rights Reserved.
 ****************************************************************************
 */

package com.ibm.icu.text;

import java.util.Locale;

import com.ibm.icu.impl.CalendarData;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ChineseCalendar;
import com.ibm.icu.util.ULocale;

/**
 * A subclass of {@link DateFormatSymbols} for {@link ChineseDateFormat}.
 * This class contains additional symbols corresponding to the
 * <code>ChineseCalendar.IS_LEAP_MONTH</code> field.
 *
 * @see ChineseDateFormat
 * @see com.ibm.icu.util.ChineseCalendar
 * @author Alan Liu
 * @stable ICU 2.0
 */
public class ChineseDateFormatSymbols extends DateFormatSymbols {
    // Generated by serialver from JDK 1.4.1_01
    static final long serialVersionUID = 6827816119783952890L;
    
    /*
     * Package-private array that ChineseDateFormat needs to be able to
     * read.
     */
    String isLeapMonth[]; // Do NOT add =null initializer

    /**
     * Construct a ChineseDateFormatSymbols for the default locale.
     * @stable ICU 2.0
     */
    public ChineseDateFormatSymbols() {
        this(ULocale.getDefault());
    }

    /**
     * Construct a ChineseDateFormatSymbols for the provided locale.
     * @param locale the locale
     * @stable ICU 2.0
     */
    public ChineseDateFormatSymbols(Locale locale) {
        super(ChineseCalendar.class, ULocale.forLocale(locale));
    }

    /**
     * Construct a ChineseDateFormatSymbols for the provided locale.
     * @param locale the locale
     * @stable ICU 3.2
     */
    public ChineseDateFormatSymbols(ULocale locale) {
        super(ChineseCalendar.class, locale);
    }

    /**
     * Construct a ChineseDateFormatSymbols for the provided calendar and locale.
     * @param cal the Calendar
     * @param locale the locale
     * @stable ICU 2.0
     */
    public ChineseDateFormatSymbols(Calendar cal, Locale locale) {
        super(cal==null?null:cal.getClass(), locale);
    }

    /**
     * Construct a ChineseDateFormatSymbols for the provided calendar and locale.
     * @param cal the Calendar
     * @param locale the locale
     * @stable ICU 3.2
     */
    public ChineseDateFormatSymbols(Calendar cal, ULocale locale) {
        super(cal == null ? null : cal.getClass(), locale);
    }

    // New API
    /**
     * @stable ICU 2.0
     */
    public String getLeapMonth(int leap) {
        return isLeapMonth[leap];
    }

    /**
     * {@inheritDoc}
     * @stable ICU 3.0
     */
    protected void initializeData(ULocale loc, CalendarData calData) {
        super.initializeData(loc, calData);
        isLeapMonth = calData.getStringArray("isLeapMonth");
    }

    void initializeData(DateFormatSymbols dfs) {
        super.initializeData(dfs);
        if (dfs instanceof ChineseDateFormatSymbols) {
            this.isLeapMonth = ((ChineseDateFormatSymbols)dfs).isLeapMonth;
        }
    }
}
