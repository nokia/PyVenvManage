/*
 * Copyright (C) 2017 Nokia
 */

package com.github.nokia;

import com.intellij.ide.IconLayerProvider;
import com.intellij.ide.IconProvider;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.jetbrains.python.sdk.PythonSdkType;
import icons.PythonIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

/**
 * Sets the icon of Virtual Environment directories in the project view.
 */
public class VenvIconProvider extends IconProvider implements IconLayerProvider {
    @Nullable
    @Override
    public Icon getIcon(@NotNull PsiElement element, int flags) {
        if (element instanceof PsiDirectory) {
            final String venvRootPath = ((PsiDirectory) element).getVirtualFile().getPath();
            if (PythonSdkType.getPythonExecutable(venvRootPath) != null) {
                return PythonIcons.Python.Virtualenv;
            };
        }
        return null;
    }

    @Nullable
    @Override
    public Icon getLayerIcon(@NotNull Iconable element, boolean isLocked) {
        if (element instanceof PsiDirectory) {
            final String venvRootPath = ((PsiDirectory) element).getVirtualFile().getPath();
            if (PythonSdkType.getPythonExecutable(venvRootPath) != null) {
                return PythonIcons.Python.Virtualenv;
            };
        }
        return null;
    }

    @NotNull
    @Override
    public String getLayerDescription() {
        return "Python venv";
    }
}
