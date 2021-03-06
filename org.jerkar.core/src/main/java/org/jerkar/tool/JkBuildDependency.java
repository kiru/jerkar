package org.jerkar.tool;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.jerkar.api.depmanagement.JkComputedDependency;
import org.jerkar.api.utils.JkUtilsIterable;
import org.jerkar.api.utils.JkUtilsReflect;
import org.jerkar.api.utils.JkUtilsString;

/**
 * A dependency on files generated by a Jerkar build (mainly on external
 * project).
 * 
 * @author Jerome Angibaud
 */
public final class JkBuildDependency extends JkComputedDependency {

    private static final long serialVersionUID = 1L;

    private final JkBuild projectBuild;

    private final List<String> methods;

    private JkBuildDependency(JkBuild projectBuild, List<String> methods, List<File> files) {
        super(new Invoker(projectBuild, methods), files);
        this.methods = methods;
        this.projectBuild = projectBuild;
    }

    private static class Invoker implements Runnable, Serializable {

        private static final long serialVersionUID = 1L;

        private final JkBuild build;

        private final List<String> methods;

        Invoker(JkBuild build, List<String> methods) {
            super();
            this.build = build;
            this.methods = methods;
        }

        @Override
        public void run() {
            for (final String method : methods) {
                JkUtilsReflect.invoke(build, method);
            }
        }

    }

    public static JkBuildDependency of(JkBuild projectBuild, List<File> files) {
        return of(projectBuild, JkConstants.DEFAULT_METHOD, files);
    }

    public static JkBuildDependency of(JkBuild projectBuild, String methods, List<File> files) {
        final List<String> list = Arrays.asList(JkUtilsString.split(methods, " "));
        return new JkBuildDependency(projectBuild, list, JkUtilsIterable.listWithoutDuplicateOf(files));
    }

    public static JkBuildDependency of(JkBuild projectBuild, File... files) {
        return of(projectBuild, Arrays.asList(files));
    }

    public static JkBuildDependency of(JkBuild projectBuild, String methods, File... files) {
        return of(projectBuild, methods, Arrays.asList(files));
    }

    public JkBuild projectBuild() {
        return projectBuild;
    }

    @Override
    public String toString() {
        return projectBuild.toString() + " (" + this.projectBuild.getClass().getName() + " "
                + methods + ")";
    }

}