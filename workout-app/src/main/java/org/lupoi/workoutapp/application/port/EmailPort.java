package org.lupoi.workoutapp.application.port;/*
    @author Andrii
    @project workout
    @class EmailPort
    @version 1.0.0
    @since 09.05.2026 - 13.42
*/

public interface EmailPort {
    void sendPasswordResetEmail(String to, String resetLink);
}

