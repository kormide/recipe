package ca.derekcormier.recipe.generator;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.ArrayList;
import java.util.List;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_TEST_RESOURCES)
public class RecipeGeneratorMojo extends AbstractMojo {
    @Parameter
    private String flavour;
    @Parameter(defaultValue = "${project.basedir}/cookbook.yaml")
    private String cookbook;
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/recipe")
    private String targetDir;
    @Parameter
    private String javaPackage;

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

        Main.main(args.toArray(new String[args.size()]));
    }
}
