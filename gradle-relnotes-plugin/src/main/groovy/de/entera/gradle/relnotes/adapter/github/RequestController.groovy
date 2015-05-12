package de.entera.gradle.relnotes.adapter.github

import java.util.function.Consumer
import java.util.function.Predicate

class RequestController<C> {
    final C context
    Predicate<C> contextPredicate

    public RequestController(C context) {
        this.context = context
    }

    void updateContext(Consumer<C> contextConsumer) {
        contextConsumer.accept(context)
    }

    boolean checkPredicate() {
        if (!isPredicateNull(this.context, this.contextPredicate)) {
            if (isPredicateTrue(this.context, this.contextPredicate)) {
                return true
            }
        }
        return false
    }

    private boolean isPredicateNull(Object value,
                                    Predicate predicate) {
        return value == null || predicate == null
    }

    private boolean isPredicateTrue(Object value,
                                    Predicate predicate) {
        return predicate.test(value)
    }
}
