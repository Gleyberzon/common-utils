package kubernetesUtils;

import Enumerations.MessageLevel;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.nio.file.Path;

import static propertyUtils.PropertyUtils.getGlobalProperty;
import static ReportUtils.Report.reportAndLog;

public class FileUtils {

    /**
     * Checks if the test is running on Kubernetes.
     * If so, it will copy the file/folder to the shared dir
     * @author Tzvika.Sela
     * @param sourceFile full path of source file/folder
     * @since 15.01.2023
     */
    public static Path copyFileToKubernetesSharedDir(String sourceFile) throws Exception {
        String gridHubURL = System.getProperty("hubURL");
        if(gridHubURL == null || gridHubURL.equals("NA"))
        {
            reportAndLog("Test is running locally, no need to copy to shared Dir", MessageLevel.INFO);
            throw new Exception("Not running on Kubernetes");
        }
        else
        {
            String jobName = System.getProperty("jobName");
            String buildId = System.getProperty("BuildID");
            //in case of k8s we should copy the file to the shared dir first
            String destFolder = getGlobalProperty("kubernetes_download_path") + File.separator
                    + jobName + File.separator + buildId + File.separator;

            reportAndLog(String.format("Test is running on Kubernetes, copying source file %s to shared Dir %s",sourceFile,destFolder)
                    , MessageLevel.INFO);

            String destFile = destFolder + FilenameUtils.getName(sourceFile);
            return fileUtils.FileUtils.duplicateFile(sourceFile,destFile);

        }
    }
}
