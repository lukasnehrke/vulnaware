package dev.lukasnehrke.vulnaware.user.error;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class UserAlreadyExistsProblem extends AbstractThrowableProblem {

    public UserAlreadyExistsProblem() {
        super(null, "User already exists", Status.BAD_REQUEST, "User already exists");
    }
}
