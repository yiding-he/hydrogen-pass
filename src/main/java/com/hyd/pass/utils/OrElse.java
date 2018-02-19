package com.hyd.pass.utils;

/**
 *
 *
 * @author yidin
 */
public class OrElse {

    private boolean predicate;

    public static OrElse negative() {
        return new OrElse(false);
    }

    public static OrElse positive(Runnable positiveAction) {
        return new OrElse(true, positiveAction);
    }

    private OrElse(boolean predicate) {
        this.predicate = predicate;
    }

    private OrElse(boolean predicate, Runnable positiveAction) {
        this.predicate = predicate;

        if (predicate) {
            if (positiveAction != null) {
                positiveAction.run();
            }
        }
    }

    public void orElse(Runnable negativeAction) {
        if (!predicate) {
            if (negativeAction != null) {
                negativeAction.run();
            }
        }
    }
}
