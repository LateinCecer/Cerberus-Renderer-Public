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

import com.cerberustek.logic.math.Vector3f;

public class LightUtil {

    public static int minWavelength = 380; // nm
    public static int maxWavelength = 780; // nm

    public static int minWarmth = 1800; // Kelvin
    public static int maxWarmth = 16000; // Kelvin

    public static int C = 299792458; // Speed of light in m/s

    private static Vector3f intensity = new Vector3f(0.212671f, 0.71516f, 0.072169f);

    public static float calcIntensity(Vector3f rgb) {
        return rgb.dot(intensity);
    }

    public static Vector3f warmthToRGB(int warmth) {
        if (warmth < minWarmth) {
            return new Vector3f(1, (float) Math.exp(-1),
                    (float) Math.exp(-2)).mul((float) warmth / (float) minWarmth);
        } else if (warmth < maxWarmth) {
            float percentage = (float) (warmth - minWarmth) * 2f / (float) (maxWarmth - minWarmth);
            return new Vector3f(
                    (float) Math.exp(-percentage),
                    (float) Math.exp(-1f),
                    (float) Math.exp(percentage - 2f));
        } else {
            return new Vector3f((float) Math.exp(-2),
                    (float) Math.exp(-1f), 1).mul(1 - ((float) (warmth - maxWarmth) / (float) minWarmth));
        }
    }

    public static Vector3f calcLightFromTime(float min, float max, float flux, float season, float timeOfDay) {
        if (season > 1)
            season %= 1;
        if (timeOfDay > 1)
            timeOfDay %= 1;

        float min_own = (float) (min + flux * Math.sin(season * Math.PI));
        float max_own = (float) (max + flux * Math.sin(season * Math.PI));

        float phi = (float) (Math.PI * (Math.sin(2 * season * Math.PI) + 1)) * flux / max;

        float intensity = (float) (Math.cos(Math.PI * (1.25f + 2f * timeOfDay)) + 1f) / 2f;
        return warmthToRGB((int) ((minWarmth + maxWarmth) * timeOfDay))
                .mul(max_own * intensity).add(min_own).div(min_own + max_own);
    }

    private static final double ln = Math.log(Math.E);
    public static double ln(double value) {
        return Math.log(value) / ln;
    }
}
