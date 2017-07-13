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
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.python.configuration.PyConfigurableInterpreterList;
import com.jetbrains.python.sdk.PythonSdkType;

import java.text.MessageFormat;
import java.util.Collection;

/**
 * Configures the selected directory as the virtual environment for the containing project.
 */
public class ConfigurePythonVenv extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(CommonDataKeys.PROJECT);

        if (project == null) {
            return;
        }

        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);

        if (file == null || !file.isDirectory()) {
            return;
        }

        final String pythonExecutable = PythonSdkType.getPythonExecutable(file.getPath());

        if (pythonExecutable != null) {
            final PyConfigurableInterpreterList interpreterList = PyConfigurableInterpreterList.getInstance(project);
            Collection<Sdk> sdks = interpreterList.getModel().getProjectSdks().values();
            Sdk s = findExistingSdkForExecutable(pythonExecutable, sdks);

            if (s == null) {
                s = SdkConfigurationUtil.createAndAddSDK(pythonExecutable, PythonSdkType.getInstance());
            }

            SdkConfigurationUtil.setDirectoryProjectSdk(project, s);
            showNotification(project, s);
        }
    }

    private Sdk findExistingSdkForExecutable(String pythonExecutablePath, Collection<Sdk> sdks) {
        for (Sdk sdk : sdks) {
            if (pythonExecutablePath.equals(sdk.getHomePath())) {
                return sdk;
            }
        }
        return null;
    }

    private void showNotification(Project project, Sdk s) {
        NotificationGroup notificationGroup = NotificationGroup.balloonGroup("SDK changed notification");
        String message = MessageFormat.format("Updated SDK for project {0} to: {1}", project, s);
        Notification notification = notificationGroup.createNotification(message, MessageType.INFO);
        notification.notify(project);
    }

    @Override
    public void update(AnActionEvent e) {
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);

        if (file == null) {
            return;
        }

        if (file.isDirectory()) {

            if (PythonSdkType.getPythonExecutable(file.getPath()) != null) {
                e.getPresentation().setEnabledAndVisible(true);
                return;
            }

        }

        e.getPresentation().setEnabledAndVisible(false);
    }


}
