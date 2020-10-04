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

package com.cerberustek.util;


import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class PriorityConsumer implements Comparable<PriorityConsumer> {

    public final Consumer<Double> consumer;
    public long time;

    public PriorityConsumer(Consumer<Double> consumer) {
        this.consumer = consumer;
        this.time = System.currentTimeMillis();
    }

    public void accept(Double time) {
        consumer.accept(time);
        this.time = System.currentTimeMillis();
    }

    @Override
    public int compareTo(@NotNull PriorityConsumer o) {
        return Math.round(time - o.time);
    }
}
