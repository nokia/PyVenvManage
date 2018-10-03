package com.github.nokia;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.python.sdk.PythonSdkType;

import java.text.MessageFormat;

public class ConfigureProjectPythonVenv extends ConfigurePythonVenv {

    protected void setInterpreter(Project project, VirtualFile file, String pythonExecutable) {
        Sdk sdk = findExistingSdkForExecutable(pythonExecutable, project);
        if (sdk == null) {
            sdk = SdkConfigurationUtil.createAndAddSDK(pythonExecutable, PythonSdkType.getInstance());
        }

        SdkConfigurationUtil.setDirectoryProjectSdk(project, sdk);
        String message = MessageFormat.format("Updated SDK for project {0} to: {1}", project, sdk);
        showNotification(project, message);
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(isEventOnVenvDir(e));
    }

}
