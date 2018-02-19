package com.hyd.pass.utils;

/**
 * @author yidin
 */
public class OrElse {

    private boolean predicate;

    public OrElse(boolean predicate) {
        this.predicate = predicate;
    }

    public OrElse(boolean predicate, Runnable positiveAction) {
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
