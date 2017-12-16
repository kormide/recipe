package ca.derekcormier.recipe.generator;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_TEST_RESOURCES)
public class RecipeGeneratorMojo extends AbstractMojo {
    @Parameter
    private String flavour;
    @Parameter(defaultValue = "${project.basedir}/cookbook.yaml")
    private String cookbook;
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/recipe")
    private String targetDir;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Main.main(new String[]{flavour, cookbook, targetDir});
    }
}
