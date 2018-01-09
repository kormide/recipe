package ca.derekcormier.recipe.generator;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.util.ArrayList;
import java.util.List;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class RecipeGeneratorMojo extends AbstractMojo {
    @Component
    private MojoExecution execution;
    @Component
    private MavenProject project;

    @Parameter
    private String flavour;
    @Parameter(defaultValue = "${project.basedir}/cookbook.yaml")
    private String cookbook;
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/recipe")
    private String targetDir;
    @Parameter
    private String javaPackage;
    @Parameter
    private String ingredientPostfix;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        List<String> args = new ArrayList<>();

        args.add(flavour);
        args.add(cookbook);
        args.add(targetDir);

        if (null != javaPackage) {
            args.add("--javaPackage");
            args.add(javaPackage);
        }

        if (null != ingredientPostfix) {
            args.add("--ingredientPostfix");
            args.add(ingredientPostfix);
        }

        Main.main(args.toArray(new String[args.size()]));

        if (flavour.startsWith("java-")) {
            if (execution.getLifecyclePhase().equals("generate-sources")) {
                project.addCompileSourceRoot(targetDir);
            }
            else if (execution.getLifecyclePhase().equals("generate-test-sources")) {
                project.addTestCompileSourceRoot(targetDir);
            }
        }
    }
}
