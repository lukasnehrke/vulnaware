package dev.lukasnehrke.vulnaware.util;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class ResourceNotFoundProblem extends AbstractThrowableProblem {

    public ResourceNotFoundProblem(final String resource, final Object id) {
        super(null, "Not found", Status.NOT_FOUND, String.format("%s not found: %s", resource, id));
    }
}
