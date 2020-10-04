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

package com.cerberustek.window.impl;

import static org.lwjgl.egl.EGL10.*;
import static org.lwjgl.egl.EGL11.EGL_CONTEXT_LOST;

public enum EGLError {

    SUCCESS(EGL_SUCCESS),
    NOT_INITIATED(EGL_NOT_INITIALIZED),
    BAD_ACCESS(EGL_BAD_ACCESS),
    BAD_ALLOC(EGL_BAD_ALLOC),
    BAD_ATTRIBUTE(EGL_BAD_ATTRIBUTE),
    BAD_CONTEXT(EGL_BAD_CONTEXT),
    BAD_CONFIG(EGL_BAD_CONFIG),
    BAD_CURRENT_SURFACE(EGL_BAD_CURRENT_SURFACE),
    BAD_DISPLAY(EGL_BAD_DISPLAY),
    BAD_SURFACE(EGL_BAD_SURFACE),
    BAD_MATCH(EGL_BAD_MATCH),
    BAD_PARAMETER(EGL_BAD_PARAMETER),
    BAD_NATIVE_PIXMAP(EGL_BAD_NATIVE_PIXMAP),
    BAD_NATIVE_WINDOW(EGL_BAD_NATIVE_WINDOW),
    CONTEXT_LOST(EGL_CONTEXT_LOST);

    private final int egl;

    EGLError(int egl) {
        this.egl = egl;
    }

    public int getEGL() {
        return egl;
    }

    public static EGLError fromEGL(int egl) {
        switch(egl) {
            case EGL_NOT_INITIALIZED:
                return NOT_INITIATED;
            case EGL_BAD_ACCESS:
                return BAD_ACCESS;
            case EGL_BAD_ATTRIBUTE:
                return BAD_ATTRIBUTE;
            case EGL_BAD_CONTEXT:
                return BAD_CONTEXT;
            case EGL_BAD_CONFIG:
                return BAD_CONFIG;
            case EGL_BAD_CURRENT_SURFACE:
                return BAD_CURRENT_SURFACE;
            case EGL_BAD_DISPLAY:
                return BAD_DISPLAY;
            case EGL_BAD_SURFACE:
                return BAD_SURFACE;
            case EGL_BAD_MATCH:
                return BAD_MATCH;
            case EGL_BAD_PARAMETER:
                return BAD_PARAMETER;
            case EGL_BAD_NATIVE_PIXMAP:
                return BAD_NATIVE_PIXMAP;
            case EGL_BAD_NATIVE_WINDOW:
                return BAD_NATIVE_WINDOW;
            case EGL_CONTEXT_LOST:
                return CONTEXT_LOST;
            default:
                return SUCCESS;
        }
    }
}
