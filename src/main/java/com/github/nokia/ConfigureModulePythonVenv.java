package com.github.nokia;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil;
import com.intellij.openapi.roots.ModuleRootModificationUtil;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.python.sdk.PythonSdkType;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;

public class ConfigureModulePythonVenv extends ConfigurePythonVenv {

    private final String POPUP_ITEM_TEXT = "Set module venv for %s";

    protected void setInterpreter(Project project, VirtualFile file, String pythonExecutable) {
        Sdk sdk = findExistingSdkForExecutable(pythonExecutable, project);
        if (sdk == null) {
            sdk = SdkConfigurationUtil.createAndAddSDK(pythonExecutable, PythonSdkType.getInstance());
        }
        PythonSdkType.getInstance().setupSdkPaths(sdk);

        Module module = ProjectFileIndex.SERVICE.getInstance(project).getModuleForFile(file, false);

        if (null != module) {
            ModuleRootModificationUtil.setModuleSdk(module, sdk);
            String message = MessageFormat.format("Updated SDK for module {0} to: {1}", module, sdk);
            showNotification(project, message);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        if (isEventOnVenvDir(e)) {
            VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
            Project project = e.getProject();
            Module module = ProjectFileIndex.SERVICE.getInstance(project).getModuleForFile(file, false);
            if (null != module) {
                e.getPresentation().setText(String.format(POPUP_ITEM_TEXT, module.getName()));
                e.getPresentation().setEnabledAndVisible(true);
                return;
            }
        }
        e.getPresentation().setEnabledAndVisible(false);
    }

}
