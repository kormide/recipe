package ca.derekcormier.recipe.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;

import ca.derekcormier.recipe.cookbook.Cookbook;
import liqp.RenderSettings;
import liqp.Template;

public abstract class CookbookGenerator {
    private ObjectMapper objectMapper = new ObjectMapper();

    public abstract void generate(Cookbook cookbook, String targetDir);

    protected String renderTemplate(String templatePath, Map<String,Object> data) {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(templatePath);
        Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        String templateContent = scanner.hasNext() ? scanner.next() : "";
        Template template = Template.parse(templateContent).withRenderSettings(new RenderSettings.Builder().withStrictVariables(true).build());
        try {
            return template.render(objectMapper.writeValueAsString(data));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected String createDirectories(String targetDir) {
        File directory = new File(targetDir);
        directory.mkdirs();
        return directory.getPath();
    }

    protected void writeToFile(String filepath, String content) {
        File file = new File(filepath);

        try(FileWriter writer = new FileWriter(file)) {
            BufferedWriter bufferedWriter = new BufferedWriter(writer);

            bufferedWriter.write(content);

            bufferedWriter.close();
            writer.close();

        } catch (IOException e) {
            throw new RuntimeException("could not write to " +  filepath);
        }
    }
}
