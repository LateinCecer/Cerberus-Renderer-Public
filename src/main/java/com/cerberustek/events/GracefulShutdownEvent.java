/*
 * Cerberus-Renderer is a OpenGL-based rendering engine.
 * Visit https://cerberustek.com for more details
 * Copyright (c)  2020  Adrian Paskert
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. See the file LICENSE included with this
 * distribution for more information.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.cerberustek.events;

import com.cerberustek.event.Event;
import com.cerberustek.service.CerberusService;

public class GracefulShutdownEvent implements Event {

    private final CerberusService service;
    private final String reason;
    private final StackTraceElement[] stackTrace;
    private final long time;

    public GracefulShutdownEvent(CerberusService service, String reason, StackTraceElement[] stackTrace) {
        this.service = service;
        this.reason = reason;
        this.stackTrace = stackTrace;
        this.time = System.currentTimeMillis();
    }

    public CerberusService getService() {
        return service;
    }

    public String getReason() {
        return reason;
    }

    public StackTraceElement[] getStackTrace() {
        return stackTrace;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Graceful shutdown requested by ").append(service.getClass().getSimpleName()).
                append(" because of ").append(reason).append(" at:").append('\n').append('\n');
        // builder.append("\t#############################################################################################");
        builder.append('\n');
        for (StackTraceElement e : stackTrace)
            builder.append("\t# ").append(e.toString()).append('\n');
        // builder.append("\t#############################################################################################");
        builder.append('\n');
        return builder.toString();
    }
}
