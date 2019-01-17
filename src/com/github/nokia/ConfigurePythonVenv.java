/*
 * Copyright (C) 2017 Nokia
 */

package com.github.nokia;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.python.configuration.PyConfigurableInterpreterList;
import com.jetbrains.python.sdk.PythonSdkType;

import java.util.Collection;

/**
 * Configures the selected directory as the virtual environment for the containing project.
 */
public abstract class ConfigurePythonVenv extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getData(CommonDataKeys.PROJECT);

        if (project == null) {
            return;
        }

        final VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);

        if (file == null || !file.isDirectory()) {
            return;
        }

        final String pythonExecutable = PythonSdkType.getPythonExecutable(file.getPath());
        if (pythonExecutable != null) {
            setInterpreter(project, file, pythonExecutable);
        }
    }

    abstract void setInterpreter(Project project, VirtualFile file, String pythonExecutable);

    Sdk findExistingSdkForExecutable(String pythonExecutablePath, Project project) {
        final PyConfigurableInterpreterList interpreterList = PyConfigurableInterpreterList.getInstance(project);
        Collection<Sdk> sdks = interpreterList.getModel().getProjectSdks().values();
        for (Sdk sdk : sdks) {
            if (pythonExecutablePath.equals(sdk.getHomePath())) {
                return sdk;
            }
        }
        return null;
    }

    void showNotification(Project project, String message) {
        NotificationGroup notificationGroup = NotificationGroup.balloonGroup("SDK changed notification");
        Notification notification = notificationGroup.createNotification(message, MessageType.INFO);
        notification.notify(project);
    }

    boolean isEventOnVenvDir(AnActionEvent e) {
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);

        if (file == null) {
            return false;
        }

        if (file.isDirectory()) {

            if (PythonSdkType.getPythonExecutable(file.getPath()) != null) {
                e.getPresentation().setEnabledAndVisible(true);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isDumbAware() {
        return false;
    }

}
