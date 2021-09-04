package edu.pure.server.util;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class ChineseAlphabeticalComparator implements Comparator<String> {
    private final Collator collator = Collator.getInstance(Locale.CHINA);

    @Override
    public int compare(final String lhs, final String rhs) {
        return this.collator.compare(lhs, rhs);
    }
}
