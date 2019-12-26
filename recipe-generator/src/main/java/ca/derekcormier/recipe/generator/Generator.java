package ca.derekcormier.recipe.generator;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.cookbook.Ingredient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import liqp.RenderSettings;
import liqp.Template;
import liqp.filters.Filter;

public abstract class Generator {
  private final Cookbook cookbook;
  private ObjectMapper objectMapper = new ObjectMapper();

  public Generator(Cookbook cookbook) {
    this.cookbook = cookbook;
  }

  public abstract void generate(String domain, String targetDir, Map<String, Object> options);

  protected String renderTemplate(String templatePath, Map<String, Object> data) {
    InputStream inputStream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream(templatePath);
    Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
    String templateContent = scanner.hasNext() ? scanner.next() : "";
    Template template =
        Template.parse(templateContent)
            .withRenderSettings(new RenderSettings.Builder().withStrictVariables(false).build());

    for (Filter filter : getTemplateFilters()) {
      template = template.with(filter);
    }

    try {
      return template.render(objectMapper.writeValueAsString(data));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  protected List<Filter> getTemplateFilters() {
    return Collections.emptyList();
  }

  protected String createDirectories(String targetDir) {
    File directory = new File(targetDir);
    directory.mkdirs();
    return directory.getPath();
  }

  protected void writeToFile(String filepath, String content) {
    File file = new File(filepath);

    try (FileWriter writer = new FileWriter(file)) {
      BufferedWriter bufferedWriter = new BufferedWriter(writer);

      bufferedWriter.write(content);

      bufferedWriter.close();
      writer.close();

    } catch (IOException e) {
      throw new RuntimeException("could not write to " + filepath);
    }
  }

  protected List<List<String>> getConstantKeyValueArrays(Ingredient ingredient) {
    List<String> keys = new ArrayList<>();
    List<String> values = new ArrayList<>();

    for (String key : ingredient.getConstants().keySet()) {
      keys.add(key);
      values.add(ingredient.getConstants().get(key));
    }

    List<List<String>> keyValues = new ArrayList<>();
    keyValues.add(keys);
    keyValues.add(values);
    return keyValues;
  }

  public Cookbook getCookbook() {
    return cookbook;
  }
}
